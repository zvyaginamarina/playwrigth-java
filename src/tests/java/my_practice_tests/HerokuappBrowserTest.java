package tests.java.my_practice_tests;

import org.junit.jupiter.api.AfterAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Locator.FilterOptions;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.PlaywrightException;
import com.microsoft.playwright.Response;
import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import com.microsoft.playwright.options.AriaRole;

public class HerokuappBrowserTest {

    static Playwright playwright;
    static Browser browser;

    @BeforeAll
    static void setUp() {
        playwright = Playwright.create();
        browser = playwright.chromium()
                .launch(new BrowserType.LaunchOptions()
                        .setHeadless(false)
                        .setSlowMo(500));
    }

    @AfterAll
    static void tearDown() {
        playwright.close();
    }

    @Test
    void openPage() {
        BrowserContext context = browser.newContext();
        Page page = context.newPage();
        page.navigate("https://the-internet.herokuapp.com/");
        Locator link = page.locator("li");
        page.getByText("Add/Remove Elements").click();

    }

    @Test
    void twoContextTwoPage() {
        BrowserContext context1 = browser.newContext();
        Page page1 = context1.newPage();

        page1.navigate("https://the-internet.herokuapp.com/");
        page1.getByText("Form Authentication").click();

        page1.getByRole(AriaRole.TEXTBOX).filter().first().fill("tomsmith");
        page1.getByRole(AriaRole.TEXTBOX).filter().last().fill("SuperSecretPassword!");
        page1.getByRole(AriaRole.BUTTON).filter(new FilterOptions().setHasText("Login")).click();
        assertThat(page1).hasURL("https://the-internet.herokuapp.com/secure");
        assertThat(page1.locator("#flash")).containsText("You logged into a secure area!");

        BrowserContext context2 = browser.newContext();
        Page page2 = context2.newPage();
        page2.navigate("https://the-internet.herokuapp.com/secure");

        assertThat(page2).hasURL("https://the-internet.herokuapp.com/login");
        assertThat(page2.locator("#flash")).containsText("You must login to view the secure area!");
    }

    @Test
    void twoPageOneContext() {
        BrowserContext context = browser.newContext();
        Page page1 = context.newPage();

        page1.navigate("https://the-internet.herokuapp.com/login");
        Locator login = page1.locator("login");
        page1.getByRole(AriaRole.TEXTBOX).filter().first().fill("tomsmith");
        page1.getByRole(AriaRole.TEXTBOX).filter().last().fill("SuperSecretPassword!");
        page1.getByRole(AriaRole.BUTTON).filter(new FilterOptions().setHasText("Login")).click();
        assertThat(page1).hasURL("https://the-internet.herokuapp.com/secure");
        assertThat(page1.locator("#flash")).containsText("You logged into a secure area!");

        Page page2 = context.newPage();
        page2.navigate("https://the-internet.herokuapp.com/secure");
        assertThat(page2).hasURL("https://the-internet.herokuapp.com/secure");
    }

    @Test
    void authWithCredentials() {
        BrowserContext context1 = browser.newContext();
        Page page1 = context1.newPage();

        PlaywrightException ex = assertThrows(
                PlaywrightException.class,
                () -> page1.navigate("https://the-internet.herokuapp.com/basic_auth"));

        assertTrue(ex.getMessage().contains("ERR_INVALID_AUTH_CREDENTIALS"));

        BrowserContext context2 = browser
                .newContext(new Browser.NewContextOptions().setHttpCredentials("admin", "admin"));
        Page page2 = context2.newPage();

        Response response2 = page2.navigate("https://the-internet.herokuapp.com/basic_auth");
        assertEquals(200, response2.status());
        assertThat(page2.locator("#content")).containsText("Congratulations!");

    }
}
