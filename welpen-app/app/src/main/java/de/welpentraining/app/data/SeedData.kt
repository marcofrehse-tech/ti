package de.welpentraining.app.data

import java.time.DayOfWeek

/** Vordefinierte Kommandos und Trainings, die beim ersten Start angelegt werden. */
object SeedData {

    private val everyDay = DayOfWeek.entries.toSet()

    val commands = listOf(
        Command(
            id = "cmd_name", name = "Name / Aufmerksamkeit", category = "Grundlagen",
            description = "Der Welpe soll auf seinen Namen hin Blickkontakt aufnehmen. Grundlage für jedes weitere Training.",
            steps = listOf(
                "In ruhiger Umgebung den Namen einmal freundlich sagen.",
                "Sobald der Welpe dich anschaut: Markerwort („Yes“/„Fein“) und sofort belohnen.",
                "Namen nie mit Schimpfen oder Unangenehmem verknüpfen.",
                "Schrittweise mit mehr Ablenkung üben (Garten, Spaziergang).",
            ),
            builtIn = true,
        ),
        Command(
            id = "cmd_sitz", name = "Sitz", category = "Grundgehorsam",
            description = "Der Welpe setzt sich auf Signal hin und bleibt kurz sitzen.",
            steps = listOf(
                "Leckerli vor die Nase halten und langsam über den Kopf nach hinten führen.",
                "Sobald der Po den Boden berührt: markern und belohnen.",
                "Klappt es zuverlässig, das Wort „Sitz“ kurz vor der Bewegung sagen.",
                "Handzeichen langsam abbauen, bis das Wortsignal reicht.",
            ),
            builtIn = true,
        ),
        Command(
            id = "cmd_platz", name = "Platz", category = "Grundgehorsam",
            description = "Der Welpe legt sich auf Signal hin.",
            steps = listOf(
                "Aus dem Sitz ein Leckerli von der Nase senkrecht zum Boden führen.",
                "Dann langsam etwas vom Welpen weg ziehen, bis er sich ablegt.",
                "Liegt er: markern und zwischen den Vorderpfoten belohnen.",
                "Signal „Platz“ einführen, wenn die Bewegung sicher kommt.",
            ),
            builtIn = true,
        ),
        Command(
            id = "cmd_bleib", name = "Bleib", category = "Grundgehorsam",
            description = "Der Welpe bleibt in Position, bis er aufgelöst wird.",
            steps = listOf(
                "Welpe sitzt oder liegt. „Bleib“ sagen, eine Sekunde warten, belohnen.",
                "Immer mit einem Auflösewort beenden (z. B. „Okay“ oder „Lauf“).",
                "Nur eine Sache steigern: Dauer, Distanz ODER Ablenkung.",
                "Lieber kurz und erfolgreich als lang und mit Fehlern.",
            ),
            builtIn = true,
        ),
        Command(
            id = "cmd_hier", name = "Hier (Rückruf)", category = "Grundgehorsam",
            description = "Der Welpe kommt auf Signal sofort zu dir. Eines der wichtigsten Kommandos überhaupt.",
            steps = listOf(
                "Drinnen beginnen: Signal einmal rufen, in die Hocke gehen, Welpe kommt – Jackpot-Belohnung.",
                "Kommen muss sich immer lohnen; nie zum Schimpfen oder für Unangenehmes rufen.",
                "Zu zweit üben: abwechselnd rufen („Ping-Pong“-Rückruf).",
                "Draußen anfangs an der Schleppleine absichern.",
            ),
            builtIn = true,
        ),
        Command(
            id = "cmd_fuss", name = "Leinenführigkeit", category = "Alltag",
            description = "Locker an der Leine laufen, ohne zu ziehen.",
            steps = listOf(
                "Belohnen, wenn die Leine locker durchhängt und der Welpe neben dir ist.",
                "Zieht er: stehen bleiben, warten bis die Leine locker ist, dann weitergehen.",
                "Häufige Richtungswechsel machen dich interessant.",
                "Kurze Einheiten – Welpen ermüden schnell.",
            ),
            builtIn = true,
        ),
        Command(
            id = "cmd_aus", name = "Aus / Gib", category = "Alltag",
            description = "Der Welpe gibt einen Gegenstand freiwillig her.",
            steps = listOf(
                "Mit einem Spielzeug spielen, dann ein hochwertiges Leckerli vor die Nase halten.",
                "Lässt er los: „Aus“ sagen, markern, Leckerli geben.",
                "Spielzeug danach oft zurückgeben – Hergeben bedeutet nicht Spielende.",
                "Nie gewaltsam aus dem Maul ziehen.",
            ),
            builtIn = true,
        ),
        Command(
            id = "cmd_lass", name = "Lass es", category = "Impulskontrolle",
            description = "Der Welpe lernt, von etwas abzulassen (Futter am Boden, Schuhe …).",
            steps = listOf(
                "Leckerli in der geschlossenen Faust anbieten.",
                "Sobald der Welpe aufhört zu schnüffeln/knabbern: markern und mit der ANDEREN Hand belohnen.",
                "Später die offene Hand, dann Leckerli am Boden.",
                "Signal „Lass es“ einführen, wenn das Abwenden sicher kommt.",
            ),
            builtIn = true,
        ),
        Command(
            id = "cmd_warte", name = "Warte", category = "Impulskontrolle",
            description = "Kurzes Warten vor Futter, Türen oder dem Aussteigen aus dem Auto.",
            steps = listOf(
                "Napf langsam absenken; steht der Welpe auf, Napf wieder anheben.",
                "Bleibt er kurz ruhig: Napf abstellen und mit Freigabewort freigeben.",
                "Genauso an Türen üben – erst auf Freigabe hindurch.",
            ),
            builtIn = true,
        ),
        Command(
            id = "cmd_decke", name = "Decke / Ruheplatz", category = "Ruhe",
            description = "Der Welpe geht auf seinen Platz und entspannt dort.",
            steps = listOf(
                "Jedes Betreten der Decke ruhig belohnen.",
                "Belohnungen auf die Decke legen, solange der Welpe dort liegt.",
                "Signal „Decke“ einführen und Dauer langsam steigern.",
                "Ruhe ist ein Lernziel: Welpen brauchen 18–20 Stunden Schlaf am Tag.",
            ),
            builtIn = true,
        ),
        Command(
            id = "cmd_pipi", name = "Stubenreinheit", category = "Alltag",
            description = "Lösen draußen. Versuch = rausgegangen, Erfolg = draußen gelöst.",
            steps = listOf(
                "Nach dem Schlafen, Fressen, Spielen und alle 1–2 Stunden rausgehen.",
                "Immer zur gleichen Stelle; ruhig warten.",
                "Löst er sich: ruhig loben und belohnen, ggf. ein Signalwort verknüpfen.",
                "Malheur drinnen: kommentarlos mit Enzymreiniger säubern, nie schimpfen.",
            ),
            builtIn = true,
        ),
        Command(
            id = "cmd_alleine", name = "Alleinbleiben", category = "Alltag",
            description = "Schrittweise lernen, entspannt kurz allein zu sein.",
            steps = listOf(
                "Mit Sekunden beginnen: Raum verlassen, sofort zurückkommen, bevor der Welpe unruhig wird.",
                "Kommen und Gehen unspektakulär gestalten.",
                "Dauer sehr langsam steigern; vorher auslasten und lösen lassen.",
                "Erfolg = Welpe bleibt ruhig.",
            ),
            builtIn = true,
        ),
        Command(
            id = "cmd_handling", name = "Pflege & Anfassen", category = "Alltag",
            description = "Pfoten, Ohren, Zähne und Fell anschauen lassen – erleichtert später Tierarztbesuche.",
            steps = listOf(
                "Kurz eine Pfote berühren, belohnen, loslassen.",
                "Langsam steigern: Pfote halten, Ohren anschauen, Lefzen anheben, bürsten.",
                "Immer abbrechen, bevor der Welpe sich wehrt.",
            ),
            builtIn = true,
        ),
        Command(
            id = "cmd_beiss", name = "Beißhemmung", category = "Sozialverhalten",
            description = "Der Welpe lernt, sein Maul vorsichtig einzusetzen.",
            steps = listOf(
                "Wird es beim Spiel zu grob: Spiel sofort ruhig unterbrechen.",
                "Kauartikel oder Spielzeug als Alternative anbieten.",
                "Übermüdung vermeiden – müde Welpen beißen mehr.",
                "Erfolg = Spiel bleibt sanft.",
            ),
            builtIn = true,
        ),
        Command(
            id = "cmd_pfote", name = "Pfote geben", category = "Tricks",
            description = "Ein einfacher, beliebter Trick, der die Bindung stärkt.",
            steps = listOf(
                "Welpe sitzt. Leckerli in der Faust tief vor eine Pfote halten.",
                "Hebt er die Pfote an die Hand: markern und belohnen.",
                "Signal „Pfote“ hinzufügen, dann offene Hand hinhalten.",
            ),
            builtIn = true,
        ),
    )

    val trainings = listOf(
        Training(
            id = "tr_morgen", name = "Morgenrunde Grundgehorsam",
            description = "Kurze Einheit vor dem Frühstück: Aufmerksamkeit und Basis-Kommandos.",
            durationMinutes = 10,
            items = listOf(
                TrainingItem("cmd_name", 5),
                TrainingItem("cmd_sitz", 5),
                TrainingItem("cmd_platz", 3),
                TrainingItem("cmd_hier", 5),
            ),
            weekdays = everyDay, builtIn = true,
        ),
        Training(
            id = "tr_stubenrein", name = "Stubenreinheit",
            description = "Über den Tag verteilt: jeden Gang nach draußen eintragen.",
            durationMinutes = 5,
            items = listOf(TrainingItem("cmd_pipi", 6)),
            weekdays = everyDay, builtIn = true,
        ),
        Training(
            id = "tr_abend", name = "Abends: Ruhe & Impulskontrolle",
            description = "Ruhige Übungen, die beim Runterfahren helfen.",
            durationMinutes = 10,
            items = listOf(
                TrainingItem("cmd_decke", 3),
                TrainingItem("cmd_lass", 5),
                TrainingItem("cmd_warte", 3),
                TrainingItem("cmd_handling", 3),
            ),
            weekdays = everyDay, builtIn = true,
        ),
        Training(
            id = "tr_leine", name = "Leinenführigkeit",
            description = "Auf einem kurzen Spaziergang üben – Rückruf an der Schleppleine.",
            durationMinutes = 10,
            items = listOf(TrainingItem("cmd_fuss", 10), TrainingItem("cmd_hier", 5)),
            weekdays = setOf(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY), builtIn = true,
        ),
        Training(
            id = "tr_alleine", name = "Alleinbleiben",
            description = "In winzigen Schritten aufbauen. Vorher lösen lassen und auslasten.",
            durationMinutes = 5,
            items = listOf(TrainingItem("cmd_alleine", 3)),
            weekdays = setOf(DayOfWeek.TUESDAY, DayOfWeek.THURSDAY, DayOfWeek.SATURDAY), builtIn = true,
        ),
        Training(
            id = "tr_spass", name = "Spaß & Tricks",
            description = "Spielerisches Training am Wochenende.",
            durationMinutes = 10,
            items = listOf(
                TrainingItem("cmd_pfote", 5),
                TrainingItem("cmd_aus", 5),
                TrainingItem("cmd_bleib", 3),
                TrainingItem("cmd_beiss", 3),
            ),
            weekdays = setOf(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY), builtIn = true,
        ),
    )

    fun initial() = AppData(commands = commands, trainings = trainings)
}
