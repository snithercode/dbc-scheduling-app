package helper;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Manages translations and locale-specific settings for the application
 */
public class LocalizationService {

    /**
     * Resource bundle containing loaded translations.
     */
    private final ResourceBundle bundle;

    /**
     * Initializes resource bundle based on the default system locale.
     */
    public LocalizationService() {
        Locale currentLocale = Locale.getDefault();
        this.bundle = ResourceBundle.getBundle("lang.common", currentLocale);
    }

    /**
     * Gets a translated string for a key from the resource bundle.
     *
     * @param key The key for the string translation
     * @return The translated string.
     */
    public String getTranslation(String key) {
        return bundle.getString(key);
    }

    /**
     * Returns the resource bundle used for localization.
     *
     * @return The loaded ResourceBundle.
     */
    public ResourceBundle getBundle() {
        return this.bundle;
    }
}
