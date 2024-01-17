package app.seamansbook.tests.models;

import java.util.List;

import app.seamansbook.tests.R;

public class Question {
    private String _id;
    private String question;
    private List<String> answers;

    private String type;
    private String media;
    private String submodule;
    private List<Integer> correctAnswersIndexes;

    public Question(String _id, String question, List<String> answers, List<Integer> correctAnswersIndexes, String type) {
        this._id = _id;
        this.question = question;
        this.answers = answers;
        this.correctAnswersIndexes = correctAnswersIndexes;
        this.type = type;
    }
    public Question(String _id, String question, List<String> answers, List<Integer> correctAnswersIndexes, String type, String media, String submodule) {
        this._id = _id;
        this.question = question;
        this.answers = answers;
        this.correctAnswersIndexes = correctAnswersIndexes;
        this.type = type;
        this.media = media;
        this.submodule = submodule;
    }
    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }
    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public List<String> getAnswers() {
        return answers;
    }

    public void setAnswers(List<String> answers) {
        this.answers = answers;
    }

    public List<Integer> getCorrectAnswersIndexes() {
        return correctAnswersIndexes;
    }

    public void setCorrectAnswersIndexes(List<Integer> correctAnswersIndexes) {
        this.correctAnswersIndexes = correctAnswersIndexes;
    }

    public Boolean hasOneCorrectAnswer() {
        return correctAnswersIndexes.size() == 1;
    }

    public int getButtonType() {
        return hasOneCorrectAnswer() ? 1 : 2;
    }

    public int getButtonId() {
        return hasOneCorrectAnswer() ? R.id.radioButtonAnswer : R.id.checkboxButtonAnswer;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMedia() {
        return media;
    }

    public void setMedia(String media) {
        this.media = media;
    }

    public String getSubmodule() {
        return submodule;
    }

    public void setSubmodule(String submodule) {
        this.submodule = submodule;
    }
}
