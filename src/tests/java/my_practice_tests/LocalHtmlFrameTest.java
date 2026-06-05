package tests.java.my_practice_tests;

import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType.LaunchOptions;
import com.microsoft.playwright.Frame;
import com.microsoft.playwright.FrameLocator;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import com.microsoft.playwright.options.AriaRole;

import net.datafaker.Faker;

public class LocalHtmlFrameTest {

    static Playwright playwright;
    static Browser browser;
    static BrowserContext context;
    static Page page;
    static Faker faker;

    @BeforeAll
    static void setUp() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new LaunchOptions().setHeadless(false));
        faker = new Faker();
    }

    @BeforeEach
    void contextSetUp() {
        context = browser.newContext();
        page = context.newPage();
        page.navigate("file:///D:/Java/playwright-java/src/tests/resources/frame-practice.html");
    }

    @AfterAll
    static void tearDown() {
        context.close();
        browser.close();
        playwright.close();
    }

    @Test
    void basicFrameEdit() {
        Locator frame = page.locator("#editor-frame");
        FrameLocator frameLoc = frame.contentFrame();

        String fullName = faker.name().fullName();

        frameLoc.getByLabel("Name").fill(fullName);
        frameLoc.getByRole(AriaRole.BUTTON).click();

        assertThat(frameLoc.locator("#result")).containsText("Hello, " + fullName);
    }

    @Test
    void fourFrames() {
        FrameLocator leftFrame = page.frameLocator("iframe[name='frame-left']");
        FrameLocator middleFrame = page.frameLocator("iframe[name='frame-middle']");
        FrameLocator rightFrame = page.frameLocator("iframe[name='frame-right']");
        FrameLocator bottomFrame = page.frameLocator("iframe[name='frame-bottom']");

        assertThat(leftFrame.locator("h3")).containsText("LEFT");
        assertThat(middleFrame.locator("h3")).containsText("MIDDLE");
        assertThat(rightFrame.locator("h3")).containsText("RIGHT");
        assertThat(bottomFrame.locator("h3")).containsText("BOTTOM");

    }

    @Test
    void getAllFrames() {
        Frame mainFrame = page.mainFrame();

        List<Frame> allFrames = page.frames();

        System.out.println(allFrames.size());

        for (int i = 0; i < allFrames.size(); i++) {
            Frame childFrame = allFrames.get(i);
            System.out.println(childFrame.name() + " " + childFrame.url());
        }

    }

    @Test
    void chainingFrame() {
        Locator innerBtn = page.frameLocator("iframe[name='outer']")
                .frameLocator("iframe[name='inner']")
                .locator("#inner-btn");

        assertThat(innerBtn).isVisible();
        innerBtn.click();
    }

}
