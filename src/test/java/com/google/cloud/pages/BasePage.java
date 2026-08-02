package com.google.cloud.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.WaitForSelectorState;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;

/**
 * BasePage
 * ============================================================
 * Abstract base class for all Page Object classes.
 *
 * <p>Provides a thin, re-usable layer of Playwright action
 * wrappers (click, type, navigate, wait, …) with built-in
 * logging and Allure {@code @Step} annotations so every
 * interaction is visible in the HTML report.
 *
 * <p>Concrete page objects extend this class and receive the
 * {@link Page} instance via the constructor.
 * ============================================================
 */
@Slf4j
public abstract class BasePage {

    /** The Playwright {@link Page} driving the browser tab. */
    protected final Page page;

    // -------------------------------------------------------
    // Constructor
    // -------------------------------------------------------

    protected BasePage(Page page) {
        this.page = page;
    }

    // -------------------------------------------------------
    // Navigation
    // -------------------------------------------------------

    /**
     * Navigates to the given URL and waits until the network
     * is idle (suitable for most SPAs and static sites).
     *
     * @param url full URL to navigate to
     */
    @Step("Navigate to: {url}")
    protected void navigateTo(String url) {
        log.info("Navigating to: {}", url);
        page.navigate(url, new Page.NavigateOptions()
                .setWaitUntil(com.microsoft.playwright.options.WaitUntilState.DOMCONTENTLOADED));
    }

    // -------------------------------------------------------
    // Interactions
    // -------------------------------------------------------

    /**
     * Waits for the element to be visible, then clicks it.
     *
     * @param locator the target element locator
     * @param description human-readable description for logs
     */
    @Step("Click on: {description}")
    protected void click(Locator locator, String description) {
        log.info("Clicking: {}", description);
        locator.waitFor(new Locator.WaitForOptions()
                .setState(WaitForSelectorState.VISIBLE));
        locator.click();
    }

    /**
     * Waits for the element to be visible, then types text.
     *
     * @param locator     the target input locator
     * @param text        text to type
     * @param description human-readable description for logs
     */
    @Step("Type '{text}' into: {description}")
    protected void typeText(Locator locator, String text, String description) {
        log.info("Typing '{}' into: {}", text, description);
        locator.waitFor(new Locator.WaitForOptions()
                .setState(WaitForSelectorState.VISIBLE));
        locator.fill(text);
    }

    // -------------------------------------------------------
    // Waits & state queries
    // -------------------------------------------------------

    /**
     * Returns the current page URL.
     *
     * @return current URL string
     */
    public String getCurrentUrl() {
        String url = page.url();
        log.debug("Current URL: {}", url);
        return url;
    }

    /**
     * Returns the current page title.
     *
     * @return page title string
     */
    public String getPageTitle() {
        String title = page.title();
        log.debug("Page title: {}", title);
        return title;
    }

    /**
     * Waits for the given locator to become visible within
     * the default timeout.
     *
     * @param locator     the element to wait for
     * @param description human-readable description for logs
     */
    @Step("Wait for element to be visible: {description}")
    protected void waitForVisible(Locator locator, String description) {
        log.info("Waiting for element to be visible: {}", description);
        locator.waitFor(new Locator.WaitForOptions()
                .setState(WaitForSelectorState.VISIBLE));
    }

    /**
     * Checks whether a given element is visible on the page.
     *
     * @param locator the element locator to check
     * @return {@code true} if the element is visible
     */
    protected boolean isVisible(Locator locator) {
        return locator.isVisible();
    }
}
