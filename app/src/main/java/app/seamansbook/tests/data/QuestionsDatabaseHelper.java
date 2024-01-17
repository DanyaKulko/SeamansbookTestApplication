package app.seamansbook.tests.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class QuestionsDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "my_database.db";
    private static final int DATABASE_VERSION = 1;

    private static final String QUESTIONS_TABLE_NAME = "questions";
    private static final String TEST_PASSING_TABLE_NAME = "test_passing";
    static final String USER_SCORES_TABLE_NAME = "user_scores";


    private static final String CREATE_QUESTIONS_TABLE_QUERY =
            "CREATE TABLE " + QUESTIONS_TABLE_NAME + " ( " +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "question_id TEXT, " +
                    "question TEXT, " +
                    "answers TEXT, " +
                    "type TEXT, " +
                    "correct_answers TEXT, " +
                    "image_url TEXT, " +
                    "submodule TEXT) ";

    private static final String CREATE_TEST_PASSING_TABLE_QUERY =
            "CREATE TABLE " + TEST_PASSING_TABLE_NAME + " ( " +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "questions TEXT," +
                    "answers TEXT," +
                    "current_question INTEGER," +
                    "is_completed TEXT," +
                    "percent TEXT)";


    private static final String CREATE_USER_SCORES_TABLE_QUERY =
            "CREATE TABLE " + USER_SCORES_TABLE_NAME + " (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "question_id TEXT, " +
                    "score INTEGER DEFAULT 0)";

    public QuestionsDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_QUESTIONS_TABLE_QUERY);
        db.execSQL(CREATE_TEST_PASSING_TABLE_QUERY);
        db.execSQL(CREATE_USER_SCORES_TABLE_QUERY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion != oldVersion) {
            db.execSQL("DELETE FROM " + QUESTIONS_TABLE_NAME);
            db.execSQL("DELETE FROM " + TEST_PASSING_TABLE_NAME);
        }
        onCreate(db);
    }
}
