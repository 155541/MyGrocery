package revolhope.splanes.com.mygrocery.ui.grocery.historic;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import revolhope.splanes.com.mygrocery.R;
import revolhope.splanes.com.mygrocery.data.model.ItemHistoric;

public class ItemHistoricAdapter extends RecyclerView.Adapter<ItemHistoricAdapter.Holder> {

    private Context context;
    private List<ItemHistoric> itemHistoricList;

    ItemHistoricAdapter(Context context) {
        this.context = context;
        this.itemHistoricList = new ArrayList<>();
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(context).inflate(R.layout.historic_holder, parent,
                false));
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        if (itemHistoricList.size() < position)  return;
        ItemHistoric item = itemHistoricList.get(position);
        holder.textViewUserName.setText(item.getUserName());
        holder.textViewAction.setText(item.getAction());
        holder.textViewPreviousState.setText(item.getPreviousState());
        holder.textViewNewState.setText(item.getNewState());
        holder.textViewDate.setText(item.getDate());
    }

    @Override
    public int getItemCount() {
        return itemHistoricList.size();
    }

    void setItemHistoricList(List<ItemHistoric> items) {
        this.itemHistoricList = items;
        notifyDataSetChanged();
    }

    class Holder extends RecyclerView.ViewHolder {

        private TextView textViewUserName;
        private TextView textViewAction;
        private TextView textViewPreviousState;
        private TextView textViewNewState;
        private TextView textViewDate;

        private Holder(View view) {
            super(view);
            textViewUserName = view.findViewById(R.id.textViewUserName);
            textViewAction = view.findViewById(R.id.textViewAction);
            textViewPreviousState = view.findViewById(R.id.textViewPreviousState);
            textViewNewState = view.findViewById(R.id.textViewNewState);
            textViewDate = view.findViewById(R.id.textViewDate);
        }
    }
}
