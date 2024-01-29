package app.seamansbook.tests;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;

import app.seamansbook.tests.interfaces.BottomNavigationController;

import android.content.Intent;
import android.util.Log;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Locale;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements BottomNavigationController {
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("SELECTED_NAV_ITEM", bottomNavigationView.getSelectedItemId());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setUpLocaleAndTheme();

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;

            switch (item.getItemId()) {
                case R.id.action_home:
                    selectedFragment = new Main();
                    break;
                case R.id.action_statistics:
                    selectedFragment = new StatisticsFragment();
                    break;
                case R.id.action_search:
                    selectedFragment = new SearchFragment();
                    break;
                case R.id.action_favorite:
                    selectedFragment = new FavoritesFragment();
                    break;
                case R.id.action_settings:
                    selectedFragment = new SettingsFragment();
                    break;
            }

            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
            }

            return true;
        });


        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("OPEN_FRAGMENT")) {
            String fragmentId = intent.getStringExtra("OPEN_FRAGMENT");
            if (Objects.equals(fragmentId, "FAVORITES")) {
                bottomNavigationView.setSelectedItemId(R.id.action_favorite);
                Fragment selectedFragment = new FavoritesFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
            }
        } else if (savedInstanceState != null) {
            int selectedItemId = savedInstanceState.getInt("SELECTED_NAV_ITEM", R.id.action_home);
            bottomNavigationView.setSelectedItemId(selectedItemId);
        } else {
            bottomNavigationView.setSelectedItemId(R.id.action_home);
        }
    }

    private void setUpLocaleAndTheme() {
        SharedPreferences sharedPreferences = getPreferences(Context.MODE_PRIVATE);
        try {
            int themeCode = sharedPreferences.getInt("selectedTheme", -1);
            if (themeCode != -1) {
                switch (themeCode) {
                    case ThemeSelectionFragment.ThemeUtils.THEME_LIGHT:
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                        break;
                    case ThemeSelectionFragment.ThemeUtils.THEME_DARK:
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                        break;
                    case ThemeSelectionFragment.ThemeUtils.THEME_DEFAULT:
                    default:
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                        break;
                }
            }
        } catch (ClassCastException e) {
            Log.d("ThemeSelectionFragment", "Error setting theme: " + e.getMessage());
            e.printStackTrace();
        }

        String language = sharedPreferences.getString("selected_language", "null");
        if (!Objects.equals(language, "null")) {
            Resources res = getResources();
            Configuration configuration = res.getConfiguration();
            Locale newLocale = new Locale(language);
            configuration.setLocale(newLocale);
            res.updateConfiguration(configuration, res.getDisplayMetrics());
        }
    }


    @Override
    public void onItemSelected(int itemId) {
        bottomNavigationView.setSelectedItemId(itemId);
    }
}