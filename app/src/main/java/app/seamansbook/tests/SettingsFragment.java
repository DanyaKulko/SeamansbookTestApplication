package app.seamansbook.tests;

import static app.seamansbook.tests.Config.getLanguages;
import static app.seamansbook.tests.MainActivity.expandClickArea;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.SwitchCompat;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.skydoves.balloon.ArrowOrientation;
import com.skydoves.balloon.ArrowPositionRules;
import com.skydoves.balloon.Balloon;
import com.skydoves.balloon.BalloonAnimation;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

import app.seamansbook.tests.data.QuestionScoresDBManager;
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
        expandClickArea(tooltipPushNotifications, 80);

        tooltipNewsAndUpdates.setOnClickListener(v -> {
            showTooltip(getString(R.string.fragment_settings_hint_two), tooltipNewsAndUpdates);
        });
        expandClickArea(tooltipNewsAndUpdates, 80);


        TextView clear_statistics = requireActivity().findViewById(R.id.clear_statistics);
        TextView clear_favorites = requireActivity().findViewById(R.id.clear_favorites);

        expandClickArea(clear_statistics, 40);
        expandClickArea(clear_favorites, 40);

        clear_statistics.setOnClickListener(v -> showConfirmDialog("statistics"));
        clear_favorites.setOnClickListener(v -> showConfirmDialog("favorites"));

        SwitchCompat disableNewsAndUpdatesSwitch = requireActivity().findViewById(R.id.disableNewsAndUpdatesSwitch);
        SwitchCompat disablePushNotificationsSwitch = requireActivity().findViewById(R.id.disablePushNotificationsSwitch);

        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("seamansbookMain", Context.MODE_PRIVATE);
        boolean disableNewsAndUpdates = sharedPreferences.getBoolean("disableNewsAndUpdates", false);
        boolean disablePushNotifications = sharedPreferences.getBoolean("disablePushNotifications", false);

        disableNewsAndUpdatesSwitch.setChecked(disableNewsAndUpdates);
        disablePushNotificationsSwitch.setChecked(disablePushNotifications);

        disableNewsAndUpdatesSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("disableNewsAndUpdates", isChecked);
            editor.apply();
        });

        disablePushNotificationsSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("disablePushNotifications", isChecked);
            editor.apply();
        });


        initializeLanguageSelection();
        initializeThemeSelection();



    }

    private void showConfirmDialog(String type) {
        Dialog settings_dialog = new Dialog(requireContext());
        Objects.requireNonNull(settings_dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        settings_dialog.setContentView(R.layout.dialog_settings_clear_data);
        AppCompatButton continueButton = settings_dialog.findViewById(R.id.continueButton);

        continueButton.setOnClickListener(v2 -> settings_dialog.dismiss());

        AppCompatButton dropDataButton = settings_dialog.findViewById(R.id.dropDataButton);
        dropDataButton.setOnClickListener(v2 -> {
            if(type.equals("statistics")) {
                QuestionScoresDBManager scoresDBManager = new QuestionScoresDBManager(requireContext());
                scoresDBManager.clearStatistics();
                Toast.makeText(requireContext(), getString(R.string.statistics_cleared), Toast.LENGTH_SHORT).show();
            } else {
                SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("seamansbookMain", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putStringSet("favorite_questions", new HashSet<>());
                editor.apply();
                Toast.makeText(requireContext(), getString(R.string.favorites_cleared), Toast.LENGTH_SHORT).show();
            }
            settings_dialog.dismiss();
        });

        ImageView close_button = settings_dialog.findViewById(R.id.close_button);
        close_button.setOnClickListener(v2 -> settings_dialog.dismiss());

        TextView dialogTitle = settings_dialog.findViewById(R.id.dialog_title);
        TextView dialogDescription = settings_dialog.findViewById(R.id.dialog_description);

        if(type.equals("statistics")) {
            dialogTitle.setText(getString(R.string.clear_statistics));
            dialogDescription.setText(getString(R.string.clear_statistics_description));
        } else {
            dialogTitle.setText(getString(R.string.clear_favorites));
            dialogDescription.setText(getString(R.string.clear_favorites_description));
        }
        settings_dialog.show();
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