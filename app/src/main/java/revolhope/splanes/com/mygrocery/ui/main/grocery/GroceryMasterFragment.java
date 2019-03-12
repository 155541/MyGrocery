package revolhope.splanes.com.mygrocery.ui.main.grocery;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.Fade;

import org.jetbrains.annotations.Contract;

import java.util.ArrayList;
import java.util.List;

import revolhope.splanes.com.mygrocery.R;
import revolhope.splanes.com.mygrocery.data.model.item.Filter;
import revolhope.splanes.com.mygrocery.data.model.item.Item;
import revolhope.splanes.com.mygrocery.data.model.item.ItemViewModel;
import revolhope.splanes.com.mygrocery.data.model.item.ItemViewModelFactory;
import revolhope.splanes.com.mygrocery.helpers.firebase.AppFirebase;
import revolhope.splanes.com.mygrocery.helpers.repository.AppRepository;
import revolhope.splanes.com.mygrocery.ui.main.grocery.item.GroceryItemFragment;
import revolhope.splanes.com.mygrocery.ui.main.grocery.item.OnItemCreatedListener;

public class GroceryMasterFragment extends Fragment implements OnItemCreatedListener {

    private static final int[] filterIcons = new int[]{
            R.drawable.ic_grocery, R.drawable.ic_help,
            R.drawable.ic_kitchen, R.drawable.ic_tool,
            R.drawable.ic_device, R.drawable.ic_cake,
            R.drawable.ic_home, R.drawable.ic_priority_high2,
            R.drawable.ic_priority_medium, R.drawable.ic_priority_low
    };
    private static String[] filterStrings;
    private static OnItemCreatedListener onItemCreatedListener;
    private static OnItemClickListener onItemClickListener;
    private static List<Item> pendingItems;

    private Context context;
    private Activity activity;
    private FragmentManager fragmentManager;
    private GroceryListAdapter adapter;
    private ItemViewModel itemViewModel;

    @Contract("_, _ -> new")
    static GroceryMasterFragment newInstance(OnItemClickListener itemClickListener,
                                             List<Item> items) {
        onItemClickListener = itemClickListener;
        pendingItems = items;
        return new GroceryMasterFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getContext() == null || getFragmentManager() == null || getActivity() == null) return;
        else {
            context = getContext();
            activity = getActivity();
            fragmentManager = getFragmentManager();
        }

        filterStrings = context.getResources().getStringArray(R.array.filters);
        itemViewModel = ViewModelProviders.of(this,
                new ItemViewModelFactory(filterStrings)).get(ItemViewModel.class);
        adapter = new GroceryListAdapter(context, onItemClickListener);
        onItemCreatedListener = this;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_grocery_master, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final ImageView imageViewAppliedFilter = view.findViewById(R.id.imageViewFilter);
        final TextView textViewAppliedFilter = view.findViewById(R.id.textViewFilter);

        imageViewAppliedFilter.setImageResource(filterIcons[0]);
        textViewAppliedFilter.setText(filterStrings[0]);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewList);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutAnimation(AnimationUtils.
                loadLayoutAnimation(context, R.anim.layout_animation_fall_down));

        itemViewModel.getItems().observe(this, new Observer<List<Item>>() {
            @Override
            public void onChanged(@Nullable List<Item> items) {
                adapter.update(itemViewModel.getFilteredItems());
            }
        });
        itemViewModel.itemsDataChanged(pendingItems);
        itemViewModel.filterDataChanged(0);
        itemViewModel.getFilter().observe(this, new Observer<Filter>() {
            @Override
            public void onChanged(@Nullable Filter filter) {
                if (filter == null) return;
                imageViewAppliedFilter.setImageResource(filterIcons[filter.getIndex()]);
                textViewAppliedFilter.setText(filterStrings[filter.getIndex()]);
                adapter.update(itemViewModel.getFilteredItems());
            }
        });

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final SelectFilterModal selectFilterModal = new SelectFilterModal();
                selectFilterModal.setCallback(new SelectFilterModal.Callback() {
                    @Override
                    public void onPickedValue(int value) {
                        itemViewModel.filterDataChanged(value);
                        selectFilterModal.dismiss();
                    }
                });
                if (getFragmentManager() != null)
                    selectFilterModal.show(getFragmentManager(), SelectFilterModal.TAG);
            }
        };

        view.findViewById(R.id.imageViewApplyFilter).setOnClickListener(listener);
        textViewAppliedFilter.setOnClickListener(listener);

        view.findViewById(R.id.buttonAdd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setEnterTransition(new Fade(Fade.IN));
                setExitTransition(new Fade(Fade.OUT));
                fragmentManager.beginTransaction()
                        .replace(R.id.container, GroceryItemFragment
                                .newInstance(onItemCreatedListener))
                        .addToBackStack(null)
                        .commit();
            }
        });
    }

    public void onItemCreated(final Item item) {
        AppFirebase firebase = AppFirebase.getInstance();
        firebase.pushItem(item, new AppFirebase.OnComplete() {
            @Override
            public void taskCompleted(boolean success, Object... parameters) {
                if (success) {
                    Toast.makeText(context, "F*cking work", Toast.LENGTH_LONG).show();
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            List<Item> items = itemViewModel.getItemsSafe();
                            items.add(item);
                            itemViewModel.itemsDataChanged(items);
                        }
                    });
                }
                else {
                    Toast.makeText(context, "Oooopps...", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
