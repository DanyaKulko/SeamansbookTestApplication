package app.seamansbook.tests;

import android.content.Context;

import java.util.Arrays;
import java.util.List;

import app.seamansbook.tests.models.LanguageModel;
import app.seamansbook.tests.models.ThemeModel;

public class Config {
    public static final String API_URL = "https://api.seamansbook.app/";

    public static final String QUESTIONS_NUMBER = "71";

    public static final String APP_KEY = "658aee2e78df64df1cf70169";
//    public static final String APP_KEY = "658adb6378df64df1cf70050";


    public static List<LanguageModel> getLanguages() {
        return Arrays.asList(
                new LanguageModel("en", "English"),
                new LanguageModel("ru", "Русский")
        );
    }

    public static List<ThemeModel> getThemes(Context context) {
        return Arrays.asList(
                new ThemeModel(ThemeSelectionFragment.ThemeUtils.THEME_DEFAULT, context.getString(R.string.fragment_settings__theme_default)),
                new ThemeModel(ThemeSelectionFragment.ThemeUtils.THEME_LIGHT, context.getString(R.string.fragment_settings__theme_light)),
                new ThemeModel(ThemeSelectionFragment.ThemeUtils.THEME_DARK, context.getString(R.string.fragment_settings__theme_dark))
        );
    }
}