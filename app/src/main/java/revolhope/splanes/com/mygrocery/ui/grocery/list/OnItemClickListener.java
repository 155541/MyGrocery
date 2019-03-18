package revolhope.splanes.com.mygrocery.ui.grocery.list;

import android.view.View;

import revolhope.splanes.com.mygrocery.data.model.item.Item;

public interface OnItemClickListener {
    void onItemClick(Item itemClicked, View... sharedViews);
}
