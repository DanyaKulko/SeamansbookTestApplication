package app.seamansbook.tests.data;

import static android.content.Context.MODE_PRIVATE;
import static app.seamansbook.tests.data.QuestionsDatabaseHelper.QUESTIONS_TABLE_NAME;
import static app.seamansbook.tests.data.QuestionsDatabaseHelper.TEST_PASSING_TABLE_NAME;
import static app.seamansbook.tests.data.QuestionsDatabaseHelper.USER_SCORES_TABLE_NAME;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import app.seamansbook.tests.models.QuizResultModel;
import app.seamansbook.tests.models.StatisticsMainInfoModel;
import app.seamansbook.tests.models.WrongAnswersFragmentModel;
import app.seamansbook.tests.models.WrongAnswersModel;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

public class QuestionScoresDBManager {
    QuestionsDatabaseHelper db;
    Context context;

    public QuestionScoresDBManager(Context context) {
        this.db = new QuestionsDatabaseHelper(context);
        this.context = context;
    }

    public void updateScore(String questionId, int newScore) {
        SQLiteDatabase db = this.db.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("score", newScore);

        int rows = db.update(USER_SCORES_TABLE_NAME, values, "question_id = ?", new String[]{questionId});
        if (rows == 0) {
            values.put("question_id", questionId);
            db.insert(USER_SCORES_TABLE_NAME, null, values);
        }
        db.close();
    }

    public int getScore(String questionId) {
        SQLiteDatabase db = this.db.getReadableDatabase();
        Cursor cursor = db.query(USER_SCORES_TABLE_NAME, new String[]{"score"}, "question_id = ?", new String[]{questionId}, null, null, null);
        int score = 0;
        if (cursor.moveToFirst()) {
            score = cursor.getInt(cursor.getColumnIndexOrThrow("score"));
        }
        cursor.close();
        db.close();
        return score;
    }

    public int getTotalScore() {
        SQLiteDatabase db = this.db.getReadableDatabase();
        Cursor cursor = db.query(USER_SCORES_TABLE_NAME, new String[]{"score"}, null, null, null, null, null);
        int score = 0;
        if (cursor.moveToFirst()) {
            do {
                score += cursor.getInt(cursor.getColumnIndexOrThrow("score"));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return score;
    }

    public StatisticsMainInfoModel getStatisticsMainInfo() {
        SQLiteDatabase db = this.db.getReadableDatabase();
        Cursor cursor = db.query(TEST_PASSING_TABLE_NAME, new String[]{"percent"}, "is_completed = ?", new String[]{"true"}, null, null, "id ASC");

        SharedPreferences sharedPreferences = context.getSharedPreferences("seamansbookMain", MODE_PRIVATE);
        float passingPercent = sharedPreferences.getFloat("passingPercent", 0);

        int totalTests = cursor.getCount();
        int failedTests = 0;
        int totalScore = 0;
        int firstScore = 0;
        int bestScore = 0;

        if (totalTests > 0 && cursor.moveToFirst()) {
            int percentColumnIndex = cursor.getColumnIndexOrThrow("percent");

            firstScore = Math.round(Float.parseFloat(cursor.getString(percentColumnIndex)));

            do {
                String percentStr = cursor.getString(percentColumnIndex);
                float percent = 0;
                try {
                    percent = Float.parseFloat(percentStr);
                } catch (NumberFormatException e) {
                    continue;
                }

                if (percent < passingPercent) {
                    failedTests++;
                }

                if (percent > bestScore) {
                    bestScore = Math.round(percent);
                }

                totalScore += (int) percent;
            } while (cursor.moveToNext());
        }

        int successTests = totalTests - failedTests;
        int averageScore = totalTests > 0 ? (totalScore / totalTests) : 0;

        cursor.close();
        db.close();
        return new StatisticsMainInfoModel(successTests, Integer.toString(failedTests), firstScore, averageScore, bestScore);
    }


    public List<QuizResultModel> getQuizResults() {
        SQLiteDatabase db = this.db.getReadableDatabase();
        Cursor cursor = db.query(TEST_PASSING_TABLE_NAME, new String[]{"id", "questions", "answers", "percent", "date"}, "is_completed = ?", new String[]{"true"}, null, null, "id DESC");
        Gson gson = new Gson();

        Cursor answersCursor = db.query(QUESTIONS_TABLE_NAME, new String[]{"question_id", "answers", "correct_answers"}, null, null, null, null, null);
        HashMap<String, List<String>> correctAnswersMap = new HashMap<>();

        while (answersCursor.moveToNext()) {
            String questionId = answersCursor.getString(answersCursor.getColumnIndexOrThrow("question_id"));
            List<Integer> answersIndexes = gson.fromJson(answersCursor.getString(answersCursor.getColumnIndexOrThrow("correct_answers")), new TypeToken<List<Integer>>() {
            }.getType());
            List<String> answers = gson.fromJson(answersCursor.getString(answersCursor.getColumnIndexOrThrow("answers")), List.class);
            List<String> correctAnswers = new ArrayList<>();

            for (int i = 0; i < answersIndexes.size(); i++) {
                correctAnswers.add(answers.get(answersIndexes.get(i)));
            }
            correctAnswersMap.put(questionId, correctAnswers);
        }
        answersCursor.close();

        List<QuizResultModel> quizResultModels = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                String id = cursor.getString(cursor.getColumnIndexOrThrow("id"));
                String score = cursor.getString(cursor.getColumnIndexOrThrow("percent"));
                String questionsJson = cursor.getString(cursor.getColumnIndexOrThrow("questions")); // user questions ids
                String answersJson = cursor.getString(cursor.getColumnIndexOrThrow("answers")); // user answers
                String date = cursor.getString(cursor.getColumnIndexOrThrow("date"));

                String[] questionsArr = gson.fromJson(questionsJson, String[].class);
                String[] answersArr = gson.fromJson(answersJson, String[].class);

                int questionErrors = 0;
                for (int i = 0; i < questionsArr.length; i++) {
                    if (!correctAnswersMap.containsKey(questionsArr[i])) {
                        continue;
                    }
                    List<String> correctAnswers = correctAnswersMap.get(questionsArr[i]); // correct
                    String[] answers = gson.fromJson(answersArr[i], String[].class); // user

                    if (correctAnswers.size() != answers.length) {
                        questionErrors++;
                        continue;
                    }

                    for (int j = 0; j < answers.length; j++) {
                        if (!correctAnswers.contains(answers[j])) {
                            questionErrors++;
                        }
                    }
                }

                quizResultModels.add(new QuizResultModel(id, date, score, questionErrors));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return quizResultModels;
    }

    public WrongAnswersFragmentModel getWrongAnswers(String testPassingId) {

        WrongAnswersFragmentModel wrongAnswersFragmentModel = new WrongAnswersFragmentModel("date", new ArrayList<>());
        List<WrongAnswersModel> wrongAnswersModels = new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = this.db.getReadableDatabase();
            cursor = db.query(TEST_PASSING_TABLE_NAME, new String[]{"id", "questions", "answers", "date"}, "is_completed = ? AND id = ?", new String[]{"true", testPassingId}, null, null, "id DESC");
            Gson gson = new Gson();

            if (cursor.moveToFirst()) {
                String date = cursor.getString(cursor.getColumnIndexOrThrow("date"));
                String questionsJson = cursor.getString(cursor.getColumnIndexOrThrow("questions"));
                String answersJson = cursor.getString(cursor.getColumnIndexOrThrow("answers"));

                wrongAnswersFragmentModel.setDate(date);

                String[] questionsArr = gson.fromJson(questionsJson, String[].class);
                String[] answersArr = gson.fromJson(answersJson, String[].class);

                for (int i = 0; i < questionsArr.length; i++) {
                    String questionId = questionsArr[i];
                    List<String> userAnswers = gson.fromJson(answersArr[i], new TypeToken<List<String>>() {
                    }.getType());

                    Cursor answersCursor = db.query(QUESTIONS_TABLE_NAME, new String[]{"question_id", "question", "answers", "correct_answers"},
                            "question_id = ?", new String[]{questionId}, null, null, null);

                    if (answersCursor.moveToFirst()) {
                        String id = answersCursor.getString(answersCursor.getColumnIndexOrThrow("question_id"));
                        String question = answersCursor.getString(answersCursor.getColumnIndexOrThrow("question"));
                        List<Integer> correctAnswersIndexes = gson.fromJson(answersCursor.getString(answersCursor.getColumnIndexOrThrow("correct_answers")),
                                new TypeToken<List<Integer>>() {
                                }.getType());
                        List<String> allAnswers = gson.fromJson(answersCursor.getString(answersCursor.getColumnIndexOrThrow("answers")),
                                new TypeToken<List<String>>() {
                                }.getType());

                        List<String> correctAnswers = new ArrayList<>();
                        for (Integer index : correctAnswersIndexes) {
                            correctAnswers.add(allAnswers.get(index));
                        }

                        HashSet<String> userAnswersSet = new HashSet<>(userAnswers);
                        HashSet<String> correctAnswersSet = new HashSet<>(correctAnswers);

                        if (!userAnswersSet.equals(correctAnswersSet)) {
                            wrongAnswersModels.add(new WrongAnswersModel(id, question, userAnswers, correctAnswers));
                        }
                    }
                    answersCursor.close();
                }
            }
        } catch (JsonSyntaxException e) {
            Log.e("DatabaseHelper", "JSON parsing error: " + e.getMessage());
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Database error: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }
        wrongAnswersFragmentModel.setWrongAnswers(wrongAnswersModels);
        return wrongAnswersFragmentModel;
    }

    public void clearStatistics() {
        SQLiteDatabase db = this.db.getWritableDatabase();
        db.delete(TEST_PASSING_TABLE_NAME, null, null);
        db.close();
    }

}
