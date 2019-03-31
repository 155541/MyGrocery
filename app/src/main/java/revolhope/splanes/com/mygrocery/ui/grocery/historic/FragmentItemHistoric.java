package revolhope.splanes.com.mygrocery.ui.grocery.historic;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.Contract;

import java.util.Arrays;

import revolhope.splanes.com.mygrocery.R;
import revolhope.splanes.com.mygrocery.data.model.ItemHistoric;
import revolhope.splanes.com.mygrocery.data.model.item.Item;
import revolhope.splanes.com.mygrocery.helpers.firebase.AppFirebase;
import revolhope.splanes.com.mygrocery.ui.MainActivity;

public class FragmentItemHistoric extends Fragment {

    private static Item _item;
    private Context context;
    private MainActivity activity;

    @Contract("_ -> new")
    public static FragmentItemHistoric newInstance(Item item) {
        _item = item;
        return new FragmentItemHistoric();
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
        return inflater.inflate(R.layout.fragment_grocery_historic, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        if (context == null || activity == null || _item == null) super.onViewCreated(view, savedInstanceState);

        TextView textViewItemName = view.findViewById(R.id.textViewItemName);
        ImageView buttonBack = view.findViewById(R.id.buttonBack);
        final RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        final ProgressBar progressBar = view.findViewById(R.id.progressBar);

        textViewItemName.setText(_item.getItemName());
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.pop();
                activity.showItemDetails(_item);
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        final ItemHistoricAdapter historicAdapter = new ItemHistoricAdapter(context);
        recyclerView.setAdapter(historicAdapter);

        AppFirebase.getInstance().fetchHistoric(_item.getId(), new AppFirebase.OnComplete(){
                    @Override
                    public void taskCompleted(final boolean success, final Object... parameters) {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (success) {
                                    historicAdapter.setItemHistoricList(Arrays
                                            .asList((ItemHistoric[]) parameters));
                                    recyclerView.setVisibility(View.VISIBLE);
                                    progressBar.setVisibility(View.GONE);
                                }
                                else {
                                    progressBar.setVisibility(View.GONE);
                                    Toast.makeText(context, (String) parameters[0],
                                            Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }
                });
    }
}
