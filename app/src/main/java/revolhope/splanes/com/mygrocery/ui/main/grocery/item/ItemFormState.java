package revolhope.splanes.com.mygrocery.ui.main.grocery.item;

import androidx.annotation.Nullable;

import org.jetbrains.annotations.Contract;

class ItemFormState {

    private boolean isDataValid;
    @Nullable
    private Integer nameError;
    @Nullable
    private Integer amountError;
    @Nullable
    private Integer categoryError;
    @Nullable
    private Integer priorityError;

    ItemFormState(@Nullable Integer nameError, @Nullable Integer amountError,
                          @Nullable Integer categoryError, @Nullable Integer priorityError) {
        this.nameError = nameError;
        this.amountError = amountError;
        this.categoryError = categoryError;
        this.priorityError = priorityError;
        isDataValid = false;
    }

    ItemFormState(boolean isDataValid) {
        this.nameError = null;
        this.amountError = null;
        this.categoryError = null;
        this.priorityError = null;
        this.isDataValid = isDataValid;
    }

    @Contract(pure = true)
    boolean isDataValid() {
        return isDataValid;
    }

    @Contract(pure = true)
    @Nullable
    Integer getNameError() {
        return nameError;
    }

    @Contract(pure = true)
    @Nullable
    Integer getAmountError() {
        return amountError;
    }

    @Contract(pure = true)
    @Nullable
    Integer getCategoryError() {
        return categoryError;
    }

    @Contract(pure = true)
    @Nullable
    Integer getPriorityError() {
        return priorityError;
    }
}
