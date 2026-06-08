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
import com.microsoft.playwright.Locator.FilterOptions;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Page.GetByRoleOptions;
import com.microsoft.playwright.Playwright;
import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import com.microsoft.playwright.options.AriaRole;

public class AssertionTest {

    static Playwright playwright;
    static Browser browser;
    BrowserContext context;
    Page page;

    @BeforeAll
    static void setUp() {
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
    static void tearDown() {
        browser.close();
        playwright.close();
    }

    @Test
    void checkStateAfterRemove() {
        page.navigate("https://practice.expandtesting.com/dynamic-controls");

        Locator checkBoxLocator = page.getByRole(AriaRole.CHECKBOX);

        assertThat(checkBoxLocator).isVisible();
        assertThat(checkBoxLocator).not().isChecked();

        page.getByRole(AriaRole.BUTTON).filter(new FilterOptions().setHasText("Remove")).click();

        assertThat(checkBoxLocator).not().isVisible();
        assertThat(page.locator("#message")).containsText("It's gone!");

        page.getByRole(AriaRole.BUTTON).filter(new FilterOptions().setHasText("Add")).click();

        assertThat(checkBoxLocator).isVisible();
        assertThat(page.locator("#message")).containsText("It's back!");
    }

    @Test
    void checkFormFieldsState() {
        page.navigate("https://practice.expandtesting.com/dynamic-controls");

        Locator inputField = page.getByRole(AriaRole.TEXTBOX);

        assertThat(inputField).isDisabled();
        assertThat(inputField).not().isEditable();

        page.getByRole(AriaRole.BUTTON).filter(new FilterOptions().setHasText("Enable")).click();

        assertThat(inputField).isEditable();

        inputField.fill("Hello world!");
        // String fieldWithText = inputField.inputValue();

        assertThat(inputField).not().isEmpty();
        // assertEquals("Hello world!", fieldWithText);
        assertThat(inputField).hasValue("Hello world!");

    }

    @Test
    void addRemoveBttns() {
        page.navigate("https://practice.expandtesting.com/add-remove-elements");

        Locator addBttn = page.getByRole(AriaRole.BUTTON, new GetByRoleOptions().setName("Add Element"));
        Locator deleteBttn = page.getByRole(AriaRole.BUTTON, new GetByRoleOptions().setName("Delete"));

        assertThat(deleteBttn).hasCount(0);

        for (int i = 0; i < 3; i++) {
            addBttn.click();

        }

        assertThat(deleteBttn).hasCount(3);

        page.getByRole(AriaRole.BUTTON, new GetByRoleOptions().setName("Delete")).filter().nth(0).click();
        assertThat(deleteBttn).hasCount(2);

        int i = 0;
        while (i < 2) {
            page.getByRole(AriaRole.BUTTON, new GetByRoleOptions().setName("Delete")).filter().nth(0).click();
            i++;
        }

        assertThat(deleteBttn).hasCount(0);

    }

    @Test
    void loginPageTest() {
        page.navigate("https://practice.expandtesting.com/login");
        assertThat(page).hasURL("https://practice.expandtesting.com/login");
        assertThat(page).hasURL(Pattern.compile(".*/login"));
        assertThat(page).hasTitle("Test Login Page for Automation Testing Practice");

        String userName = page.locator("li")
                .filter(new Locator.FilterOptions().setHasText("Username"))
                .locator("b")
                .textContent();
        String password = page.locator("li")
                .filter(new Locator.FilterOptions().setHasText("Password"))
                .locator("b")
                .textContent();

        page.getByRole(AriaRole.TEXTBOX).and(page.getByLabel("Username")).fill(userName);
        page.getByRole(AriaRole.TEXTBOX).and(page.getByLabel("Password")).fill(password);
        page.getByRole(AriaRole.BUTTON, new GetByRoleOptions().setName("Login")).click();

        assertThat(page).hasURL("https://practice.expandtesting.com/secure");
        assertThat(page).hasURL(Pattern.compile(".*/secure"));

        assertThat(page.locator("#flash")).containsText("You logged into a secure area!");
        assertThat(page.locator("#username")).containsText("Hi, " + userName);
        assertThat(page.locator("//*[@id=\"core\"]/div/div"))
                .containsText("Welcome to the Secure Area. When you are done click logout below.");
    }

    @Test
    void checkBoxTest() {
        page.navigate("https://practice.expandtesting.com/checkboxes");

        Locator checkBox1 = page.getByRole(AriaRole.CHECKBOX, new GetByRoleOptions().setName("Checkbox 1"));
        Locator checkBox2 = page.getByRole(AriaRole.CHECKBOX, new GetByRoleOptions().setName("Checkbox 2"));

        assertThat(page.getByRole(AriaRole.CHECKBOX)).hasCount(2);
        assertThat(checkBox1).not().isChecked();
        assertThat(checkBox2).isChecked();
        assertThat(checkBox1).hasAttribute("type", "checkbox");
        assertThat(checkBox2).hasAttribute("type", "checkbox");

        checkBox1.setChecked(true);
        assertThat(checkBox1).isChecked();

        checkBox2.setChecked(false);
        assertThat(checkBox2).not().isChecked();
    }

    @Test
    void wrongLogin() {
        page.navigate("https://practice.expandtesting.com/login");

        page.getByRole(AriaRole.TEXTBOX, new GetByRoleOptions().setName("Username")).fill("practice");
        page.getByRole(AriaRole.TEXTBOX, new GetByRoleOptions().setName("Password")).fill("practice");
        page.getByRole(AriaRole.BUTTON, new GetByRoleOptions().setName("Login")).click();

        assertThat(page).not().hasURL(Pattern.compile(".*/secure"));
        assertThat(page.locator("#flash")).containsText("Your password is invalid!");

        page.getByRole(AriaRole.TEXTBOX, new GetByRoleOptions().setName("Username")).fill("practice");
        page.getByRole(AriaRole.TEXTBOX, new GetByRoleOptions().setName("Password")).fill("SuperSecretPassword!");
        page.getByRole(AriaRole.BUTTON, new GetByRoleOptions().setName("Login")).click();

        assertThat(page).hasURL(Pattern.compile(".*/secure"));
        assertThat(page.locator("#flash")).not().containsText("Your password is invalid!");

    }
}
