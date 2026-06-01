package tests.java;

import org.junit.jupiter.api.Test;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;

public class IphoneEmulationTest extends BaseTest {

    @Test
    void testDeviceEmulation() {
        // Создаем контекст с параметрами устройства iPhone 11
        BrowserContext context = browser.newContext(new Browser.NewContextOptions()
                .setUserAgent(
                        "Mozilla/5.0 (iPhone; CPU iPhone OS 13_2_3 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/13.0.3 Mobile/15E148 Safari/604.1")
                .setViewportSize(414, 896) // Ширина x Высота
                .setDeviceScaleFactor(2) // Плотность пикселей
                .setIsMobile(true) // Мобильный режим
                .setHasTouch(true)); // Поддержка touch-событий

        Page page = context.newPage();
        page.navigate("https://example.com");

        System.out.println("User Agent: " + page.evaluate("navigator.userAgent"));
        System.out.println("Viewport: " + page.evaluate("window.innerWidth + 'x' + window.innerHeight"));

        context.close();
    }
}
