package app.seamansbook.tests.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Response2 {
    @SerializedName("version")
    private String version;

    @SerializedName("title")
    private String title;
    @SerializedName("passingPercent")
    private float passingPercent;
    @SerializedName("questions")
    private List<Question> questions;

    public Response2(String version, String title, float passingPercent, List<Question> questions) {
        this.title = title;
        this.passingPercent = passingPercent;
        this.version = version;
        this.questions = questions;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> question) {
        this.questions = question;
    }

    public float getPassingPercent() {
        return passingPercent;
    }

    public void setPassingPercent(float passingPercent) {
        this.passingPercent = passingPercent;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
