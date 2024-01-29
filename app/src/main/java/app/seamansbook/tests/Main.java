package app.seamansbook.tests;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.skydoves.balloon.ArrowOrientation;
import com.skydoves.balloon.ArrowPositionRules;
import com.skydoves.balloon.Balloon;
import com.skydoves.balloon.BalloonAnimation;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import app.seamansbook.tests.data.QuestionDBManager;
import app.seamansbook.tests.data.QuestionScoresDBManager;
import app.seamansbook.tests.interfaces.ApiInterface;
import app.seamansbook.tests.interfaces.BottomNavigationController;
import app.seamansbook.tests.models.Version;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Main extends Fragment {
    private SharedPreferences preferences;
    ConstraintLayout installUpdatedQuestions;
    ProgressBar progressBar;
    private ExecutorService executorService;
    private Handler mainHandler;
    private QuestionDBManager dbManager;
    private QuestionScoresDBManager scoresDBManager;
    private BottomNavigationController bottomNavController;


    public Main() {
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof BottomNavigationController) {
            bottomNavController = (BottomNavigationController) context;
        }
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        executorService = Executors.newSingleThreadExecutor();
        mainHandler = new Handler(Looper.getMainLooper());
        dbManager = new QuestionDBManager(requireContext());
        scoresDBManager = new QuestionScoresDBManager(requireContext());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ConstraintLayout statisticsBlock = view.findViewById(R.id.statisticsBlock);
        ConstraintLayout favoriteBlock = view.findViewById(R.id.favoriteBlock);
        ImageView settingIcon = view.findViewById(R.id.settingIcon);
        ImageView notificationsIcon = view.findViewById(R.id.notificationsIcon);

        statisticsBlock.setOnClickListener(v -> {
            Fragment statisticsFragment = new StatisticsFragment();
            requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, statisticsFragment).commit();
            bottomNavController.onItemSelected(R.id.action_statistics);
        });

        favoriteBlock.setOnClickListener(v -> {
            Fragment favoritesFragment = new FavoritesFragment();
            requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, favoritesFragment).commit();
            bottomNavController.onItemSelected(R.id.action_favorite);
        });

        settingIcon.setOnClickListener(v -> {
            Fragment settingsFragment = new SettingsFragment();
            requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, settingsFragment).commit();
            bottomNavController.onItemSelected(R.id.action_settings);
        });

        notificationsIcon.setOnClickListener(v -> {
            TopSheetNotificationsFragment bottomSheetFragment = new TopSheetNotificationsFragment();
            bottomSheetFragment.show(requireActivity().getSupportFragmentManager(), bottomSheetFragment.getTag());
        });

        progressBar = view.findViewById(R.id.simpleProgressBar);

        loadDatabaseInfo();

        preferences = requireActivity().getSharedPreferences("seamansbookMain", MODE_PRIVATE);
        boolean disableHintsValue = preferences.getBoolean("disableHints", false);
        String assemblyTitle = preferences.getString("title", "Not specified");

        TextView assemblyTitleTextView = view.findViewById(R.id.assemblyTitleTextView);
        Button myBtn = view.findViewById(R.id.startButton);
        SwitchCompat disableHints = view.findViewById(R.id.disableHintsSwitch);
        ImageView hints_eye_icon = view.findViewById(R.id.hints_eye_icon);


        assemblyTitleTextView.setText(assemblyTitle);
        disableHints.setChecked(disableHintsValue);

        if (disableHintsValue) {
            hints_eye_icon.setImageResource(R.drawable.icon__hints_eye_open);
        } else {
            hints_eye_icon.setImageResource(R.drawable.icon__hints_eye);
        }



        CardView tooltipCardView = view.findViewById(R.id.tooltipCardView);
        tooltipCardView.setOnClickListener(v -> {
            Balloon balloon = new Balloon.Builder(requireContext())
                    .setArrowSize(10)
                    .setArrowOrientation(ArrowOrientation.TOP)
                    .setArrowPositionRules(ArrowPositionRules.ALIGN_ANCHOR)
                    .setArrowPosition(0.5f)
                    .setWidth(200)
                    .setPadding(4)
                    .setTextSize(12f)
                    .setCornerRadius(8f)
                    .setAlpha(0.9f)
                    .setText(getString(R.string.fragment_main_tint))
                    .setBalloonAnimation(BalloonAnimation.FADE)
                    .build();

            balloon.showAlignBottom(tooltipCardView);
        });



        disableHints.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("disableHints", isChecked);
            editor.apply();

            if (isChecked) {
                hints_eye_icon.setImageResource(R.drawable.icon__hints_eye_open);
            } else {
                hints_eye_icon.setImageResource(R.drawable.icon__hints_eye);
            }
        });




        installUpdatedQuestions = view.findViewById(R.id.installUpdatedQuestions);

        installUpdatedQuestions.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), DownloadQuestionsActivity.class);
            intent.putExtra("canReturn", true);
            startActivity(intent);
        });

        myBtn.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), QuizActivity.class);
            intent.putExtra("disableHints", !disableHints.isChecked());
            startActivity(intent);
        });


        ImageView profileImageView = view.findViewById(R.id.profile_image);

        String profileImgUrl = preferences.getString("profileImgUrl", "");
        if (!profileImgUrl.equals("")) {
            Glide.with(this).load(profileImgUrl).into(profileImageView);
        } else {
            GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(requireContext());
            if (account != null) {
                String name = account.getDisplayName();
                Uri imageUrl = account.getPhotoUrl();

                if (imageUrl != null) {
                    Glide.with(this).load(imageUrl).into(profileImageView);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("profileImgUrl", String.valueOf(imageUrl));
                    editor.apply();
                } else {
                    String firstLetter = name.substring(0, 1).toUpperCase();
                    Bitmap letterBitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
                    Canvas canvas = new Canvas(letterBitmap);
                    canvas.drawColor(Color.WHITE);
                    Paint paint = new Paint();
                    paint.setColor(Color.BLUE);
                    paint.setTextSize(50);
                    paint.setTypeface(Typeface.DEFAULT_BOLD);
                    paint.setTextAlign(Paint.Align.CENTER);
                    canvas.drawText(firstLetter, 50, 67, paint);
                    profileImageView.setImageBitmap(letterBitmap);
                }
            }
        }


        getVersion();
    }

    private void loadDatabaseInfo() {
        executorService.execute(() -> {
            int totalQuestions = dbManager.getQuestionsCount();
            int totalScore = scoresDBManager.getTotalScore();

            mainHandler.post(() -> updateUI(totalQuestions, totalScore));
        });
    }

    private void updateUI(int totalQuestions, int totalScore) {
        Activity activity = getActivity();
        if (activity == null || !isAdded()) {
            return;
        }

        int progressValue = (int) Math.round(totalScore / (totalQuestions * 2.0) * 100);

        if (progressValue < 33) {
            progressBar.setProgressTintList(
                    ColorStateList.valueOf(ContextCompat.getColor(activity, (R.color.progressRedColor))));
        } else if (progressValue < 66) {
            progressBar.setProgressTintList(
                    ColorStateList.valueOf(ContextCompat.getColor(activity, (R.color.progressYellowColor))));
        } else {
            progressBar.setProgressTintList(
                    ColorStateList.valueOf(ContextCompat.getColor(activity, (R.color.progressGreenColor))));
        }

        progressBar.setProgress(progressValue);
    }


    private void getVersion() {
        ApiInterface apiService = RetrofitClient.getRetrofitInstance().create(ApiInterface.class);

        Call<Version> call = apiService.getVersion(Config.APP_KEY);
        call.enqueue(new Callback<Version>() {
            @Override
            public void onResponse(@NonNull Call<Version> call, @NonNull Response<Version> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String version = response.body().getVersion();
                    String currentVersion = preferences.getString("version", "0.0");
                    if (!version.equals(currentVersion)) {
                        installUpdatedQuestions.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<Version> call, @NonNull Throwable t) {
                installUpdatedQuestions.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }
}