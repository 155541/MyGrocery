package revolhope.splanes.com.mygrocery.ui.grocery.details;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.transition.Fade;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import org.jetbrains.annotations.Contract;

import java.text.SimpleDateFormat;
import java.util.Locale;

import revolhope.splanes.com.mygrocery.R;
import revolhope.splanes.com.mygrocery.data.model.item.Item;
import revolhope.splanes.com.mygrocery.ui.MainActivity;

public class FragmentGroceryItemDetails extends Fragment {

    private final int[] iconCategory = new int[] {
            R.drawable.ic_help, R.drawable.ic_kitchen,
            R.drawable.ic_tool, R.drawable.ic_device,
            R.drawable.ic_cake, R.drawable.ic_home
    };
    private static Item item;
    private Context context;
    private MainActivity activity;
    private FragmentManager fm;

    @Contract("_ -> new")
    public static FragmentGroceryItemDetails newInstance(Item item) {
        FragmentGroceryItemDetails.item = item;
        return new FragmentGroceryItemDetails();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setEnterTransition(new Fade(Fade.IN));
        if (getContext() != null && getActivity() != null) {
            context = getContext();
            activity = (MainActivity) getActivity();
        }
        if (getFragmentManager() != null) {
            fm = getFragmentManager();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return  inflater.inflate(R.layout.fragment_grocery_details, container,
                false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView title = view.findViewById(R.id.textViewItemName);
        TextView dateCreate = view.findViewById(R.id.textViewDateCreated);
        TextInputEditText editTextCategory = view.findViewById(R.id.editTextCategory);
        final TextInputEditText editTextAmount = view.findViewById(R.id.editTextAmount);
        TextInputEditText editTextPriority = view.findViewById(R.id.editTextPriority);
        TextInputEditText editTextDefaultUser = view.findViewById(R.id.editTextDefaultUser);
        TextInputEditText editTextReminder = view.findViewById(R.id.editTextReminder);
        MaterialButton buttonBuy = view.findViewById(R.id.buttonBuy);
        MaterialButton buttonActions = view.findViewById(R.id.buttonActions);

        ImageView icon = view.findViewById(R.id.iconCategory);
        ImageView buttonBack = view.findViewById(R.id.buttonBack);

        Resources resources = context.getResources();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.FRANCE);
        title.setText(item.getItemName());
        dateCreate.setText(sdf.format(item.getDateCreated()));
        icon.setImageResource(iconCategory[item.getCategory()]);
        editTextCategory.setText(resources.getStringArray(R.array.categories)[item.getCategory()]);
        editTextAmount.setText(String.valueOf(item.getAmount()));
        editTextPriority.setText(resources.getStringArray(R.array.priorities)[item.getPriority()]);

        if (item.getDateReminder() > 0) {
            editTextReminder.setText(sdf.format(item.getDateReminder()));
        } else {
            editTextReminder.setText("Sense recordatori");
        }

        StringBuilder sb = new StringBuilder();
        int i = 0;
        for (String target : item.getUsersTarget()) {
            if (i > 0) sb.append("\n");
            sb.append(target);
            i++;
        }
        editTextDefaultUser.setText(sb.toString());

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.showItemList();
            }
        });
        buttonBuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int newAmount = item.getAmount()-1;
                if (newAmount > 0) {
                    item.setAmount(newAmount);
                    editTextAmount.setText(String.valueOf(newAmount));
                    activity.itemBought(item);
                }
                else {
                    activity.deleteItem(item);
                }
            }
        });
        buttonActions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DialogActions dialogActions = new DialogActions();
                dialogActions.setOnActionSelectedListener(
                        new DialogActions.OnActionSelectedListener() {
                    @Override
                    public void onActionSelected(int option) {
                        switch (option) {
                            case DialogActions.ACTION_EDIT:
                                activity.showEditItem(item);
                                break;
                            case DialogActions.ACTION_HISTORICAL:
                                activity.showHistoricItem(item);
                                break;
                            case DialogActions.ACTION_DELETE:
                                DialogDelete dialogDelete = new DialogDelete();
                                dialogDelete.setOnConfirmListener(
                                        new DialogDelete.OnConfirmListener() {
                                    @Override
                                    public void onConfirm() {
                                        activity.deleteItem(item);
                                    }
                                });
                                dialogDelete.show(fm, DialogDelete.TAG);
                                break;
                        }
                    }
                });
                dialogActions.show(fm, DialogActions.TAG);
            }
        });
    }
}
