package revolhope.splanes.com.mygrocery.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import revolhope.splanes.com.mygrocery.R;
import revolhope.splanes.com.mygrocery.data.model.ItemHistoric;
import revolhope.splanes.com.mygrocery.data.model.User;
import revolhope.splanes.com.mygrocery.data.model.item.Item;
import revolhope.splanes.com.mygrocery.helpers.firebase.AppFirebase;
import revolhope.splanes.com.mygrocery.helpers.firebase.service.ItemService;
import revolhope.splanes.com.mygrocery.helpers.reminder.AppReminder;
import revolhope.splanes.com.mygrocery.ui.grocery.details.FragmentGroceryItemDetails;
import revolhope.splanes.com.mygrocery.ui.grocery.edition.FragmentEditGroceryItem;
import revolhope.splanes.com.mygrocery.ui.grocery.historic.FragmentItemHistoric;
import revolhope.splanes.com.mygrocery.ui.grocery.list.FragmentGroceryList;
import revolhope.splanes.com.mygrocery.ui.loader.FragmentLoader;
import revolhope.splanes.com.mygrocery.ui.signin.FragmentSignIn;

public class MainActivity extends AppCompatActivity {

    private FragmentManager fragmentManager;
    private User appUser;
    private Item[] pendingItems;

    private TextView textViewUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fragmentManager = getSupportFragmentManager();
        textViewUser = findViewById(R.id.textViewUser);
        showLoader();
    }

    private void showLoader() {
        fragmentManager
                .beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .replace(R.id.container, FragmentLoader.newInstance())
                .commit();
    }

    public void showSignIn() {
        fragmentManager
                .beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .replace(R.id.container, FragmentSignIn.newInstance())
                .commit();
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
        fragmentManager.popBackStack();
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
            appFirebase.pushItem(item, false, appUser.getId(),new AppFirebase.OnComplete() {
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
            ItemHistoric itemHistoric = new ItemHistoric();
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.FRANCE);
            itemHistoric.setUserName(appUser.getDisplayName());
            itemHistoric.setAction("Acció: creació d'un nou article");
            itemHistoric.setPreviousState("Article creat: " + item.getItemName());
            itemHistoric.setNewState("Quantitat: " + item.getAmount());
            itemHistoric.setDate(sdf.format(new Date(item.getDateCreated())));
            appFirebase.pushHistoric(item.getId(), itemHistoric);
        }
    }

    public void showEditItem(final Item item) {
        fragmentManager.popBackStack();
        fragmentManager.beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .replace(R.id.container,
                        FragmentEditGroceryItem.newInstance(true, item))
                .addToBackStack(null)
                .commit();
    }

    public void showHistoricItem(final Item item) {
        fragmentManager.beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .replace(R.id.container, FragmentItemHistoric.newInstance(item))
                .addToBackStack(null)
                .commit();
    }

    public void itemUpdated(final Item item) {
        deleteItem(item);
        if (item != null) {
            AppFirebase appFirebase = AppFirebase.getInstance();
            appFirebase.pushItem(item, true, appUser.getId(), new AppFirebase.OnComplete() {
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
            ItemHistoric itemHistoric = new ItemHistoric();
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.FRANCE);
            itemHistoric.setUserName(appUser.getDisplayName());
            itemHistoric.setAction("Acció: edició d'un article");
            itemHistoric.setPreviousState("Article modificat: " + item.getItemName());
            itemHistoric.setNewState("Quantitat: " + item.getAmount());
            itemHistoric.setDate(sdf.format(Calendar.getInstance().getTime()));
            appFirebase.pushHistoric(item.getId(), itemHistoric);
        }
    }

    public void itemBought(final Item item) {
        deleteItem(item);
        if (item != null) {
            AppFirebase appFirebase = AppFirebase.getInstance();
            appFirebase.pushItem(item, true, appUser.getId(), new AppFirebase.OnComplete() {
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
            ItemHistoric itemHistoric = new ItemHistoric();
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.FRANCE);
            itemHistoric.setUserName(appUser.getDisplayName());
            itemHistoric.setAction("Acció: comprar " + item.getItemName());
            itemHistoric.setPreviousState("Quantitat comprada: 1");
            itemHistoric.setNewState("Resten: " + item.getAmount());
            itemHistoric.setDate(sdf.format(Calendar.getInstance().getTime()));
            appFirebase.pushHistoric(item.getId(), itemHistoric);
        }
    }

    public void deleteItem(final Item item) {
        AppFirebase appFirebase = AppFirebase.getInstance();
        appFirebase.deleteItem(item.getId(), appUser.getId(), new AppFirebase.OnComplete() {
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

    public void setAppUser(User u) {
        this.appUser = u;
        textViewUser.setText(u.getDisplayName());
        Intent intent = new Intent(this, ItemService.class);
        intent.putExtra(ItemService.INTENT_USERID, u.getId());
        startService(intent);
    }

    public User getAppUser() {
        return this.appUser;
    }

    public void setPendingItems(List<Item> items) {
        this.pendingItems = items.toArray(new Item[0]);
    }

    public void pop() {
        fragmentManager.popBackStack();
    }

    @Override
    protected void onDestroy() {
        AppFirebase appFirebase = AppFirebase.getInstance();
        appFirebase.deleteMyItems(appUser.getId());
        for (Item item : pendingItems) {
            item.setIsSeen(1);
            appFirebase.pushItemForMe(item, appUser.getId());
        }
        appFirebase.signOut();
        super.onDestroy();
    }
}