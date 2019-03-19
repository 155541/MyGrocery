package revolhope.splanes.com.mygrocery.data.model.item;

import java.io.Serializable;
import java.util.List;

public class Item implements Serializable {
    private String id;
    private String itemName;
    private int amount;
    private int category;
    private int priority;
    private long dateReminder;
    private int isReminderSet;
    private int isSeen;
    private long dateCreated;
    private String userCreated;
    private List<String> usersTarget;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public long getDateReminder() {
        return dateReminder;
    }

    public void setDateReminder(long dateReminder) {
        this.dateReminder = dateReminder;
    }

    public long getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(long dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getUserCreated() {
        return userCreated;
    }

    public void setUserCreated(String userCreated) {
        this.userCreated = userCreated;
    }

    public List<String> getUsersTarget() {
        return usersTarget;
    }

    public void setUsersTarget(List<String> usersTarget) {
        this.usersTarget = usersTarget;
    }

    public int getIsReminderSet() {
        return isReminderSet;
    }

    public void setIsReminderSet(int isReminderSet) {
        this.isReminderSet = isReminderSet;
    }

    public int getIsSeen() {
        return isSeen;
    }

    public void setIsSeen(int isSeen) {
        this.isSeen = isSeen;
    }
}
