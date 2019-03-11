package revolhope.splanes.com.mygrocery.helpers.database.model;

public class Preferences {
    private String id;
    private String email;
    private String pwd;
    private String target;

    public Preferences() {
    }

    public Preferences(String id, String email, String pwd, String target) {
        this.id = id;
        this.email = email;
        this.pwd = pwd;
        this.target = target;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }
}
