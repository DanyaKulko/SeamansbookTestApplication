package app.seamansbook.tests.models;

public class StatisticsMainInfoModel {
    private String successfulTestsCount;
    private String failedTestsCount;
    private String knowledgeLevel;
    private String firstScore;
    private String averageScore;
    private String bestScore;

    public StatisticsMainInfoModel(String successfulTestsCount, String failedTestsCount, String knowledgeLevel, String firstScore, String averageScore, String bestScore) {
        this.successfulTestsCount = successfulTestsCount;
        this.failedTestsCount = failedTestsCount;
        this.knowledgeLevel = knowledgeLevel;
        this.firstScore = firstScore;
        this.averageScore = averageScore;
        this.bestScore = bestScore;
    }

    public String getSuccessfulTestsCount() {
        return successfulTestsCount;
    }

    public String getFailedTestsCount() {
        return failedTestsCount;
    }

    public String getKnowledgeLevel() {
        return knowledgeLevel;
    }

    public String getFirstScore() {
        return firstScore;
    }

    public String getAverageScore() {
        return averageScore;
    }

    public String getBestScore() {
        return bestScore;
    }
}
