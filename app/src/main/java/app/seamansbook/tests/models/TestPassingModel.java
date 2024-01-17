package app.seamansbook.tests.models;

import java.util.List;
import java.util.Objects;

public class TestPassingModel {
    private int id;
    private List<String> questions;
    private List<String> answers;
    private int currentQuestion;
    private boolean isCompleted;

    public TestPassingModel(int id, List<String> questions, List<String> answers, int currentQuestion, String isCompleted) {
        this.id = id;
        this.questions = questions;
        this.answers = answers;
        this.currentQuestion = currentQuestion;
        this.isCompleted = Objects.equals(isCompleted, "true");
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<String> getQuestions() {
        return questions;
    }

    public void setQuestions(List<String> questions) {
        this.questions = questions;
    }

    public List<String> getAnswers() {
        return answers;
    }

    public void setAnswers(List<String> answers) {
        this.answers = answers;
    }

    public int getCurrentQuestion() {
        return currentQuestion;
    }

    public void setCurrentQuestion(int currentQuestion) {
        this.currentQuestion = currentQuestion;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }
}
