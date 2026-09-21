package tests.listeners;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class ExtentReportManager {

    private static final String REPORT_DIR = "target/extent-reports";
    private static ExtentReports extentReports;

    private ExtentReportManager() {
    }

    public static synchronized ExtentReports getReporter() {
        if (extentReports == null) {
            extentReports = createReporter();
        }
        return extentReports;
    }

    public static synchronized void flushReport() {
        if (extentReports != null) {
            extentReports.flush();
        }
    }

    public static Path screenshotDirectory() {
        Path screenshotDir = Paths.get(REPORT_DIR, "screenshots");
        try {
            Files.createDirectories(screenshotDir);
        } catch (Exception e) {
            throw new RuntimeException("Unable to create screenshot directory: " + screenshotDir, e);
        }
        return screenshotDir;
    }

    private static ExtentReports createReporter() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));
        Path reportPath = Paths.get(REPORT_DIR, "extent-report-" + timestamp + ".html");

        try {
            Files.createDirectories(reportPath.getParent());
        } catch (Exception e) {
            throw new RuntimeException("Unable to create report directory: " + reportPath.getParent(), e);
        }

        ExtentSparkReporter sparkReporter = new ExtentSparkReporter(reportPath.toString());
        sparkReporter.config().setReportName("Selenium Automation Report");
        sparkReporter.config().setDocumentTitle("Execution Results");

        ExtentReports reports = new ExtentReports();
        reports.attachReporter(sparkReporter);
        reports.setSystemInfo("Framework", "Selenium + TestNG");
        reports.setSystemInfo("Report", reportPath.toAbsolutePath().toString());
        return reports;
    }
}
