package de.welpentraining.app.knowledge

data class Section(val heading: String, val text: String)

data class Article(
    val id: String,
    val title: String,
    val summary: String,
    val sections: List<Section>,
)

data class Breed(
    val id: String,
    val name: String,
    val group: String,
    val size: String,
    /** 1–5 */
    val energy: Int,
    /** 1–5: wie leicht die Rasse in der Regel zu trainieren ist. */
    val trainability: Int,
    val character: String,
    val trainingTips: String,
    val care: String,
)

/** Allgemeine Hinweise. Kein Ersatz für Tierarzt oder Hundeschule. */
object KnowledgeContent {

    val articles = listOf(
        Article(
            id = "grundregeln", title = "Grundregeln des Welpentrainings",
            summary = "Kurz, positiv, konsequent – so lernen Welpen am besten.",
            sections = listOf(
                Section("Kurze Einheiten", "Welpen können sich nur wenige Minuten konzentrieren. Mehrere Einheiten von 3–10 Minuten über den Tag verteilt bringen mehr als eine lange. Höre auf, solange es noch gut läuft."),
                Section("Timing der Belohnung", "Belohne innerhalb von ein bis zwei Sekunden nach dem gewünschten Verhalten. Ein Markerwort („Yes“, „Fein“) oder ein Clicker überbrückt die Zeit, bis das Leckerli kommt."),
                Section("Positive Verstärkung", "Belohntes Verhalten wird häufiger gezeigt. Strafe erzeugt Angst und schadet der Bindung. Unerwünschtes Verhalten besser umlenken und eine Alternative belohnen."),
                Section("Ein Signal – eine Bedeutung", "Alle im Haushalt sollten dieselben Wörter und Handzeichen verwenden. Signale nur einmal sagen; wiederholtes Rufen lehrt den Welpen, erst beim dritten Mal zu reagieren."),
                Section("Generalisieren", "Hunde lernen ortsbezogen. Ein „Sitz“ im Wohnzimmer bedeutet nicht automatisch „Sitz“ im Park. Übe neue Kommandos an vielen Orten und steigere Ablenkung langsam."),
            ),
        ),
        Article(
            id = "sozialisierung", title = "Sozialisierung & Prägung",
            summary = "Die sensible Phase bis etwa zur 16. Lebenswoche prägt den Hund fürs Leben.",
            sections = listOf(
                Section("Warum so wichtig?", "In den ersten Lebensmonaten lernt der Welpe, was „normal“ ist. Was er jetzt positiv kennenlernt, macht ihm später weniger Angst."),
                Section("Was kennenlernen?", "Verschiedene Menschen (Kinder, Menschen mit Hut, Rollstuhl …), andere Hunde und Tiere, Untergründe, Geräusche (Staubsauger, Verkehr, Gewitter-Aufnahmen), Auto- und Bahnfahrten, Tierarztpraxis, Bürsten und Anfassen."),
                Section("Qualität vor Quantität", "Jede Begegnung sollte für den Welpen gut ausgehen. Lass ihn aus sicherer Entfernung beobachten, belohne ruhiges Verhalten und überfordere ihn nicht. Zeigt er Angst, vergrößere den Abstand."),
                Section("Welpenspielgruppe", "Eine gut geführte Welpengruppe mit ähnlich alten Hunden fördert Sozialverhalten und Beißhemmung. Achte darauf, dass Spiel unterbrochen wird, wenn ein Welpe gemobbt wird."),
            ),
        ),
        Article(
            id = "stubenreinheit", title = "Stubenreinheit",
            summary = "Mit Routine und Geduld meist innerhalb weniger Wochen bis Monate.",
            sections = listOf(
                Section("Wann raus?", "Direkt nach dem Aufwachen, nach dem Fressen und Trinken, nach dem Spielen und anfangs etwa alle 1–2 Stunden. Faustregel: Ein Welpe kann es ungefähr so viele Stunden halten, wie er Monate alt ist (plus eine)."),
                Section("Anzeichen", "Schnüffeln am Boden, im Kreis drehen, Unruhe oder plötzliches Weglaufen aus dem Spiel sind typische Hinweise."),
                Section("Belohnen statt schimpfen", "Lösen draußen ruhig loben und belohnen. Malheur drinnen nicht bestrafen – der Welpe versteht das nicht und versteckt sich sonst beim Lösen. Mit Enzymreiniger säubern, damit kein Geruch bleibt."),
                Section("Nachts", "Viele Welpen müssen anfangs nachts ein- bis zweimal raus. Kurz, ruhig und ohne Spiel – danach direkt zurück zum Schlafplatz."),
            ),
        ),
        Article(
            id = "schlaf", title = "Schlaf & Ruhe",
            summary = "Welpen brauchen 18–20 Stunden Schlaf am Tag.",
            sections = listOf(
                Section("Ruhe ist Lernzeit", "Im Schlaf verarbeitet das Gehirn das Gelernte. Übermüdete Welpen werden oft überdreht, beißen mehr und lernen schlechter."),
                Section("Ruhezeiten einplanen", "Nach Training, Spiel oder Besuch bewusst Ruhe einleiten. Ein fester Ruheplatz, Kauartikel oder eine Schleckmatte können helfen."),
                Section("Ungestört schlafen", "Kinder und Besucher sollten den schlafenden Welpen nicht wecken. Ein Platz abseits vom Trubel hilft beim Abschalten."),
            ),
        ),
        Article(
            id = "beisshemmung", title = "Beißen & Beißhemmung",
            summary = "Welpen erkunden die Welt mit dem Maul – das ist normal.",
            sections = listOf(
                Section("Warum beißen Welpen?", "Zahnwechsel (ca. 4.–7. Monat), Spiel, Übermüdung und Erkundung. Unter Hunden lernen Welpen durch Reaktion der Spielpartner, wie fest sie zubeißen dürfen."),
                Section("Was hilft?", "Wird es zu grob, das Spiel kurz und ruhig unterbrechen. Ein passendes Kauspielzeug anbieten. Häufig ist Beißen ein Zeichen von Müdigkeit – dann ist Ruhe die beste Antwort."),
                Section("Was vermeiden?", "Kein Schnauzgriff, kein Anschreien, kein wildes Gerangel mit den Händen. Das kann das Beißen verstärken oder Angst auslösen."),
            ),
        ),
        Article(
            id = "ernaehrung", title = "Ernährung",
            summary = "Hochwertiges Welpenfutter, feste Zeiten und Leckerlis einrechnen.",
            sections = listOf(
                Section("Mahlzeiten", "Bis ca. 4 Monate meist 3–4 Mahlzeiten täglich, danach 2–3. Halte dich an die Empfehlung des Züchters bzw. Futterherstellers und passe die Menge an die Körperform an."),
                Section("Leckerlis", "Trainingsleckerlis zählen zur Tagesration. Kleine, weiche Stücke lassen sich schnell fressen. Ein Teil der normalen Futterration kann direkt im Training verwendet werden."),
                Section("Giftig für Hunde", "Schokolade, Weintrauben/Rosinen, Zwiebeln, Knoblauch, Xylit (Süßstoff), Macadamianüsse, Alkohol und gekochte Knochen. Im Verdachtsfall sofort den Tierarzt kontaktieren."),
                Section("Große Rassen", "Welpen großer Rassen brauchen speziell angepasstes Futter, damit sie nicht zu schnell wachsen. Das schont Knochen und Gelenke."),
            ),
        ),
        Article(
            id = "gesundheit", title = "Gesundheit & Tierarzt",
            summary = "Impfungen, Entwurmung und positive Tierarztbesuche.",
            sections = listOf(
                Section("Impfungen", "Die Grundimmunisierung erfolgt üblicherweise ab der 8. Lebenswoche in mehreren Terminen. Den genauen Impfplan bespricht dein Tierarzt mit dir."),
                Section("Parasiten", "Regelmäßige Entwurmung und Schutz vor Flöhen und Zecken sind wichtig. Häufigkeit und Mittel mit dem Tierarzt abstimmen."),
                Section("Positive Besuche", "Gehe gelegentlich nur zum „Hallo sagen“ in die Praxis, mit Leckerlis und ohne Behandlung. Übe zu Hause das Anfassen von Pfoten, Ohren und Maul."),
                Section("Bewegung", "Welpen sollten nicht übermäßig belastet werden: keine langen Joggingrunden, kein häufiges Treppensteigen bei großen Rassen, keine Sprünge aus großer Höhe. Freies Spiel und Erkunden sind ideal."),
            ),
        ),
        Article(
            id = "koerpersprache", title = "Körpersprache verstehen",
            summary = "Stress- und Beschwichtigungssignale früh erkennen.",
            sections = listOf(
                Section("Stresssignale", "Gähnen, Lecken über die Schnauze, Wegschauen, Kratzen, Schütteln, eingeklemmte Rute, angelegte Ohren oder das Weiße im Auge (Walauge) können zeigen, dass dem Welpen etwas zu viel ist."),
                Section("Entspannt", "Weicher Körper, lockere Rute, entspanntes Maul und ruhiges Atmen deuten auf Wohlbefinden hin."),
                Section("Reagieren", "Zeigt dein Welpe Stress, nimm Druck raus: Abstand vergrößern, Übung vereinfachen oder Pause machen. Training funktioniert nur, wenn der Hund entspannt genug ist, um zu lernen."),
            ),
        ),
    )

    val breeds = listOf(
        Breed(
            "labrador", "Labrador Retriever", "Apportierhund", "groß (54–57 cm)", 4, 5,
            "Freundlich, verfressen, menschenbezogen und arbeitsfreudig. Meist sehr sozial gegenüber Menschen und Hunden.",
            "Sehr gut mit Futter motivierbar. Ruhe und Impulskontrolle früh üben, da Labradore oft stürmisch sind. Apportierspiele und Nasenarbeit lieben sie.",
            "Neigt zu Übergewicht – Leckerlis von der Ration abziehen. Gelenke im Wachstum schonen.",
        ),
        Breed(
            "golden", "Golden Retriever", "Apportierhund", "groß (51–61 cm)", 4, 5,
            "Sanft, geduldig, verspielt und sehr anhänglich. Ein klassischer Familienhund.",
            "Lernt gern und schnell, reagiert sensibel auf harte Worte. Apportieren, Suchspiele und Wasser sind ideale Beschäftigung.",
            "Regelmäßiges Bürsten des dichten Fells. Gelenke im Wachstum schonen.",
        ),
        Breed(
            "schaeferhund", "Deutscher Schäferhund", "Hüte-/Gebrauchshund", "groß (55–65 cm)", 5, 5,
            "Intelligent, loyal, wachsam und arbeitsfreudig. Braucht Aufgaben und klare Führung.",
            "Sehr lernwillig. Neben Gehorsam auch Kopfarbeit bieten (Fährte, Tricks). Gute Sozialisierung mit Fremden wichtig, da die Rasse eher wachsam ist.",
            "Viel Fellwechsel. Auf gesunde Gelenkentwicklung achten (HD/ED-Vorsorge beim Züchter).",
        ),
        Breed(
            "border", "Border Collie", "Hütehund", "mittel (46–53 cm)", 5, 5,
            "Extrem intelligent, sensibel und arbeitswütig. Kann bei Unterforderung Ersatzhandlungen (z. B. Jagen von Autos/Schatten) entwickeln.",
            "Lernt rasend schnell – auch Unerwünschtes. Ruhe und Abschalten bewusst trainieren. Mentale Auslastung ist wichtiger als endloses Ballwerfen.",
            "Nicht überfordern: Viele Border Collies schaukeln sich hoch. Feste Ruhezeiten einplanen.",
        ),
        Breed(
            "aussie", "Australian Shepherd", "Hütehund", "mittel (46–58 cm)", 5, 5,
            "Aktiv, klug, menschenbezogen, teils reserviert gegenüber Fremden.",
            "Ähnlich wie Border Collie: viel Kopfarbeit, Impulskontrolle und Ruhetraining. Liebt Hundesport wie Agility oder Obedience.",
            "Fellpflege regelmäßig. Bei Merle-Verpaarungen auf seriöse Zucht achten (Seh- und Hörprobleme).",
        ),
        Breed(
            "dackel", "Dackel (Teckel)", "Jagdhund (Erdhund)", "klein (bis ca. 35 cm)", 3, 2,
            "Mutig, selbstständig, eigensinnig und mit viel Jagdtrieb. Sehr charmant.",
            "Belohnung muss sich lohnen – mit Geduld und Humor trainieren. Rückruf früh und konsequent aufbauen, Jagdtrieb im Blick behalten.",
            "Rücken schonen: Treppen und Sprünge (z. B. vom Sofa) möglichst vermeiden, Übergewicht unbedingt verhindern.",
        ),
        Breed(
            "franzbulldogge", "Französische Bulldogge", "Gesellschaftshund", "klein (24–35 cm)", 2, 3,
            "Verspielt, anhänglich, humorvoll und teils stur.",
            "Kurze, spielerische Einheiten. Bei Hitze kein Training. Gut mit Futter motivierbar.",
            "Kurzköpfige Rasse: auf Atemprobleme achten, Überhitzung vermeiden, Geschirr statt Halsband. Seriöse Zucht mit freien Atemwegen wählen.",
        ),
        Breed(
            "pudel", "Pudel", "Gesellschaftshund", "Toy bis Groß (24–60 cm)", 4, 5,
            "Sehr intelligent, sensibel, verspielt und eng mit seinen Menschen verbunden.",
            "Lernt schnell und liebt Tricks. Sanftes, abwechslungsreiches Training. Sozialisierung gegen Geräuschempfindlichkeit.",
            "Fell wächst ständig – regelmäßiges Scheren und Bürsten. Haart kaum.",
        ),
        Breed(
            "jackrussell", "Jack Russell Terrier", "Terrier", "klein (25–30 cm)", 5, 3,
            "Energiegeladen, mutig, selbstbewusst und mit ausgeprägtem Jagdtrieb.",
            "Viel Impulskontrolle und Frustrationstoleranz üben. Spiel als Belohnung nutzen. Rückruf mit Schleppleine absichern.",
            "Braucht viel Beschäftigung, sonst sucht er sich selbst Aufgaben (Buddeln, Bellen).",
        ),
        Breed(
            "beagle", "Beagle", "Laufhund", "mittel (33–40 cm)", 4, 2,
            "Fröhlich, freundlich, gesellig und stark nasengesteuert.",
            "Die Nase hat oft Vorrang – Rückruf braucht viel Training und hochwertige Belohnungen. Nasenarbeit (Suchspiele, Mantrailing) ist perfekte Auslastung.",
            "Futter gut verschließen, Beagle sind sehr verfressen. Neigt zu Übergewicht.",
        ),
        Breed(
            "chihuahua", "Chihuahua", "Gesellschaftshund", "sehr klein (15–23 cm)", 3, 3,
            "Lebhaft, wachsam, sehr auf seine Bezugsperson fixiert.",
            "Wie ein „richtiger“ Hund erziehen – auch kleine Hunde brauchen Regeln. Nicht ständig hochheben, sondern selbst laufen und Erfahrungen machen lassen.",
            "Kälteempfindlich. Zahnpflege wichtig. Vorsicht bei Spiel mit großen Hunden.",
        ),
        Breed(
            "husky", "Siberian Husky", "Schlittenhund", "mittel-groß (50–60 cm)", 5, 2,
            "Freundlich, unabhängig, lauffreudig, oft nicht sehr gehorsamsorientiert.",
            "Rückruf ist eine Herausforderung – Freilauf nur in sicheren Bereichen. Zugarbeit (Canicross, später Zughundesport) als Ventil nutzen.",
            "Viel Unterwolle, starker Fellwechsel. Hitzeempfindlich. Ausbruchsicherer Garten nötig.",
        ),
        Breed(
            "mischling", "Mischling", "verschieden", "variabel", 3, 3,
            "Jeder Mischling ist einzigartig – Charakter und Bedürfnisse hängen von den beteiligten Rassen und der Prägung ab.",
            "Beobachte, was deinen Welpen motiviert (Futter, Spiel, Nase, Bewegung) und richte das Training danach aus.",
            "Bei unbekannter Herkunft: Größe und Energielevel mit dem Tierarzt einschätzen.",
        ),
    )
}
