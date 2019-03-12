package revolhope.splanes.com.mygrocery.ui.main.grocery.item;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.jetbrains.annotations.Contract;

import revolhope.splanes.com.mygrocery.R;

class ItemFormViewModel extends ViewModel {

    private MutableLiveData<ItemFormState> itemFormState = new MutableLiveData<>();

    ItemFormViewModel() {
        super();
    }

    LiveData<ItemFormState> getItemFormState() {
        return itemFormState;
    }

    void itemFormDataChanged(String name, String amount, String category,
                             String priority) {
        if (!isNameValid(name)) {
            itemFormState.setValue(new ItemFormState(
                    R.string.invalid_item_name,
                    null,
                    null,
                    null));
        } else if (!isAmountValid(amount)) {
            itemFormState.setValue(new ItemFormState(
                    null,
                    R.string.invalid_item_amount,
                    null,
                    null));
        } else if (!isCategoryValid(category)) {
            itemFormState.setValue(new ItemFormState(
                    null,
                    null,
                    R.string.invalid_item_category,
                    null));
        } else if (!isPriorityValid(priority)){
            itemFormState.setValue(new ItemFormState(
                    null,
                    null,
                    null,
                    R.string.invalid_item_priority));
        } else {
            itemFormState.setValue(new ItemFormState(true));
        }
    }
    @Contract("null -> false")
    private boolean isNameValid(String name) {
        if (name == null) {
            return false;
        }
        return !name.trim().isEmpty();
    }
    @Contract("null -> false")
    private boolean isAmountValid(String amount) {
        if (amount == null) {
            return false;
        }
        try {
            int i = Integer.parseInt(amount);
            return i > 0;
        }
        catch (NumberFormatException ignored) {
            return false;
        }
    }
    @Contract("null -> false")
    private boolean isCategoryValid(String category) {
        return category != null && !category.trim().isEmpty();
    }
    @Contract("null -> false")
    private boolean isPriorityValid(String priority) {
        return priority != null && !priority.trim().isEmpty();
    }
}
