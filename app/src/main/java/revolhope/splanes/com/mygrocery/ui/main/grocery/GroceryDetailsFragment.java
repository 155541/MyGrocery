package revolhope.splanes.com.mygrocery.ui.main.grocery;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.transition.Fade;
import androidx.transition.Slide;

import org.jetbrains.annotations.Contract;

import revolhope.splanes.com.mygrocery.R;
import revolhope.splanes.com.mygrocery.data.model.item.Item;

public class GroceryDetailsFragment extends Fragment {

    private static Item item;

    @Contract("_ -> new")
    static GroceryDetailsFragment newInstance(Item item) {
        GroceryDetailsFragment.item = item;
        return new GroceryDetailsFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setEnterTransition(new Fade(Fade.IN));
        postponeEnterTransition();
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
        title.setTransitionName(item.getId());
        Slide slide = new Slide();
        slide.setDuration(500);
        setSharedElementEnterTransition(slide);
        setSharedElementReturnTransition(slide);
        title.setText(item.getItemName());
        startPostponedEnterTransition();
    }
}
