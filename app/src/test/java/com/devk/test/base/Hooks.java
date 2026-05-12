package com.devk.test.base;

import com.devk.test.config.TestConfig;
import com.microsoft.playwright.*;

import io.cucumber.java.After;
import io.cucumber.java.AfterAll;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;

import java.nio.file.Paths;

public class Hooks {
    private static Playwright playwright;
    private static Browser browser;

    private BrowserContext context;
    private Page page;

    @Before(order = 0)
    public void setup() {
        if (playwright == null) {
            playwright = Playwright.create();
            browser = launchBrowser(TestConfig.browser());
        }

        context = browser.newContext(new Browser.NewContextOptions()
                .setRecordVideoDir(Paths.get("build/videos/"))
                .setRecordVideoSize(1280, 720));
        context.setDefaultTimeout(TestConfig.defaultTimeoutMillis());

        page = context.newPage();
    }

    @After
    public void teardown(Scenario scenario) {
        if (scenario.isFailed() && page != null) {
            byte[] screenshot = page.screenshot(new Page.ScreenshotOptions().setFullPage(true));
            scenario.attach(screenshot, "image/png", "failure-screenshot");
        }

        if (context != null) {
            context.close();
        }

        /*if (!scenario.isFailed() && !TestConfig.keepVideos() && page != null && page.video() != null) {
            page.video().delete();
        }
        */
    }

    @AfterAll
    public static void shutdown() {
        if (browser != null) {
            browser.close();
            browser = null;
        }
        if (playwright != null) {
            playwright.close();
            playwright = null;
        }
    }

    public Page getPage() {
        if (page == null) {
            throw new IllegalStateException("Playwright page is not initialized. Check Cucumber hook order.");
        }
        return page;
    }

    private static Browser launchBrowser(String browserName) {
        BrowserType.LaunchOptions options = new BrowserType.LaunchOptions()
                .setHeadless(TestConfig.headless());

        switch (browserName.toLowerCase()) {
            case "firefox":
                return playwright.firefox().launch(options);
            case "safari":
            case "webkit":
                return playwright.webkit().launch(options);
            case "chrome":
                return playwright.chromium().launch(options.setChannel("chrome"));
            case "edge":
                return playwright.chromium().launch(options.setChannel("msedge"));
            case "chromium":
                return playwright.chromium().launch(options);
            default:
                throw new IllegalArgumentException("Unsupported browser: " + browserName);
        }
    }
}
