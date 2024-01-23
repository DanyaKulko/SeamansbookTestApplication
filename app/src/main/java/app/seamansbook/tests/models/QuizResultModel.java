package app.seamansbook.tests.models;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class QuizResultModel {

    private String id;
    private String date;
    private String score;
    private int errorsCount;

    public QuizResultModel(String id, String date, String score, int errorsCount) {
        this.id = id;
        this.date = date;
        this.score = score;
        this.errorsCount = errorsCount;
    }

    public String getId() {
        return id;
    }

    public String getDate() {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            Date date = dateFormat.parse(this.date);

            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM", Locale.getDefault());

            return sdf.format(date).toUpperCase();
        } catch (Exception e) {
            return "";
        }
    }

    public String getTime() {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            Date time = dateFormat.parse(this.date);

            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());

            return sdf.format(time);
        } catch (Exception e) {
            return "";
        }
    }

    public String getScore() {
        return score;
    }

    public int getErrorsCount() {
        return errorsCount;
    }
}
