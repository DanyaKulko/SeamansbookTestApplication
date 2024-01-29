package app.seamansbook.tests.models;

public class LanguageModel {
    private final String code;
    private final String name;

    public LanguageModel(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

}
