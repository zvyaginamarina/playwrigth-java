// package tests.java;

// import org.junit.jupiter.api.Test;

// import com.microsoft.playwright.Browser;
// import com.microsoft.playwright.BrowserType;
// import com.microsoft.playwright.Page;
// import com.microsoft.playwright.options.Proxy;

// public class ProxyTest extends BaseTest {

// @Test
// void testProxy() {
// // Настройка прокси
// Browser browser = playwright.chromium().launch(new
// BrowserType.LaunchOptions()
// .setProxy(new Proxy("http://my-proxy-server:3128")));

// Page page = browser.newPage();
// page.navigate("https://google.com");
// System.out.println("Page title: " + page.title());
// }
// }