package revolhope.splanes.com.mygrocery.data.model.item;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class ItemViewModelFactory implements ViewModelProvider.Factory {

    private String[] filterArray;

    public ItemViewModelFactory(String[] filterArray) {
        this.filterArray = filterArray;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(ItemViewModel.class)) {
            return (T) new ItemViewModel(filterArray);
        } else {
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}
