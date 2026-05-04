package com.devk.test.steps;

import com.devk.test.pages.TarifrechnerPage;
import com.microsoft.playwright.*;
import com.microsoft.playwright.assertions.LocatorAssertions;
import com.microsoft.playwright.options.WaitForSelectorState;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.*;
import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

import java.util.List;
import static org.junit.Assert.*;

public class TarifrechnerSteps {

    private Playwright playwright;
    private Browser browser;
    private Page page;
    private TarifrechnerPage tarifrechner;

    @Before
    public void setup() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(
                new BrowserType.LaunchOptions().setHeadless(false)
        );
        page = browser.newPage();
        tarifrechner = new TarifrechnerPage(page);
    }

    @After
    public void teardown() {
        tarifrechner.screenshot("test-abschluss");
        browser.close();
        playwright.close();
    }

    @Given("der Nutzer oeffnet die DEVK Startseite")
    public void nutzerOeffnetStartseite() {
        tarifrechner.navigiereZurStartseite();
    }

    @Given("der Nutzer lehnt die Cookies ab")
    public void nutzerLehntCookiesAb() {
        tarifrechner.cookiesAblehnen();
    }

    @Then("die Sektion {string} ist sichtbar")
    public void sektionIstSichtbar(String text) {
        assertTrue("Sektion nicht sichtbar: " + text,
                tarifrechner.ueberschriftIstSichtbar(text));
    }

    @Then("die folgenden Versicherungskarten sind sichtbar:")
    public void versicherungskartenSindSichtbar(DataTable dataTable) {
        List<String> karten = dataTable.asList();
        for (String karte : karten) {
            assertTrue("Versicherungskarte nicht sichtbar: " + karte,
                    tarifrechner.versicherungskarteIstSichtbar(karte));
            System.out.println("Sichtbar: " + karte);
        }
    }

    @When("der Nutzer klickt auf {string}")
    public void nutzerKlicktAuf(String text) {
        tarifrechner.buttonKlicken(text);
    }

    @Then("die Seite {string} wird angezeigt")
    public void seiteWirdAngezeigt(String seitenname) {
        // 1. Screenshot erstellen (ersetzt Leerzeichen und Sonderzeichen für den Dateinamen)
        tarifrechner.screenshot(seitenname.replaceAll("[^a-zA-Z0-9]", "_"));

        // 2. Den String aus der Feature-Datei am ">" trennen
        // Das Ergebnis ist ein Array, z.B. ["Kfz-Versicherung", "Pkw"]
        String[] breadcrumbTeile = seitenname.split(">");

        // 3. Den gesamten Text des Breadcrumbs von der Webseite holen
        // Playwright durchdringt das Shadow DOM automatisch
        String sichtbarerText = page.locator("nav[aria-label='Breadcrumb']").innerText();

        // 4. Jedes Teil aus dem Array dynamisch prüfen
        for (String teil : breadcrumbTeile) {
            String bereinigterTeil = teil.trim(); // Entfernt Leerzeichen vor/nach dem Text

            assertTrue("Fehler: Der Breadcrumb-Teil '" + bereinigterTeil + "' wurde auf der Seite nicht gefunden!",
                    sichtbarerText.contains(bereinigterTeil));
        }
    }

    @Then("der Breadcrumb enthaelt {string}")
    public void breadcrumbEnthaelt(String text) {
        assertTrue("Breadcrumb enthaelt nicht: " + text,
                tarifrechner.breadcrumbEnthaelt(text));
    }

    @When("der Nutzer waehlt {string}")
    public void nutzerWaehlt(String option) {
        if (option.equals("Versicherung wechseln")) {
            tarifrechner.versicherungWechselnWaehlen();
        } else {
            tarifrechner.buttonKlicken(option);
        }
    }

    @Then("der Button {string} ist {string}")
    public void checkButtonStatus(String buttonText, String erwarteterStatus) {
        // 1. Hole den echten Zustand
        boolean istTatsaechlichAktiv = tarifrechner.isButtonEnabled(buttonText);

        // 2. Bestimme, was laut Feature-File sein soll
        boolean sollAktivSein = erwarteterStatus.equalsIgnoreCase("aktiv");

        // 3. Vergleiche beides: Muss der Soll-Zustand gleich dem Ist-Zustand sein?
        assertEquals("Der Status des Buttons '" + buttonText + "' entspricht nicht der Erwartung!",
                sollAktivSein, istTatsaechlichAktiv);
    }

    @Then("die HSN TSN Eingabeseite wird angezeigt")
    public void hsnTsnSeiteWirdAngezeigt() {
        tarifrechner.screenshot("hsn-tsn-seite");
        assertTrue("HSN/TSN Seite nicht sichtbar!",
                tarifrechner.hsnTsnSeiteIstSichtbar());
    }

    @When("der Nutzer gibt HSN {string} und TSN {string} ein")
    public void hsnTsnEingeben(String hsn, String tsn) {
        tarifrechner.fuelleHsnTsnAus(hsn, tsn);
    }

    @When("der Nutzer gibt die Erstzulassung Ihres Fahrzeugs {string} ein")
    public void erstzulassung(String datum) {
        tarifrechner.fuelleDatumAus("Wann war die Erstzulassung Ihres Fahrzeugs?", datum);
    }


    @When("der Nutzer das Datum der Zulassung auf sich selbst {string} eingibt")
    public void eigeneZulassungEingeben(String datum) {
        tarifrechner.fuelleDatumAus("Wann wurde das Fahrzeug erstmalig auf Sie zugelassen?", datum);
    }

    @When("der Nutzer den Versicherungsbeginn {string} eingibt")
    public void versicherungsbeginnEingeben(String datum) {
        tarifrechner.fuelleDatumAus("Ab wann soll Ihr Fahrzeug versichert sein?", datum);
    }

    @When("der Nutzer gibt den Kaufpreis {string} ein")
    public void kaufpreisEingeben(String preis) {
        // Sayısal değer girişi için
        tarifrechner.fuelleKaufpreisAus(preis);
    }

    @When("der Nutzer waehlt die Finanzierungsart {string} aus")
    public void finanzierungAuswaehlen(String art) {
        // Barkauf, Kredit veya Leasing butonuna tıklamak için
        tarifrechner.waehleFinanzierung(art);
    }

    @When("der Nutzer beantwortet die Tuning-Frage mit {string}")
    public void tuningBeantworten(String antwort) {
        // Ja veya Nein seçeneği için
        tarifrechner.waehleFinanzierung(antwort);
    }

    @When("der Nutzer die Nutzungsart {string} waehlt")
    @When("der Nutzer den Fahrzeughalter {string} waehlt")
    @When("der Nutzer als Fahrer {string} auswaehlt")
    public void klickeSelectionCard(String text) {
        // Diese Methode klickt auf die großen Auswahl-Karten
        tarifrechner.klickeCard(text);
    }

    @When("der Nutzer sein Geburtsdatum {string} eingibt")
    public void geburtsdatumEingeben(String datum) {
        tarifrechner.fuelleDatumAus("Wie ist Ihr Geburtsdatum?", datum);
    }

    @When("der Nutzer das Geburtsdatum des juengsten Fahrers {string} eingibt")
    public void geburtsdatumJuengsterFahrer(String datum) {
        tarifrechner.fuelleDatumAus("Wie ist das Geburtsdatum des:der jüngsten Fahrer:in?", datum);
    }

    @When("der Nutzer das Geburtsdatum des aeltesten Fahrers {string} eingibt")
    public void geburtsdatumAeltesterFahrer(String datum) {
        tarifrechner.fuelleDatumAus("Wie ist das Geburtsdatum des:der ältesten Fahrer:in?", datum);
    }

    @When("der Nutzer die PLZ {string} fuer den Zulassungsort eingibt")
    public void plzEingeben(String plz) {
        tarifrechner.fuellePlzAus(plz);
    }

    @When("der Nutzer den Versicherungsschutz {string} waehlt")
    public void versicherungsSchutzWaehlen(String schutz) {
        // Wir können hier die bereits optimierte klickeCard Methode nutzen
        tarifrechner.klickeCard(schutz);
    }

    @When("der Nutzer die SF-Klasse {string} fuer die Haftpflicht waehlt")
    public void sfKlasseHaftpflicht(String klasse) {
        tarifrechner.waehleSfKlasseHaftpflicht(klasse);
    }

    @When("der Nutzer die SF-Klasse {string} fuer die Vollkasko waehlt")
    public void sfKlasseVollkasko(String klasse) {
        tarifrechner.waehleSfKlasseKasko(klasse);
    }

    @Then("sollte der Nutzer auf der Seite {string} landen")
    public void pruefeUeberschrift(String expectedTitle) {
        // Mit dem statischen Import von PlaywrightAssertions funktioniert das jetzt:
        assertThat(page.locator("h1")).containsText(expectedTitle);
    }



    @When("der Nutzer die Zahlungsperiode {string} waehlt")
    public void waehleZahlungsperiode(String periode) {
        // Da dies wieder Auswahlkarten sind, nutzen wir unsere bewährte Methode
        tarifrechner.klickeCard(periode);
    }

    @Then("sollte ein Versicherungsbeitrag groesser als {string} Euro angezeigt werden")
    public void pruefePreis(String minimalBetrag) {
        // Falls die oberen Selektoren nicht klappen, versuche diesen hier:
        Locator preisElement = page.locator("p.devk-price__amount").first();

        // Warten, bis das Element sichtbar ist und Inhalt hat
        preisElement.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));

        String preisText = preisElement.innerText().trim(); // z.B. "125,50 €"

        // Umwandlung: Komma zu Punkt, Euro-Zeichen weg, Leerzeichen weg
        double preis = Double.parseDouble(preisText.replace(",", ".").replaceAll("[^0-9.]", ""));

        // Jetzt mit der richtigen Reihenfolge: (Condition, Message)
        assertTrue("Fehler: Der Preis " + preis + " ist nicht groesser als " + minimalBetrag,
                preis > Double.parseDouble(minimalBetrag));
    }
}