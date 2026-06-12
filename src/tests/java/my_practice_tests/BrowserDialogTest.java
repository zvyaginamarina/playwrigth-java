package tests.java.my_practice_tests;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Consumer;
import java.util.regex.Pattern;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType.LaunchOptions;
import com.microsoft.playwright.Dialog;
import com.microsoft.playwright.Download;
import com.microsoft.playwright.FileChooser;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Page.GetByRoleOptions;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.Response;
import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import com.microsoft.playwright.options.AriaRole;

public class BrowserDialogTest {

    static Playwright playwright;
    static Browser browser;
    BrowserContext context;
    Page page;

    private String dialogType;
    private String dialogMessage;

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
    void dialogOpenAlert() {
        page.navigate("https://the-internet.herokuapp.com/javascript_alerts");

        Locator result = page.locator("#result");

        page.onDialog(dialog -> {
            dialogType = dialog.type();
            dialogMessage = dialog.message();
            dialog.accept();
        });

        page.getByRole(AriaRole.BUTTON, new GetByRoleOptions().setName("Click for JS Alert")).click();
        assertEquals("alert", dialogType);
        assertEquals("I am a JS Alert", dialogMessage);
        assertThat(result).containsText("You successfully clicked an alert");

    }

    @Test
    void dialogOpenConfirm() {
        page.navigate("https://the-internet.herokuapp.com/javascript_alerts");

        Locator result = page.locator("#result");

        page.onDialog(dialog -> {
            dialogType = dialog.type();
            dialogMessage = dialog.message();
            dialog.dismiss();

        });

        page.getByRole(AriaRole.BUTTON, new GetByRoleOptions().setName("Click for JS Confirm")).click();
        assertEquals("confirm", dialogType);
        assertEquals("I am a JS Confirm", dialogMessage);
        assertThat(result).containsText("You clicked: Cancel");

    }

    @Test
    void dialogOpenPrompt() {
        page.navigate("https://the-internet.herokuapp.com/javascript_alerts");

        Locator result = page.locator("#result");
        String promptInput = "Hello world";

        page.onDialog(dialog -> {
            dialogType = dialog.type();
            dialogMessage = dialog.message();
            dialog.accept(promptInput);
        });

        page.getByRole(AriaRole.BUTTON, new GetByRoleOptions().setName("Click for JS Prompt")).click();
        assertEquals("prompt", dialogType);
        assertEquals("I am a JS prompt", dialogMessage);
        assertThat(result).containsText("You entered: Hello world");

    }

    @Test
    void fileDownload() throws IOException {
        page.navigate("https://the-internet.herokuapp.com/download");

        String fileName = "some-file.txt";

        Download download = page.waitForDownload(() -> {
            page.getByText(fileName).click();
        });

        Path savePath = Paths.get("target", "downloads", download.suggestedFilename());
        download.saveAs(savePath);

        boolean fileExist = Files.exists(savePath);
        assertTrue(fileExist);

        long fileSize = Files.size(savePath);
        assertTrue(fileSize > 0);

        assertEquals(fileName, download.suggestedFilename());
        assertTrue(download.url().contains("download/some-file.txt"));

        Files.deleteIfExists(savePath);

    }

    @Test
    void fileUploadFileChooser() {
        page.navigate("https://the-internet.herokuapp.com/upload");

        Locator selectFileButton = page.locator("#file-upload");
        Locator uploadButton = page.getByRole(AriaRole.BUTTON, new GetByRoleOptions().setName("Upload"));

        FileChooser chooser = page.waitForFileChooser(() -> selectFileButton.click());

        chooser.setFiles(Paths.get("src", "tests", "resources", "cat-in-house.jpg"));
        uploadButton.click();

        assertThat(page.locator("#uploaded-files")).isVisible();
        assertThat(page.locator("#uploaded-files")).containsText("cat-in-house.jpg");
    }

    @Test
    void fileUploadSetInput() {
        page.navigate("https://the-internet.herokuapp.com/upload");

        Locator selectFileButton = page.locator("#file-upload");
        Locator uploadButton = page.getByRole(AriaRole.BUTTON, new GetByRoleOptions().setName("Upload"));

        selectFileButton.setInputFiles(Paths.get("src", "tests", "resources", "cat-in-house.jpg"));
        uploadButton.click();

        assertThat(page.locator("#uploaded-files")).containsText("cat-in-house.jpg");
    }

    @Test
    void confirmNoListener() {
        page.navigate("https://the-internet.herokuapp.com/javascript_alerts");

        Locator result = page.locator("#result");
        page.getByRole(AriaRole.BUTTON, new GetByRoleOptions().setName("Click for JS Confirm")).click();
        assertThat(result).containsText("You clicked: Cancel");
    }

    @Test
    void confirmDialog() {
        page.navigate("https://the-internet.herokuapp.com/javascript_alerts");

        Locator result = page.locator("#result");

        Consumer<Dialog> handler = dialog -> {
            dialog.accept();
        };

        page.onDialog(handler);

        page.getByRole(AriaRole.BUTTON, new GetByRoleOptions().setName("Click for JS Confirm")).click();
        assertThat(result).containsText("You clicked: Ok");

        page.offDialog(handler);

        page.getByRole(AriaRole.BUTTON, new GetByRoleOptions().setName("Click for JS Confirm")).click();
        assertThat(result).containsText("You clicked: Cancel");

    }

    @Test
    void responseOnUpload() {
        page.navigate("https://the-internet.herokuapp.com/upload");

        Locator selectFile = page.locator("#file-upload");
        Locator uploadButton = page.getByRole(AriaRole.BUTTON, new GetByRoleOptions().setName("Upload"));

        Path filePath = Paths.get("src", "tests", "resources", "cat-in-house.jpg");

        selectFile.setInputFiles(filePath);

        Response response = page.waitForResponse(Pattern.compile(".*/upload"), () -> uploadButton.click());
        assertEquals(200, response.status());
        assertThat(page.locator("#uploaded-files")).containsText("cat-in-house.jpg");

    }

}
