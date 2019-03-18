package revolhope.splanes.com.mygrocery.ui.grocery;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import revolhope.splanes.com.mygrocery.R;
import revolhope.splanes.com.mygrocery.data.model.item.Item;
import revolhope.splanes.com.mygrocery.helpers.firebase.AppFirebase;
import revolhope.splanes.com.mygrocery.helpers.repository.AppRepository;
import revolhope.splanes.com.mygrocery.ui.grocery.details.FragmentGroceryItemDetails;
import revolhope.splanes.com.mygrocery.ui.grocery.edition.FragmentEditGroceryItem;
import revolhope.splanes.com.mygrocery.ui.grocery.list.FragmentGroceryList;

public class MainActivity extends AppCompatActivity{

    public static final String ITEMS = "PendingItems";
    private FragmentManager fragmentManager;
    private Item[] pendingItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragmentManager = getSupportFragmentManager();
        TextView textViewUser = findViewById(R.id.textViewUser);

        if (AppRepository.getAppUser() != null &&
            AppRepository.getAppUser().getDisplayName() != null) {
            textViewUser.setText(AppRepository.getAppUser().getDisplayName());
        }
        pendingItems = (Item[]) getIntent().getSerializableExtra(ITEMS);
        showItemList();
    }

    public void createNewItem() {
        fragmentManager.beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .replace(R.id.container,
                        FragmentEditGroceryItem.newInstance(false, null))
                .addToBackStack(null)
                .commit();
    }

    public void showItemList() {
        FragmentGroceryList fragmentGroceryList = new FragmentGroceryList();
        Bundle bundle = new Bundle();
        bundle.putSerializable(FragmentGroceryList.ARG_PENDING_ITEMS,
                pendingItems);
        fragmentGroceryList.setArguments(bundle);
        fragmentManager
                .beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .replace(R.id.container, fragmentGroceryList)
                .commit();
    }

    public void showItemDetails(Item item) {
        fragmentManager.beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .replace(R.id.container, FragmentGroceryItemDetails.newInstance(item))
                .addToBackStack(null)
                .commit();
    }

    public void newItemCreated(final Item item) {
        if (item != null) {
            AppFirebase appFirebase = AppFirebase.getInstance();
            appFirebase.pushItem(item, new AppFirebase.OnComplete() {
                @Override
                public void taskCompleted(boolean success, Object... parameters) {
                    if (success) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                List<Item> items = new ArrayList<>(Arrays.asList(pendingItems));
                                items.add(item);
                                pendingItems = items.toArray(new Item[0]);
                                showItemList();
                            }
                        });
                    }
                }
            });
        }
    }

    public void itemUpdated(final Item item) {
       /* if (item != null) {
            AppFirebase appFirebase = AppFirebase.getInstance();
            appFirebase.pushItem(item, new AppFirebase.OnComplete() {
                @Override
                public void taskCompleted(boolean success, Object... parameters) {
                    if (success) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                fragmentGroceryList.addItem(item);
                            }
                        });
                    }
                }
            });
        }*/
    }

    public void deleteItem(final Item item) {
        AppFirebase appFirebase = AppFirebase.getInstance();
        appFirebase.deleteItem(item.getId(), new AppFirebase.OnComplete() {
            @Override
            public void taskCompleted(boolean success, Object... parameters) {
                if (success) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            List<Item> aux = new ArrayList<>();
                            for (Item it : pendingItems) {
                                if (!it.getId().equals(item.getId())) {
                                    aux.add(it);
                                }
                            }
                            pendingItems = aux.toArray(new Item[0]);
                            showItemList();
                        }
                    });
                }
            }
        });
    }

    public void setPendingItems(List<Item> items) {
        this.pendingItems = items.toArray(new Item[0]);
    }
}