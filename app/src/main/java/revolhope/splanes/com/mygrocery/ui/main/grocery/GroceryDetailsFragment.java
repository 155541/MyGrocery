package revolhope.splanes.com.mygrocery.ui.main.grocery;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.transition.Fade;

import com.google.android.material.textfield.TextInputEditText;

import org.jetbrains.annotations.Contract;

import java.text.SimpleDateFormat;
import java.util.Locale;

import revolhope.splanes.com.mygrocery.R;
import revolhope.splanes.com.mygrocery.data.model.item.Item;

public class GroceryDetailsFragment extends Fragment {

    private final int[] iconCategory = new int[] {
            R.drawable.ic_help, R.drawable.ic_kitchen,
            R.drawable.ic_tool, R.drawable.ic_device,
            R.drawable.ic_cake, R.drawable.ic_home
    };
    private static Item item;
    private Context context;
    private FragmentManager fm;

    @Contract("_ -> new")
    public static GroceryDetailsFragment newInstance(Item item) {
        GroceryDetailsFragment.item = item;
        return new GroceryDetailsFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setEnterTransition(new Fade(Fade.IN));
        if (getContext() != null) {
            context = getContext();
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
        TextInputEditText editTextAmount = view.findViewById(R.id.editTextAmount);
        TextInputEditText editTextPriority = view.findViewById(R.id.editTextPriority);
        TextInputEditText editTextDefaultUser = view.findViewById(R.id.editTextDefaultUser);
        TextInputEditText editTextReminder = view.findViewById(R.id.editTextReminder);

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


        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fm.popBackStack();
            }
        });
    }
}
