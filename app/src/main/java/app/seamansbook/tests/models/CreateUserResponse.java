package app.seamansbook.tests.models;

import com.google.gson.annotations.SerializedName;

public class CreateUserResponse {

    @SerializedName("_id")
    private String id;

    public CreateUserResponse(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
