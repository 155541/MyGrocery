package revolhope.splanes.com.mygrocery.ui.grocery.list;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import revolhope.splanes.com.mygrocery.R;
import revolhope.splanes.com.mygrocery.data.model.item.Item;

public class GroceryListAdapter extends RecyclerView.Adapter<GroceryListAdapter.Holder> {

    private final int[] iconCategory = new int[] {
            R.drawable.ic_help, R.drawable.ic_kitchen,
            R.drawable.ic_tool, R.drawable.ic_device,
            R.drawable.ic_cake, R.drawable.ic_home
    };

    private final int[] iconsPriority = new int[] {
            R.drawable.ic_priority_high2,
            R.drawable.ic_priority_medium, R.drawable.ic_priority_low
    };

    private Context context;
    private OnItemClickListener onItemClickListener;
    private List<Item> items;

    GroceryListAdapter(Context context, OnItemClickListener onItemClickListener) {
        this.context = context;
        this.items = new ArrayList<>();
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_holder, viewGroup,
                false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        Item item = items.get(position);
        holder.textViewItemName.setText(item.getItemName());
        holder.textViewAmount.setText(String.valueOf(item.getAmount()));
        holder.iconCategory.setImageResource(iconCategory[item.getCategory()]);
        holder.iconPriority.setImageResource(iconsPriority[item.getPriority()]);
        if (item.getIsSeen() == 0) {
            holder.iconNew.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    void update(List<Item> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    class Holder extends RecyclerView.ViewHolder {

        private ImageView iconCategory;
        private TextView textViewItemName;
        private TextView textViewAmount;
        private ImageView iconPriority;
        private ImageView iconNew;

        private Holder(final View view) {
            super(view);
            textViewItemName = view.findViewById(R.id.textViewItemName);
            textViewAmount = view.findViewById(R.id.textViewAmount);
            iconCategory = view.findViewById(R.id.iconCategory);
            iconPriority = view.findViewById(R.id.iconPriority);
            iconNew = view.findViewById(R.id.iconNew);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Item item = items.get(getAdapterPosition());
                    onItemClickListener.onItemClick(item, textViewItemName);
                }
            });
        }
    }
}
