package com.google.cloud.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;

/**
 * PricingPage
 * ============================================================
 * Page Object representing the Google Cloud Pricing page
 * ( https://cloud.google.com/pricing ).
 *
 * <p>Exposes verification helpers that tests use to assert
 * the page loaded correctly, keeping assertion logic and
 * selectors out of the test class itself.
 * ============================================================
 */
@Slf4j
public class PricingPage extends BasePage {

    // -------------------------------------------------------
    // Expected constants
    // -------------------------------------------------------

    /** Substring expected to be present in the page URL. */
    public static final String EXPECTED_URL_FRAGMENT = "/pricing";

    /**
     * Substring expected to be present in the {@code <title>}
     * element of the Pricing page.
     */
    public static final String EXPECTED_TITLE_FRAGMENT = "Pricing";

    // -------------------------------------------------------
    // Locators
    // -------------------------------------------------------

    /**
     * The main {@code <h1>} heading of the Pricing page.
     * Used as a landmark to confirm the page rendered.
     */
    private final Locator pageHeading;

    // -------------------------------------------------------
    // Constructor
    // -------------------------------------------------------

    public PricingPage(Page page) {
        super(page);
        // Target the first visible h1 on the page
        this.pageHeading = page.locator("h1").first();
    }

    // -------------------------------------------------------
    // Verification helpers
    // -------------------------------------------------------

    /**
     * Waits until the Pricing page heading is visible,
     * confirming the page has fully loaded.
     *
     * @return this {@link PricingPage} (fluent API)
     */
    @Step("Wait for the Pricing page to fully load")
    public PricingPage waitForPageToLoad() {
        log.info("Waiting for Pricing page heading to be visible");
        waitForVisible(pageHeading, "Pricing page h1 heading");
        return this;
    }

    /**
     * Returns the current browser URL so the test can assert
     * that navigation reached the Pricing page.
     *
     * @return current URL as a {@link String}
     */
    @Step("Get current page URL")
    public String getUrl() {
        return getCurrentUrl();
    }

    /**
     * Returns the current page title so the test can assert
     * the document title reflects the Pricing page.
     *
     * @return current page title as a {@link String}
     */
    @Step("Get current page title")
    public String getTitle() {
        return getPageTitle();
    }

    /**
     * Convenience method: returns {@code true} when the
     * browser URL contains the expected Pricing path fragment.
     *
     * @return {@code true} if the URL contains "/pricing"
     */
    @Step("Verify URL contains the pricing path fragment")
    public boolean isOnPricingPage() {
        String currentUrl = getCurrentUrl();
        boolean result = currentUrl.contains(EXPECTED_URL_FRAGMENT);
        log.info("URL '{}' contains '{}': {}", currentUrl, EXPECTED_URL_FRAGMENT, result);
        return result;
    }
}
