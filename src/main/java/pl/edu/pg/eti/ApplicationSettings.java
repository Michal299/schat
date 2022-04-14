package pl.edu.pg.eti;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.FileBasedConfiguration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApplicationSettings {

    private static ApplicationSettings _instance;
    private static final Logger log = LoggerFactory.getLogger(ApplicationSettings.class);

    private final FileBasedConfigurationBuilder<FileBasedConfiguration> configurationBuilder;
    private final Configuration configuration;

    private ApplicationSettings(final String settingsPath) throws ConfigurationException {
        configurationBuilder = new FileBasedConfigurationBuilder<FileBasedConfiguration>(PropertiesConfiguration.class)
                .configure(new Parameters().properties().setFileName(settingsPath));
        configuration = configurationBuilder.getConfiguration();
    }

    public static ApplicationSettings getInstance() {
        if (_instance == null) {
            throw new IllegalStateException("Application setting are not initialized");
        }
        return _instance;
    }

    public static void initialize(final String settingsFilePath) {
        try {
            _instance = new ApplicationSettings(settingsFilePath);
        } catch (ConfigurationException e) {
            e.printStackTrace();
        }
    }

    public static void flushSettings() {
        _instance.saveConfiguration();
    }

    private void saveConfiguration() {
        try {
            configurationBuilder.save();
        } catch (ConfigurationException e) {
            e.printStackTrace();
        }
    }

    public String getProperty(final String propertyName, final String defaultValue) {
        return configuration.getString(propertyName, defaultValue);
    }

    public void setProperty(final String propertyName, final String value) {
        configuration.setProperty(propertyName, value);
    }
}
