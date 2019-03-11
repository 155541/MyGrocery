package revolhope.splanes.com.mygrocery.ui.main.grocery;

import android.view.View;

import revolhope.splanes.com.mygrocery.data.model.item.Item;

public interface OnShowDetails {
    void showDetails(Item item, View sharedViews);
}
