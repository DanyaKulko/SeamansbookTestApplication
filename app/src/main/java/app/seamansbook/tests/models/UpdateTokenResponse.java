package app.seamansbook.tests.models;

public class UpdateTokenResponse {
    private String id;

    public UpdateTokenResponse(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
