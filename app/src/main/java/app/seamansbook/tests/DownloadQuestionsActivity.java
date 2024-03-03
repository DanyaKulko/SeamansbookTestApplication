package app.seamansbook.tests;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import app.seamansbook.tests.data.QuestionDBManager;
import app.seamansbook.tests.data.TestPassingDBManager;
import app.seamansbook.tests.interfaces.ApiInterface;
import app.seamansbook.tests.models.Question;
import app.seamansbook.tests.models.Response2;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DownloadQuestionsActivity extends AppCompatActivity {
    private QuestionDBManager dbHelper;
    private ProgressDialog progressDialog;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_questions);

        boolean canReturn = getIntent().getBooleanExtra("canReturn", false);

        if(canReturn) {
            Button returnButton = findViewById(R.id.returnButton);
            returnButton.setVisibility(Button.VISIBLE);
            returnButton.setOnClickListener(v -> {
                Intent intent = new Intent(DownloadQuestionsActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            });
        }


        Button downloadButton = findViewById(R.id.downloadButton);
        context = this;

        dbHelper = new QuestionDBManager(this);
        downloadButton.setOnClickListener(v -> {
            TestPassingDBManager dbHelper2 = new TestPassingDBManager(this);
            dbHelper2.deleteUnfinishedTest();


            ApiInterface apiService = RetrofitClient.getRetrofitInstance().create(ApiInterface.class);

            Call<Response2> call = apiService.getQuestions(Config.APP_KEY);
            progressDialog = ProgressDialog.show(this, "", "Loading...");

            call.enqueue(new Callback<Response2>() {

                @Override
                public void onResponse(@NonNull Call<Response2> call, @NonNull Response<Response2> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Response2 serverResponse = response.body();
                        List<Question> questions = serverResponse.getQuestions();
                        dbHelper.setQuestions(questions);

                        for (Question question : questions) {
                            if (question.getType().equals("image") && !question.getMedia().equals("")) {
                                downloadImage(question.getSubmodule(), question.getMedia());
                            }
                        }

                        SharedPreferences sharedPreferences = getSharedPreferences("seamansbookMain", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("version", serverResponse.getVersion());
                        editor.putString("title", serverResponse.getTitle());
                        editor.putFloat("passingPercent", serverResponse.getPassingPercent());
                        editor.apply();

                        Toast.makeText(context, "Вопросы были успешно установлены!", Toast.LENGTH_LONG).show();

                        Intent intent = new Intent(DownloadQuestionsActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(context, "Error", Toast.LENGTH_LONG).show();
                    }

                    progressDialog.dismiss();
                }

                @Override
                public void onFailure(@NonNull Call<Response2> call, @NonNull Throwable t) {
                    Log.d("getLocalizedMessage", String.valueOf(call.request().url()));
                    progressDialog.dismiss();
                }
            });
        });
    }

    private void downloadImage(String submodule, String fileName) {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(Config.API_URL + "public/media/" + submodule + "/" + fileName)
                .build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(@NonNull okhttp3.Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull okhttp3.Call call, @NonNull okhttp3.Response response) {
                if (response.isSuccessful() && response.body() != null) {
                    String contentType = response.header("Content-Type");
                    if(contentType == null) {
                        return;
                    }
                    String[] parts = contentType.split("/");
                    if (parts.length == 2) {
                        Bitmap bitmap = BitmapFactory.decodeStream(response.body().byteStream());
                        saveImageToInternalStorage(context, bitmap, fileName);
                    }
                }
            }
        });
    }

    private void saveImageToInternalStorage(Context context, Bitmap bitmapImage, String fileName) {
        try {
            FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}