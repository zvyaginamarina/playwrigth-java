package tests.java;

import java.util.Collections;

import org.junit.jupiter.api.Test;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;

public class LocaleAndGeolocationTest extends BaseTest {

    @Test
    void testLocaleAndGeolocation() {
        // Настройка языка и геолокации
        BrowserContext context = browser.newContext(new Browser.NewContextOptions()
                .setLocale("fr-FR") // Устанавливаем французский язык
                .setGeolocation(48.8566, 2.3522) // Устанавливаем геолокацию (Париж)
                .setPermissions(Collections.singletonList("geolocation"))); // Разрешаем доступ к геолокации

        Page page = context.newPage();
        page.navigate("https://example.com");
        System.out.println("Язык браузера: " + page.evaluate("navigator.language"));
    }
}