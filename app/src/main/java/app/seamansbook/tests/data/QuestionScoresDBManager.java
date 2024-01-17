package app.seamansbook.tests.data;

import static app.seamansbook.tests.data.QuestionsDatabaseHelper.USER_SCORES_TABLE_NAME;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

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
            score = cursor.getInt(cursor.getColumnIndex("score"));
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
                score += cursor.getInt(cursor.getColumnIndex("score"));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return score;
    }
}
