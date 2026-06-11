package tests.java.my_practice_tests;

import java.util.regex.Pattern;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType.LaunchOptions;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Locator.WaitForOptions;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Page.GetByRoleOptions;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.TimeoutError;
import com.microsoft.playwright.assertions.LocatorAssertions.IsVisibleOptions;
import com.microsoft.playwright.assertions.PageAssertions;
import com.microsoft.playwright.assertions.PlaywrightAssertions;
import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.LoadState;
import static com.microsoft.playwright.options.WaitForSelectorState.VISIBLE;

public class WaitingTest {

    static Playwright playwright;
    static Browser browser;
    BrowserContext context;
    Page page;

    @BeforeAll
    static void browserSetUp() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new LaunchOptions().setHeadless(false));
    }

    @BeforeEach
    void contextSetUp() {
        context = browser.newContext();
        page = context.newPage();
    }

    @AfterEach
    void contextTearDown() {
        context.close();
    }

    @AfterAll
    static void browserTearDown() {
        browser.close();
        playwright.close();
    }

    @Test
    void waitForLoadContentOnPage() {
        page.navigate("https://the-internet.herokuapp.com/infinite_scroll");

        int pageHeight = (int) page.evaluate("document.body.scrollHeight");
        System.out.println("Page height: " + pageHeight);

        page.waitForLoadState(LoadState.NETWORKIDLE);

        int newPageHeight = (int) page.evaluate("document.body.scrollHeight");
        System.out.println("New page height: " + newPageHeight);

        assertNotEquals(pageHeight, newPageHeight);

    }

    @Test
    void waitForRedirect() {
        page.navigate("https://the-internet.herokuapp.com/login");
        page.getByRole(AriaRole.TEXTBOX, new GetByRoleOptions().setName("Username")).fill("tomsmith");
        page.getByLabel("Password").fill("SuperSecretPassword!");
        page.getByRole(AriaRole.BUTTON, new GetByRoleOptions().setName("Login")).click();

        page.waitForURL(Pattern.compile(".*/secure"));

        assertThat(page.locator("#flash")).containsText("You logged into a secure area!");
    }

    @Test
    void waitForLoading() {
        page.navigate("https://the-internet.herokuapp.com/dynamic_loading/2");

        Locator loadedText = page.locator("#finish");
        Locator startButton = page.getByRole(AriaRole.BUTTON, new GetByRoleOptions().setName("Start"));

        startButton.click();
        loadedText.waitFor(new WaitForOptions().setState(VISIBLE));

        assertThat(loadedText).containsText("Hello World!");

        page.reload();

        startButton.click();
        assertThat(loadedText).isVisible(new IsVisibleOptions().setTimeout(10000));
        assertThat(loadedText).containsText("Hello World!");

    }

    @Test
    void timeOutErrorCatch() {
        page.navigate("https://the-internet.herokuapp.com/login");
        page.getByRole(AriaRole.TEXTBOX, new GetByRoleOptions().setName("Username")).fill("tomsmith");
        page.getByLabel("Password").fill("SuperPassword");
        page.getByRole(AriaRole.BUTTON, new GetByRoleOptions().setName("Login")).click();

        try {
            page.waitForURL(Pattern.compile(".*/secure"), new Page.WaitForURLOptions().setTimeout(2000));
            fail("URL incorrect");
        } catch (TimeoutError e) {
            System.out.println("Exception catched");
        }

        assertThat(page.locator("#flash")).containsText("invalid");
        assertThat(page).not().hasURL(Pattern.compile(".*/secure"), new PageAssertions.HasURLOptions().setTimeout(10));

    }

    @Test
    void globalTimeouts() {
        context.setDefaultTimeout(5000);
        PlaywrightAssertions.setDefaultAssertionTimeout(0);

        page.navigate("https://the-internet.herokuapp.com/login");
        page.getByRole(AriaRole.TEXTBOX, new GetByRoleOptions().setName("Username")).fill("tomsmith");
        page.getByLabel("Password").fill("SuperSecretPassword!");
        page.getByRole(AriaRole.BUTTON, new GetByRoleOptions().setName("Login")).click();

        assertThat(page.locator("#flash")).containsText("You logged into a secure area!");

        PlaywrightAssertions.setDefaultAssertionTimeout(5000);
    }

    @Test
    void refactorAntiPattern() {

        page.waitForResponse("https://the-internet.herokuapp.com/infinite_scroll/2", () -> {
            page.navigate("https://the-internet.herokuapp.com/infinite_scroll");
        });

        Locator div = page.locator(".jscroll-added");
        int divCountBefore = div.count();
        System.out.println("BEFORE SCROLL: " + divCountBefore);

        page.mouse().wheel(0, 5000);

        assertThat(div).not().hasCount(divCountBefore);

        int divCountAfter = div.count();

        System.out.println("AFTER SCROLL: " + divCountAfter);

    }

}
