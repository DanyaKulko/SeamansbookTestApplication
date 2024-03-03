package app.seamansbook.tests.models;

public class StatisticsMainInfoModel {
    private int successfulTestsCount;
    private String failedTestsCount;
    private String knowledgeLevel;
    private int firstScore;
    private int averageScore;
    private int bestScore;

    public StatisticsMainInfoModel(int successfulTestsCount, String failedTestsCount, int firstScore, int averageScore, int bestScore) {
        this.successfulTestsCount = successfulTestsCount;
        this.failedTestsCount = failedTestsCount;
        this.knowledgeLevel = knowledgeLevel;
        this.firstScore = firstScore;
        this.averageScore = averageScore;
        this.bestScore = bestScore;
    }

    public int getSuccessfulTestsCount() {
        return successfulTestsCount;
    }

    public String getFailedTestsCount() {
        return failedTestsCount;
    }

    public String getKnowledgeLevel() {
        return knowledgeLevel;
    }

    public int getFirstScore() {
        return firstScore;
    }

    public int getAverageScore() {
        return averageScore;
    }

    public int getBestScore() {
        return bestScore;
    }
}
