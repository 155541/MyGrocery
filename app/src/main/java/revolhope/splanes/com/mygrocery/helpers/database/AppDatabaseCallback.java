package revolhope.splanes.com.mygrocery.helpers.database;

import androidx.annotation.Nullable;

import revolhope.splanes.com.mygrocery.helpers.database.model.Preferences;

public class AppDatabaseCallback {

    private AppDatabaseCallback(){
    }

    public interface Select {
        void selected(@Nullable Preferences preferences);
    }

    public interface Modify {
        void modified(@Nullable Integer affectedRows);
    }
}
