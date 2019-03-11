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

import revolhope.splanes.com.mygrocery.R;
import revolhope.splanes.com.mygrocery.data.model.item.Item;

public class GroceryFragment extends Fragment implements OnShowDetails {

    private FragmentManager fragmentManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_grocery, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fragmentManager = getFragmentManager();
        if (fragmentManager != null) {
            GroceryMasterFragment masterFragment = GroceryMasterFragment.newInstance(this);
            fragmentManager.beginTransaction()
                    .add(R.id.container, masterFragment)
                    .commit();
        }
    }

    @Override
    public void showDetails(Item item, View view) {
        ViewCompat.setTransitionName(view, item.getId());
        fragmentManager
                .beginTransaction()
                .addSharedElement(view, item.getId())
                .replace(R.id.container, GroceryDetailsFragment.newInstance(item))
                .addToBackStack(null)
                .commit();
    }
}
