package com.google.cloud.config;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * ConfigLoader
 * ============================================================
 * Singleton utility that loads the correct {@code .properties}
 * file from {@code src/test/resources/config/<env>.properties}
 * based on the {@code -Denv} system property (defaults to
 * {@code prod}).
 *
 * <p>Usage:
 * <pre>{@code
 *   String baseUrl = ConfigLoader.getInstance().get("base.url");
 * }</pre>
 * ============================================================
 */
@Slf4j
public class ConfigLoader {

    private static final String ENV_PROPERTY = "env";
    private static final String DEFAULT_ENV  = "prod";
    private static final String CONFIG_PATH  = "config/%s.properties";

    /** Lazy-initialised singleton holder. */
    private static ConfigLoader instance;

    private final Properties properties = new Properties();

    // -------------------------------------------------------
    // Constructor – private to enforce singleton
    // -------------------------------------------------------
    private ConfigLoader() {
        String env          = System.getProperty(ENV_PROPERTY, DEFAULT_ENV);
        String resourcePath = String.format(CONFIG_PATH, env);

        log.info("Loading configuration from: {}", resourcePath);

        try (InputStream is = getClass().getClassLoader().getResourceAsStream(resourcePath)) {
            if (is == null) {
                throw new IllegalStateException(
                        "Configuration file not found on classpath: " + resourcePath);
            }
            properties.load(is);
            log.info("Configuration loaded successfully for environment: {}", env);
        } catch (IOException e) {
            throw new IllegalStateException(
                    "Failed to load configuration file: " + resourcePath, e);
        }
    }

    // -------------------------------------------------------
    // Public API
    // -------------------------------------------------------

    /**
     * Returns the singleton instance (thread-safe, double-checked locking).
     */
    public static synchronized ConfigLoader getInstance() {
        if (instance == null) {
            instance = new ConfigLoader();
        }
        return instance;
    }

    /**
     * Retrieves a configuration value by key.
     *
     * @param key the property key
     * @return the property value
     * @throws IllegalArgumentException if the key is not found
     */
    public String get(String key) {
        String value = properties.getProperty(key);
        if (value == null) {
            throw new IllegalArgumentException("Property not found in configuration: " + key);
        }
        return value;
    }

    /**
     * Retrieves a configuration value by key, returning a
     * default value if the key is absent.
     *
     * @param key          the property key
     * @param defaultValue fallback value
     * @return the property value or defaultValue
     */
    public String get(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    /**
     * Retrieves a configuration value as an {@code int}.
     *
     * @param key the property key
     * @return the parsed integer value
     */
    public int getInt(String key) {
        return Integer.parseInt(get(key));
    }

    /**
     * Retrieves a configuration value as a {@code boolean}.
     *
     * @param key the property key
     * @return the parsed boolean value
     */
    public boolean getBoolean(String key) {
        return Boolean.parseBoolean(get(key));
    }
}
