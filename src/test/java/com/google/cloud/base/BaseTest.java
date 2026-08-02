package com.google.cloud.base;

import com.google.cloud.config.BrowserFactory;
import com.google.cloud.config.ConfigLoader;
import com.microsoft.playwright.*;
import io.qameta.allure.Allure;
import lombok.extern.slf4j.Slf4j;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.io.ByteArrayInputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * BaseTest
 * ============================================================
 * Abstract base class for all UI test classes.
 *
 * <p>Lifecycle per test method:
 * <ol>
 *   <li>Starts Playwright + Browser + BrowserContext + Page</li>
 *   <li>Configures viewport and navigation timeout from config</li>
 *   <li>On failure: captures a screenshot and attaches it to
 *       the Allure report</li>
 *   <li>Tears down Page → Context → Browser → Playwright</li>
 * </ol>
 *
 * <p>Subclasses access the {@link Page} via the protected
 * {@code page} field.
 * ============================================================
 */
@Slf4j
public abstract class BaseTest {

    // -------------------------------------------------------
    // Playwright objects – one set per test method thread
    // -------------------------------------------------------
    protected Playwright    playwright;
    protected Browser       browser;
    protected BrowserContext context;
    protected Page          page;

    // -------------------------------------------------------
    // Shared configuration
    // -------------------------------------------------------
    protected final ConfigLoader config = ConfigLoader.getInstance();

    // -------------------------------------------------------
    // Set-up
    // -------------------------------------------------------

    /**
     * Initialises the full Playwright stack before every test
     * method.  The {@code @BeforeMethod} runs in the same
     * thread as the test, so these fields are effectively
     * thread-local when Surefire forks.
     */
    @BeforeMethod(alwaysRun = true)
    public void setUpBrowser() {
        log.info("=== Setting up browser for test ===");

        playwright = Playwright.create();
        browser    = BrowserFactory.createBrowser(playwright);

        // Build a context with viewport and tracing options
        Browser.NewContextOptions contextOptions = new Browser.NewContextOptions()
                .setViewportSize(
                        config.getInt("viewport.width"),
                        config.getInt("viewport.height"))
                .setIgnoreHTTPSErrors(true);

        context = browser.newContext(contextOptions);

        // Enable Playwright tracing (attached to Allure on failure)
        context.tracing().start(
                new Tracing.StartOptions()
                        .setScreenshots(true)
                        .setSnapshots(true)
                        .setSources(false));

        page = context.newPage();

        // Apply navigation timeout from config
        page.setDefaultNavigationTimeout(config.getInt("default.timeout"));
        page.setDefaultTimeout(config.getInt("default.timeout"));

        log.info("Browser setup complete. Base URL: {}", config.get("base.url"));
    }

    // -------------------------------------------------------
    // Tear-down
    // -------------------------------------------------------

    /**
     * Captures a screenshot (and Playwright trace) on test
     * failure, then tears down the full Playwright stack.
     */
    @AfterMethod(alwaysRun = true)
    public void tearDownBrowser(ITestResult result) {
        if (result.getStatus() == ITestResult.FAILURE) {
            log.warn("Test FAILED: {}. Capturing evidence…", result.getName());
            captureScreenshot(result.getName());
            captureTrace(result.getName());
        } else {
            // Stop tracing without saving when the test passes
            try {
                context.tracing().stop();
            } catch (Exception ignored) {
                // Context may already be closed
            }
        }

        // Teardown order: page → context → browser → playwright
        closeSafely(page,       "Page");
        closeSafely(context,    "BrowserContext");
        closeSafely(browser,    "Browser");
        closeSafely(playwright, "Playwright");

        log.info("=== Browser tear-down complete ===");
    }

    // -------------------------------------------------------
    // Evidence helpers
    // -------------------------------------------------------

    /**
     * Takes a full-page screenshot and attaches it to the
     * Allure report.
     */
    private void captureScreenshot(String testName) {
        try {
            String timestamp = LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String fileName  = String.format("screenshots/%s_%s.png", testName, timestamp);

            byte[] screenshotBytes = page.screenshot(
                    new Page.ScreenshotOptions()
                            .setFullPage(true)
                            .setPath(Paths.get("target", fileName)));

            Allure.addAttachment(
                    "Screenshot – " + testName,
                    "image/png",
                    new ByteArrayInputStream(screenshotBytes),
                    "png");

            log.info("Screenshot saved: target/{}", fileName);
        } catch (Exception e) {
            log.error("Failed to capture screenshot for test: {}", testName, e);
        }
    }

    /**
     * Saves the Playwright trace zip and attaches it to the
     * Allure report so it can be loaded in the Trace Viewer.
     */
    private void captureTrace(String testName) {
        try {
            String timestamp = LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            Path tracePath = Paths.get(
                    "target", "traces", testName + "_" + timestamp + ".zip");

            context.tracing().stop(
                    new Tracing.StopOptions().setPath(tracePath));

            Allure.addAttachment(
                    "Playwright Trace – " + testName,
                    "application/zip",
                    tracePath.toFile().toURI().toURL().openStream(),
                    "zip");

            log.info("Trace saved: {}", tracePath);
        } catch (Exception e) {
            log.error("Failed to capture trace for test: {}", testName, e);
        }
    }

    /**
     * Silently closes any {@link AutoCloseable} Playwright
     * object, logging the name for diagnostics.
     */
    private void closeSafely(AutoCloseable resource, String name) {
        if (resource != null) {
            try {
                resource.close();
                log.debug("{} closed successfully.", name);
            } catch (Exception e) {
                log.warn("Error closing {}: {}", name, e.getMessage());
            }
        }
    }
}
