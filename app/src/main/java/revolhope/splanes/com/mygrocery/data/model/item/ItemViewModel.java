package revolhope.splanes.com.mygrocery.data.model.item;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import revolhope.splanes.com.mygrocery.data.model.Params;

public class ItemViewModel extends ViewModel {

    private String[] filterArray;
    private MutableLiveData<Filter> filter = new MutableLiveData<>();
    private MutableLiveData<List<Item>> items = new MutableLiveData<>();

    public ItemViewModel(String[] filterArray) {
        this.filterArray = filterArray;
    }

    public LiveData<Filter> getFilter() {
        return filter;
    }

    public LiveData<List<Item>> getItems() {
        return items;
    }

    public void filterDataChanged(int index) {
        filter.setValue(new Filter(filterArray, index));
    }

    public void itemsDataChanged(List<Item> items) {
        this.items.setValue(items);
    }

    @NonNull
    public List<Item> getFilteredItems() {
        List<Item> aux = new ArrayList<>();
        if (items == null || items.getValue() == null || filter == null ||
            filter.getValue() == null) return aux;

        List<Item> itemList = items.getValue();
        int indexFilter = filter.getValue().getIndex();

        if (indexFilter == Params.FILTER_ALL) {
            return sort(itemList);
        } else if (indexFilter == Params.FILTER_CATEGORY_1 ||
                   indexFilter == Params.FILTER_CATEGORY_2 ||
                   indexFilter == Params.FILTER_CATEGORY_3 ||
                   indexFilter == Params.FILTER_CATEGORY_4 ||
                   indexFilter == Params.FILTER_CATEGORY_5 ||
                   indexFilter == Params.FILTER_CATEGORY_6) {
            for (Item i : itemList) {
                if (i.getCategory() == indexFilter-1) {
                    aux.add(i);
                }
            }
            return sort(aux);
        } else {
            for (Item i : itemList) {
                if (i.getPriority() == indexFilter-7) {
                    aux.add(i);
                }
            }
            return sort(aux);
        }
    }

    private List<Item> sort(@NonNull List<Item> list) {
        List<Item> aux = new ArrayList<>();
        for (Item i : list) {
            if (i.getPriority() == Params.INT_PRIORITY_LOW) {
                aux.add(0,i);
            }
        }
        for (Item i : list) {
            if (i.getPriority() == Params.INT_PRIORITY_MEDIUM) {
                aux.add(0,i);
            }
        }
        for (Item i : list) {
            if (i.getPriority() == Params.INT_PRIORITY_HIGH) {
                aux.add(0,i);
            }
        }
        return aux;
    }
}
