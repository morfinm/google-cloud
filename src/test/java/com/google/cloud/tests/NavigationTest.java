package com.google.cloud.tests;

import com.google.cloud.base.BaseTest;
import com.google.cloud.pages.HomePage;
import com.google.cloud.pages.PricingPage;
import io.qameta.allure.*;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

/**
 * NavigationTest
 * ============================================================
 * Verifies core navigation flows on the Google Cloud website.
 *
 * <p>Test coverage:
 * <ul>
 *   <li>TC-001 – Navigate from the Home page to the Pricing
 *       page via the top navigation bar and confirm the
 *       browser lands on the correct URL.</li>
 * </ul>
 * ============================================================
 */
@Slf4j
@Epic("Google Cloud Website")
@Feature("Top Navigation")
public class NavigationTest extends BaseTest {

    // -------------------------------------------------------
    // TC-001
    // -------------------------------------------------------

    /**
     * TC-001: Verify navigation to the Pricing page.
     *
     * <p>Steps:
     * <ol>
     *   <li>Open https://cloud.google.com/</li>
     *   <li>Click the "Pricing" link in the top nav bar</li>
     *   <li>Assert that the URL contains "/pricing"</li>
     *   <li>Assert that the page title contains "Pricing"</li>
     * </ol>
     */
    @Test(
        testName = "TC-001: Navigate to Pricing page via top navigation",
        description = "Verifies that clicking 'Pricing' in the top navigation bar "
                    + "redirects the user to the Pricing page.",
        groups = {"smoke", "navigation"}
    )
    @Story("User navigates to the Pricing page")
    @Severity(SeverityLevel.CRITICAL)
    @Description(
        "Opens the Google Cloud home page, clicks the 'Pricing' menu item "
      + "in the top navigation bar, and verifies that the browser URL and "
      + "page title both reflect the Pricing page."
    )
    public void shouldNavigateToPricingPage() {

        log.info("▶ TC-001 – Navigate to Pricing page via top navigation");

        // ── Step 1: Open the Google Cloud home page ──────────────────────
        String baseUrl = config.get("base.url");
        HomePage homePage = new HomePage(page);
        homePage.open(baseUrl);
        log.info("✔ Step 1 complete – Home page opened at: {}", baseUrl);

        // ── Step 2: Click the Pricing navigation link ────────────────────
        PricingPage pricingPage = homePage.clickPricing();
        log.info("✔ Step 2 complete – Clicked 'Pricing' nav link");

        // ── Step 3 & 4: Wait for the page to load and assert navigation ──
        pricingPage.waitForPageToLoad();

        String currentUrl   = pricingPage.getUrl();
        String currentTitle = pricingPage.getTitle();

        log.info("Current URL  : {}", currentUrl);
        log.info("Current Title: {}", currentTitle);

        // Use SoftAssert so both assertions are always evaluated
        SoftAssert softAssert = new SoftAssert();

        softAssert.assertTrue(
            currentUrl.contains(PricingPage.EXPECTED_URL_FRAGMENT),
            String.format(
                "Expected URL to contain '%s' but was: '%s'",
                PricingPage.EXPECTED_URL_FRAGMENT, currentUrl)
        );

        softAssert.assertTrue(
            currentTitle.contains(PricingPage.EXPECTED_TITLE_FRAGMENT),
            String.format(
                "Expected page title to contain '%s' but was: '%s'",
                PricingPage.EXPECTED_TITLE_FRAGMENT, currentTitle)
        );

        softAssert.assertAll();

        log.info("✔ TC-001 PASSED – Successfully navigated to the Pricing page.");
    }
}
