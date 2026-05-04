@kfz @smoke
Feature: KFZ-Tarifrechner – Online Versicherung berechnen

  @happy-path
  Scenario: Nutzer berechnet erfolgreich einen PKW-Tarif
    Given der Nutzer oeffnet die DEVK Startseite
    And der Nutzer lehnt die Cookies ab
    Then die Sektion "Unsere beliebtesten Versicherungen" ist sichtbar
    And die folgenden Versicherungskarten sind sichtbar:
      | Autoversicherung         |
      | Rechtsschutzversicherung |
      | Haftpflichtversicherung  |
      | Gebäudeversicherung      |
      | Hausratversicherung      |
    When der Nutzer klickt auf "Autoversicherung"
    Then die Seite "Kfz-Versicherung>Pkw" wird angezeigt
    When der Nutzer klickt auf "Online berechnen"
    Then der Breadcrumb enthaelt "Rechner"
    Then der Button "Weiter zur Fahrzeugauswahl" ist "inaktiv"
    When der Nutzer waehlt "Versicherung wechseln"
    Then der Button "Weiter zur Fahrzeugauswahl" ist "aktiv"
    When der Nutzer klickt auf "Weiter zur Fahrzeugauswahl"
    Then der Button "Weiter zu Ihrer Fahrzeugzulassung" ist "inaktiv"
    And der Nutzer gibt HSN "0005" und TSN "APT" ein
    Then der Button "Weiter zu Ihrer Fahrzeugzulassung" ist "aktiv"
    When der Nutzer klickt auf "Weiter zu Ihrer Fahrzeugzulassung"
    And der Nutzer gibt die Erstzulassung Ihres Fahrzeugs "20.12.2020" ein
    And der Nutzer das Datum der Zulassung auf sich selbst "02.05.2024" eingibt
    And der Nutzer den Versicherungsbeginn "17.05.2026" eingibt
    And der Nutzer gibt den Kaufpreis "25000" ein
    And der Nutzer waehlt die Finanzierungsart "Barkauf" aus
    And der Nutzer beantwortet die Tuning-Frage mit "Nein"
    And der Button "Weiter zur Fahrzeugnutzung" ist "aktiv"
    And der Nutzer klickt auf "Weiter zur Fahrzeugnutzung"
    And  der Nutzer die Nutzungsart "Nur privat (inkl. Fahrten zur Arbeit)" waehlt
    And der Nutzer den Fahrzeughalter "Ich (Versicherungsnehmer:in)" waehlt
   # And der Nutzer als Fahrer "Ich" auswaehlt
    And der Nutzer als Fahrer "Partner:in" auswaehlt
    And der Nutzer sein Geburtsdatum "10.01.1990" eingibt
    And der Nutzer das Geburtsdatum des juengsten Fahrers "10.01.1990" eingibt
    And der Nutzer das Geburtsdatum des aeltesten Fahrers "10.01.1987" eingibt
    And der Nutzer die PLZ "50667" fuer den Zulassungsort eingibt
    And der Button "Weiter zum Versicherungsumfang" ist "aktiv"
    And der Nutzer klickt auf "Weiter zum Versicherungsumfang"
    And der Nutzer den Versicherungsschutz "Haftpflicht + Teilkasko" waehlt
    And der Nutzer die SF-Klasse "SF-Klasse 10" fuer die Haftpflicht waehlt
    #And der Nutzer die SF-Klasse "SF-Klasse 1" fuer die Vollkasko waehlt
    And der Button "Weiter zum individuellen Angebot" ist "aktiv"
    And der Nutzer klickt auf "Weiter zum individuellen Angebot"
    And sollte der Nutzer auf der Seite "Ihr Angebot" landen
    And der Nutzer die Zahlungsperiode "jährlich" waehlt
    And sollte ein Versicherungsbeitrag groesser als "0" Euro angezeigt werden
