package revolhope.splanes.com.mygrocery.ui.loader;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.jetbrains.annotations.Contract;

import java.util.Arrays;

import revolhope.splanes.com.mygrocery.R;
import revolhope.splanes.com.mygrocery.data.model.User;
import revolhope.splanes.com.mygrocery.data.model.item.Item;
import revolhope.splanes.com.mygrocery.helpers.database.AppDatabase;
import revolhope.splanes.com.mygrocery.helpers.database.AppDatabaseCallback;
import revolhope.splanes.com.mygrocery.helpers.database.model.Preferences;
import revolhope.splanes.com.mygrocery.helpers.firebase.AppFirebase;
import revolhope.splanes.com.mygrocery.helpers.reminder.AppReminder;
import revolhope.splanes.com.mygrocery.ui.MainActivity;

public class FragmentLoader extends Fragment {

    private MainActivity activity;
    private Context context;

    private TextView textViewProgress;
    private ProgressBar progressBar;
    private ImageView errorIcon;

    @Contract(" -> new")
    public static FragmentLoader newInstance() {
        return new FragmentLoader();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
        activity = (MainActivity) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        if (activity != null && context != null)
            return inflater.inflate(R.layout.fragment_loader, container, false);
        else
            return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        textViewProgress = view.findViewById(R.id.info);
        progressBar = view.findViewById(R.id.progressBar);
        errorIcon = view.findViewById(R.id.errorIcon);

        textViewProgress.setText(R.string.loader_init_info);
        AppDatabase appDatabase = new AppDatabase(context);
        appDatabase.selectPreferences(new AppDatabaseCallback.Select() {
            @Override
            public void selected(@Nullable final Preferences pref) {
                if (pref == null) {
                    activity.showSignIn();
                }
                else {
                    connect(pref);
                }
            }
        });
    }

    private void connect(final Preferences preferences) {
        activity.runOnUiThread(new Runnable() {
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
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    textViewProgress.setText(R.string.loader_success_firebaselogin);
                                }
                            });
                            fetchUser(preferences.getId());
                        } else {
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    textViewProgress.setError(getString(R.string.loader_fail_firebaselogin));
                                    errorIcon.setVisibility(View.VISIBLE);
                                    progressBar.setVisibility(View.GONE);
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
                    initialize((User) parameters[0]);
                }
                else {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            textViewProgress.setError(parameters[0] != null ?
                                    ((String) parameters[0]) :
                                    getString(R.string.loader_fail_user));
                            errorIcon.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.GONE);
                        }
                    });
                }
            }
        });
    }

    private void initialize(final User user) {
        final AppFirebase appFirebase = AppFirebase.getInstance();
        appFirebase.fetchItems(user.getId(), new AppFirebase.OnComplete() {
            @Override
            public void taskCompleted(boolean success, Object... parameters) {
                if (success) {
                    boolean alreadyShown = false;
                    if (parameters != null) {
                        Item[] items = new Item[parameters.length];
                        int i = 0;
                        for (Object o : parameters) {
                            items[i] = (Item) o;
                            i++;
                        }
                        activity.setPendingItems(Arrays.asList(items));
                        activity.setAppUser(user);
                        for (Item item : items) {
                            if (item.getIsReminderSet() == 0 && item.getDateReminder() != 0L) {
                                AppReminder.setReminder(item, context.getContentResolver());
                                item.setIsReminderSet(1);
                                activity.itemUpdated(item);
                                alreadyShown = true;
                            }
                        }
                    }
                    if (!alreadyShown) activity.showItemList();
                }
            }
        });
    }
}
