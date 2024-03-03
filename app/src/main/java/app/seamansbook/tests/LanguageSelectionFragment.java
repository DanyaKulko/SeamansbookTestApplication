package app.seamansbook.tests;

import static app.seamansbook.tests.Config.getLanguages;
import static app.seamansbook.tests.MainActivity.expandClickArea;
import static app.seamansbook.tests.MainActivity.subscribeToTopic;
import static app.seamansbook.tests.MainActivity.unsubscribeFromTopic;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Locale;

import app.seamansbook.tests.adapters.LanguageAdapter;
import app.seamansbook.tests.models.LanguageModel;

public class LanguageSelectionFragment extends Fragment {

    private RecyclerView recyclerView;
    private SharedPreferences sharedPreferences;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_language_selection, container, false);
        recyclerView = view.findViewById(R.id.languagesRecyclerView);
        sharedPreferences = requireActivity().getPreferences(Context.MODE_PRIVATE);

        TextView backButton = view.findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> {
            requireActivity()
                    .getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new SettingsFragment())
                    .commit();
        });
        expandClickArea(backButton, 100);

        initializeRecyclerView();
        return view;
    }

    private void initializeRecyclerView() {
        List<LanguageModel> languages = getLanguages();
        String selectedLanguage = loadSelectedLanguage();

        LanguageAdapter adapter = new LanguageAdapter(languages, selectedLanguage, language -> {
            saveSelectedLanguage(language.getCode());
            for (LanguageModel languageModel : languages) {
                unsubscribeFromTopic(languageModel.getCode());
            }
            subscribeToTopic(language.getCode());
            recreateFragment();
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
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

    private void saveSelectedLanguage(String languageCode) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("selected_language", languageCode);
        editor.apply();

        Resources res = requireActivity().getResources();
        Configuration configuration = res.getConfiguration();
        Locale newLocale = new Locale(languageCode);
        configuration.setLocale(newLocale);
        res.updateConfiguration(configuration, res.getDisplayMetrics());
    }

    public void recreateFragment() {
        requireActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, new LanguageSelectionFragment())
                .commit();
    }
}
