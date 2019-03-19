package revolhope.splanes.com.mygrocery.ui.grocery.edition;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import androidx.transition.Fade;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.jetbrains.annotations.Contract;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

import revolhope.splanes.com.mygrocery.R;
import revolhope.splanes.com.mygrocery.data.model.item.Item;
import revolhope.splanes.com.mygrocery.ui.MainActivity;

public class FragmentEditGroceryItem extends Fragment {

    private int selectedCategory = 0;
    private int selectedPriority = 1;

    private static boolean _isUpdating;
    private static Item _itemUpdating;

    private MainActivity activity;
    private Context context;
    private FragmentManager fragmentManager;
    private Calendar reminder;
    private ItemFormViewModel itemFormViewModel;

    private TextInputEditText editTextName;
    private TextInputEditText editTextAmount;
    private TextInputEditText editTextCategory;
    private TextInputEditText editTextPriority;
    private TextInputEditText editTextDefault;
    private TextInputEditText editTextReminder;
    private ImageView imageViewReminder;

    @Contract("_, _ -> new")
    public static FragmentEditGroceryItem newInstance(boolean isUpdating, Item itemToUpdate) {
        _isUpdating = isUpdating;
        _itemUpdating = itemToUpdate;
        return new FragmentEditGroceryItem();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
        activity = (MainActivity) getActivity();
        fragmentManager = getFragmentManager();
        reminder = Calendar.getInstance();
        setEnterTransition(new Fade(Fade.IN));
        setExitTransition(new Fade(Fade.OUT));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_grocery_item, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (context == null || activity == null || fragmentManager == null) return;

        final TextInputLayout layoutName = view.findViewById(R.id.textInputLayoutName);
        final TextInputLayout layoutAmount = view.findViewById(R.id.textInputLayoutAmount);
        final TextInputLayout layoutCategory = view.findViewById(R.id.textInputLayoutCategory);
        final TextInputLayout layoutPriority = view.findViewById(R.id.textInputLayoutPriority);
        editTextName = view.findViewById(R.id.editTextName);
        editTextAmount = view.findViewById(R.id.editTextAmount);
        editTextCategory = view.findViewById(R.id.editTextCategory);
        editTextPriority = view.findViewById(R.id.editTextPriority);
        editTextDefault = view.findViewById(R.id.editTextDefaultUser);
        editTextReminder = view.findViewById(R.id.editTextReminder);
        MaterialButton buttonCreate = view.findViewById(R.id.iconCreate);

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
                    item.setItemName(name);
                    item.setAmount(intAmount);
                    item.setBought(0);
                    item.setCategory(selectedCategory);
                    item.setPriority(selectedPriority);
                    if (!defaultTarget.isEmpty()) {
                        String[] aux = defaultTarget.split("\n");
                        if (aux.length != 0 && !aux[0].isEmpty()) {
                            List<String> targets = Arrays.asList(aux);
                            item.setUsersTarget(targets);
                        }
                    }
                    else {
                        item.setUsersTarget(new ArrayList<String>());
                    }
                    Calendar now = Calendar.getInstance();
                    item.setDateCreated(now.getTimeInMillis());
                    item.setDateReminder(reminder.after(now) ? reminder.getTimeInMillis() : 0);
                    item.setUserCreated(activity.getAppUser().getId());
                    if (_isUpdating) {
                        item.setId(_itemUpdating.getId());
                        activity.itemUpdated(item);
                    } else {
                        item.setId(UUID.randomUUID().toString().replace("-", ""));
                        activity.newItemCreated(item);
                    }
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
                    dialogPicker.show(fragmentManager, DialogPicker.TAG);
                    editTextCategory.clearFocus();
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
                    dialogPicker.show(fragmentManager, DialogPicker.TAG);
                }
                editTextPriority.clearFocus();
            }
        });

        view.findViewById(R.id.iconCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentManager.popBackStack();
            }
        });

        buttonCreate.setOnClickListener(new View.OnClickListener() {
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

        if (activity.getAppUser() != null &&
            activity.getAppUser().getDefaultUserTarget() != null) {
            editTextDefault.setText(activity.getAppUser().getDefaultUserTarget());
        }
        imageViewReminder = view.findViewById(R.id.imageViewReminder);
        editTextReminder.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    showDatePicker();
                    editTextReminder.clearFocus();
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

        if (_isUpdating) {
            editTextName.setText(_itemUpdating.getItemName());
            editTextAmount.setText(String.valueOf(_itemUpdating.getAmount()));
            editTextCategory.setText(context.getResources()
                    .getStringArray(R.array.categories)[_itemUpdating.getCategory()]);
            editTextPriority.setText(context.getResources()
                    .getStringArray(R.array.priorities)[_itemUpdating.getPriority()]);
            if (_itemUpdating.getDateReminder() > 0) {
                editTextReminder.setText(
                        new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.FRANCE)
                                .format(_itemUpdating.getDateReminder()));
            }
            buttonCreate.setText("Desar");
        }
    }

    private void showDatePicker() {
        final Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(context,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month,
                                          int dayOfMonth) {
                        reminder.set(Calendar.YEAR, year);
                        reminder.set(Calendar.MONTH, month);
                        reminder.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        if (!reminder.after(calendar)) {
                            showTimePicker(calendar);
                        }
                    }
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private void showTimePicker(Calendar calendar) {
        TimePickerDialog timePickerDialog =
                new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                reminder.set(Calendar.HOUR_OF_DAY, hourOfDay);
                reminder.set(Calendar.MINUTE, minute);
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy HH:mm",
                        Locale.FRANCE);
                editTextReminder.setText(sdf.format(reminder.getTime()));
                imageViewReminder.setVisibility(View.VISIBLE);
            }
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
        timePickerDialog.show();
    }
}