package app.seamansbook.tests;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.snackbar.Snackbar;

import app.seamansbook.tests.interfaces.ApiInterface;
import app.seamansbook.tests.models.CreateUserRequest;
import app.seamansbook.tests.models.CreateUserResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 1000;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        preferences = getSharedPreferences("seamansbookMain", MODE_PRIVATE);
        boolean isLoggedIn = !preferences.getString("userId", "").equals("");

        if (isLoggedIn) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }

        SignInButton loginButton = findViewById(R.id.loginButton);
        try {
            TextView textView = (TextView) loginButton.getChildAt(0);
            textView.setText(R.string.login_activity_main_button);
        } catch (Exception ignored) {}
        loginButton.setOnClickListener(v -> loginUsingGoogle());
    }

    private void loginUsingGoogle() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        mGoogleSignInClient.signOut();

        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);

                ApiInterface apiService = RetrofitClient.getRetrofitInstance().create(ApiInterface.class);

                String token = preferences.getString("notificationToken", "");

                CreateUserRequest createUserRequest = new CreateUserRequest(
                        account.getDisplayName(),
                        account.getEmail(),
                        String.valueOf(account.getPhotoUrl()),
                        Config.APP_KEY,
                        token
                );


                Call<CreateUserResponse> call = apiService.registerUser(createUserRequest);

                call.enqueue(new Callback<CreateUserResponse>() {
                    @Override
                    public void onResponse(Call<CreateUserResponse> call, Response<CreateUserResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {

                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putString("userId", response.body().getId());
                            editor.apply();

                            String version = preferences.getString("version", "0.0");
                            Intent intent;

                            if (version.equals("0.0")) {
                                intent = new Intent(LoginActivity.this, DownloadQuestionsActivity.class);
                            } else {
                                intent = new Intent(LoginActivity.this, MainActivity.class);
                            }

                            startActivity(intent);
                            finish();
                        } else {
                            Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Произошла ошибка сервера: Empty response", Snackbar.LENGTH_LONG);
                            snackbar.setAction("Повторить", v -> loginUsingGoogle());
                            snackbar.show();
                        }
                    }

                    @Override
                    public void onFailure(Call<CreateUserResponse> call, Throwable t) {
                        Log.d("getLocalizedMessage", String.valueOf(t));
                        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Произошла ошибка сервера", Snackbar.LENGTH_LONG);
                        snackbar.setAction("Повторить", v -> loginUsingGoogle());
                        snackbar.show();
                    }
                });

            } catch (Exception e) {
                Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Произошла ошибка: " + e.getMessage(), Snackbar.LENGTH_LONG);
                snackbar.setAction("Повторить", v -> loginUsingGoogle());
                snackbar.show();
            }
        }
    }
}