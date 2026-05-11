@kfz @smoke
Feature: KFZ-Tarifrechner – Online Versicherung berechnen

  @happy-path
  Scenario: Nutzer berechnet erfolgreich einen PKW-Tarif
    Given der Nutzer oeffnet die DEVK Startseite
    When die Sektion "Unsere beliebtesten Versicherungen" ist sichtbar
    And die folgenden Versicherungskarten sind sichtbar:
      | Autoversicherung         |
      | Rechtsschutzversicherung |
      | Haftpflichtversicherung  |
      | Gebäudeversicherung      |
      | Hausratversicherung      |
    And der Nutzer klickt auf "Autoversicherung"
    And die Seite "Kfz-Versicherung>Pkw" wird angezeigt
    And der Nutzer klickt auf "Online berechnen"
    And der Breadcrumb enthaelt "Rechner"
    And der Button "Weiter zur Fahrzeugauswahl" ist "inaktiv"
    And der Nutzer waehlt "Versicherung wechseln"
    And der Button "Weiter zur Fahrzeugauswahl" ist "aktiv"
    And der Nutzer klickt auf "Weiter zur Fahrzeugauswahl"
    And der Button "Weiter zu Ihrer Fahrzeugzulassung" ist "inaktiv"
    And der Nutzer gibt HSN "0005" und TSN "APT" ein
    And der Button "Weiter zu Ihrer Fahrzeugzulassung" ist "aktiv"
    And der Nutzer klickt auf "Weiter zu Ihrer Fahrzeugzulassung"
    And der Nutzer gibt die Erstzulassung Ihres Fahrzeugs "30.05.2012" ein
    And der Nutzer das Datum der Zulassung auf sich selbst "24.03.2024" eingibt
    And der Nutzer den Versicherungsbeginn "01.06.2026" eingibt
    And der Nutzer gibt den Kaufpreis "10000" ein
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
    And der Nutzer die PLZ "50354" fuer den Zulassungsort eingibt
    And der Button "Weiter zum Versicherungsumfang" ist "aktiv"
    And der Nutzer klickt auf "Weiter zum Versicherungsumfang"
    And der Nutzer die SF-Klasse "SF-Klasse 4" fuer die Haftpflicht waehlt
    And der Nutzer den Versicherungsschutz "Haftpflicht + Teilkasko" waehlt
    #And der Nutzer die SF-Klasse "SF-Klasse 1" fuer die Vollkasko waehlt
    And der Button "Weiter zum individuellen Angebot" ist "aktiv"
    And der Nutzer klickt auf "Weiter zum individuellen Angebot"
    And sollte der Nutzer auf der Seite "Ihr Angebot" landen
    And der Nutzer die Zahlungsperiode "jährlich" waehlt
    Then sind die drei Tarifoptionen sichtbar und ihre Beiträge liegen über "0" Euro
