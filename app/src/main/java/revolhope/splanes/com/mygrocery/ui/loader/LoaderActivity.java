package revolhope.splanes.com.mygrocery.ui.loader;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.TextView;

import java.util.Arrays;

import revolhope.splanes.com.mygrocery.R;
import revolhope.splanes.com.mygrocery.data.model.User;
import revolhope.splanes.com.mygrocery.data.model.item.Item;
import revolhope.splanes.com.mygrocery.helpers.database.AppDatabase;
import revolhope.splanes.com.mygrocery.helpers.database.AppDatabaseCallback;
import revolhope.splanes.com.mygrocery.helpers.database.model.Preferences;
import revolhope.splanes.com.mygrocery.helpers.firebase.AppFirebase;
import revolhope.splanes.com.mygrocery.helpers.firebase.service.ItemService;
import revolhope.splanes.com.mygrocery.helpers.repository.AppRepository;
import revolhope.splanes.com.mygrocery.ui.login.LoginActivity;
import revolhope.splanes.com.mygrocery.ui.grocery.MainActivity;

public class LoaderActivity extends AppCompatActivity {
    private TextView textViewProgress;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loader);


        textViewProgress = findViewById(R.id.info);
        textViewProgress.setText(R.string.loader_init_info);

        AppDatabase appDatabase = new AppDatabase(this);
        appDatabase.selectPreferences(new AppDatabaseCallback.Select() {
            @Override
            public void selected(@Nullable final Preferences pref) {
                if (pref == null) {
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
                else {
                    connect(pref);
                }
            }
        });
    }

    private void connect(final Preferences preferences) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                textViewProgress.setText(R.string.loader_connecting_firebase);
            }
        });
        final AppFirebase appFirebase = AppFirebase.getInstance();
        appFirebase.signIn(preferences.getEmail(), preferences.getPwd(),
                new AppFirebase.OnComplete() {
            @Override
            public void taskCompleted(boolean success, Object... parameters) {
                if (success) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            textViewProgress.setText(R.string.loader_success_firebaselogin);
                        }
                    });
                    fetchUser(preferences.getId());
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            textViewProgress.setError(getString(R.string.loader_fail_firebaselogin));
                            findViewById(R.id.errorIcon).setVisibility(View.VISIBLE);
                            findViewById(R.id.progressBar).setVisibility(View.GONE);
                        }
                    });
                }
            }
        });
    }

    private void fetchUser(String id) {
        final AppFirebase appFirebase = AppFirebase.getInstance();
        appFirebase.fetchUser(id, new AppFirebase.OnComplete() {
            @Override
            public void taskCompleted(boolean success, final Object... parameters) {
                if (success) {
                    AppRepository.setAppUser((User)parameters[0]);
                    initialize();
                }
                else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            textViewProgress.setError(parameters[0] != null ?
                                    ((String) parameters[0]) :
                                    getString(R.string.loader_fail_user));
                            findViewById(R.id.errorIcon).setVisibility(View.VISIBLE);
                            findViewById(R.id.progressBar).setVisibility(View.GONE);
                        }
                    });
                }
            }
        });
    }

    private void initialize() {
        final AppFirebase appFirebase = AppFirebase.getInstance();
        appFirebase.fetchItems(AppRepository.getAppUser().getId(), new AppFirebase.OnComplete() {
            @Override
            public void taskCompleted(boolean success, Object... parameters) {
                if (success) {
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    if (parameters != null) {
                        Item[] items = new Item[parameters.length];
                        int i = 0;
                        for (Object o : parameters) {
                            items[i] = (Item) o;
                            i++;
                        }
                        intent.putExtra(MainActivity.ITEMS, items);
                    }
                    Intent service = new Intent(getApplicationContext(), ItemService.class);
                    startService(service);
                    appFirebase.fetchUsers(new AppFirebase.OnComplete(){
                        @Override
                        public void taskCompleted(boolean success, Object... parameters) {
                            if (success) {
                                User[] users = (User[]) parameters;
                                AppRepository.setUsers(Arrays.asList(users));
                            }
                        }
                    });
                    startActivity(intent);
                    setResult(Activity.RESULT_OK);
                    finish();
                }
            }
        });
    }
}
