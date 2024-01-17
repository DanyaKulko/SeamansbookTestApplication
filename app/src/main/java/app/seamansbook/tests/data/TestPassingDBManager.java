package app.seamansbook.tests.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import app.seamansbook.tests.models.Question;
import app.seamansbook.tests.models.TestPassingModel;

public class TestPassingDBManager {

    QuestionsDatabaseHelper db;
    Context context;

    public TestPassingDBManager(Context context) {
        this.context = context;
        this.db = new QuestionsDatabaseHelper(context);
    }


    public TestPassingModel createTestPassing(List<Question> questions) {
        SQLiteDatabase database = db.getWritableDatabase();
        Gson gson = new Gson();
        TestPassingModel testPassing = null;

        try {
            database.beginTransaction();

            List<String> questionIds = new ArrayList<>();
            for (Question question : questions) {
                questionIds.add(question.get_id());
            }
            String jsonQuestionIds = gson.toJson(questionIds);

            ContentValues values = new ContentValues();
            values.put("questions", jsonQuestionIds);
            values.put("answers", "[]");
            values.put("current_question", 1);
            values.put("is_completed", "false");
            values.put("percent", "0");

            long result = database.insert("test_passing", null, values);
            if (result == -1) {
                Toast.makeText(context, "Error while inserting data to DB. Contact to developers.", Toast.LENGTH_LONG).show();
            } else {
                testPassing = new TestPassingModel(
                        (int) result,
                        questionIds,
                        new ArrayList<>(),
                        1,
                        "false"
                );
                database.setTransactionSuccessful();
            }
        } catch (SQLException e) {
            Toast.makeText(context, "Database transaction failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
        } finally {
            database.endTransaction();
            database.close();
        }

        return testPassing;
    }

    public TestPassingModel getNotFinishedTest() {
        SQLiteDatabase db = this.db.getReadableDatabase();

        if (db == null) {
            Toast.makeText(context, "No data presented", Toast.LENGTH_SHORT).show();
            return null;
        }

        String query = "SELECT * FROM test_passing WHERE is_completed = 'false' ORDER BY id DESC LIMIT 1";


        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            Gson gson = new Gson();

            TestPassingModel testPassing = new TestPassingModel(
                    cursor.getInt(0),
                    gson.fromJson(cursor.getString(1), new TypeToken<List<String>>() {
                    }.getType()),
                    gson.fromJson(cursor.getString(2), new TypeToken<List<String>>() {
                    }.getType()),
                    cursor.getInt(3),
                    cursor.getString(4)
            );

            closeCursorAndDatabase(cursor, db);

            return testPassing;
        }

        closeCursorAndDatabase(cursor, db);
        return null;
    }

    public TestPassingModel getTestPassingToContinue() {
        TestPassingModel testPassing = getNotFinishedTest();

        if (testPassing != null) {
            return testPassing;
        }

        QuestionDBManager questionDBManager = new QuestionDBManager(context);
        List<Question> questions = questionDBManager.getQuestions();

        testPassing = createTestPassing(questions);

        return testPassing;
    }

    public void insertAnswer(int testPassingId, String answer) {
        SQLiteDatabase database = db.getWritableDatabase();

        Cursor cursor = null;

        database.beginTransaction();
        try {

            String query = "SELECT * FROM test_passing WHERE id = ?";
            cursor = database.rawQuery(query, new String[]{String.valueOf(testPassingId)});

            if (cursor != null && cursor.moveToFirst()) {
                Gson gson = new Gson();
                List<String> answers = gson.fromJson(cursor.getString(2), new TypeToken<List<String>>() {}.getType());
                answers.add(answer);

                String jsonAnswers = gson.toJson(answers);
                ContentValues values = new ContentValues();
                values.put("answers", jsonAnswers);

                int rowsAffected = database.update("test_passing", values, "id = ?", new String[]{String.valueOf(testPassingId)});
                if (rowsAffected > 0) {
                    database.setTransactionSuccessful();
                }
            } else {
                Log.d("TestPassingDBManager", "insertAnswer: cursor is null");
            }
        } catch (Exception e) {
            Log.d("TestPassingDBManager", "insertAnswer: " + e.getMessage());
        } finally {
            database.endTransaction();
            closeCursorAndDatabase(cursor, database);
        }
    }

    public void setIsCompleted(int testPassingId, String percent) {
        SQLiteDatabase database = db.getWritableDatabase();
        Cursor cursor = null;

        try {
            String query = "SELECT * FROM test_passing WHERE id = ?";
            cursor = database.rawQuery(query, new String[]{String.valueOf(testPassingId)});
            database.beginTransaction();

            if (cursor != null && cursor.moveToFirst()) {
                ContentValues values = new ContentValues();
                values.put("is_completed", "true");
                values.put("percent", percent);

                int rowsAffected = database.update("test_passing", values, "id = ?", new String[]{String.valueOf(testPassingId)});
                if (rowsAffected > 0) {
                    database.setTransactionSuccessful();
                }
            } else {
                Log.d("TestPassingDBManager", "insertAnswer: cursor is null");
            }
        } catch (Exception e) {
            Log.d("TestPassingDBManager", "Exception insertAnswer: " + e.getMessage());
        } finally {
            database.endTransaction();
            closeCursorAndDatabase(cursor, database);
        }
    }

    public String getAnswers(int testPassingId) {
        SQLiteDatabase database = db.getReadableDatabase();
        String selectQuery = "SELECT  * FROM test_passing WHERE id = " + testPassingId + " ORDER BY id LIMIT 1";
        Cursor cursor = database.rawQuery(selectQuery, null);
        String answers = null;

        if (cursor.moveToFirst()) {
            answers = cursor.getString(2);
        }

        closeCursorAndDatabase(cursor, database);
        return answers;
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
