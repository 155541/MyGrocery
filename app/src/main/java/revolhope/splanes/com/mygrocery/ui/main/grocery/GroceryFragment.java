package revolhope.splanes.com.mygrocery.ui.main.grocery;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import revolhope.splanes.com.mygrocery.R;
import revolhope.splanes.com.mygrocery.data.model.item.Item;

public class GroceryFragment extends Fragment implements OnItemClickListener {

    private FragmentManager fragmentManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_grocery, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fragmentManager = getFragmentManager();
        if (fragmentManager != null) {
            fragmentManager.beginTransaction()
                    .add(R.id.container, GroceryMasterFragment.newInstance(this))
                    .commit();
        }
    }

    @Override
    public void onItemClick(Item itemClicked, View... sharedViews) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        for (View view : sharedViews) {
            ViewCompat.setTransitionName(view, itemClicked.getId() + view.getId());
            transaction.addSharedElement(view, itemClicked.getId() + view.getId());
        }
        transaction.replace(R.id.container, GroceryDetailsFragment.newInstance(itemClicked))
                   .addToBackStack(null)
                   .commit();
    }
}
