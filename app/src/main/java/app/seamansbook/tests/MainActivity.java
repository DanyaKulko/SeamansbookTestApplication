package app.seamansbook.tests;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.util.Log;

import app.seamansbook.tests.interfaces.BottomNavigationController;

import android.content.Intent;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Objects;

public class MainActivity extends AppCompatActivity implements BottomNavigationController {
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;

            Log.d("MainActivity", "onNavigationItemSelected: " + item.getItemId() + " " + R.id.action_home);
            switch (item.getItemId()) {
                case R.id.action_home:
                    selectedFragment = new Main();
                    break;
                case R.id.action_statistics:
                    selectedFragment = new StatisticsFragment();
                    break;
                case R.id.action_search:
//                    selectedFragment = new SearchFragment();
                    Toast.makeText(this, "Coming soon", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.action_favorite:
                    selectedFragment = new FavoritesFragment();
                    break;
//                case R.id.action_settings:
//                    selectedFragment = new SettingsFragment();
//                    break;
            }

            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
            }

            return true;
        });


        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("OPEN_FRAGMENT")) {
            String fragmentId = intent.getStringExtra("OPEN_FRAGMENT");
            if (Objects.equals(fragmentId, "STATISTICS")) {
                bottomNavigationView.setSelectedItemId(R.id.action_statistics);
                Fragment selectedFragment = new StatisticsFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
            }
        } else {
            bottomNavigationView.setSelectedItemId(R.id.action_home);
        }

//        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
//        if (account != null) {
//            String name = account.getDisplayName();
//        }
    }

    @Override
    public void onItemSelected(int itemId) {
        bottomNavigationView.setSelectedItemId(itemId);
    }

}