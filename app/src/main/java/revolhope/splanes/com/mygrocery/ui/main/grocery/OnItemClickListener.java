package revolhope.splanes.com.mygrocery.ui.main.grocery;

import android.view.View;

import revolhope.splanes.com.mygrocery.data.model.item.Item;

public interface OnItemClickListener {
    void onItemClick(Item... item, View... sharedViews);
}
