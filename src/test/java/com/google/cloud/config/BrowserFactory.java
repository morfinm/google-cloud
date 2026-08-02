package com.google.cloud.config;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Playwright;
import lombok.extern.slf4j.Slf4j;

/**
 * BrowserFactory
 * ============================================================
 * Responsible for creating {@link Browser} instances based on
 * the {@code -Dbrowser} and {@code -Dheadless} system
 * properties.
 *
 * <p>Supported browser values (case-insensitive):
 * <ul>
 *   <li>{@code chromium} (default)</li>
 *   <li>{@code firefox}</li>
 *   <li>{@code webkit}</li>
 * </ul>
 * ============================================================
 */
@Slf4j
public class BrowserFactory {

    private static final String BROWSER_PROPERTY  = "browser";
    private static final String HEADLESS_PROPERTY = "headless";
    private static final String DEFAULT_BROWSER   = "chromium";

    // Private constructor – static utility class
    private BrowserFactory() {}

    /**
     * Creates and returns a {@link Browser} instance.
     *
     * @param playwright the active {@link Playwright} instance
     * @return configured {@link Browser}
     */
    public static Browser createBrowser(Playwright playwright) {
        String  browserName = System.getProperty(BROWSER_PROPERTY, DEFAULT_BROWSER).toLowerCase();
        boolean headless    = Boolean.parseBoolean(
                System.getProperty(HEADLESS_PROPERTY, "true"));

        log.info("Launching browser: {} | headless: {}", browserName, headless);

        BrowserType.LaunchOptions launchOptions = new BrowserType.LaunchOptions()
                .setHeadless(headless);

        return switch (browserName) {
            case "firefox" -> playwright.firefox().launch(launchOptions);
            case "webkit"  -> playwright.webkit().launch(launchOptions);
            default        -> playwright.chromium().launch(launchOptions);
        };
    }
}
