package revolhope.splanes.com.mygrocery.data.model;

import java.io.Serializable;

public class ItemHistoric implements Serializable {

    private String userName;
    private String action;
    private String previousState;
    private String newState;
    private String date;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getPreviousState() {
        return previousState;
    }

    public void setPreviousState(String previousState) {
        this.previousState = previousState;
    }

    public String getNewState() {
        return newState;
    }

    public void setNewState(String newState) {
        this.newState = newState;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
