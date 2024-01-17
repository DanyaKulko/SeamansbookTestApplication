package app.seamansbook.tests.models;

public class CreateUserRequest {
    private String displayName;
    private String email;
    private String photoURL;
    private String assemblyId;

    private String token;

    public CreateUserRequest(String displayName, String email, String photoURL, String assemblyId, String token) {
        this.displayName = displayName;
        this.email = email;
        this.photoURL = photoURL;
        this.assemblyId = assemblyId;
        this.token = token;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhotoURL() {
        return photoURL;
    }

    public void setPhotoURL(String photoURL) {
        this.photoURL = photoURL;
    }

    public String getAssemblyId() {
        return assemblyId;
    }

    public void setAssemblyId(String assemblyId) {
        this.assemblyId = assemblyId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
