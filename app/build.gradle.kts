plugins {
    java
}

repositories {
    mavenCentral()
}

dependencies {
    // Playwright - Browser-Automatisierung
    implementation("com.microsoft.playwright:playwright:1.43.0")

    // Cucumber - BDD Framework
    testImplementation("io.cucumber:cucumber-java:7.15.0")
    testImplementation("io.cucumber:cucumber-junit:7.15.0")
    testImplementation("io.cucumber:cucumber-picocontainer:7.15.0")

    // JUnit - Test Runner
    testImplementation("junit:junit:4.13.2")

    // Allure - Berichterstattung
    testImplementation("io.qameta.allure:allure-cucumber7-jvm:2.25.0")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

tasks.withType<JavaCompile> {
    // UTF-8 Encoding - löst Probleme mit Sonderzeichen (ö, ä, ü)
    options.encoding = "UTF-8"
    //useJUnit()
}

tasks.test {
    useJUnit();
    // UTF-8 für Test-Ausführung
    systemProperty("file.encoding", "UTF-8")

    listOf("browser", "headless", "baseUrl", "timeout", "keepVideos", "cucumber.filter.tags").forEach {
        System.getProperty(it)?.let { value -> systemProperty(it, value) }
    }
}