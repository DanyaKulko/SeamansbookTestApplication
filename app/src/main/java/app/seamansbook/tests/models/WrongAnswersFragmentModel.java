package app.seamansbook.tests.models;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class WrongAnswersFragmentModel {
    private String date;
    private List<WrongAnswersModel> wrongAnswers;

    public WrongAnswersFragmentModel(String date, List<WrongAnswersModel> wrongAnswers) {
        this.date = date;
        this.wrongAnswers = wrongAnswers;
    }

    public String getDate(Locale locale) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", locale);
            Date date = dateFormat.parse(this.date);

            SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM", locale);

            return sdf.format(date).toUpperCase();
        } catch (Exception e) {
            return "";
        }
    }

    public List<WrongAnswersModel> getWrongAnswers() {
        return wrongAnswers;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setWrongAnswers(List<WrongAnswersModel> wrongAnswers) {
        this.wrongAnswers = wrongAnswers;
    }
}
