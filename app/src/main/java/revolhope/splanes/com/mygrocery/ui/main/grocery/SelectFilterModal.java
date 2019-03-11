package revolhope.splanes.com.mygrocery.ui.main.grocery;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.NumberPicker;

import revolhope.splanes.com.mygrocery.R;

public class SelectFilterModal extends BottomSheetDialogFragment {

    public static final String TAG = "SelectFilterModal";
    private int pickedValue = 0;
    private Callback callback;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Context context = getContext();
        if (context == null || callback == null)
            return super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.modal_select_filter, container, false);
        NumberPicker numberPickerFilter = view.findViewById(R.id.numberPickerFilter);
        Button buttonFilter = view.findViewById(R.id.buttonSelect);

        String[] data = context.getResources()
                .getStringArray(R.array.filters);

        numberPickerFilter.setValue(0);
        numberPickerFilter.setMinValue(0);
        numberPickerFilter.setMaxValue(data.length-1);
        numberPickerFilter.setWrapSelectorWheel(false);
        numberPickerFilter.setDisplayedValues(data);
        numberPickerFilter.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                pickedValue = newVal;
            }
        });

        buttonFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onPickedValue(pickedValue);
            }
        });

        return view;
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public interface Callback {
        void onPickedValue(int value);
    }
}
