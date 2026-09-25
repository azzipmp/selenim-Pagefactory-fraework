package tests;

import framework.ConfigReader;
import framework.DriverFactory;
import framework.LoggerUtil;
import framework.TestDataColumns;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import pages.HomePage;
import pages.LoginPage;
import tests.listeners.ExtentTestListener;
import tests.listeners.RetryAnalyzer;

@Listeners(ExtentTestListener.class)
public class LoginTest {

    private static final String DEFAULT_LOGIN_CSV = "testdata/login-credentials.csv";
 

    private WebDriver driver;

    private CsvUtils.CsvTable loginTestData() {
        CsvUtils.CsvTable csvTable = CsvUtils.readCsvData(DEFAULT_LOGIN_CSV);
        LoggerUtil.info("Loaded " + csvTable.rowCount() + " test data entries from CSV file: " + csvTable.source());
        return csvTable;
    }


    @BeforeTest(alwaysRun = true)
    public void setUp() {
        String browser = ConfigReader.getString("browser", "chrome");
        DriverFactory.setBrowser(browser);
        this.driver = DriverFactory.getDriver();
        LoggerUtil.info("Test setup complete for browser: " + browser);
    }

    @AfterTest(alwaysRun = true)
    public void tearDown() {
        DriverFactory.quitDriver();
        LoggerUtil.info("Browser session closed after test");
    }

    @Test(groups = {"smoke"}, retryAnalyzer = RetryAnalyzer.class)
    public void validLoginNavigatesToHomePage() {
        this.driver = DriverFactory.getDriver();
        CsvUtils.CsvTable testData = loginTestData();
        String url = ConfigReader.getString("url", "");
        int executedRows = 0;

        for (int rowIndex = 0; rowIndex < testData.rowCount(); rowIndex++) {
            String username = testData.getValue(rowIndex, TestDataColumns.USERNAME_COLUMN);
            String password = testData.getValue(rowIndex, TestDataColumns.PASSWORD_COLUMN);
            if (username == null || password == null || username.isBlank() || password.isBlank()) {
                continue;
            }

            if (executedRows > 0) {
                DriverFactory.quitDriver();
                this.driver = DriverFactory.getDriver();
            }

            LoggerUtil.info("Running valid login test for user: " + username);
            LoginPage loginPage = new LoginPage(driver);
            loginPage.open(url);

            HomePage homePage = loginPage.login(username, password);

            Assert.assertTrue(homePage.isLoaded(), "Expected successful login to land on the logged-in page");
            executedRows++;
        }

        Assert.assertTrue(!url.isBlank(), "Expected url to be configured in config.properties");
        Assert.assertTrue(executedRows > 0, "Expected at least one valid username/password row in CSV test data");

        LoggerUtil.info("Login flow completed successfully");
    }

    @Test(groups = {"regression"})
    public void multiplePageObjectsCanBeUsedInOneFlow() {
        CsvUtils.CsvTable testData = loginTestData();
        String url = ConfigReader.getString("url", "");
        int executedRows = 0;

        for (int rowIndex = 0; rowIndex < testData.rowCount(); rowIndex++) {
            String username = testData.getValue(rowIndex, TestDataColumns.USERNAME_COLUMN);
            String password = testData.getValue(rowIndex, TestDataColumns.PASSWORD_COLUMN);
            if (username == null || password == null || username.isBlank() || password.isBlank()) {
                continue;
            }

            if (executedRows > 0) {
                DriverFactory.quitDriver();
                this.driver = DriverFactory.getDriver();
            }

            LoggerUtil.info("Running multi-page validation for user: " + username);
            LoginPage loginPage = new LoginPage(driver);
            HomePage homePage = new HomePage(driver);

            loginPage.open(url);
            loginPage.login(username, password);

            Assert.assertTrue(homePage.isLoaded(), "Expected the home page object to reflect the logged-in state");
            executedRows++;
        }

        Assert.assertTrue(!url.isBlank(), "Expected url to be configured in config.properties");
        Assert.assertTrue(executedRows > 0, "Expected at least one valid username/password row in CSV test data");

        LoggerUtil.info("Multiple page objects validated in the same flow");
    }
}
