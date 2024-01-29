package app.seamansbook.tests;

import static app.seamansbook.tests.Config.getLanguages;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.skydoves.balloon.ArrowOrientation;
import com.skydoves.balloon.ArrowPositionRules;
import com.skydoves.balloon.Balloon;
import com.skydoves.balloon.BalloonAnimation;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

import app.seamansbook.tests.models.LanguageModel;


public class SettingsFragment extends Fragment {

    private SharedPreferences sharedPreferences;
    public SettingsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        sharedPreferences = requireActivity().getPreferences(Context.MODE_PRIVATE);
        return inflater.inflate(R.layout.fragment_settings, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        CardView tooltipPushNotifications = requireActivity().findViewById(R.id.tooltipPushNotifications);
        CardView tooltipNewsAndUpdates = requireActivity().findViewById(R.id.tooltipNewsAndUpdates);

        tooltipPushNotifications.setOnClickListener(v -> {
            showTooltip(getString(R.string.fragment_settings_hint_one), tooltipPushNotifications);
        });

        tooltipNewsAndUpdates.setOnClickListener(v -> {
            showTooltip(getString(R.string.fragment_settings_hint_two), tooltipNewsAndUpdates);
        });

        initializeLanguageSelection();
        initializeThemeSelection();



    }

    private void showTooltip(String text, View view) {
        Balloon balloon = new Balloon.Builder(requireContext())
                .setArrowSize(10)
                .setArrowOrientation(ArrowOrientation.TOP)
                .setArrowPositionRules(ArrowPositionRules.ALIGN_ANCHOR)
                .setArrowPosition(0.5f)
                .setWidth(200)
                .setPadding(8)
                .setTextSize(12f)
                .setCornerRadius(8f)
                .setAlpha(0.9f)
                .setText(text)
                .setBalloonAnimation(BalloonAnimation.FADE)
                .build();

        balloon.showAlignBottom(view);
    }

    private void initializeLanguageSelection() {
        TextView selectedLanguageTextView = requireActivity().findViewById(R.id.selectedLanguageNameTextView);

        List<LanguageModel> languages = getLanguages();
        String selectedLanguage = loadSelectedLanguage();
        Optional<LanguageModel> matchedLanguage = languages.stream().filter(l -> l.getCode().equals(selectedLanguage)).findFirst();

        String selectedLanguageName;

        if (matchedLanguage.isPresent()) {
            selectedLanguageName = matchedLanguage.get().getName();
        } else {
            selectedLanguageName = "English";
        }

        selectedLanguageTextView.setText(selectedLanguageName.concat(" >"));

        selectedLanguageTextView.setOnClickListener(v -> {
            Fragment statisticsFragment = new LanguageSelectionFragment();
            requireActivity().getSupportFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.fragment_container, statisticsFragment).commit();
        });
    }

    private void initializeThemeSelection() {
        TextView selectedThemeNameTextView = requireActivity().findViewById(R.id.selectedThemeNameTextView);

        selectedThemeNameTextView.setOnClickListener(v -> {
            Fragment themeSelectionFragment = new ThemeSelectionFragment();
            requireActivity().getSupportFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.fragment_container, themeSelectionFragment).commit();
        });

        int theme = sharedPreferences.getInt("selectedTheme", ThemeSelectionFragment.ThemeUtils.THEME_DEFAULT);
        String themeName = Config.getThemes(requireContext()).stream().filter(t -> t.getCode() == theme).findFirst().get().getName();
        selectedThemeNameTextView.setText(themeName.concat(" >"));
    }


    private String loadSelectedLanguage() {
        String language = sharedPreferences.getString("selected_language", "null");

        List<LanguageModel> availableLanguages = getLanguages();

        if (language.equals("null")) {
            String defaultLanguage = Locale.getDefault().getLanguage();
            if (availableLanguages.stream().anyMatch(l -> l.getCode().equals(defaultLanguage))) {
                return defaultLanguage;
            }
            return "en";
        }

        return language;
    }

}