package com.devk.test.config;

public class TestConfig {

    private static final String DEFAULT_BASE_URL = "https://www.devk.de/";
    private static final String DEFAULT_BROWSER = "chromium";
    private static final String DEFAULT_HEADLESS = "true";

    private TestConfig() {
    }

    public static String baseUrl() {
        return property("baseUrl", DEFAULT_BASE_URL);
    }

    public static String browser() {
        return property("browser", DEFAULT_BROWSER).toLowerCase();
    }

    public static boolean headless() {
        return Boolean.parseBoolean(property("headless", DEFAULT_HEADLESS));
    }

    public static int defaultTimeoutMillis() {
        return Integer.parseInt(property("timeout", "30000"));
    }

    public static boolean keepVideos() {
        return Boolean.parseBoolean(property("keepVideos", "false"));
    }

    private static String property(String name, String defaultValue) {
        String systemValue = System.getProperty(name);
        if (systemValue != null && !systemValue.isBlank()) {
            return systemValue;
        }

        String envValue = System.getenv(toEnvName(name));
        if (envValue != null && !envValue.isBlank()) {
            return envValue;
        }

        return defaultValue;
    }

    private static String toEnvName(String name) {
        return "TEST_" + name.replaceAll("([a-z])([A-Z])", "$1_$2").toUpperCase();
    }

}
