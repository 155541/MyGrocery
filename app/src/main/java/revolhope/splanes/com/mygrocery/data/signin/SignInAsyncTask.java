package revolhope.splanes.com.mygrocery.data.signin;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.Nullable;

import java.util.UUID;

import revolhope.splanes.com.mygrocery.data.model.User;
import revolhope.splanes.com.mygrocery.helpers.database.AppDatabase;
import revolhope.splanes.com.mygrocery.helpers.database.AppDatabaseCallback;
import revolhope.splanes.com.mygrocery.helpers.database.model.Preferences;
import revolhope.splanes.com.mygrocery.helpers.firebase.AppFirebase;

public class SignInAsyncTask extends AsyncTask<String, Void, Void>{

    private SignInCallback callback;
    private AppDatabase appDatabase;

    public SignInAsyncTask(Context context, SignInCallback callback) {
        this.callback = callback;
        this.appDatabase = new AppDatabase(context);
    }

    @Override
    protected Void doInBackground(final String... data) {
        Preferences preferences = new Preferences();
        final String uuid = UUID.randomUUID().toString().replace("-","");
        preferences.setId(uuid);
        preferences.setEmail(data[1]);
        preferences.setPwd(data[2]);
        preferences.setTarget(data[3]);
        appDatabase.insertPreferences(preferences, new AppDatabaseCallback.Modify() {
            @Override
            public void modified(@Nullable Integer affectedRows) {
                if (affectedRows != null && affectedRows == 1) {
                    final AppFirebase appFirebase = AppFirebase.getInstance();
                    appFirebase.signUp(data[1], data[2], new AppFirebase.OnComplete() {
                        @Override
                        public void taskCompleted(boolean success, Object... parameters) {
                            if (success) {
                                final User u = new User(uuid, data[0], data[1], data[3]);
                                appFirebase.pushUser(u, new AppFirebase.OnComplete() {
                                    @Override
                                    public void taskCompleted(boolean success, Object...
                                            parameters) {
                                        if (success) {
                                            callback.onComplete(true, u);
                                        }
                                        else {
                                            appDatabase.removePreferences();
                                            callback.onComplete(false, parameters[0]);
                                        }
                                    }
                                });
                            }
                            else {
                                appDatabase.removePreferences();
                                callback.onComplete(false, (Exception) parameters[0]);
                            }
                        }
                    });
                }
                else callback.onComplete(false, "Database return null");
            }
        });
        return null;
    }
}
