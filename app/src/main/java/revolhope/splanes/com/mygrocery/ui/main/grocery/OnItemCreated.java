package revolhope.splanes.com.mygrocery.ui.main.grocery;

import java.io.Serializable;

import revolhope.splanes.com.mygrocery.data.model.item.Item;

public interface OnItemCreated extends Serializable {
    void addItem(Item item);
}
