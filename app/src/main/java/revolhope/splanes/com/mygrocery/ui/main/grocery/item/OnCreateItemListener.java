package revolhope.splanes.com.mygrocery.ui.main.grocery.item;

import java.io.Serializable;

import revolhope.splanes.com.mygrocery.data.model.item.Item;

public interface OnCreateItemListener extends Serializable {
    void createItem(Item item);
}
