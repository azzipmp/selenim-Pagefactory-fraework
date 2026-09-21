package framework;

import org.openqa.selenium.WebDriver;

import java.lang.reflect.Constructor;

/**
 * Lightweight page-object factory used by the project when a test wants to construct
 * a page object from the active WebDriver without duplicating setup logic.
 */
public final class PageObjectExtension {

    private PageObjectExtension() {
    }

    public static <T extends BasePage> T createPage(Class<T> pageClass, WebDriver driver) {
        try {
            Constructor<T> constructor = pageClass.getDeclaredConstructor(WebDriver.class);
            constructor.setAccessible(true);
            return constructor.newInstance(driver);
        } catch (Exception e) {
            throw new IllegalStateException(
                    "Could not instantiate page object: " + pageClass.getSimpleName()
                            + ". Every page object must expose a constructor(WebDriver driver).", e);
        }
    }
}
