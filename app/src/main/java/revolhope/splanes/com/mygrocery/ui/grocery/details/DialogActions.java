package revolhope.splanes.com.mygrocery.ui.grocery.details;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import revolhope.splanes.com.mygrocery.R;

public class DialogActions extends DialogFragment {

    final static String TAG = "ItemActionDialog";
    final static int ACTION_EDIT = 0;
    final static int ACTION_HISTORICAL = 1;
    final static int ACTION_DELETE = 2;
    private OnActionSelectedListener listener;
    private int option = -1;

    public interface OnActionSelectedListener {
        void onActionSelected(int option);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        if (getContext() == null) {
            return super.onCreateDialog(savedInstanceState);
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(),
                R.style.AppDialogStyle);
        Spannable titleSpan = new SpannableString("Accions");
        Spannable deleteSpan = new SpannableString("Eliminar");
        titleSpan.setSpan(new ForegroundColorSpan(getContext().getColor(R.color.colorAccent)),
                0, titleSpan.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        deleteSpan.setSpan(new ForegroundColorSpan(getContext().getColor(R.color.colorAccent)),
                0, deleteSpan.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.setTitle(titleSpan);
        builder.setSingleChoiceItems(
                new CharSequence[]{"Editar", "Veure històric", deleteSpan}, -1,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        option = which;
                    }
                });
        builder.setPositiveButton("Escollir", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (option != -1 && listener != null) listener.onActionSelected(option);
            }
        });
        builder.setNegativeButton("Cancel·lar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        return builder.create();
    }

    void setOnActionSelectedListener(OnActionSelectedListener listener) {
        this.listener = listener;
    }
}
