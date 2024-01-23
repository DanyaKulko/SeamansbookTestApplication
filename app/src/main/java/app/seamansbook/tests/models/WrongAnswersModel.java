package app.seamansbook.tests.models;

import java.util.List;

public class WrongAnswersModel {
    private String id;
    private String question;
    private List<String> userAnswers;
    private List<String> correctAnswers;

    public WrongAnswersModel(String id, String question, List<String> userAnswers, List<String> correctAnswers) {
        this.id = id;
        this.question = question;
        this.userAnswers = userAnswers;
        this.correctAnswers = correctAnswers;
    }

    public String getId() {
        return id;
    }

    public String getQuestion() {
        return question;
    }

    public List<String> getUserAnswer() {
        return userAnswers;
    }

    public List<String> getCorrectAnswer() {
        return correctAnswers;
    }
}
