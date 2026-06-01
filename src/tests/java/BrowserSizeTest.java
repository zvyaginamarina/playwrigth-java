package tests.java;

import org.junit.jupiter.api.Test;

public class BrowserSizeTest extends BaseTest {
    @Test
    void testBrowserSize() {
        page.setViewportSize(1280, 720);
        page.navigate("https://example.com");
        System.out.println("Window size: " + page.viewportSize());
    }
}
