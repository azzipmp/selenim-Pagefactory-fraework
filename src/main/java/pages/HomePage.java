package pages;

import framework.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class HomePage extends BasePage {

    @FindBy(xpath = "//button[contains(.,'Log out')] | //a[contains(.,'Log out')]")
    private WebElement logoutButton;

    public HomePage(WebDriver driver) {
        super(driver);
    }

    public boolean isLoaded() {
        wait.until(d -> driver.getCurrentUrl().contains("logged-in-successfully")
                || driver.getPageSource().contains("Congratulations")
                || driver.getPageSource().contains("successfully logged in")
                || logoutButton.isDisplayed());

        return driver.getCurrentUrl().contains("logged-in-successfully")
                || driver.getPageSource().contains("Congratulations")
                || driver.getPageSource().contains("successfully logged in")
                || logoutButton.isDisplayed();
    }

    public String getWelcomeText() {
        return driver.findElement(By.tagName("body")).getText();
    }
}
