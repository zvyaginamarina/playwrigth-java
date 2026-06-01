package tests.java;

import com.microsoft.playwright.Browser;

public class DeviceList {
    Browser.NewContextOptions getDeviceOptions(String deviceName) {
        switch (deviceName.toLowerCase()) {
            case "iphone 11":
                return new Browser.NewContextOptions()
                        .setUserAgent(
                                "Mozilla/5.0 (iPhone; CPU iPhone OS 12_2 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/13.0 Mobile/15E148 Safari/604.1")
                        .setViewportSize(414, 896)
                        .setDeviceScaleFactor(2)
                        .setIsMobile(true)
                        .setHasTouch(true);
            // Тут можно добавить другие устройства
            default:
                throw new IllegalArgumentException("Unknown device");
        }
    }

}
