package com.google.cloud.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;

/**
 * HomePage
 * ============================================================
 * Page Object representing the Google Cloud home page
 * ( https://cloud.google.com/ ).
 *
 * <p>Encapsulates all element locators and actions that a
 * test may perform on this page, keeping selectors away from
 * test code and making future selector changes a one-line fix.
 * ============================================================
 */
@Slf4j
public class HomePage extends BasePage {

    // -------------------------------------------------------
    // Locators
    // -------------------------------------------------------

    /**
     * The "Pricing" link in the top navigation bar.
     *
     * <p>Strategy: locate the nav link by its exact visible
     * text, which is stable across layout changes.
     */
    private final Locator pricingNavLink;

    // -------------------------------------------------------
    // Constructor
    // -------------------------------------------------------

    public HomePage(Page page) {
        super(page);
        // Targets the top-nav anchor whose visible text is exactly "Pricing"
        this.pricingNavLink = page.getByRole(
                com.microsoft.playwright.options.AriaRole.LINK,
                new Page.GetByRoleOptions().setName("Pricing").setExact(true));
    }

    // -------------------------------------------------------
    // Actions
    // -------------------------------------------------------

    /**
     * Opens the Google Cloud home page.
     *
     * @param baseUrl the base URL from configuration
     * @return this {@link HomePage} (fluent API)
     */
    @Step("Open Google Cloud home page")
    public HomePage open(String baseUrl) {
        log.info("Opening Google Cloud home page: {}", baseUrl);
        navigateTo(baseUrl);
        return this;
    }

    /**
     * Clicks the Pricing link in the top navigation bar.
     *
     * @return a new {@link PricingPage} representing the page
     *         the browser will land on after the click
     */
    @Step("Click 'Pricing' in the top navigation bar")
    public PricingPage clickPricing() {
        log.info("Clicking the 'Pricing' navigation link");
        click(pricingNavLink, "Pricing nav link");
        return new PricingPage(page);
    }
}
