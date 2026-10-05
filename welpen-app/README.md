# Welpentraining – Android-App

Eine Tracking-App fürs Welpentraining: tägliche Einheiten, Fortschritt pro Kommando,
frei gestaltbare Trainings und eine Wissenssektion zu Hunden und Rassen.
Alle Daten bleiben lokal auf dem Gerät.

## Funktionen

- **Heute** – die für den Wochentag geplanten Einheiten mit Status (offen/erledigt),
  Serie („Tage in Folge“), Einheiten dieser Woche, Alter des Welpen und Verlauf der
  letzten Einheiten. Zusätzlich lässt sich jederzeit eine freie Einheit starten.
- **Einheit durchführen** – pro Kommando „Geklappt“ / „Nicht“ tippen, rückgängig machen,
  Anleitung aufklappen, Kommandos spontan hinzufügen, Stimmung (1–5 Sterne) und Notiz.
- **Trainings** – 6 vordefinierte Trainings (Morgenrunde, Stubenreinheit, Abends: Ruhe &
  Impulskontrolle, Leinenführigkeit, Alleinbleiben, Spaß & Tricks). Alle anpassbar;
  eigene Trainings frei zusammenstellen: Name, Ziel, Dauer, Wochentage, Kommandos mit
  Ziel-Wiederholungen und Reihenfolge.
- **Fortschritt** – für jedes Kommando Lernstufe (Lernphase → Klappt teilweise →
  Fast geschafft → Sitzt!), Erfolgsquote, Diagramm über die Einheiten und Verlauf.
  Eigene Kommandos mit Anleitung anlegen.
- **Wissen** – allgemeine Hinweise (Grundregeln, Sozialisierung, Stubenreinheit, Schlaf,
  Beißhemmung, Ernährung, Gesundheit, Körpersprache) und Steckbriefe zu 13 Rassen,
  mit Suche.
- **Profil** – Name, Rasse (mit Link zu den Rassehinweisen) und Geburtsdatum des Welpen.

## Installieren

Jeder Push baut die App per GitHub Actions (Workflow „Welpentraining App“).
Unter *Actions → letzter Lauf → Artifacts → welpentraining-apk* liegt das APK
(`app-release.apk` ist kleiner und schneller). Auf dem Handy die Installation aus
unbekannten Quellen erlauben und das APK öffnen.

## Selbst bauen

Voraussetzung: Android Studio (oder JDK 17 + Android SDK 35).

```bash
cd welpen-app
./gradlew assembleDebug
```

Technik: Kotlin, Jetpack Compose (Material 3), Navigation Compose; Speicherung als
JSON-Datei im privaten App-Speicher. Mindestversion Android 8.0 (API 26).
