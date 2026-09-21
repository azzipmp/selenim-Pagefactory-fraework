package framework;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import java.util.Locale;

/**
 * Owns the WebDriver lifecycle and supports multiple browsers through configuration.
 * The browser can be selected via config.properties or runtime system property.
 */
public final class DriverFactory {

    private static final ThreadLocal<WebDriver> DRIVER_THREAD_LOCAL = new ThreadLocal<>();
    private static String browserName = null;

    private DriverFactory() {
    }

    public static void setBrowser(String browser) {
        DriverFactory.browserName = (browser == null) ? null : browser.trim().toLowerCase(Locale.ROOT);
    }

    public static WebDriver getDriver() {
        if (DRIVER_THREAD_LOCAL.get() == null) {
            createDriver();
        }
        return DRIVER_THREAD_LOCAL.get();
    }

    public static void createDriver() {
        String selectedBrowser = browserName != null ? browserName : ConfigReader.getString("browser", "chrome");
        WebDriver driver;

        switch (selectedBrowser.toLowerCase(Locale.ROOT)) {
            case "firefox":
                WebDriverManager.firefoxdriver().setup();
                FirefoxOptions firefoxOptions = new FirefoxOptions();
                if (ConfigReader.getBoolean("headless", true)) {
                    firefoxOptions.addArguments("-headless");
                }
                driver = new FirefoxDriver(firefoxOptions);
                break;
            case "edge":
                WebDriverManager.edgedriver().setup();
                EdgeOptions edgeOptions = new EdgeOptions();
                if (ConfigReader.getBoolean("headless", true)) {
                    edgeOptions.addArguments("--headless=new");
                }
                driver = new EdgeDriver(edgeOptions);
                break;
            case "chrome":
            default:
                WebDriverManager.chromedriver().setup();
                ChromeOptions chromeOptions = new ChromeOptions();
                if (ConfigReader.getBoolean("headless", true)) {
                    chromeOptions.addArguments("--headless=new", "--window-size=1920,1080");
                }
                chromeOptions.addArguments("--disable-dev-shm-usage", "--no-sandbox");
                driver = new ChromeDriver(chromeOptions);
                break;
        }

        driver.manage().window().maximize();
        DRIVER_THREAD_LOCAL.set(driver);
        LoggerUtil.info("WebDriver initialized for browser: " + selectedBrowser);
    }

    public static void quitDriver() {
        WebDriver driver = DRIVER_THREAD_LOCAL.get();
        if (driver != null) {
            LoggerUtil.info("Closing WebDriver session");
            driver.quit();
            DRIVER_THREAD_LOCAL.remove();
        }
    }
}
