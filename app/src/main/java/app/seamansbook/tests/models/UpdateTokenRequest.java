package app.seamansbook.tests.models;

import app.seamansbook.tests.Config;

public class UpdateTokenRequest {
    private String id;
    private String token;
    private String assemblyId;

    public UpdateTokenRequest(String id, String token) {
        this.id = id;
        this.token = token;
        this.assemblyId = Config.APP_KEY;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getAssemblyId() {
        return assemblyId;
    }

    public void setAssemblyId(String assemblyId) {
        this.assemblyId = assemblyId;
    }
}
