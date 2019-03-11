package revolhope.splanes.com.mygrocery.ui.modal;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import revolhope.splanes.com.mygrocery.R;
import revolhope.splanes.com.mygrocery.data.model.item.Item;

public class ItemOptionsModal extends BottomSheetDialogFragment {

    public static final String TAG = "ItemOptionsModal";
    public static final int BUY = 0;
    public static final int INFO = 1;
    public static final int EDIT = 2;
    public static final int DELETE = 3;

    private Item mItem;
    private OnClickListener onClickListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        if (mItem == null) return super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.modal_item_details, container, false);

        Button buttonBuy = view.findViewById(R.id.buttonBuy);
        Button buttonInfo = view.findViewById(R.id.buttonInfo);
        Button buttonEdit = view.findViewById(R.id.buttonEdit);
        Button buttonDelete = view.findViewById(R.id.buttonDelete);

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int id = v.getId();
                switch (id) {
                    case R.id.buttonBuy:
                        onClickListener.onClick(BUY);
                        if (getDialog() != null) getDialog().dismiss();
                        break;
                    case R.id.buttonInfo:
                        onClickListener.onClick(INFO);
                        if (getDialog() != null) getDialog().dismiss();
                        break;
                    case R.id.buttonEdit:
                        onClickListener.onClick(EDIT);
                        if (getDialog() != null) getDialog().dismiss();
                        break;
                    case R.id.buttonDelete:
                        onClickListener.onClick(DELETE);
                        if (getDialog() != null) getDialog().dismiss();
                        break;
                }
            }
        };

        buttonBuy.setOnClickListener(listener);
        buttonInfo.setOnClickListener(listener);
        buttonEdit.setOnClickListener(listener);
        buttonDelete.setOnClickListener(listener);

        return view;
    }

    public void setItem(Item item) {
        this.mItem = item;
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public interface OnClickListener {
        void onClick(int option);
    }
}
