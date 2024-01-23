package app.seamansbook.tests.data;

import static app.seamansbook.tests.Config.QUESTIONS_NUMBER;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import app.seamansbook.tests.models.Question;

public class QuestionDBManager {

    QuestionsDatabaseHelper db;
    Context context;

    public QuestionDBManager(Context context) {
        this.db = new QuestionsDatabaseHelper(context);
        this.context = context;
    }

    public void setQuestions(List<Question> questions) {
        try (SQLiteDatabase database = this.db.getWritableDatabase()) {
            Gson gson = new Gson();
            database.beginTransaction();
            try {
                database.execSQL("DELETE FROM questions");

                for (int i = 0; i < questions.size(); i++) {
                    Question question = questions.get(i);

                    ContentValues values = new ContentValues();
                    values.put("question_id", question.get_id());
                    values.put("question", question.getQuestion());
                    values.put("answers", gson.toJson(question.getAnswers()));
                    values.put("correct_answers", gson.toJson(question.getCorrectAnswersIndexes()));
                    values.put("type", question.getType());
                    values.put("image_url", question.getMedia());
                    values.put("submodule", question.getSubmodule());


                    long result = database.insert("questions", null, values);
                    if (result == -1) {
                        Toast.makeText(context, "Error while inserting data to DB. Contact to developers.", Toast.LENGTH_LONG).show();
                    }
                }

                database.setTransactionSuccessful();

            } catch (SQLException e) {
                Toast.makeText(context, "Database transaction failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
            } finally {
                database.endTransaction();
                database.close();
            }
        }
    }

    public List<Question> getQuestions() {
        String query = "SELECT * FROM questions ORDER BY RANDOM() LIMIT " + QUESTIONS_NUMBER;
        SQLiteDatabase database = db.getReadableDatabase();

        if (database == null) {
            Toast.makeText(context, "No data presented", Toast.LENGTH_SHORT).show();
            return null;
        }

        Cursor cursor = database.rawQuery(query, null);

        if (cursor.getCount() == 0) {
            Toast.makeText(context, "No data presented", Toast.LENGTH_SHORT).show();
            closeCursorAndDatabase(cursor, database);
            return null;
        }

        List<Question> questions = new ArrayList<>();

        while (cursor.moveToNext()) {
            Gson gson = new Gson();
            Question question = new Question(
                    cursor.getString(1),
                    cursor.getString(2),
                    gson.fromJson(cursor.getString(3), new TypeToken<List<String>>() {
                    }.getType()),
                    gson.fromJson(cursor.getString(5), new TypeToken<List<Integer>>() {
                    }.getType()),
                    cursor.getString(4),
                    cursor.getString(6),
                    cursor.getString(7)

            );
            questions.add(question);
        }
        closeCursorAndDatabase(cursor, database);
        return questions;
    }

    public int getQuestionsCount() {
        SQLiteDatabase database = db.getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT COUNT(*) FROM questions", null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        closeCursorAndDatabase(cursor, database);
        return count;
    }

    private String getInClause(int size) {
        StringBuilder inClause = new StringBuilder();
        for (int i = 0; i < size; i++) {
            if (i > 0) {
                inClause.append(",");
            }
            inClause.append("?");
        }
        return inClause.toString();
    }

    private String createOrderClause(List<String> ids) {
        StringBuilder orderClause = new StringBuilder("ORDER BY CASE question_id ");
        for (int i = 0; i < ids.size(); i++) {
            orderClause.append("WHEN '").append(ids.get(i)).append("' THEN ").append(i).append(" ");
        }
        orderClause.append("END");
        return orderClause.toString();
    }

    public List<Question> getQuestionsByIds(List<String> ids) {
        SQLiteDatabase database = db.getReadableDatabase();
        String[] idArray = ids.toArray(new String[0]);
        String selectQuery = "SELECT  * FROM questions WHERE question_id IN (" + getInClause(ids.size()) + ") " + createOrderClause(ids);
        Cursor cursor = database.rawQuery(selectQuery, idArray);

        Gson gson = new Gson();
        List<Question> questions = new ArrayList<>();

        while (cursor.moveToNext()) {
            Question question = new Question(
                    cursor.getString(1),
                    cursor.getString(2),
                    gson.fromJson(cursor.getString(3), new TypeToken<List<String>>() {
                    }.getType()),
                    gson.fromJson(cursor.getString(5), new TypeToken<List<Integer>>() {
                    }.getType()),
                    cursor.getString(4),
                    cursor.getString(6),
                    cursor.getString(7)
            );
            questions.add(question);
        }
        closeCursorAndDatabase(cursor, database);
        return questions;
    }

    private void closeCursorAndDatabase(Cursor cursor, SQLiteDatabase db) {
        if (cursor != null) {
            cursor.close();
        }
        if (db != null) {
            db.close();
        }
    }

}
