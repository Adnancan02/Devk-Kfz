@kfz @smoke
Feature: KFZ-Tarifrechner - Online Versicherung berechnen

  @happy-path
  Scenario: Nutzer berechnet erfolgreich einen PKW-Tarif
   # --- GIVEN: Ausgangssituation ---
    Given der Nutzer oeffnet die DEVK Startseite
    # --- THEN: Initiale Validierung der Startseite ---
    Then die Sektion "Unsere beliebtesten Versicherungen" ist sichtbar
    And die folgenden Versicherungskarten sind sichtbar:
      | Autoversicherung         |
      | Rechtsschutzversicherung |
      | Haftpflichtversicherung  |
      | Gebäudeversicherung      |
      | Hausratversicherung      |
   # --- WHEN: Auswahl der Sparte und Einstieg in den Rechner ---
    When der Nutzer die Sparte "Autoversicherung" waehlt
    Then die Seite "Kfz-Versicherung>Pkw" wird angezeigt
    When der Nutzer die Sparte "Online berechnen" waehlt
    Then der Breadcrumb enthaelt "Rechner"
    And der Button "Weiter zur Fahrzeugauswahl" ist "inaktiv"

    # --- WHEN: Dateneingabe für das Fahrzeug ---
    When der Nutzer waehlt "Versicherung wechseln"
    Then der Button "Weiter zur Fahrzeugauswahl" ist "aktiv"

    When der Nutzer klickt auf "Weiter zur Fahrzeugauswahl"
    And der Nutzer gibt HSN "0005" und TSN "APT" ein
    Then der Button "Weiter zu Ihrer Fahrzeugzulassung" ist "aktiv"

    # --- AND: Angaben zur Nutzung und zum Fahrer ---
    When der Nutzer klickt auf "Weiter zu Ihrer Fahrzeugzulassung"
    And der Nutzer gibt die Erstzulassung Ihres Fahrzeugs "30.05.2012" ein
    And der Nutzer das Datum der Zulassung auf sich selbst "24.03.2024" eingibt
    And der Nutzer den Versicherungsbeginn "01.06.2026" eingibt
    And der Nutzer gibt den Kaufpreis "10000" ein
    And der Nutzer waehlt die Finanzierungsart "Barkauf" aus
    And der Nutzer beantwortet die Tuning-Frage mit "Nein"
    And der Nutzer klickt auf "Weiter zur Fahrzeugnutzung"
    And der Nutzer die Nutzungsart "Nur privat (inkl. Fahrten zur Arbeit)" waehlt
    And der Nutzer den Fahrzeughalter "Ich (Versicherungsnehmer:in)" waehlt
    And der Nutzer als Fahrer "Partner:in" auswaehlt
    And der Nutzer sein Geburtsdatum "06.12.1988" eingibt
    And der Nutzer das Geburtsdatum des juengsten Fahrers "10.01.1990" eingibt
    And der Nutzer das Geburtsdatum des aeltesten Fahrers "06.12.1988" eingibt
    And der Nutzer die PLZ "50354" fuer den Zulassungsort eingibt
    And der Nutzer die Jahresfahrleistung "Bis 12.000" waehlt
    And der Nutzer klickt auf "Weiter zum Versicherungsumfang"

    # --- AND: Auswahl des Versicherungsschutzes ---
    And der Nutzer die SF-Klasse "SF-Klasse 4" fuer die Haftpflicht waehlt
    And der Nutzer den Versicherungsschutz "Haftpflicht + Teilkasko" waehlt
    And der Nutzer klickt auf "Weiter zum individuellen Angebot"

      # --- THEN: Finales Angebot und Beitragsprüfung ---
    Then sollte der Nutzer auf der Seite "Ihr Angebot" landen
    And der Nutzer die Zahlungsperiode "jährlich" waehlt
    And sind die drei Tarifoptionen sichtbar und ihre Beitraege liegen ueber "0" Euro
