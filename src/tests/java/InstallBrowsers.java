package tests.java;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.Playwright;

public class InstallBrowsers {

    private static Playwright playwright; // Объект Playwright
    private static Browser chromiumBrowser; // Chromium браузер
    private static Browser firefoxBrowser; // Firefox браузер
    private static Browser webkitBrowser; // WebKit браузер

    @BeforeAll
    public static void setUp() {
        playwright = Playwright.create(); // Инициализация Playwright
        chromiumBrowser = playwright.chromium().launch(); // Запуск Chromium
        firefoxBrowser = playwright.firefox().launch(); // Запуск Firefox
        webkitBrowser = playwright.webkit().launch(); // Запуск WebKit
    }

    @AfterAll
    public static void tearDown() {
        if (chromiumBrowser != null)
            chromiumBrowser.close(); // Закрыть Chromium
        if (firefoxBrowser != null)
            firefoxBrowser.close(); // Закрыть Firefox
        if (webkitBrowser != null)
            webkitBrowser.close(); // Закрыть WebKit
        if (playwright != null)
            playwright.close(); // Закрыть Playwright
    }

    @Test
    public void dummyTest() {
        System.out.println("All browsers are setup and ready for tests"); // Заглушка теста
    }
}
