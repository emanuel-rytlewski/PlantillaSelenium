package driver;

import java.util.HashMap;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

public enum BrowserType implements DriverSetup {

  FIREFOX {
      /**
       * Firefox Driver.
       *
       * @param capabilities list of desired capabilities
       * @return FirefoxDriver object with the desired capabilities
       */
      public RemoteWebDriver getWebDriverObject(DesiredCapabilities capabilities) {
          return new FirefoxDriver(getOptions(capabilities));
      }

      /**
       * Gets the default firefox options as a DesiredCapabilities object
       * to initialize a new RemoteWebDriver or FirefoxDriver.
       *
       * @param capabilities the capabilities to be merged with the FirefoxDriver object
       * @return a FirefoxOptions object to be used by the new() RemoteWebDriver or FirefoxDriver
       */
      public DesiredCapabilities getBrowserCapabilities(DesiredCapabilities capabilities) {
          capabilities.setCapability(FirefoxOptions.FIREFOX_OPTIONS, getOptions(capabilities));
          return capabilities;
      }

      private @NotNull
      FirefoxOptions getOptions(@NotNull DesiredCapabilities capabilities) {
          FirefoxProfile testProfile = new FirefoxProfile();
          // disable web notifications
          testProfile.setPreference("dom.webnotifications.enabled", false);

          // disable push notifications
          testProfile.setPreference("dom.push.enabled", false);
          testProfile.setPreference("geo.enabled", true);
          testProfile.setPreference("geo.provider.use_corelocation", true);
          testProfile.setPreference("geo.prompt.testing", true);
          testProfile.setPreference("geo.prompt.testing.allow", true);
          capabilities.setCapability(FirefoxDriver.PROFILE, testProfile);

          FirefoxOptions options = new FirefoxOptions();
          options.addArguments("--width=1920");
          options.addArguments("--height=1080");
          options.merge(capabilities);
          return options;
      }
  },
  CHROME {
    /**
     * Chrome Driver.
     * @param capabilities list of desired capabilities
     * @return FirefoxDriver object with the desired capabilities
     */
    public RemoteWebDriver getWebDriverObject(DesiredCapabilities capabilities) {
      return new ChromeDriver(getOptions(capabilities));
    }

    /**
     * Gets the default ChromeOptions as DesiredCapabilities to initialize a new
     * RemoteWebDriver or ChromeDriver.
     * @param capabilities the capabilities to be merged with the ChromeOptions object
     * @return a ChromeOptions object to be used by the new() RemoteWebDriver or ChromeDriver
     */
    public DesiredCapabilities getBrowserCapabilities(DesiredCapabilities capabilities) {
      capabilities.setCapability(ChromeOptions.CAPABILITY, getOptions(capabilities));
      return capabilities;
    }

    private @NotNull ChromeOptions getOptions(DesiredCapabilities capabilities) {
      HashMap<String, Object> chromePreferences = new HashMap<>();
      chromePreferences.put("profile.password_manager_enabled", false);
      chromePreferences.put("credentials_enable_service", false);

      ChromeOptions options = new ChromeOptions();
      options.addArguments("window-size=1920,1080");
      options.addArguments("disable-extensions");
      options.addArguments("disable-popup-blocking");
      options.addArguments("no-sandbox");
      options.addArguments("acceptSslCerts=true");
      options.addArguments("unexpectedAlertBehaviour=accept");
      options.addArguments("no-default-browser-check");
      options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
      options.setExperimentalOption("prefs", chromePreferences);
      options.merge(capabilities);
      return options;
    }
  }
}
