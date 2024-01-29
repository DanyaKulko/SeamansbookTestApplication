package app.seamansbook.tests;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import app.seamansbook.tests.adapters.ThemeAdapter;
import app.seamansbook.tests.models.ThemeModel;


public class ThemeSelectionFragment extends Fragment {

    public static class ThemeUtils {
        public static final int THEME_DEFAULT = 0;
        public static final int THEME_LIGHT = 1;
        public static final int THEME_DARK = 2;
    }

    private RecyclerView recyclerView;
    private SharedPreferences sharedPreferences;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_theme_selection, container, false);

        recyclerView = view.findViewById(R.id.themesRecyclerView);
        sharedPreferences = requireActivity().getPreferences(Context.MODE_PRIVATE);

        TextView backButton = view.findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> {
            requireActivity()
                    .getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new SettingsFragment())
                    .commit();
        });

        initializeRecyclerView();

        return view;
    }


    private void initializeRecyclerView() {
        List<ThemeModel> themes = Config.getThemes(requireContext());

        int selectedTheme = loadSelectedTheme();

        //            recreateFragment();
        ThemeAdapter adapter = new ThemeAdapter(themes, selectedTheme, theme -> {
            saveSelectedTheme(theme.getCode());
//            recreateFragment();
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }

    private int loadSelectedTheme() {
        return sharedPreferences.getInt("selectedTheme", ThemeUtils.THEME_DEFAULT);
    }

    private void saveSelectedTheme(int themeCode) {
        sharedPreferences.edit().putInt("selectedTheme", themeCode).apply();

        switch (themeCode) {
            case ThemeUtils.THEME_LIGHT:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            case ThemeUtils.THEME_DARK:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
            case ThemeUtils.THEME_DEFAULT:
            default:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                break;
        }

        recreateFragment();
    }

    public void recreateFragment() {
        if (isAdded()) {
            requireActivity().runOnUiThread(() -> {
                requireActivity()
                        .getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, new ThemeSelectionFragment())
                        .commitAllowingStateLoss();
            });
        }
    }
}