package tests;

import framework.ConfigReader;
import framework.DriverFactory;
import framework.LoggerUtil;
import framework.TestData;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import pages.HomePage;
import pages.LoginPage;

public class LoginTest {

    private WebDriver driver;

    @DataProvider(name = "browserProvider")
    public Object[][] browserProvider() {
        String configuredBrowser = ConfigReader.getString("browser", "chrome");
        return new Object[][] {{ configuredBrowser }};
    }

    @BeforeMethod(alwaysRun = true)
    public void setUp() {
        String browser = ConfigReader.getString("browser", "chrome");
        DriverFactory.setBrowser(browser);
        this.driver = DriverFactory.getDriver();
        LoggerUtil.info("Test setup complete for browser: " + browser);
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        DriverFactory.quitDriver();
        LoggerUtil.info("Browser session closed after test");
    }

    @Test(dataProvider = "browserProvider")
    public void validLoginNavigatesToHomePage(String browser) {
        LoggerUtil.info("Running valid login test in browser: " + browser);
        LoginPage loginPage = new LoginPage(driver);
        loginPage.open(TestData.url());

        HomePage homePage = loginPage.login(TestData.username(), TestData.password());

        Assert.assertTrue(homePage.isLoaded(), "Expected successful login to land on the logged-in page");
        LoggerUtil.info("Login flow completed successfully");
    }

    @Test(dataProvider = "browserProvider")
    public void multiplePageObjectsCanBeUsedInOneFlow(String browser) {
        LoggerUtil.info("Running multi-page validation in browser: " + browser);
        LoginPage loginPage = new LoginPage(driver);
        HomePage homePage = new HomePage(driver);

        loginPage.open(TestData.url());
        loginPage.login(TestData.username(), TestData.password());

        Assert.assertTrue(homePage.isLoaded(), "Expected the home page object to reflect the logged-in state");
        LoggerUtil.info("Multiple page objects validated in the same flow");
    }
}
