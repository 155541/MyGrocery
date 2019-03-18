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

public class DialogDelete extends DialogFragment {

    static final String TAG = "DialogDelete";
    private OnConfirmListener listener;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        if (getContext() == null) {
            return super.onCreateDialog(savedInstanceState);
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(),
                R.style.AppDialogStyle);
        Spannable titleSpan = new SpannableString("Alerta");
        titleSpan.setSpan(new ForegroundColorSpan(getContext().getColor(R.color.colorAccent)),
                0, titleSpan.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.setTitle(titleSpan);
        builder.setMessage("Aquesta acció no es pot desfer." +
                "\nEstas segur que vols eliminar l'article?");
        builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (listener != null) listener.onConfirm();
            }
        });
        builder.setNegativeButton("Cancel·lar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        return builder.create();
    }

    public void setOnConfirmListener(OnConfirmListener listener) {
        this.listener = listener;
    }

    public interface OnConfirmListener {
        void onConfirm();
    }
}
