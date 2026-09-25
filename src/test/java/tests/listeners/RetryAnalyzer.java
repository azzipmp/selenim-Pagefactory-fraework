package tests.listeners;

import framework.DriverFactory;
import framework.LoggerUtil;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

public final class RetryAnalyzer implements IRetryAnalyzer {

    private static final int MAX_RETRIES = 2;
    private int retryCount;

    @Override
    public boolean retry(ITestResult result) {
        if (retryCount >= MAX_RETRIES) {
            return false;
        }

        retryCount++;
        LoggerUtil.warn("Retrying " + result.getMethod().getQualifiedName()
                + " (attempt " + (retryCount + 1) + " of " + (MAX_RETRIES + 1) + ")");
        DriverFactory.quitDriver();
        return true;
    }
}
