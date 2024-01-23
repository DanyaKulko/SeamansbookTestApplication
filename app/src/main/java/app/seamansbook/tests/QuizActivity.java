package app.seamansbook.tests;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.UiModeManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.FileInputStream;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

import app.seamansbook.tests.adapters.QuestionListAdapter;
import app.seamansbook.tests.data.QuestionDBManager;
import app.seamansbook.tests.data.QuestionScoresDBManager;
import app.seamansbook.tests.data.TestPassingDBManager;
import app.seamansbook.tests.interfaces.ApiInterface;
import app.seamansbook.tests.interfaces.OnButtonClickListener;
import app.seamansbook.tests.models.Question;
import app.seamansbook.tests.models.QuestionButton;
import app.seamansbook.tests.models.SendReportRequest;
import app.seamansbook.tests.models.TestPassingModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class QuizActivity extends AppCompatActivity implements OnButtonClickListener {

    private TextView questionCounter;
    private TextView questionText;
    private ImageView favoriteBtn;
    private ImageView questionImage;
    private AppCompatButton nextBtn;
    private RecyclerView recyclerView;
    private QuestionListAdapter buttonsAdapter;
    private Set<String> favoriteQuestionsIds;
    private Boolean isResultShown = false;
    private List<Question> questionList;
    private int currentQuestionIndex = 0;
    List<String> userSelectedAnswers = new ArrayList<>();
    private boolean disableHints;
    private Dialog dialog_result;
    private Dialog reportDialog;
    private final Gson gson = new Gson();
    private final TestPassingDBManager testPassingDBManager = new TestPassingDBManager(this);
    private TestPassingModel testPassingObj;
    private final QuestionScoresDBManager questionsDatabaseHelper = new QuestionScoresDBManager(this);


    @SuppressLint("UseCompatTextViewDrawableApis")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

        Bundle extras = getIntent().getExtras();
        disableHints = extras.getBoolean("disableHints", false);

        testPassingObj = testPassingDBManager.getTestPassingToContinue();


        QuestionDBManager dbHelper = new QuestionDBManager(this);
        questionList = dbHelper.getQuestionsByIds(testPassingObj.getQuestions());

        currentQuestionIndex = testPassingObj.getAnswers().size();


        dialog_result = new Dialog(QuizActivity.this);
        Objects.requireNonNull(dialog_result.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog_result.setContentView(R.layout.dialog_custom);
        dialog_result.setCanceledOnTouchOutside(false);
        dialog_result.setCancelable(false);
        AppCompatButton startAgainButton = dialog_result.findViewById(R.id.startAgainButton);
        AppCompatButton favoritesButton = dialog_result.findViewById(R.id.favoritesButton);
        AppCompatButton backToMainScreenButton = dialog_result.findViewById(R.id.backToMainScreenButton);


        ImageView quitTest = findViewById(R.id.quitTest);

        quitTest.setOnClickListener(v -> leaveTest());
        startAgainButton.setOnClickListener(v -> recreate());

        favoritesButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("OPEN_FRAGMENT", "FAVORITES");
            startActivity(intent);
            finish();
        });

        backToMainScreenButton.setOnClickListener(v -> {
            Intent intent = new Intent(QuizActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
        ImageView reportButton = findViewById(R.id.report);

        reportButton.setOnClickListener(v -> reportQuestion());


        questionCounter = findViewById(R.id.questionCounter);
        questionText = findViewById(R.id.questionText);
        nextBtn = findViewById(R.id.nextBtn);
        recyclerView = findViewById(R.id.recycler_view);
        favoriteBtn = findViewById(R.id.favorite_button);
        questionImage = findViewById(R.id.imageView);
        TextView passPercent = dialog_result.findViewById(R.id.passPercent);


        SharedPreferences sharedPreferences = getSharedPreferences("seamansbookMain", Context.MODE_PRIVATE);
        float passingPercent = sharedPreferences.getFloat("passingPercent", 0);
        passPercent.setText(passingPercent + "%");

        favoriteQuestionsIds = sharedPreferences.getStringSet("favorite_questions", new HashSet<>());

        if (currentQuestionIndex + 2 == questionList.size()) {
            nextBtn.setText(R.string.finish_button_text);
        }

        favoriteBtn.setOnClickListener(v -> {
            String questionId = questionList.get(currentQuestionIndex).get_id();
            if (favoriteQuestionsIds.contains(questionId)) {
                favoriteQuestionsIds.remove(questionId);
                Toast.makeText(this, R.string.question_deleted_from_favorites, Toast.LENGTH_SHORT).show();
            } else {
                favoriteQuestionsIds.add(questionId);
                Toast.makeText(this, R.string.question_added_to_favorites, Toast.LENGTH_SHORT).show();
            }

            setFavoriteButtonStyle(questionId);

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putStringSet("favorite_questions", favoriteQuestionsIds);
            editor.apply();
        });


        recyclerView.setNestedScrollingEnabled(false);

        List<QuestionButton> buttons = this.prepareQuestion();
        buttonsAdapter = new QuestionListAdapter(this, buttons, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(buttonsAdapter);


        nextBtn.setOnClickListener(v -> {
            if (userSelectedAnswers.size() == 0) {
                Toast.makeText(this, R.string.select_answer, Toast.LENGTH_SHORT).show();
                return;
            }

            Question currentQuestion = questionList.get(currentQuestionIndex);
            List<Integer> correctAnswers = currentQuestion.getCorrectAnswersIndexes();

            List<String> correctAnswersText = new ArrayList<>();
            for (int i = 0; i < correctAnswers.size(); i++) {
                correctAnswersText.add(currentQuestion.getAnswers().get(correctAnswers.get(i)));
            }


            if (!isResultShown && !disableHints) {

                for (int i = 0; i < currentQuestion.getAnswers().size(); i++) {
                    int buttonId = currentQuestion.getButtonId();

                    Button button = recyclerView.getChildAt(i).findViewById(buttonId);

                    ColorStateList correctAnswerStyle = new ColorStateList(
                            new int[][]{
                                    new int[]{android.R.attr.state_checked},
                                    new int[]{-android.R.attr.state_checked}
                            },
                            new int[]{
                                    Color.parseColor("#33C98E"),
                                    Color.parseColor("#FF6259"),
                            }
                    );
                    ColorStateList inCorrectAnswerStyle = new ColorStateList(
                            new int[][]{
                                    new int[]{android.R.attr.state_checked},
                                    new int[]{-android.R.attr.state_checked}
                            },
                            new int[]{
                                    Color.parseColor("#FF6259"),
                                    Color.parseColor("#33C98E"),
                            }
                    );
                    String answer = button.getText().toString();

                    if (userSelectedAnswers.contains(answer) && !correctAnswersText.contains(answer)) {
                        button.setBackground(ContextCompat.getDrawable(this, R.drawable.round_back_red));
                        button.setTextColor(Color.parseColor("#FF6259"));
                        button.setCompoundDrawableTintList(inCorrectAnswerStyle);
                    } else if (userSelectedAnswers.contains(answer) && correctAnswersText.contains(answer)) {
                        button.setBackground(ContextCompat.getDrawable(this, R.drawable.round_back_green1));
                        button.setTextColor(Color.parseColor("#33C98E"));
                        button.setCompoundDrawableTintList(correctAnswerStyle);
                    } else if (!userSelectedAnswers.contains(answer) && correctAnswersText.contains(answer)) {
                        button.setCompoundDrawableTintList(inCorrectAnswerStyle);
                        button.setBackground(ContextCompat.getDrawable(this, R.drawable.round_back_green1));
                        button.setTextColor(Color.parseColor("#33C98E"));
                    }
                }
            } else {

                if (currentQuestionIndex + 2 == questionList.size()) {
                    nextBtn.setText(R.string.finish_button_text);
                }

                boolean isCorrect = new HashSet<>(correctAnswersText).containsAll(userSelectedAnswers)
                        && new HashSet<>(userSelectedAnswers).containsAll(correctAnswersText);

                processAnswer(isCorrect, currentQuestion.get_id());

                String userSelectedAnswersString = gson.toJson(userSelectedAnswers);
                testPassingDBManager.insertAnswer(testPassingObj.getId(), userSelectedAnswersString);

                userSelectedAnswers.clear();


                if (currentQuestionIndex + 1 == questionList.size()) {
                    showTestResult();
                } else {
                    currentQuestionIndex++;
                    List<QuestionButton> newButtons = this.prepareQuestion();
                    buttonsAdapter.updateData(newButtons);
                }
            }

            isResultShown = !isResultShown;
        });
    }

    private void reportQuestion() {
        reportDialog = new Dialog(this);
        Objects.requireNonNull(reportDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        reportDialog.setContentView(R.layout.activity_report_question);
        reportDialog.setCanceledOnTouchOutside(false);
        reportDialog.setCancelable(false);


        EditText reportText = reportDialog.findViewById(R.id.reportText);
        Button sendReport = reportDialog.findViewById(R.id.sendReport);
        Button closeModal = reportDialog.findViewById(R.id.closeModal);
        Spinner dropdown = reportDialog.findViewById(R.id.dropdown_menu);

        String[] items = new String[]{
                getString(R.string.dropdown_option1),
                getString(R.string.dropdown_option2),
                getString(R.string.dropdown_option3),
                getString(R.string.dropdown_option4)
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, items);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        dropdown.setAdapter(adapter);


        Context context = this;

        sendReport.setOnClickListener(v -> {
            String report = reportText.getText().toString();
            if (report.isEmpty()) {
                Toast.makeText(this, R.string.error__describe_problem, Toast.LENGTH_SHORT).show();
                return;
            }

            ApiInterface apiService = RetrofitClient.getRetrofitInstance().create(ApiInterface.class);

            SharedPreferences sharedPreferences = getSharedPreferences("seamansbookMain", Context.MODE_PRIVATE);
            String userId = sharedPreferences.getString("userId", "");
            String reason = dropdown.getSelectedItem().toString();
            
            SendReportRequest reportRequest = new SendReportRequest(
                    Config.APP_KEY,
                    userId,
                    questionList.get(currentQuestionIndex).get_id(),
                    report,
                    reason
            );

            Call<Void> call = apiService.createReport(reportRequest);

            call.enqueue(new Callback<Void>() {

                @Override
                public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(context, R.string.report_sent, Toast.LENGTH_SHORT).show();
                    } else {
                        Log.d("myLogs", "response: " + response.code() + " " + response.message() + " " + response.errorBody());
                        Toast.makeText(context, "Error", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                    Toast.makeText(context, "Error 2", Toast.LENGTH_LONG).show();
                }
            });

            reportDialog.dismiss();
        });

        closeModal.setOnClickListener(v -> reportDialog.dismiss());
        reportDialog.show();
    }

    private void showTestResult() {
        float totalAnswersCount = 0.0f;
        for (int i = 0; i < questionList.size(); i++) {
            totalAnswersCount += questionList.get(i).getCorrectAnswersIndexes().size();
        }

        TextView yourResult = dialog_result.findViewById(R.id.yourResult);
        TextView addedToFavorites = dialog_result.findViewById(R.id.addedToFavorites);

        String userAnswers = testPassingDBManager.getAnswers(testPassingObj.getId());
        List<String> userAnswersArr = gson.fromJson(userAnswers, new TypeToken<List<String>>() {
        }.getType());
        int correctAnswersCount = 0;

        for (int i = 0; i < questionList.size(); i++) {
            Question question = questionList.get(i);
            List<Integer> correctAnswers = question.getCorrectAnswersIndexes();
            List<String> userAnswersPositions = gson.fromJson(userAnswersArr.get(i), new TypeToken<List<String>>() {
            }.getType());

            for (int j = 0; j < correctAnswers.size(); j++) {
                if (userAnswersPositions.contains(question.getAnswers().get(correctAnswers.get(j)))) {
                    correctAnswersCount++;
                }
            }
        }


        float result = correctAnswersCount / totalAnswersCount * 100;

        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        DecimalFormat df = new DecimalFormat("#.##", symbols);
        String resultStr = df.format(result);

        testPassingDBManager.setIsCompleted(testPassingObj.getId(), resultStr);

        yourResult.setText(resultStr + "%");
        addedToFavorites.setText(String.valueOf(favoriteQuestionsIds.size()));

        Bitmap map = takeScreenShot(QuizActivity.this);

        Bitmap fast = fastblur(map, 80);
        final Drawable draw = new BitmapDrawable(getResources(), fast);
        Objects.requireNonNull(dialog_result.getWindow()).setBackgroundDrawable(draw);
        dialog_result.show();
    }

    private void processAnswer(boolean isCorrect, String questionId) {
        int currentScore = questionsDatabaseHelper.getScore(questionId);
        if (isCorrect) {
            if (currentScore < 2) {
                currentScore++;
            }
        } else {
            currentScore = 0;
        }
        questionsDatabaseHelper.updateScore(questionId, currentScore);
    }

    @Override
    public void onButtonClick(QuestionButton buttonType, int position, View view) {
        Question currentQuestion = questionList.get(currentQuestionIndex);

        int id = currentQuestion.hasOneCorrectAnswer() ? R.id.radioButtonAnswer : R.id.checkboxButtonAnswer;
        RadioButton clickedRadio = recyclerView.getChildAt(position).findViewById(id);

        if (isResultShown && !disableHints) {
            clickedRadio.setChecked(false);
            return;
        }

        view.setSelected(!view.isSelected());
        String answer = (String) clickedRadio.getText();

        if (currentQuestion.hasOneCorrectAnswer()) {
            for (int i = 0; i < recyclerView.getChildCount(); i++) {
                View childView = recyclerView.getChildAt(i);
                RadioButton otherRadioButton = childView.findViewById(R.id.radioButtonAnswer);
                if (i != position) {
                    otherRadioButton.setChecked(false);
                    otherRadioButton.setSelected(false);
                    userSelectedAnswers.remove((String) otherRadioButton.getText());
                }
            }
        }

        if (view.isSelected()) {
            userSelectedAnswers.add(answer);
        } else {
            userSelectedAnswers.remove(answer);
            clickedRadio.setChecked(false);
        }
    }


    private void loadImageFromInternalStorage(Context context, ImageView imageView, String fileName) {
        try {
            FileInputStream fis = context.openFileInput(fileName);
            Bitmap bitmap = BitmapFactory.decodeStream(fis);
            fis.close();
            imageView.setImageBitmap(bitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<QuestionButton> prepareQuestion() {
        String questionNumber = (currentQuestionIndex + 1) + "/" + questionList.size();
        questionCounter.setText(questionNumber);
        Question question = questionList.get(currentQuestionIndex);
        questionText.setText(question.getQuestion());

        if (question.getType().equals("image") && !question.getMedia().equals("")) {
            questionImage.setVisibility(View.VISIBLE);
            loadImageFromInternalStorage(QuizActivity.this, questionImage, question.getMedia());
        } else {
            questionImage.setVisibility(View.GONE);
        }

        setFavoriteButtonStyle(question.get_id());

        List<QuestionButton> buttonTypes = new ArrayList<>();


        for (int i = 0; i < question.getAnswers().size(); i++) {
            QuestionButton myBtn = new QuestionButton(
                    question.getAnswers().get(i),
                    question.getButtonType()
            );
            buttonTypes.add(myBtn);
        }

        Collections.shuffle(buttonTypes);

        return buttonTypes;
    }

    private void setFavoriteButtonStyle(String questionId) {
        if (favoriteQuestionsIds.contains(questionId)) {
            favoriteBtn.setImageResource(R.drawable.favorite_button_field);
            favoriteBtn.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.favorite_button_field));
            favoriteBtn.setColorFilter(0xFFFFC107);
        } else {
            favoriteBtn.setImageResource(R.drawable.star_icon);
            UiModeManager uiModeManager = (UiModeManager) getSystemService(Context.UI_MODE_SERVICE);

            if (uiModeManager.getNightMode() == UiModeManager.MODE_NIGHT_YES) {
                favoriteBtn.setColorFilter(Color.WHITE);

            } else {
                favoriteBtn.setColorFilter(Color.BLACK);
            }
        }
    }

    @Override
    public void onBackPressed() {
        leaveTest();
        if (isResultShown) {

            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        if (dialog_result != null && dialog_result.isShowing()) {
            dialog_result.dismiss();
        }
        super.onDestroy();
    }

    private void leaveTest() {
        if (!isResultShown) {
            AlertDialog.Builder alert = new AlertDialog.Builder(QuizActivity.this);
            alert.setTitle(R.string.leave_the_test);
            alert.setMessage(R.string.leave_the_test_confirmation);

            alert.setNegativeButton(R.string.button_no, (dialog, which) -> dialog.dismiss());
            alert.setPositiveButton(R.string.button_yes, (dialog, which) -> {
                dialog.dismiss();
                QuizActivity.super.onBackPressed();
            });
            alert.show();
        }
    }


    private static Bitmap takeScreenShot(Activity activity) {
        View view = activity.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap b1 = view.getDrawingCache();
        Rect frame = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int statusBarHeight = frame.top;
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;

        Bitmap b = Bitmap.createBitmap(b1, 0, statusBarHeight, width, height - statusBarHeight);
        view.destroyDrawingCache();
        return b;
    }

    public Bitmap fastblur(Bitmap sentBitmap, int radius) {
        Bitmap bitmap = sentBitmap.copy(sentBitmap.getConfig(), true);

        if (radius < 1) {
            return (null);
        }

        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        int[] pix = new int[w * h];
        Log.e("pix", w + " " + h + " " + pix.length);
        bitmap.getPixels(pix, 0, w, 0, 0, w, h);

        int wm = w - 1;
        int hm = h - 1;
        int wh = w * h;
        int div = radius + radius + 1;

        int[] r = new int[wh];
        int[] g = new int[wh];
        int[] b = new int[wh];
        int rsum, gsum, bsum, x, y, i, p, yp, yi, yw;
        int[] vmin = new int[Math.max(w, h)];

        int divsum = (div + 1) >> 1;
        divsum *= divsum;
        int[] dv = new int[256 * divsum];
        for (i = 0; i < 256 * divsum; i++) {
            dv[i] = (i / divsum);
        }

        yw = yi = 0;

        int[][] stack = new int[div][3];
        int stackpointer;
        int stackstart;
        int[] sir;
        int rbs;
        int r1 = radius + 1;
        int routsum, goutsum, boutsum;
        int rinsum, ginsum, binsum;

        for (y = 0; y < h; y++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            for (i = -radius; i <= radius; i++) {
                p = pix[yi + Math.min(wm, Math.max(i, 0))];
                sir = stack[i + radius];
                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);
                rbs = r1 - Math.abs(i);
                rsum += sir[0] * rbs;
                gsum += sir[1] * rbs;
                bsum += sir[2] * rbs;
                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }
            }
            stackpointer = radius;

            for (x = 0; x < w; x++) {

                r[yi] = dv[rsum];
                g[yi] = dv[gsum];
                b[yi] = dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (y == 0) {
                    vmin[x] = Math.min(x + radius + 1, wm);
                }
                p = pix[yw + vmin[x]];

                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[(stackpointer) % div];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi++;
            }
            yw += w;
        }
        for (x = 0; x < w; x++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            yp = -radius * w;
            for (i = -radius; i <= radius; i++) {
                yi = Math.max(0, yp) + x;

                sir = stack[i + radius];

                sir[0] = r[yi];
                sir[1] = g[yi];
                sir[2] = b[yi];

                rbs = r1 - Math.abs(i);

                rsum += r[yi] * rbs;
                gsum += g[yi] * rbs;
                bsum += b[yi] * rbs;

                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }

                if (i < hm) {
                    yp += w;
                }
            }
            yi = x;
            stackpointer = radius;
            for (y = 0; y < h; y++) {
                // Preserve alpha channel: ( 0xff000000 & pix[yi] )
                pix[yi] = (0xff000000 & pix[yi]) | (dv[rsum] << 16) | (dv[gsum] << 8) | dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (x == 0) {
                    vmin[y] = Math.min(y + r1, hm) * w;
                }
                p = x + vmin[y];

                sir[0] = r[p];
                sir[1] = g[p];
                sir[2] = b[p];

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[stackpointer];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi += w;
            }
        }

        Log.e("pix", w + " " + h + " " + pix.length);
        bitmap.setPixels(pix, 0, w, 0, 0, w, h);

        return (bitmap);
    }
}
