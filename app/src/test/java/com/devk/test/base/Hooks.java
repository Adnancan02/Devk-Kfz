package com.devk.test.base;

import com.microsoft.playwright.*;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import java.nio.file.Paths;

public class Hooks {
    private static Playwright playwright;
    private static Browser browser;
    private BrowserContext context;
    private Page page;

    @Before
    public void setup() {
        // Playwright Instanz nur einmal pro Testlauf erstellen
        if (playwright == null) {
            playwright = Playwright.create();

            // Browser-Wahl über System-Property (Standard: chromium)
            String browserName = System.getProperty("browser", "chromium").toLowerCase();
            BrowserType.LaunchOptions options = new BrowserType.LaunchOptions().setHeadless(false);

            switch (browserName) {
                case "firefox":
                    browser = playwright.firefox().launch(options);
                    break;
                case "safari":
                case "webkit":
                    browser = playwright.webkit().launch(options);
                    break;
                case "chrome":
                    browser = playwright.chromium().launch(options.setChannel("chrome"));
                    break;
                case "edge":
                    browser = playwright.chromium().launch(options.setChannel("msedge"));
                    break;
                default:
                    browser = playwright.chromium().launch(options);
                    break;
            }
        }

        // Video-Konfiguration: Speichert Videos in build/videos
        context = browser.newContext(new Browser.NewContextOptions()
                .setRecordVideoDir(Paths.get("build/videos/"))
                .setRecordVideoSize(1280, 720));

        page = context.newPage();
    }

    @After
    public void teardown(Scenario scenario) {
        // 1. Screenshot in den Cucumber-Report einbetten, wenn der Test fehlschlägt
        if (scenario.isFailed()) {
            byte[] screenshot = page.screenshot(new Page.ScreenshotOptions().setFullPage(true));
            scenario.attach(screenshot, "image/png", "Fehler-Screenshot");
            System.out.println("Scenario fehlgeschlagen: " + scenario.getName());
        }

        // 2. Context schließen, damit das Video finalisiert wird (WICHTIG!)
        context.close();

        // 3. Optional: Video löschen, wenn der Test erfolgreich war (spart Speicherplatz)
        if (!scenario.isFailed() && page.video() != null) {
            page.video().delete();
        }
    }

    // Getter-Methode, damit die Step-Klassen auf die aktuelle Seite zugreifen können
    public Page getPage() {
        return this.page;
    }
}