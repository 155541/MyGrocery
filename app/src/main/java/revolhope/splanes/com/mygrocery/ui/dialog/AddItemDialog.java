package revolhope.splanes.com.mygrocery.ui.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.appcompat.widget.AppCompatSpinner;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.TimePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import revolhope.splanes.com.mygrocery.R;
import revolhope.splanes.com.mygrocery.data.model.item.Item;

public class AddItemDialog extends DialogFragment {
    public static final String TAG = "AddItemModal";

    private static final int[] iconsItems = new int[] {
            R.drawable.ic_help, R.drawable.ic_kitchen,
            R.drawable.ic_tool, R.drawable.ic_device,
            R.drawable.ic_cake, R.drawable.ic_home
    };

    private EditText nameArticle;
    private EditText amountArticle;
    private ImageView iconArticle;
    private TextView textViewRemember;

    private Context context;
    private Activity activity;
    private OnCreateItem callback;
    private Calendar calRemember;
    private boolean isRememberSet;
    private int iconResourceIndex;
    private int prioritySelected;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        ViewGroup viewGroup;
        if (getContext() == null || getActivity() == null || callback == null) {
            return super.onCreateDialog(savedInstanceState);
        }
        else {
            context = getContext();
            activity = getActivity();
            activity.getWindow().setSoftInputMode(WindowManager.
                    LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            viewGroup = activity.findViewById(android.R.id.content);
        }
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_item_add,
                viewGroup, false);

        calRemember = Calendar.getInstance();
        isRememberSet = false;

        bindComponents(view);

        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AppDialogStyle);
        builder.setView(view);
        builder.setTitle(R.string.prompt_title_additem_dialog);
        builder.setPositiveButton(R.string.prompt_afegir, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Item item = null;
                String name = nameArticle.getText().toString();
                String amount = amountArticle.getText().toString();
                try {
                    int intAmount = Integer.parseInt(amount);
                    if (!name.equals("") && !amount.equals("")) {
                        item = new Item();
                        item.setItemName(name);
                        item.setAmount(intAmount);
                        item.setCategory(iconResourceIndex);
                        item.setPriority(prioritySelected);
                        item.setDateReminder(isRememberSet ? calRemember.getTimeInMillis() : 0L);
                        item.setDateCreated(Calendar.getInstance().getTimeInMillis());
                    }
                    callback.addItem(item);
                } catch (NumberFormatException e){
                    e.printStackTrace();
                    callback.addItem(null);
                }
            }
        });
        builder.setNeutralButton(R.string.prompt_cancelar, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {}
        });

        return builder.create();
    }

    private void bindComponents(@NonNull View view) {

        nameArticle = view.findViewById(R.id.editTextItem);
        amountArticle = view.findViewById(R.id.editTextAmount);
        iconArticle = view.findViewById(R.id.iconCategory);
        AppCompatSpinner spinner = view.findViewById(R.id.appCompatSpinner);
        ImageView imageViewReminder = view.findViewById(R.id.imageViewReminder);
        NumberPicker numberPicker = view.findViewById(R.id.numberPickerPriority);
        textViewRemember = view.findViewById(R.id.textViewRemember);

        numberPicker.setMaxValue(2);
        numberPicker.setMinValue(0);
        numberPicker.setValue(1);
        prioritySelected = 1;
        numberPicker.setDisplayedValues(context.getResources().getStringArray(R.array.priorities));
        numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                prioritySelected = newVal;
            }
        });

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                context, R.array.categories, android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                iconArticle.setImageResource(iconsItems[position]);
                iconResourceIndex = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
        View.OnClickListener listenerRemember = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(context,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month,
                                                  int dayOfMonth) {

                                calRemember.set(Calendar.YEAR, year);
                                calRemember.set(Calendar.MONTH, month);
                                calRemember.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                                TimePickerDialog timePickerDialog = new TimePickerDialog(context,
                                        new TimePickerDialog.OnTimeSetListener() {
                                            @Override
                                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                                  int minute) {
                                                calRemember.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                                calRemember.set(Calendar.MINUTE, minute);
                                                final SimpleDateFormat sdf = new SimpleDateFormat(
                                                        "dd/MM/yyyy HH:mm",
                                                        Locale.FRANCE);
                                                activity.runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        textViewRemember.setText(sdf.
                                                                format(calRemember.getTime()));
                                                    }
                                                });
                                                isRememberSet = true;
                                            }
                                        }, 10, 0, true);
                                timePickerDialog.show();
                            }
                        }, calRemember.get(Calendar.YEAR), calRemember.get(Calendar.MONTH),
                        calRemember.get(Calendar.DAY_OF_MONTH)
                );
                datePickerDialog.show();
            }
        };
        imageViewReminder.setOnClickListener(listenerRemember);
        textViewRemember.setOnClickListener(listenerRemember);
        textViewRemember.setText(R.string.prompt_no_remember_alert);
    }

    public void setOnCreateItem(OnCreateItem callback) {
        this.callback = callback;
    }

    public interface OnCreateItem {
        void addItem(Item item);
    }
}
