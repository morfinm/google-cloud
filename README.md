# Google Cloud – Playwright Java Test Automation Framework

> End-to-end UI test automation for [Google Cloud](https://cloud.google.com/)  
> built with **Playwright for Java** and **TestNG**, with **Allure** reporting.

---

## 📁 Project Structure

```
playwright-java-framework/
├── pom.xml                                          # Maven build & dependency management
├── .gitignore
├── ci/
│   └── playwright-ci.yml                           # GitHub Actions CI workflow
└── src/
    └── test/
        ├── java/
        │   └── com/google/cloud/
        │       ├── config/
        │       │   ├── BrowserFactory.java          # Creates Playwright Browser instances
        │       │   └── ConfigLoader.java            # Reads .properties config per environment
        │       ├── base/
        │       │   └── BaseTest.java                # TestNG lifecycle: setup/teardown, screenshots, traces
        │       ├── pages/
        │       │   ├── BasePage.java                # Shared Playwright action wrappers (click, type, …)
        │       │   ├── HomePage.java                # Page Object – https://cloud.google.com/
        │       │   └── PricingPage.java             # Page Object – https://cloud.google.com/pricing
        │       └── tests/
        │           └── NavigationTest.java          # TC-001: Navigate to Pricing page
        └── resources/
            ├── config/
            │   ├── prod.properties                  # Production environment config
            │   └── staging.properties               # Staging environment config
            ├── testng/
            │   └── smoke-suite.xml                  # TestNG suite descriptor
            └── logback-test.xml                     # Logback logging configuration
```

---

## 🚀 Quick Start

### Prerequisites

| Tool | Version |
|------|---------|
| Java (JDK) | 17+ |
| Maven | 3.9+ |
| Git | any recent |

### 1 – Clone the repository

```bash
git clone https://github.com/<your-org>/git-google-cloud.git
cd git-google-cloud
```

### 2 – Install Playwright browser binaries

```bash
mvn exec:java -e \
  -D exec.mainClass=com.microsoft.playwright.CLI \
  -D exec.args="install --with-deps"
```

### 3 – Run the smoke suite

```bash
# Default: Chromium, headless, production environment
mvn test
```

---

## ⚙️ Runtime Options

All options are passed as Maven system properties (-D):

| Property | Default | Options |
|----------|---------|---------|
| browser | chromium | chromium, firefox, webkit |
| headless | true | true, false |
| env | prod | prod, staging |
| suite | smoke-suite.xml | any TestNG XML path |

### Examples

```bash
# Run in Firefox, headed mode, against staging
mvn test -Dbrowser=firefox -Dheadless=false -Denv=staging

# Run with WebKit
mvn test -Dbrowser=webkit

# Run a specific suite
mvn test -Dsuite=src/test/resources/testng/smoke-suite.xml
```

---

## 🧪 Test Cases

### TC-001 – Navigate to Pricing Page

| Property | Value |
|----------|-------|
| ID | TC-001 |
| Feature | Top Navigation |
| Severity | Critical |
| Groups | smoke, navigation |

Steps:
1. Open https://cloud.google.com/
2. Click the Pricing link in the top navigation bar
3. Assert the browser URL contains /pricing
4. Assert the page title contains Pricing

---

## 🏗️ Framework Architecture

### Design Patterns

| Pattern | Purpose |
|---------|---------|
| Page Object Model (POM) | Encapsulates selectors and actions per page |
| Base Test | Central TestNG lifecycle (setup, teardown, evidence capture) |
| Factory (BrowserFactory) | Creates the right Browser instance from -Dbrowser |
| Config Loader | Reads per-environment .properties files via -Denv |
| Fluent API | Page methods return page objects to chain steps naturally |

### Evidence on Failure

When a test fails, BaseTest automatically:
- Captures a full-page screenshot → target/screenshots/
- Saves a Playwright trace (.zip) → target/traces/
- Attaches both to the Allure report

---

## 📊 Allure Report

### Generate locally

```bash
# Run tests first, then generate the HTML report
mvn test
mvn allure:report

# Open the report in your default browser
mvn allure:serve
```

The HTML report is written to target/allure-report/index.html.

---

## 🔄 CI / CD (GitHub Actions)

The workflow file is located at ci/playwright-ci.yml.

Note: To activate GitHub Actions, copy the file to .github/workflows/playwright-ci.yml.

### What the pipeline does

1. Triggers on every push / PR to main or feature/** branches
2. Matrix strategy – runs tests in parallel on Chromium, Firefox, and WebKit
3. Uploads artefacts (Allure results, screenshots, traces, logs) on every run
4. Publishes a combined Allure HTML report to GitHub Pages on merges to main

### Run headless locally (same as CI)

```bash
mvn test -Dheadless=true
```

---

## 🌐 Adding a New Environment

1. Create src/test/resources/config/<env-name>.properties
2. Set base.url and other properties
3. Run with -Denv=<env-name>

---

## 📝 Logging

Logs are written to:
- Console – coloured, INFO-level and above
- File – target/logs/test-run.log (7-day rolling)

---

## 🤝 Contributing

1. Create a feature branch: git checkout -b feature/my-new-test
2. Add your page objects in src/test/java/com/google/cloud/pages/
3. Add your test class in src/test/java/com/google/cloud/tests/
4. Register the class in the appropriate TestNG XML suite
5. Open a Pull Request against main