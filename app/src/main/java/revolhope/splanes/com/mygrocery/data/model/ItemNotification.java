package revolhope.splanes.com.mygrocery.data.model;

import java.io.Serializable;

public class ItemNotification implements Serializable {

    private String itemName;
    private String action;
    private String category;
    private String amount;
    private String priority;
    private String description;

    public ItemNotification() {
    }

    public ItemNotification(String itemName, String action, String category, String amount,
                            String priority, String description) {
        this.itemName = itemName;
        this.action = action;
        this.category = category;
        this.amount = amount;
        this.priority = priority;
        this.description = description;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
