package de.welpentraining.app.ui

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import de.welpentraining.app.ui.screens.ArticleScreen
import de.welpentraining.app.ui.screens.BreedScreen
import de.welpentraining.app.ui.screens.CommandDetailScreen
import de.welpentraining.app.ui.screens.CommandEditScreen
import de.welpentraining.app.ui.screens.KnowledgeScreen
import de.welpentraining.app.ui.screens.ProfileScreen
import de.welpentraining.app.ui.screens.ProgressScreen
import de.welpentraining.app.ui.screens.SessionScreen
import de.welpentraining.app.ui.screens.TodayScreen
import de.welpentraining.app.ui.screens.TrainingEditScreen
import de.welpentraining.app.ui.screens.TrainingsScreen

object Routes {
    const val TODAY = "today"
    const val TRAININGS = "trainings"
    const val PROGRESS = "progress"
    const val KNOWLEDGE = "knowledge"
    const val PROFILE = "profile"

    /** Training-ID oder [NEW] für eine freie Einheit. */
    fun session(trainingId: String) = "session/$trainingId"
    fun trainingEdit(trainingId: String) = "training_edit/$trainingId"
    fun command(commandId: String) = "command/$commandId"
    fun commandEdit(commandId: String) = "command_edit/$commandId"
    fun article(id: String) = "article/$id"
    fun breed(id: String) = "breed/$id"

    const val NEW = "new"
}

private data class Tab(val route: String, val label: String, val icon: ImageVector)

private val tabs = listOf(
    Tab(Routes.TODAY, "Heute", Icons.Filled.Today),
    Tab(Routes.TRAININGS, "Trainings", Icons.Filled.FitnessCenter),
    Tab(Routes.PROGRESS, "Fortschritt", Icons.Filled.Insights),
    Tab(Routes.KNOWLEDGE, "Wissen", Icons.AutoMirrored.Filled.MenuBook),
)

@Composable
fun WelpenApp(vm: AppViewModel = viewModel()) {
    val nav = rememberNavController()
    val data by vm.data.collectAsStateWithLifecycle()
    val backStack by nav.currentBackStackEntryAsState()
    val currentRoute = backStack?.destination?.route
    val showBottomBar = tabs.any { it.route == currentRoute }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    tabs.forEach { tab ->
                        NavigationBarItem(
                            selected = currentRoute == tab.route,
                            onClick = { nav.switchTab(tab.route) },
                            icon = { Icon(tab.icon, contentDescription = null) },
                            label = { Text(tab.label) },
                        )
                    }
                }
            }
        },
    ) { padding ->
        NavHost(
            navController = nav,
            startDestination = Routes.TODAY,
            modifier = Modifier.padding(padding).consumeWindowInsets(padding),
        ) {
            composable(Routes.TODAY) {
                TodayScreen(
                    data = data,
                    onStartSession = { nav.navigate(Routes.session(it)) },
                    onOpenProfile = { nav.navigate(Routes.PROFILE) },
                    onDeleteLog = vm.repo::deleteLog,
                )
            }
            composable(Routes.TRAININGS) {
                TrainingsScreen(
                    data = data,
                    onStart = { nav.navigate(Routes.session(it)) },
                    onEdit = { nav.navigate(Routes.trainingEdit(it)) },
                )
            }
            composable(Routes.PROGRESS) {
                ProgressScreen(
                    data = data,
                    onOpenCommand = { nav.navigate(Routes.command(it)) },
                    onNewCommand = { nav.navigate(Routes.commandEdit(Routes.NEW)) },
                )
            }
            composable(Routes.KNOWLEDGE) {
                KnowledgeScreen(
                    onOpenArticle = { nav.navigate(Routes.article(it)) },
                    onOpenBreed = { nav.navigate(Routes.breed(it)) },
                )
            }
            composable(Routes.PROFILE) {
                ProfileScreen(
                    profile = data.profile,
                    onSave = { vm.repo.saveProfile(it); nav.popBackStack() },
                    onBack = { nav.popBackStack() },
                    onOpenBreed = { nav.navigate(Routes.breed(it)) },
                )
            }
            composable("session/{id}") { entry ->
                val id = entry.arguments?.getString("id") ?: Routes.NEW
                SessionScreen(
                    data = data,
                    trainingId = id.takeUnless { it == Routes.NEW },
                    onSave = { vm.repo.saveLog(it); nav.popBackStack() },
                    onBack = { nav.popBackStack() },
                )
            }
            composable("training_edit/{id}") { entry ->
                val id = entry.arguments?.getString("id") ?: Routes.NEW
                TrainingEditScreen(
                    data = data,
                    trainingId = id.takeUnless { it == Routes.NEW },
                    onSave = { vm.repo.saveTraining(it); nav.popBackStack() },
                    onDelete = { vm.repo.deleteTraining(it); nav.popBackStack() },
                    onNewCommand = { nav.navigate(Routes.commandEdit(Routes.NEW)) },
                    onBack = { nav.popBackStack() },
                )
            }
            composable("command/{id}") { entry ->
                val id = entry.arguments?.getString("id").orEmpty()
                CommandDetailScreen(
                    data = data,
                    commandId = id,
                    onEdit = { nav.navigate(Routes.commandEdit(id)) },
                    onBack = { nav.popBackStack() },
                )
            }
            composable("command_edit/{id}") { entry ->
                val id = entry.arguments?.getString("id") ?: Routes.NEW
                CommandEditScreen(
                    data = data,
                    commandId = id.takeUnless { it == Routes.NEW },
                    onSave = { vm.repo.saveCommand(it); nav.popBackStack() },
                    onDelete = {
                        vm.repo.deleteCommand(it)
                        // Zurück hinter die Detailansicht des gelöschten Kommandos.
                        if (!nav.popBackStack(Routes.PROGRESS, inclusive = false)) nav.popBackStack()
                    },
                    onBack = { nav.popBackStack() },
                )
            }
            composable("article/{id}") { entry ->
                ArticleScreen(articleId = entry.arguments?.getString("id").orEmpty(), onBack = { nav.popBackStack() })
            }
            composable("breed/{id}") { entry ->
                BreedScreen(breedId = entry.arguments?.getString("id").orEmpty(), onBack = { nav.popBackStack() })
            }
        }
    }
}

private fun NavHostController.switchTab(route: String) {
    navigate(route) {
        popUpTo(graph.findStartDestination().id) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}
