package com.devk.test.base;

import com.devk.test.config.TestConfig;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;

import io.cucumber.java.Before;

public class Hooks {
    private static Playwright playwright;
    private static Browser browser;

    private BrowserContext context;
    private Page page;

    @Before(order = 0)
    public void setup() {

        if (playwright == null) {
            playwright = Playwright.create();
            browser =launchBrowser(TestConfig.browser());
        }
    }

}