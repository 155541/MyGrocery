package revolhope.splanes.com.mygrocery.ui.main.grocery;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleRegistry;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.jetbrains.annotations.Contract;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

import revolhope.splanes.com.mygrocery.R;
import revolhope.splanes.com.mygrocery.data.model.item.Item;
import revolhope.splanes.com.mygrocery.helpers.repository.AppRepository;

public class ItemActivity extends AppCompatActivity implements LifecycleOwner {

    private int selectedCategory = 0;
    private int selectedPriority = 1;

    private Context context;
    private Calendar reminder;
    private LifecycleRegistry lifecycleRegistry;
    private ItemFormViewModel itemFormViewModel;

    private TextInputEditText editTextName;
    private TextInputEditText editTextAmount;
    private TextInputEditText editTextCategory;
    private TextInputEditText editTextPriority;
    private TextInputEditText editTextDefault;
    private TextInputEditText editTextReminder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);
        lifecycleRegistry = new LifecycleRegistry(this);
        lifecycleRegistry.markState(Lifecycle.State.CREATED);
        context = this;

        final TextInputLayout layoutName = findViewById(R.id.textInputLayoutName);
        final TextInputLayout layoutAmount = findViewById(R.id.textInputLayoutAmount);
        final TextInputLayout layoutCategory = findViewById(R.id.textInputLayoutCategory);
        final TextInputLayout layoutPriority = findViewById(R.id.textInputLayoutPriority);
        editTextName = findViewById(R.id.editTextName);
        editTextAmount = findViewById(R.id.editTextAmount);
        editTextCategory = findViewById(R.id.editTextCategory);
        editTextPriority = findViewById(R.id.editTextPriority);
        editTextDefault = findViewById(R.id.editTextDefaultUser);
        editTextReminder = findViewById(R.id.editTextReminder);


        itemFormViewModel = ViewModelProviders.of(this, new ItemFormViewModelFactory())
                .get(ItemFormViewModel.class);

        itemFormViewModel.getItemFormState().observe(this, new Observer<ItemFormState>() {
            @Override
            public void onChanged(ItemFormState itemFormState) {
                if (itemFormState.isDataValid()) {

                    String name = Objects.requireNonNull(editTextName.getText()).toString();
                    String amount = Objects.requireNonNull(editTextAmount.getText()).toString();
                    String defaultTarget =
                            Objects.requireNonNull(editTextDefault.getText()).toString();
                    int intAmount = Integer.parseInt(amount);

                    Item item = new Item();
                    item.setId(UUID.randomUUID().toString().replace("-", ""));
                    item.setItemName(name);
                    item.setAmount(intAmount);
                    item.setBought(0);
                    item.setCategory(selectedCategory);
                    item.setPriority(selectedPriority);
                    item.setUsersTarget(Arrays.asList(defaultTarget.split("\n")));
                    Calendar now = Calendar.getInstance();
                    item.setDateCreated(now.getTimeInMillis());
                    item.setDateReminder(reminder.after(now) ? reminder.getTimeInMillis() : 0);
                    item.setUserCreated(AppRepository.getAppUser().getId());
                    AppRepository.addItem(item);
                    setResult(Activity.RESULT_OK);
                    finish();
                }
                else {
                    layoutName.setError(null);
                    layoutAmount.setError(null);
                    layoutCategory.setError(null);
                    layoutPriority.setError(null);
                    if (itemFormState.getNameError() != null)
                        layoutName.setError(getString(itemFormState.getNameError()));
                    if (itemFormState.getAmountError() != null)
                        layoutAmount.setError(getString(itemFormState.getAmountError()));
                    if (itemFormState.getCategoryError() != null)
                        layoutCategory.setError(getString(itemFormState.getCategoryError()));
                    if (itemFormState.getPriorityError() != null)
                        layoutPriority.setError(getString(itemFormState.getPriorityError()));
                }
            }
        });

        clearFocus();

        editTextCategory.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    DialogPicker dialogPicker = new DialogPicker();
                    dialogPicker.setCancelable(false);
                    dialogPicker.setType(DialogPicker.CATEGORIES);
                    dialogPicker.setCallback(new DialogPicker.OnDialogResultListener() {
                        @Override
                        public void onClick(int option) {
                            editTextCategory.setText(context.getResources()
                                    .getStringArray(R.array.categories)[option]);
                            selectedCategory = option;
                        }
                    });
                    dialogPicker.show(getSupportFragmentManager(), DialogPicker.TAG);
                    clearFocus();
                }
            }
        });

        editTextPriority.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    DialogPicker dialogPicker = new DialogPicker();
                    dialogPicker.setCancelable(false);
                    dialogPicker.setType(DialogPicker.PRIORITY);
                    dialogPicker.setCallback(new DialogPicker.OnDialogResultListener() {
                        @Override
                        public void onClick(int option) {
                            editTextPriority.setText(context.getResources()
                                    .getStringArray(R.array.priorities)[option]);
                            selectedPriority = option;
                        }
                    });
                    dialogPicker.show(getSupportFragmentManager(), DialogPicker.TAG);
                }
                clearFocus();
            }
        });

        findViewById(R.id.iconCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                setResult(Activity.RESULT_OK);
            }
        });

        findViewById(R.id.iconCreate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = editTextName.getText() != null ?
                              editTextName.getText().toString() : null;
                String amount = editTextAmount.getText() != null ?
                        editTextAmount.getText().toString() : null;
                String category = editTextCategory.getText() != null ?
                        editTextCategory.getText().toString() : null;
                String priority = editTextPriority.getText() != null ?
                        editTextPriority.getText().toString() : null;
                itemFormViewModel.itemFormDataChanged(name, amount, category, priority);
            }
        });


        if (AppRepository.getAppUser() != null && AppRepository.getAppUser()
                .getDefaultUserTarget() != null) {
            editTextDefault.setText(AppRepository.getAppUser().getDefaultUserTarget());
        }
        final Calendar calendar = Calendar.getInstance();
        reminder = Calendar.getInstance();
        final ImageView imageViewReminder = findViewById(R.id.imageViewReminder);
        editTextReminder.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    DatePickerDialog datePickerDialog = new DatePickerDialog(context,
                            new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePicker view, int year, int month,
                                                      int dayOfMonth) {
                                    reminder.set(Calendar.YEAR, year);
                                    reminder.set(Calendar.MONTH, month);
                                    reminder.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                                    if (!reminder.after(calendar)) {
                                        TimePickerDialog timePickerDialog =
                                                new TimePickerDialog(context,
                                                        new TimePickerDialog.OnTimeSetListener() {
                                                            @Override
                                                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                                                  int minute) {
                                                                reminder.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                                                reminder.set(Calendar.MINUTE, minute);
                                                                SimpleDateFormat sdf =
                                                                        new SimpleDateFormat("dd/MM/yy HH:mm",
                                                                                Locale.FRANCE);
                                                                editTextReminder.setText(sdf.format(reminder.getTime()));
                                                                imageViewReminder.setVisibility(View.VISIBLE);
                                                                clearFocus();
                                                            }
                                                        },calendar.get(Calendar.HOUR_OF_DAY),
                                                        calendar.get(Calendar.MINUTE), true);
                                        timePickerDialog.show();
                                    }
                                }
                            },
                            calendar.get(Calendar.YEAR),
                            calendar.get(Calendar.MONTH),
                            calendar.get(Calendar.DAY_OF_MONTH));
                    datePickerDialog.show();
                    clearFocus();
                }
            }
        });
        imageViewReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editTextReminder.setText("");
                imageViewReminder.setVisibility(View.GONE);
            }
        });
    }

    @Override
    @NonNull
    public Lifecycle getLifecycle() {
        return lifecycleRegistry;
    }

    private void clearFocus() {
        editTextName.clearFocus();
        editTextAmount.clearFocus();
        editTextCategory.clearFocus();
        editTextPriority.clearFocus();
        editTextDefault.clearFocus();
        editTextReminder.clearFocus();
    }

    private class ItemFormViewModelFactory implements ViewModelProvider.Factory {
        @NonNull
        @Override
        @SuppressWarnings("unchecked")
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            if (modelClass.isAssignableFrom(ItemFormViewModel.class)) {
                return (T) new ItemFormViewModel();
            } else {
                throw new IllegalArgumentException("Unknown ViewModel class");
            }
        }
    }

    private class ItemFormViewModel extends ViewModel {
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

    private class ItemFormState {

        private boolean isDataValid;
        @Nullable
        private Integer nameError;
        @Nullable
        private Integer amountError;
        @Nullable
        private Integer categoryError;
        @Nullable
        private Integer priorityError;

        private ItemFormState(@Nullable Integer nameError, @Nullable Integer amountError,
                              @Nullable Integer categoryError, @Nullable Integer priorityError) {
            this.nameError = nameError;
            this.amountError = amountError;
            this.categoryError = categoryError;
            this.priorityError = priorityError;
            isDataValid = false;
        }

        private ItemFormState(boolean isDataValid) {
            this.nameError = null;
            this.amountError = null;
            this.categoryError = null;
            this.priorityError = null;
            this.isDataValid = isDataValid;
        }

        @Contract(pure = true)
        private boolean isDataValid() {
            return isDataValid;
        }

        @Contract(pure = true)
        @Nullable
        private Integer getNameError() {
            return nameError;
        }

        @Contract(pure = true)
        @Nullable
        private Integer getAmountError() {
            return amountError;
        }

        @Contract(pure = true)
        @Nullable
        private Integer getCategoryError() {
            return categoryError;
        }

        @Contract(pure = true)
        @Nullable
        private Integer getPriorityError() {
            return priorityError;
        }
    }
}
