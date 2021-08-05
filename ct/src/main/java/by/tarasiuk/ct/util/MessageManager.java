package by.tarasiuk.ct.util;

import java.util.ResourceBundle;

/**
 * Message manager, that helps with tags localization.
 */
public class MessageManager {
    private static final ResourceBundle baseLocale = ResourceBundle.getBundle("locale");
    private static final ResourceBundle ruRu = ResourceBundle.getBundle("locale_ru_RU");
    private static final ResourceBundle enUs = ResourceBundle.getBundle("locale_en_US");

    /**
     * Private constructor to prevent class object creation.
     */
    private MessageManager() {
    }

    public static String findMassage(String key, String locale) {
        String message;

        switch (locale.toUpperCase()) {
            case "RU_RU":
                message = ruRu.getString(key);
                break;
            case "EN_US":
                message = enUs.getString(key);
                break;
            default:
                message = baseLocale.getString(key);
                break;
        }

        return message;
    }
}
