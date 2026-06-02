package tests.java;

import org.junit.jupiter.api.Test;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;

public class UserAgentTest extends BaseTest {

    @Test
    void testUserAgent() {
        // Настройка User Agent
        BrowserContext context = browser.newContext(new Browser.NewContextOptions()
                .setUserAgent(
                        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36"));

        Page page = context.newPage();
        page.navigate("https://example.com");
        System.out.println("User Agent: " + page.evaluate("navigator.userAgent"));
    }
}