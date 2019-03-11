package revolhope.splanes.com.mygrocery.helpers.repository;

import java.util.ArrayList;
import java.util.List;

import revolhope.splanes.com.mygrocery.data.model.User;
import revolhope.splanes.com.mygrocery.data.model.item.Item;

public class AppRepository {

    private AppRepository() {
    }

    private static User appUser;
    private static List<Item> items = new ArrayList<>();

    public static void setAppUser(User user) {
        appUser = user;
    }

    public static User getAppUser() { return appUser; }

    public static List<Item> getItems() {
        return items;
    }

    public static List<Item> getItemsCopy() {
        return new ArrayList<>(items);
    }

    public static void addItem(Item item) {
        items.add(item);
    }
}
