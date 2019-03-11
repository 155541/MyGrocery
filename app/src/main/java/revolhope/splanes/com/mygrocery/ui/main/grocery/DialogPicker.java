package revolhope.splanes.com.mygrocery.ui.main.grocery;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
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

public class DialogPicker extends DialogFragment {

    static final String TAG = "DialogPicker";
    static final int CATEGORIES = 1;
    static final int PRIORITY = 2;

    private Callback callback;
    private int option = 0;
    private int type = -1;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        Context context = getContext();
        if (context == null || callback == null || type == -1) {
            return super.onCreateDialog(savedInstanceState);
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AppDialogStyle);
        Spannable spannable = new SpannableString("Escull");
        spannable.setSpan(new ForegroundColorSpan(context.getColor(R.color.colorAccent)),
                0, spannable.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        if (type == PRIORITY) option = 1;
        builder.setTitle(spannable);
        builder.setSingleChoiceItems(type == CATEGORIES ? R.array.categories : R.array.priorities,
                option, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                option = which;
            }
        });
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                callback.onClick(option);
            }
        });
        builder.setNegativeButton("Cancel·lar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                callback.onClick(option);
            }
        });

        return builder.create();
    }

    void setType(int type) {
        this.type = type;
    }

    void setCallback(Callback callback) {
        this.callback = callback;
    }

    public interface Callback {
        void onClick(int option);
    }
}
