package app.seamansbook.tests;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

@SuppressLint("CustomSplashScreen")
public class SplashScreenActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // sleep for 2 seconds
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        SharedPreferences preferences = getSharedPreferences("seamansbookMain", MODE_PRIVATE);
        String currentValue = preferences.getString("version", "0.0");
        boolean isLoggedIn = !preferences.getString("userId", "").equals("");

        Intent intent;

        if(!isLoggedIn) {
            intent = new Intent(this, LoginActivity.class);
        } else if(currentValue.equals("0.0")) {
            intent = new Intent(this, DownloadQuestionsActivity.class);
        } else {
            intent = new Intent(this, MainActivity.class);
        }


        startActivity(intent);
        finish();
    }
}
