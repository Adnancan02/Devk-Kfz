package com.devk.test.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.assertions.LocatorAssertions;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.SelectOption;
import com.microsoft.playwright.options.WaitForSelectorState;
import io.cucumber.java.en.When;

import java.util.ArrayList;
import java.util.List;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.Assert.assertTrue;

public class TarifrechnerPage {

    private final Page page;

    public TarifrechnerPage(Page page) {
        this.page = page;
    }

    // ── Allgemeine Methode: Klick per data-testid ───────────────────
    // Gibt die data-testid an, findet das Element und klickt darauf
    public void clickByTestId(String testId) {
        Locator button = page.locator("[data-testid='" + testId + "']");
        button.waitFor(new Locator.WaitForOptions().setTimeout(5000));
        if (button.isVisible()) {
            button.click();
            System.out.println("Geklickt (testid): " + testId);
        } else {
            System.out.println("Nicht sichtbar, wird übersprungen: " + testId);
        }
    }

    // ── Allgemeine Methode: Klick per sichtbaren Text ───────────────
    // Sucht ein Element anhand des sichtbaren Textes und klickt darauf
    public void clickByText(String text) {
        Locator element = page.getByRole(
            AriaRole.BUTTON,
            new Page.GetByRoleOptions().setName(text)
        );
        if (element.isVisible()) {
            element.click();
            System.out.println("Geklickt (text): " + text);
        } else {
            // Falls kein Button, als allgemeines Element suchen
            page.locator("text=" + text).first().click();
            System.out.println("Geklickt (locator text): " + text);
        }
    }

    // ── Cookie-Banner: Ablehnen ─────────────────────────────────────
    // Schließt den Datenschutz-Dialog durch Klick auf "Ablehnen"
    public void cookiesAblehnen() {
        clickByTestId("uc-deny-all-button");
    }

    // ── Navigation: Startseite öffnen ──────────────────────────────
    // Öffnet die DEVK-Startseite
    public void navigiereZurStartseite() {
        page.navigate("https://www.devk.de/");
        page.waitForLoadState();
        System.out.println("Startseite geöffnet");
    }

    // ── Prüfung: Versicherungskarte sichtbar ────────────────────────
// Prüft ob eine bestimmte Versicherungskarte auf der Startseite sichtbar ist
    public boolean versicherungskarteIstSichtbar(String name) {
        // Nur die Karten-Überschriften prüfen (class: b-text--h5)
        Locator karte = page.locator("span.b-text--h5")
                .filter(new Locator.FilterOptions().setHasText(name))
                .first();
        return karte.isVisible();
    }

    // ── Prüfung: Überschrift sichtbar ───────────────────────────────
    // Prüft ob eine bestimmte Überschrift sichtbar ist
    public boolean ueberschriftIstSichtbar(String text) {
        return page.locator("h2:has-text('" + text + "'), h3:has-text('" + text + "')").isVisible();
    }

    // ── Klick: Versicherungskarte ───────────────────────────────────
    // Klickt auf eine Versicherungskarte auf der Startseite
    public void versicherungskarteKlicken(String name) {
        page.locator("text=" + name).first().click();
        page.waitForLoadState();
        System.out.println("Versicherungskarte geklickt: " + name);
    }

    // ── Klick: Beliebiger Button per Text ───────────────────────────
    // Klickt auf einen Button anhand des sichtbaren Textes
    public void buttonKlicken(String buttonText) {
        Locator button = page.getByRole(
            AriaRole.BUTTON,
            new Page.GetByRoleOptions().setName(buttonText)
        );
        screenshot(buttonText.replaceAll("[^a-zA-Z0-9]", "_"));
        if (button.isVisible()) {
            button.click();
        } else {
            page.locator("text=" + buttonText).first().click();
        }
        page.waitForLoadState();
        System.out.println("Button geklickt: " + buttonText);

    }

    // ── Prüfung: Breadcrumb enthält Text ────────────────────────────
    // Prüft ob der Breadcrumb einen bestimmten Text enthält
    public boolean breadcrumbEnthaelt(String text) {
        // 1. Erstelle einen Locator für das spezifische Text-Element im Breadcrumb
        Locator zielText = page.locator("nav[aria-label='Breadcrumb']").locator("text=" + text);

        try {
            // 2. Warte bis zu 5 Sekunden, bis "Rechner" erscheint
            zielText.waitFor(new Locator.WaitForOptions().setTimeout(5000));

            // 3. Hole zur Sicherheit nochmal alle Texte für das Log
            System.out.println("Gefundene Breadcrumbs nach Warten: " +
                    page.locator("nav[aria-label='Breadcrumb'] .a-text").allInnerTexts());

            return zielText.isVisible();
        } catch (Exception e) {
            System.err.println("Timeout: Text '" + text + "' wurde im Breadcrumb nicht gefunden!");
            return false;
        }
    }

    // ── Klick: Versicherung wechseln ────────────────────────────────
    // Wählt die Option "Versicherung wechseln" im Tarifrechner
    public void versicherungWechselnWaehlen() {
        page.locator("text=Versicherung wechseln").first().click();
        System.out.println("Versicherung wechseln ausgewählt");
    }

    public boolean isButtonEnabled(String buttonText) {
        // 1. Locator für den inneren Button im Shadow DOM
        // Playwright durchdringt Shadow DOMs automatisch bei CSS-Selektoren!
        Locator innerButton = page.locator("devk-button")
                .filter(new Locator.FilterOptions().setHasText(buttonText))
                .locator("button");

        try {
            // 2. Die moderne Playwright-Art zu warten:
            // Diese Assertion hat ein eingebautes Retry-System und Timeout.
            assertThat(innerButton).isEnabled(new LocatorAssertions.IsEnabledOptions().setTimeout(30000));

            System.out.println("Status Check für '" + buttonText + "': JETZT AKTIV");
            return true;
        } catch (AssertionError | Exception e) {
            // Wenn das Timeout abläuft, ohne dass der Button aktiv wurde
            System.out.println("Status Check für '" + buttonText + "': IMMER NOCH INAKTIV");
            return false;
        }
    }

    // ── Prüfung: HSN/TSN Seite sichtbar ────────────────────────────
    // Prüft ob die Fahrzeugauswahl-Seite mit HSN/TSN Eingabe angezeigt wird
    public boolean hsnTsnSeiteIstSichtbar() {
        return page.locator("text=HSN, text=TSN, input[name='hsn'], input[placeholder*='HSN']")
                   .first()
                   .isVisible();
    }

    public void fuelleHsnTsnAus(String hsnWert, String tsnWert) {
        // 1. HSN eingeben: Gehe in devk-input#HSN und finde dort das native input
        page.locator("devk-input#HSN input").fill(hsnWert);

        // 2. TSN eingeben: (Angenommen die ID ist analog dazu 'TSN')
        page.locator("devk-input#TSN input").fill(tsnWert);
    }


    public void fuelleErstzulassung(String datum){
        page.locator("devk-input#ERSTZULASSUNG").fill(datum);
    }

    // ── Screenshot erstellen ────────────────────────────────────────
    // Erstellt einen Screenshot und speichert ihn im allure-results Ordner
    public void screenshot(String dateiname) {
        page.screenshot(new Page.ScreenshotOptions()
            .setPath(java.nio.file.Paths.get("build/reports/" + dateiname + ".png"))
            .setFullPage(true));
    }

    public void fuelleDatumAus(String labelText, String datum) {
        // 1. Wir suchen das devk-input Element, das den entsprechenden Text im Label hat
        Locator dateInput = page.locator("devk-input")
                .filter(new Locator.FilterOptions().setHasText(labelText))
                .locator("input"); // Findet das native Input-Feld im Shadow DOM

        // 2. Warten und Feld leeren, falls schon etwas drin steht (z.B. der 04.05.2026 als Default)
        dateInput.waitFor();
        dateInput.fill(""); // Feld säubern

        // 3. Neues Datum eingeben
        dateInput.type(datum); // 'type' ist bei Datumsfeldern oft sicherer als 'fill'

        // Kurzes Warten für die Validierung
        page.waitForTimeout(500);
    }

    // Kaufpreis girişi için
    public void fuelleKaufpreisAus(String preis) {
        // Kaufpreis alanı genellikle bir placeholder veya label ile bulunur
        Locator kaufpreisInput = page.locator("devk-input")
                .filter(new Locator.FilterOptions().setHasText("Wie ist der Kaufpreis Ihres Fahrzeugs?"))
                .locator("input");
        kaufpreisInput.waitFor();
        kaufpreisInput.fill(preis);
    }

    public void klickeOption(String optionText) {
        // Wir nutzen "has-text" mit dem case-insensitive Flag 'i'
        // Das '>> button' sorgt dafür, dass wir direkt das klickbare Element im Shadow DOM ansprechen
        Locator option = page.locator("devk-button")
                .filter(new Locator.FilterOptions().setHasText(optionText))
                .locator("button");

        try {
            // Warte maximal 5 Sekunden, bis der Button wirklich sichtbar und bereit ist
            option.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(5000));

            // Nutze force: true, falls ein anderes Element (Overlay) den Klick behindert
            option.click(new Locator.ClickOptions().setForce(true));

            System.out.println("Erfolgreich geklickt: " + optionText);
        } catch (com.microsoft.playwright.TimeoutError e) {
            // Falls der Text-Filter scheitert, versuchen wir es mit einem direkteren Selektor
            System.out.println("Standard-Locator fehlgeschlagen, versuche alternativen Selektor für: " + optionText);
            page.locator("button:has-text('" + optionText + "')").first().click();
        }
    }

    public void waehleFinanzierung(String optionText) {
        // 1. Wir suchen das devk-select-button Element, das den Text enthält (z.B. "Barkauf")
        // Playwright findet den Text, auch wenn er außerhalb des Shadow-Root-Buttons steht.
        Locator selectButton = page.locator("devk-select-button")
                .filter(new Locator.FilterOptions().setHasText(optionText));

        // 2. Wir greifen auf den echten Button im Shadow DOM zu
        Locator nativeButton = selectButton.locator("button");

        try {
            // Warten, bis der Button bereit ist
            nativeButton.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(5000));

            // Klick ausführen
            nativeButton.click();
            System.out.println("Finanzierungsart ausgewählt: " + optionText);
        } catch (Exception e) {
            // Backup: Falls der Shadow-DOM-Pfad Probleme macht, versuchen wir es über den Text-Locator
            System.out.println("Versuche Backup-Klick für: " + optionText);
            page.getByText(optionText).first().click();
        }
    }

    // Methode für die großen Auswahl-Karten (Cards)
    public void klickeCard(String cardText) {
        // 1. Die Karte gezielt über die DEVK-Komponente lokalisieren
        Locator card = page.locator("devk-selection-card")
                .filter(new Locator.FilterOptions().setHasText(cardText))
                .first();

        try {
            // 2. Warten, bis die Karte sichtbar ist
            card.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(5000));

            // 3. PRÜFUNG: Ist die Karte bereits ausgewählt?
            // Wir prüfen, ob im Class-Attribut das Wort "selected" vorkommt
            String classAttribute = card.getAttribute("class");
            boolean istBereitsAusgewählt = classAttribute != null && classAttribute.contains("selected");

            if (istBereitsAusgewählt) {
                // Wenn schon ausgewählt, machen wir nichts
                System.out.println("Status Check: '" + cardText + "' ist bereits ausgewählt. Klick übersprungen.");
            } else {
                // Nur klicken, wenn sie noch nicht ausgewählt ist
                card.click();
                System.out.println("Status Check: '" + cardText + "' wurde jetzt angeklickt.");
            }

        } catch (com.microsoft.playwright.TimeoutError e) {
            System.out.println("Spezifischer Locator fehlgeschlagen, versuche Backup für: " + cardText);

            // Backup-Logik (falls devk-selection-card nicht gefunden wurde)
            Locator backupCard = page.locator("main").getByText(cardText, new Locator.GetByTextOptions().setExact(true)).first();

            // Auch beim Backup kurz prüfen, ob es schon ausgewählt aussieht (über das Parent-Element)
            if (!backupCard.locator("..").getAttribute("class").contains("selected")) {
                backupCard.click();
            }
        }
    }

    public void waehleZahlungsperiode(String periode) {
        // Den Button anhand des exakten Textes finden
        Locator button = page.locator("devk-select-button")
                .filter(new Locator.FilterOptions().setHasText(
                        java.util.regex.Pattern.compile("^" + periode + "$")
                ))
                .first();

        // Warten, bis der Button sichtbar ist
        button.waitFor(new Locator.WaitForOptions()
                .setState(WaitForSelectorState.VISIBLE)
                .setTimeout(5000));

        // Prüfen, ob die Option bereits ausgewählt ist
        String selected = button.getAttribute("selected");
        if (selected == null) {
            String preisVorKlick = page.locator("devk-select-tabs option")
                    .first().getAttribute("data-price");
            System.out.println("Preis vor Klick: " + preisVorKlick);

            button.locator("button").click();
            //page.waitForTimeout(5000);
            // Warten bis sich der Preis tatsächlich aktualisiert hat
            Locator ersteOption = page.locator("devk-select-tabs option").first();
            String preisVorKlick1 = ersteOption.getAttribute("data-price");

            assertThat(ersteOption).not().hasAttribute(
                    "data-price",
                    preisVorKlick1,
                    new LocatorAssertions.HasAttributeOptions().setTimeout(10000)
            );
            System.out.println("Zahlungsperiode angeklickt: " + periode);

            // Warten bis der Button als ausgewählt markiert ist (kein JS eval nötig!)
            assertThat(button).hasAttribute("selected", "",
                    new LocatorAssertions.HasAttributeOptions().setTimeout(10000));

            System.out.println("Preis nach Klick: " +
                    page.locator("devk-select-tabs option").first().getAttribute("data-price"));
        } else {
            System.out.println("'" + periode + "' ist bereits vorausgewählt.");
        }

        System.out.println("Validierung erfolgreich: '" + periode + "' ist aktiv gesetzt.");
    }

    // Methode für die PLZ
    public void fuellePlzAus(String plz) {
        // Sucht das devk-input mit der PLZ-Beschriftung
        Locator plzInput = page.locator("devk-input")
                //.filter(new Locator.FilterOptions().setHasText("In welchem Ort wird das Fahrzeug zugelassen?"))
                .locator("input[placeholder='PLZ']");
        plzInput.waitFor();
        plzInput.fill(plz);

        // Nach PLZ Eingabe kurz warten, bis der Ort automatisch geladen wird
        page.waitForTimeout(1000);
    }

    public void waehleSfKlasseHaftpflicht(String sfKlasse) {
        // Greift direkt auf die ID zu. Playwright findet das <select> im Shadow DOM automatisch.
        page.locator("#SF_KLASSE_HAFTPFLICHT select").selectOption(new SelectOption().setLabel(sfKlasse));
        System.out.println("Haftpflicht SF-Klasse gesetzt auf: " + sfKlasse);
    }

    public void waehleSfKlasseKasko(String sfKlasse) {
        // Greift auf die Kasko-ID zu
        page.locator("#SF_KLASSE_KASKO select").selectOption(new SelectOption().setLabel(sfKlasse));
        System.out.println("Kasko SF-Klasse gesetzt auf: " + sfKlasse);
    }

    // ── Prüfung: Mehrere Tarife validieren ──────────────────────────

    /**
     * Holt die 'data-price' Attribute aller verfügbaren Tarif-Optionen.
     * Dies ist eine Validierung der Geschäftslogik (Tarifrechner-Ergebnisse).
     */
    /**
     * Holt die Preise und loggt sie zusammen mit dem Tarifnamen (z.B. Basis, Komfort).
     */
    public List<String> holeAlleTarifPreise() {
        // Locator für die Optionen (durchbricht Shadow DOM)
        Locator options = page.locator("devk-select-tabs option");
        List<Locator> allOptions = options.all();
        List<String> preisListe = new ArrayList<>();

        for (Locator option : allOptions) {
            // Name (Label) und Preis aus den Attributen ziehen
            String tarifName = option.getAttribute("label"); // z.B. "Teilkasko Basis"
            String preisRaw = option.getAttribute("data-price"); // z.B. "948,83 €"

            if (preisRaw != null) {
                preisListe.add(preisRaw);

                // Preis für den Log umwandeln
                double preisZahl = parsePreisString(preisRaw);

                // Dein gewünschter Log-Output
                System.out.println("Validierung " + (tarifName != null ? tarifName : "Tarif") + " gefunden: " + preisZahl + " €");
            }
        }

        return preisListe;
    }

    /**
     * Hilfsmethode: Wandelt den Preis-String (z.B. "1.341,59 €") in eine Zahl um.
     */
    public double parsePreisString(String preisText) {
        if (preisText == null || preisText.isEmpty()) return 0;

        // Entfernt Tausenderpunkte, ersetzt Komma durch Punkt für Java-Double
        String bereinigterPreis = preisText
                .replace(".", "")
                .replace(",", ".")
                .replaceAll("[^0-9.]", ""); // Behält nur Zahlen und den Punkt

        return Double.parseDouble(bereinigterPreis);
    }

}
