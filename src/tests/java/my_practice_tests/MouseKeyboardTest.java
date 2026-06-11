package tests.java.my_practice_tests;

import java.util.regex.Pattern;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType.LaunchOptions;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Page.GetByRoleOptions;
import com.microsoft.playwright.Playwright;
import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.BoundingBox;
import com.microsoft.playwright.options.LoadState;

public class MouseKeyboardTest {

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
    void hoverTest() {
        page.navigate("https://the-internet.herokuapp.com/hovers");

        Locator userImage = page.getByAltText("User Avatar");
        Locator userProfileLink = page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("View profile"));

        assertThat(userProfileLink.first()).not().isVisible();

        userImage.first().hover();
        assertThat(userProfileLink.first()).isVisible();

        userImage.nth(1).hover();
        assertThat(userProfileLink).isVisible();

        userProfileLink.nth(1).click();
        assertThat(page).hasURL(Pattern.compile(".*/users/2$"));

    }

    @Test
    void dragnDropHiLevel() {
        page.navigate("https://the-internet.herokuapp.com/drag_and_drop");

        Locator blockA = page.locator("#column-a");
        Locator blockB = page.locator("#column-b");

        assertThat(blockA).containsText("A");

        blockA.dragTo(blockB);

        assertThat(blockA).containsText("B");

    }

    @Test
    void dragnDropLowLevel() {
        page.navigate("https://the-internet.herokuapp.com/drag_and_drop");

        Locator blockA = page.locator("#column-a");
        BoundingBox blockABox = blockA.boundingBox();
        double aX = blockABox.x;
        double aY = blockABox.y;
        double aWidth = blockABox.width;
        double aHeight = blockABox.height;
        double aWCenter = aX + aWidth / 2;
        double aHCenter = aY + aHeight / 2;

        Locator blockB = page.locator("#column-b");
        BoundingBox blockBBox = blockB.boundingBox();
        double bX = blockBBox.x;
        double bY = blockBBox.y;
        double bWidth = blockBBox.width;
        double bHeight = blockBBox.height;
        double bWCenter = bX + bWidth / 2;
        double bHCenter = bY + bHeight / 2;

        page.mouse().move(aWCenter, aHCenter);
        page.mouse().down();
        page.mouse().move(bWCenter, bHCenter);
        page.mouse().up();

        assertThat(blockA).containsText("B");

    }

    @Test
    void keyBoardNav() {
        page.navigate("https://the-internet.herokuapp.com/login");

        Locator usernameField = page.getByRole(AriaRole.TEXTBOX, new GetByRoleOptions().setName("Username"));
        Locator passwordField = page.getByLabel("Password");

        usernameField.focus();
        page.keyboard().type("tomsmith");
        page.keyboard().press("Tab");
        page.keyboard().type("SuperSecretPassword!");
        page.keyboard().press("Tab");
        page.keyboard().press("Enter");

        assertThat(page).hasURL(Pattern.compile(".*/secure"));

    }

    @Test
    void sequentialInputTest() {
        page.navigate("https://the-internet.herokuapp.com/key_presses");

        String letter = "A";
        String number = "1";
        String specSymbol = "@";
        String arrowUp = "ArrowUp";
        String esc = "Escape";
        String shift = "Shift";

        Locator inputField = page.getByRole(AriaRole.TEXTBOX);
        Locator result = page.locator("#result");

        inputField.press(letter);
        assertThat(result).containsText("You entered: " + letter);
        inputField.press(number);
        inputField.press(specSymbol);

        inputField.clear();
        inputField.press(arrowUp);
        assertThat(result).containsText("You entered: " + "UP");

        inputField.clear();
        inputField.press(esc);
        assertThat(result).containsText(Pattern.compile(esc, Pattern.CASE_INSENSITIVE));

        inputField.clear();
        inputField.press(shift + "+" + letter);
        assertThat(result).containsText("You entered: " + letter);

        inputField.clear();
        inputField.pressSequentially("Hello World!", new Locator.PressSequentiallyOptions().setDelay(100));
    }

    @Test
    void mouseScroll() {
        page.navigate("https://the-internet.herokuapp.com/infinite_scroll");

        page.waitForLoadState(LoadState.NETWORKIDLE);

        Locator div = page.locator("#content > div > div > div > div > div");
        int divCountBefore = div.count();
        System.out.println("BEFORE SCROLL: " + divCountBefore);

        page.mouse().wheel(0, 5000);

        // page.evaluate("window.scrollBy(0, 5000)");

        assertThat(div).not().hasCount(divCountBefore);

        int divCountAfter = div.count();

        System.out.println("AFTER SCROLL: " + divCountAfter);

    }

}
