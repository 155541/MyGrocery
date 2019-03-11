package revolhope.splanes.com.mygrocery.data.model;

public class User {

    private String id;
    private String displayName;
    private String email;
    private String defaultUserTarget;

    public User(String id, String displayName, String email, String defaultUserTarget) {
        this.id = id;
        this.displayName = displayName;
        this.email = email;
        this.defaultUserTarget = defaultUserTarget;
    }

    public User() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getDefaultUserTarget() {
        return defaultUserTarget;
    }

    public void setDefaultUserTarget(String defaultUserTarget) {
        this.defaultUserTarget = defaultUserTarget;
    }
}
