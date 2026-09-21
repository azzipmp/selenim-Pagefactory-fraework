package tests.listeners;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import framework.DriverFactory;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

public class ExtentTestListener implements ITestListener {

    private static final ThreadLocal<ExtentTest> EXTENT_TEST = new ThreadLocal<>();

    @Override
    public void onStart(ITestContext context) {
        ExtentReportManager.getReporter();
    }

    @Override
    public void onTestStart(ITestResult result) {
        String methodName = result.getMethod().getMethodName();
        String className = result.getTestClass().getName();

        ExtentTest test = ExtentReportManager.getReporter().createTest(className + "." + methodName);
        String[] groups = result.getMethod().getGroups();
        if (groups != null) {
            for (String group : groups) {
                test.assignCategory(group);
            }
        }
        EXTENT_TEST.set(test);
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        ExtentTest test = EXTENT_TEST.get();
        if (test == null) {
            return;
        }
        test.log(Status.PASS, "Test passed");
        attachScreenshot(test, result, "pass");
    }

    @Override
    public void onTestFailure(ITestResult result) {
        ExtentTest test = EXTENT_TEST.get();
        if (test == null) {
            return;
        }
        Throwable throwable = result.getThrowable();
        if (throwable != null) {
            test.fail(throwable);
        } else {
            test.log(Status.FAIL, "Test failed");
        }
        attachScreenshot(test, result, "fail");
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        ExtentTest test = EXTENT_TEST.get();
        if (test == null) {
            return;
        }
        Throwable throwable = result.getThrowable();
        if (throwable != null) {
            test.skip(throwable);
        } else {
            test.log(Status.SKIP, "Test skipped");
        }
    }

    @Override
    public void onFinish(ITestContext context) {
        ExtentReportManager.flushReport();
        EXTENT_TEST.remove();
    }

    private void attachScreenshot(ExtentTest test, ITestResult result, String status) {
        WebDriver driver = DriverFactory.getCurrentDriver();
        if (!(driver instanceof TakesScreenshot)) {
            test.log(Status.WARNING, "Screenshot not available for this driver.");
            return;
        }

        try {
            Path screenshot = saveScreenshot((TakesScreenshot) driver, result, status);
            test.addScreenCaptureFromPath(screenshot.toAbsolutePath().toString());
        } catch (Exception e) {
            test.log(Status.WARNING, "Failed to attach screenshot: " + e.getMessage());
        }
    }

    private Path saveScreenshot(TakesScreenshot takesScreenshot, ITestResult result, String status) throws IOException {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss-SSS"));
        String className = result.getTestClass().getRealClass().getSimpleName();
        String methodName = result.getMethod().getMethodName();
        String fileName = className + "-" + methodName + "-" + status + "-" + timestamp + ".png";

        Path destination = ExtentReportManager.screenshotDirectory().resolve(fileName);
        File source = takesScreenshot.getScreenshotAs(OutputType.FILE);
        Files.copy(source.toPath(), destination, StandardCopyOption.REPLACE_EXISTING);
        return destination;
    }
}
