package com.devk.test.steps;

import com.devk.test.base.Hooks;
import com.devk.test.pages.TarifrechnerPage;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.WaitForSelectorState;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TarifrechnerSteps {
    private final Hooks hooks;
    private TarifrechnerPage tarifrechner;

    public TarifrechnerSteps(Hooks hooks) {
        this.hooks = hooks;
    }

    @Given("der Nutzer oeffnet die DEVK Startseite")
    public void nutzerOeffnetStartseite() {
        tarifrechner().navigiereZurStartseite();
        nutzerLehntCookiesAb();
    }

    @Given("der Nutzer lehnt die Cookies ab")
    public void nutzerLehntCookiesAb() {
        tarifrechner().cookiesAblehnen();
    }

    @Then("die Sektion {string} ist sichtbar")
    public void sektionIstSichtbar(String text) {
        assertTrue("Sektion nicht sichtbar: " + text, tarifrechner().ueberschriftIstSichtbar(text));
    }

    @Then("die folgenden Versicherungskarten sind sichtbar:")
    public void versicherungskartenSindSichtbar(DataTable dataTable) {
        List<String> karten = dataTable.asList();
        for (String karte : karten) {
            assertTrue("Versicherungskarte nicht sichtbar: " + karte,
                    tarifrechner().versicherungskarteIstSichtbar(karte));
        }
    }

    @When("der Nutzer klickt auf {string}")
    public void nutzerKlicktAuf(String text) {
        tarifrechner().screenshot(text.replaceAll("[^a-zA-Z0-9]", "_"));
        tarifrechner().klickeDynamischenWeiterButton(text);
    }

    @When("der Nutzer die Sparte {string} waehlt")
    public void waehleVersicherungsSparte(String sparteName) {
        // Wir suchen nach einem Link oder Text, der exakt so heißt wie die Sparte.

        Locator sparteCard = page().getByText(sparteName, new Page.GetByTextOptions().setExact(true));
        sparteCard.first().waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));
        sparteCard.first().click();
    }

    @Then("die Seite {string} wird angezeigt")
    public void seiteWirdAngezeigt(String seitenname) {
        tarifrechner().screenshot(seitenname.replaceAll("[^a-zA-Z0-9]", "_"));

        String[] breadcrumbTeile = seitenname.split(">");
        String sichtbarerText = page().locator("nav[aria-label='Breadcrumb']").innerText();

        for (String teil : breadcrumbTeile) {
            String bereinigterTeil = teil.trim();
            assertTrue("Breadcrumb-Teil nicht gefunden: " + bereinigterTeil,
                    sichtbarerText.contains(bereinigterTeil));
        }
    }

    @Then("der Breadcrumb enthaelt {string}")
    public void breadcrumbEnthaelt(String text) {
        assertTrue("Breadcrumb enthaelt nicht: " + text, tarifrechner().breadcrumbEnthaelt(text));
    }

    @When("der Nutzer waehlt {string}")
    public void nutzerWaehlt(String option) {
        if (option.equals("Versicherung wechseln")) {
            tarifrechner().versicherungWechselnWaehlen();
        } else {
            tarifrechner().buttonKlicken(option);
        }
    }

    @Then("der Button {string} ist {string}")
    public void checkButtonStatus(String buttonText, String erwarteterStatus) {
        // Locator für den dynamischen Button basierend auf dem Text
        Locator button = hooks.getPage().getByRole(AriaRole.BUTTON,
                new Page.GetByRoleOptions().setName(buttonText));

        if (erwarteterStatus.equalsIgnoreCase("aktiv")) {
            // SCHNELL & STABIL: Wartet automatisch, bis der Button enabled ist
            // Kein manuelles if-else oder assertEquals nötig
            assertThat(button).isEnabled();
        } else {
            // Wartet automatisch, bis der Button disabled ist
            assertThat(button).isDisabled();
        }
    }

    @Then("die HSN TSN Eingabeseite wird angezeigt")
    public void hsnTsnSeiteWirdAngezeigt() {
        tarifrechner().screenshot("hsn-tsn-seite");
        assertTrue("HSN/TSN Seite nicht sichtbar!", tarifrechner().hsnTsnSeiteIstSichtbar());
    }

    @When("der Nutzer gibt HSN {string} und TSN {string} ein")
    public void hsnTsnEingeben(String hsn, String tsn) {
        tarifrechner().fuelleHsnTsnAus(hsn, tsn);
    }

    @When("der Nutzer gibt die Erstzulassung Ihres Fahrzeugs {string} ein")
    public void erstzulassung(String datum) {
        tarifrechner().fuelleDatumAus("Wann war die Erstzulassung Ihres Fahrzeugs?", datum);
    }

    @When("der Nutzer das Datum der Zulassung auf sich selbst {string} eingibt")
    public void eigeneZulassungEingeben(String datum) {
        tarifrechner().fuelleDatumAus("Wann wurde das Fahrzeug erstmalig auf Sie zugelassen?", datum);
    }

    @When("der Nutzer den Versicherungsbeginn {string} eingibt")
    public void versicherungsbeginnEingeben(String datum) {
        tarifrechner().fuelleDatumAus("Ab wann soll Ihr Fahrzeug versichert sein?", datum);
    }

    @When("der Nutzer gibt den Kaufpreis {string} ein")
    public void kaufpreisEingeben(String preis) {
        tarifrechner().fuelleKaufpreisAus(preis);
    }

    @When("der Nutzer waehlt die Finanzierungsart {string} aus")
    public void finanzierungAuswaehlen(String art) {
        tarifrechner().waehleFinanzierung(art);
    }

    @When("der Nutzer beantwortet die Tuning-Frage mit {string}")
    public void tuningBeantworten(String antwort) {
        tarifrechner().waehleFinanzierung(antwort);
    }

    @When("der Nutzer die Nutzungsart {string} waehlt")
    @When("der Nutzer den Fahrzeughalter {string} waehlt")
    @When("der Nutzer als Fahrer {string} auswaehlt")
    public void waehleVersicherungsOption(String text) {
        tarifrechner().waehleOption(text);
    }

    @When("der Nutzer sein Geburtsdatum {string} eingibt")
    public void geburtsdatumEingeben(String datum) {
        tarifrechner().fuelleDatumAus("Wie ist Ihr Geburtsdatum?", datum);
    }

    @When("der Nutzer das Geburtsdatum des juengsten Fahrers {string} eingibt")
    public void geburtsdatumJuengsterFahrer(String datum) {
        tarifrechner().fuelleDatumAus("Wie ist das Geburtsdatum des:der jüngsten Fahrer:in?", datum);
    }

    @When("der Nutzer das Geburtsdatum des aeltesten Fahrers {string} eingibt")
    public void geburtsdatumAeltesterFahrer(String datum) {
        tarifrechner().fuelleDatumAus("Wie ist das Geburtsdatum des:der ältesten Fahrer:in?", datum);
    }

    @When("der Nutzer die PLZ {string} fuer den Zulassungsort eingibt")
    public void plzEingeben(String plz) {
        tarifrechner().fuellePlzAus(plz);
    }

    @When("der Nutzer die Jahresfahrleistung {string} waehlt")
    public void jahresfahrleistungWaehlen(String kilometer) {
        tarifrechner().waehleJahreskilometer(kilometer);
    }

    @When("der Nutzer den Versicherungsschutz {string} waehlt")
    public void versicherungsSchutzWaehlen(String schutz) {
        tarifrechner().waehleVersicherungsSchutz(schutz);
    }

    @When("der Nutzer die SF-Klasse {string} fuer die Haftpflicht waehlt")
    public void sfKlasseHaftpflicht(String klasse) {
        tarifrechner().waehleSfKlasseHaftpflicht(klasse);
    }

    @When("der Nutzer die SF-Klasse {string} fuer die Vollkasko waehlt")
    public void sfKlasseVollkasko(String klasse) {
        tarifrechner().waehleSfKlasseKasko(klasse);
    }

    @Then("sollte der Nutzer auf der Seite {string} landen")
    public void pruefeUeberschrift(String expectedTitle) {
        Locator ueberschrift = page().getByText(expectedTitle).first();
        assertThat(ueberschrift).isVisible();
    }

    @When("der Nutzer die Zahlungsperiode {string} waehlt")
    public void waehleZahlungsperiode(String periode) {
        tarifrechner().waehleZahlungsperiode(periode);
    }

    @Then("sollte ein Versicherungsbeitrag groesser als {string} Euro angezeigt werden")
    public void pruefePreis(String minimalBetrag) {
        Locator preisElement = page().locator("p.devk-price__amount").first();
        preisElement.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));

        String preisText = preisElement.innerText().trim();
        double preis = Double.parseDouble(preisText.replace(",", ".").replaceAll("[^0-9.]", ""));

        assertTrue("Preis ist nicht groesser als " + minimalBetrag,
                preis > Double.parseDouble(minimalBetrag));
    }

    @Then("sind die drei Tarifoptionen sichtbar und ihre Beitraege liegen ueber {string} Euro")
    public void pruefeAlleTarifPreise(String minimalBetrag) {
        double minLimit = Double.parseDouble(minimalBetrag);
        List<String> preiseRaw = tarifrechner().holeAlleTarifPreise();

        assertFalse("Es wurden keine Tarife auf der Seite gefunden!", preiseRaw.isEmpty());

        for (String rawText : preiseRaw) {
            double aktuellerPreis = tarifrechner().parsePreisString(rawText);
            assertTrue("Tarifpreis ist nicht groesser als " + minLimit, aktuellerPreis > minLimit);
        }

        tarifrechner().screenshot("tarifoptionen");
    }

    private Page page() {
        return hooks.getPage();
    }

    private TarifrechnerPage tarifrechner() {
        if (tarifrechner == null) {
            tarifrechner = new TarifrechnerPage(page());
        }
        return tarifrechner;
    }
}
