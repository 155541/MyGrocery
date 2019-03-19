package revolhope.splanes.com.mygrocery.ui.grocery.list;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.Fade;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import revolhope.splanes.com.mygrocery.R;
import revolhope.splanes.com.mygrocery.data.model.item.Filter;
import revolhope.splanes.com.mygrocery.data.model.item.Item;
import revolhope.splanes.com.mygrocery.data.model.item.ItemViewModel;
import revolhope.splanes.com.mygrocery.data.model.item.ItemViewModelFactory;
import revolhope.splanes.com.mygrocery.ui.MainActivity;

public class FragmentGroceryList extends Fragment {

    public static final String ARG_PENDING_ITEMS = "PendingItems";
    private static String[] namesFilter;
    private static final int[] ICONS_FILTER = new int[]{
            R.drawable.ic_grocery, R.drawable.ic_help,
            R.drawable.ic_kitchen, R.drawable.ic_tool,
            R.drawable.ic_device, R.drawable.ic_cake,
            R.drawable.ic_home, R.drawable.ic_priority_high2,
            R.drawable.ic_priority_medium, R.drawable.ic_priority_low
    };
    private Context context;
    private MainActivity activity;
    private GroceryListAdapter adapter;
    private ItemViewModel itemViewModel;
    private List<Item> pendingItems;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getContext() == null || getActivity() == null || getArguments() == null) return;
        else {
            context = getContext();
            activity = (MainActivity) getActivity();
            pendingItems = Arrays.asList((Item[]) getArguments().getSerializable(ARG_PENDING_ITEMS));
        }

        namesFilter = context.getResources().getStringArray(R.array.filters);
        itemViewModel = ViewModelProviders.of(this,
                new ItemViewModelFactory(namesFilter)).get(ItemViewModel.class);
        adapter = new GroceryListAdapter(context, new OnItemClickListener() {
            @Override
            public void onItemClick(Item itemClicked, View... sharedViews) {
                activity.setPendingItems(itemViewModel.getItemsSafe());
                activity.showItemDetails(itemClicked);
            }
        });
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

        imageViewAppliedFilter.setImageResource(ICONS_FILTER[0]);
        textViewAppliedFilter.setText(namesFilter[0]);

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
        itemViewModel.itemsDataChanged(new ArrayList<>(pendingItems));
        itemViewModel.filterDataChanged(0);
        itemViewModel.getFilter().observe(this, new Observer<Filter>() {
            @Override
            public void onChanged(@Nullable Filter filter) {
                if (filter == null) return;
                imageViewAppliedFilter.setImageResource(ICONS_FILTER[filter.getIndex()]);
                textViewAppliedFilter.setText(namesFilter[filter.getIndex()]);
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
                activity.setPendingItems(itemViewModel.getItemsSafe());
                activity.createNewItem();
                setExitTransition(new Fade(Fade.OUT));
            }
        });
    }
}
