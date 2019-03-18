package revolhope.splanes.com.mygrocery.helpers.repository;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import revolhope.splanes.com.mygrocery.R;
import revolhope.splanes.com.mygrocery.data.model.User;

public class AppRepository {

    public static final int[] ICONS_FILTER = new int[]{
            R.drawable.ic_grocery, R.drawable.ic_help,
            R.drawable.ic_kitchen, R.drawable.ic_tool,
            R.drawable.ic_device, R.drawable.ic_cake,
            R.drawable.ic_home, R.drawable.ic_priority_high2,
            R.drawable.ic_priority_medium, R.drawable.ic_priority_low
    };

    private AppRepository() {
    }

    private static User appUser;
    private static List<User> users = new ArrayList<>();


    public static void setAppUser(User user) {
        appUser = user;
    }

    @Contract(pure = true)
    public static User getAppUser() { return appUser; }

    @Contract(pure = true)
    public static List<User> getUsers() {
        return users;
    }

    public static void setUsers(List<User> _users) {
        users = _users;
    }

    @Nullable
    public static String getDisplayNameFromEmail(String email) {
        for (User u : users) {
            if (u.getEmail().equals(email)) {
                return u.getDisplayName();
            }
        }
        return null;
    }
}
