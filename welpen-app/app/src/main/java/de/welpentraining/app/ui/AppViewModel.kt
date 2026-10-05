package de.welpentraining.app.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import de.welpentraining.app.data.Repository

class AppViewModel(application: Application) : AndroidViewModel(application) {
    val repo = Repository.get(application)
    val data = repo.data
}
