package app.seamansbook.tests.models;

public class ThemeModel {
    private final int code;
    private final String name;

    public ThemeModel(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
