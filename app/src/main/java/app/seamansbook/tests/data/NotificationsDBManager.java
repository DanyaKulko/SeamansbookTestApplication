package app.seamansbook.tests.data;

import static app.seamansbook.tests.data.QuestionsDatabaseHelper.TABLE_NOTIFICATIONS;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import app.seamansbook.tests.models.NotificationItem;

public class NotificationsDBManager {
    QuestionsDatabaseHelper db;
    Context context;

    public NotificationsDBManager(Context context) {
        this.db = new QuestionsDatabaseHelper(context);
        this.context = context;
    }

    public void saveMessage(String title, String message, String emailType, String showUpdateButton, String additionalLink) {
        try (SQLiteDatabase database = this.db.getWritableDatabase()) {

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            String currentTime = sdf.format(new Date());

            ContentValues values = new ContentValues();
            values.put("title", title);
            values.put("body", message);
            values.put("emailType", emailType);
            values.put("showUpdateButton", Integer.parseInt(showUpdateButton));
            values.put("additionalLink", additionalLink);
            values.put("timestamp", currentTime);

            database.insert(TABLE_NOTIFICATIONS, null, values);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<NotificationItem> getNotifications() {
        try (SQLiteDatabase database = this.db.getWritableDatabase()) {
            Cursor cursor = database.query(TABLE_NOTIFICATIONS, null, null, null, null, null, "timestamp DESC");
            List<NotificationItem> notifications = new ArrayList<>();
            if (cursor.moveToFirst()) {
                do {
                    int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                    String title = cursor.getString(cursor.getColumnIndexOrThrow("title"));
                    String message = cursor.getString(cursor.getColumnIndexOrThrow("body"));
                    String emailType = cursor.getString(cursor.getColumnIndexOrThrow("emailType"));
                    String additionalLink = cursor.getString(cursor.getColumnIndexOrThrow("additionalLink"));
                    int showUpdateButton = cursor.getInt(cursor.getColumnIndexOrThrow("showUpdateButton"));
                    int viewed = cursor.getInt(cursor.getColumnIndexOrThrow("viewed"));
                    String timestamp = cursor.getString(cursor.getColumnIndexOrThrow("timestamp"));

                    NotificationItem notificationItem = new NotificationItem(id, title, message, emailType, additionalLink, showUpdateButton, viewed, timestamp);
                    notifications.add(notificationItem);
                } while (cursor.moveToNext());
            }

            cursor.close();
            return notifications;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public void markAsViewed(int id) {
        try (SQLiteDatabase database = this.db.getWritableDatabase()) {
            ContentValues values = new ContentValues();
            values.put("viewed", 1);
            database.update(TABLE_NOTIFICATIONS, values, "id = ?", new String[]{String.valueOf(id)});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
