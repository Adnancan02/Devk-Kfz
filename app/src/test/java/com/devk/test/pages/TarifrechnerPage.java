package com.devk.test.pages;

import com.devk.test.config.TestConfig;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.assertions.LocatorAssertions;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.SelectOption;
import com.microsoft.playwright.options.WaitForSelectorState;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;


public class TarifrechnerPage {
    private final Page page;

    public TarifrechnerPage(Page page) {
        this.page = page;
    }

    public void navigiereZurStartseite() {
        page.navigate(TestConfig.baseUrl());
        page.waitForLoadState();
    }

    public void cookiesAblehnen() {
        Locator denyButton = page.locator("[data-testid='uc-deny-all-button']").first();
        try {
            denyButton.waitFor(new Locator.WaitForOptions()
                    .setState(WaitForSelectorState.VISIBLE)
                    .setTimeout(3000));
            denyButton.click();
        } catch (RuntimeException ignored) {
            // The banner is not shown on every run, for example when consent state is already stored.
        }
    }

    public boolean versicherungskarteIstSichtbar(String name) {
        Locator karte = page.locator("span.b-text--h5")
                .filter(new Locator.FilterOptions().setHasText(name))
                .first();
        return karte.isVisible();
    }

    public boolean ueberschriftIstSichtbar(String text) {
        return page.locator("h2, h3")
                .filter(new Locator.FilterOptions().setHasText(text))
                .first()
                .isVisible();
    }
    public void buttonKlicken(String buttonText) {
        screenshot(buttonText.replaceAll("[^a-zA-Z0-9]", "_"));

        Locator button = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName(buttonText)).first();
        if (button.isVisible()) {
            button.click();
        } else {
            page.getByText(buttonText, new Page.GetByTextOptions().setExact(true)).first().click();
        }

        page.waitForLoadState();
    }

    /**
     * Klickt auf den Weiter-Button, egal welche ID (Prefix) er hat.
     * Wir nutzen einen CSS-Selektor, der nach IDs sucht, die auf '-buttons-next' enden.
     * * @param expectedText Der Text, der auf dem Button erscheinen soll.
     */
    public void klickeDynamischenWeiterButton(String expectedText) {
        // [id$='-buttons-next'] findet jede ID, die mit '-buttons-next' endet.
        // Dies deckt 'situation-buttons-next' und 'vehicleSelect-buttons-next' ab.
        Locator weiterButton = page.locator("[id$='-buttons-next']");

        // 1. Warten, bis das Element im UI sichtbar ist
        weiterButton.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));

        // 2. Sicherheitscheck: Prüfen, ob der Text auf dem Button korrekt ist
        if (weiterButton.innerText().contains(expectedText)) {
            // Playwright wartet automatisch, bis der Button nicht mehr 'disabled' ist
            weiterButton.click();
        } else {
            throw new RuntimeException("Button mit ID-Endung '-buttons-next' gefunden, aber der Text ist falsch! " +
                    "Erwartet: " + expectedText + ", Gefunden: " + weiterButton.innerText());
        }
    }

    public boolean breadcrumbEnthaelt(String text) {
        Locator zielText = page.locator("nav[aria-label='Breadcrumb']").getByText(text);

        try {
            zielText.waitFor(new Locator.WaitForOptions()
                    .setState(WaitForSelectorState.VISIBLE)
                    .setTimeout(TestConfig.defaultTimeoutMillis()));
            return zielText.isVisible();
        } catch (RuntimeException e) {
            return false;
        }
    }

    public void versicherungWechselnWaehlen() {
        page.getByText("Versicherung wechseln", new Page.GetByTextOptions().setExact(true)).first().click();
    }

    public boolean isButtonEnabled(String buttonText) {
        Locator button = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName(buttonText))
                .and(page.locator(":visible"))
                .first();

        try {
            assertThat(button).isEnabled(new LocatorAssertions.IsEnabledOptions()
                    .setTimeout(TestConfig.defaultTimeoutMillis()));
            return true;
        } catch (AssertionError | RuntimeException e) {
            Locator innerButton = page.locator("devk-button")
                    .filter(new Locator.FilterOptions().setHasText(buttonText))
                    .locator("button")
                    .first();

            try {
                assertThat(innerButton).isEnabled(new LocatorAssertions.IsEnabledOptions()
                        .setTimeout(TestConfig.defaultTimeoutMillis()));
                return true;
            } catch (AssertionError | RuntimeException ignored) {
                return false;
            }
        }
    }

    public boolean hsnTsnSeiteIstSichtbar() {
        return page.locator("text=HSN, text=TSN, input[name='hsn'], input[placeholder*='HSN']")
                .first()
                .isVisible();
    }

    public void fuelleHsnTsnAus(String hsnWert, String tsnWert) {
        page.locator("devk-input#HSN input").fill(hsnWert);
        page.locator("devk-input#TSN input").fill(tsnWert);
    }

    public void fuelleDatumAus(String labelText, String datum) {
        Locator dateInput = page.locator("devk-input")
                .filter(new Locator.FilterOptions().setHasText(labelText))
                .locator("input")
                .first();

        dateInput.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));
        dateInput.fill("");
        dateInput.type(datum);
        dateInput.press("Tab");

        String actualValue = dateInput.inputValue();
        if (!actualValue.equals(datum) && !actualValue.equals(toIsoDate(datum))) {
            throw new AssertionError("Expected date value " + datum + " or " + toIsoDate(datum)
                    + " but was " + actualValue);
        }
    }

    public void fuelleKaufpreisAus(String preis) {
        Locator kaufpreisInput = page.locator("devk-input")
                .filter(new Locator.FilterOptions().setHasText("Wie ist der Kaufpreis Ihres Fahrzeugs?"))
                .locator("input")
                .first();
        kaufpreisInput.fill(preis);
        kaufpreisInput.press("Tab");
    }

    public void waehleFinanzierung(String optionText) {
        Locator selectButton = page.locator("devk-select-button")
                .filter(new Locator.FilterOptions().setHasText(optionText))
                .locator("button")
                .first();

        try {
            selectButton.waitFor(new Locator.WaitForOptions()
                    .setState(WaitForSelectorState.VISIBLE)
                    .setTimeout(TestConfig.defaultTimeoutMillis()));
            selectButton.click();
        } catch (RuntimeException e) {
            page.getByText(optionText, new Page.GetByTextOptions().setExact(true)).first().click();
        }
    }

    public void waehleVersicherungsSchutz(String schutz) {
        // Wir suchen direkt den Button, der das Label mit dem gewünschten Text enthält
        Locator tabButton = page.locator("button.devk-select-tabs__button")
                .filter(new Locator.FilterOptions().setHasText(schutz));

        // Prüfen, ob der Button bereits aktiv ist (vermeidet unnötige Klicks)
        boolean isActive = tabButton.getAttribute("class").contains("devk-select-tabs__button--active");

        if (!isActive) {
            tabButton.click();
        }
    }

    public void waehleOption(String text) {
        // Playwright durchdringt das Shadow DOM automatisch.
        // 'getByText' ist schneller als komplexe CSS-Selektoren.
        page.getByText(text, new Page.GetByTextOptions().setExact(false))
                .first()
                .click();
    }

    public void waehleZahlungsperiode(String periode) {
        // Direkter Zugriff ohne Regex: Playwright findet den Text im Shadow DOM automatisch
        Locator button = page.locator("devk-select-button")
                .getByText(periode, new Locator.GetByTextOptions().setExact(true));

        // Falls nicht selektiert, klicken
        if (button.getAttribute("selected") == null) {
            button.click();

            // Schnelle Validierung
            assertThat(button).hasAttribute("selected", "");
        }
    }

    public void fuellePlzAus(String plz) {
        Locator plzInput = page.locator("#HALTER_PLZ input");

        // Fokus und Eingabe: .fill() ist sicherer als .type()
        plzInput.fill("");
        plzInput.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));
        plzInput.fill(plz);

        // Warten auf die automatische Befüllung des Ortes (Hürth)
        // Wir prüfen das Attribut "value" der devk-select Komponente
        assertThat(page.locator("#HALTER_ORT")).hasAttribute("value", "Hürth",
                new LocatorAssertions.HasAttributeOptions().setTimeout(3000));
    }

    public void waehleJahreskilometer(String kilometer) {
        Locator select = page.locator("devk-select")
                .filter(new Locator.FilterOptions().setHasText("Wie viele Kilometer"))
                .locator("select")
                .first();

        try {
            select.selectOption(new SelectOption().setLabel(kilometer));
            return;
        } catch (RuntimeException ignored) {
            // Some DEVK selects render as custom dropdowns instead of native select elements.
        }

        Locator dropdown = page.locator("devk-select")
                .filter(new Locator.FilterOptions().setHasText("Wie viele Kilometer"))
                .first();
        dropdown.click();
        page.getByText(kilometer, new Page.GetByTextOptions().setExact(true)).last().click();
    }

    private void waehleAutomatischGeladenenOrt(String ort) {
        try {
            Locator ortAuswahl = page.locator("devk-select")
                    .filter(new Locator.FilterOptions().setHasText(ort))
                    .first();
            ortAuswahl.waitFor(new Locator.WaitForOptions()
                    .setState(WaitForSelectorState.VISIBLE)
                    .setTimeout(TestConfig.defaultTimeoutMillis()));
            ortAuswahl.click();
            page.getByText(ort, new Page.GetByTextOptions().setExact(true)).last().click();
        } catch (RuntimeException ignored) {
            // Some postal codes resolve to a single city and do not open a selectable dropdown.
        }
    }

    public void waehleSfKlasseHaftpflicht(String sfKlasse) {
        page.locator("#SF_KLASSE_HAFTPFLICHT select").selectOption(new SelectOption().setLabel(sfKlasse));
    }

    public void waehleSfKlasseKasko(String sfKlasse) {
        page.locator("#SF_KLASSE_KASKO select").selectOption(new SelectOption().setLabel(sfKlasse));
    }

    public List<String> holeAlleTarifPreise() {
        Locator options = page.locator("devk-select-tabs option");
        List<Locator> allOptions = options.all();
        List<String> preisListe = new ArrayList<>();

        for (Locator option : allOptions) {
            String preisRaw = option.getAttribute("data-price");
            if (preisRaw != null) {
                preisListe.add(preisRaw);
            }
        }

        return preisListe;
    }

    public double parsePreisString(String preisText) {
        if (preisText == null || preisText.isEmpty()) {
            return 0;
        }

        String bereinigterPreis = preisText
                .replace(".", "")
                .replace(",", ".")
                .replaceAll("[^0-9.]", "");

        return Double.parseDouble(bereinigterPreis);
    }

    public void screenshot(String dateiname) {
        Path screenshotPath = Paths.get("build/reports/screenshots/" + dateiname + ".png");
        try {
            Files.createDirectories(screenshotPath.getParent());
        } catch (IOException e) {
            throw new IllegalStateException("Could not create screenshot directory", e);
        }

        page.screenshot(new Page.ScreenshotOptions()
                .setPath(screenshotPath)
                .setFullPage(true));
    }

    private String toIsoDate(String datum) {
        DateTimeFormatter germanDate = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        return LocalDate.parse(datum, germanDate).toString();
    }
}
