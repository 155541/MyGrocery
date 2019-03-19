package revolhope.splanes.com.mygrocery.ui.signin;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import org.jetbrains.annotations.Contract;

import java.util.ArrayList;

import revolhope.splanes.com.mygrocery.R;
import revolhope.splanes.com.mygrocery.data.model.User;
import revolhope.splanes.com.mygrocery.data.model.item.Item;
import revolhope.splanes.com.mygrocery.data.signin.SignInAsyncTask;
import revolhope.splanes.com.mygrocery.data.signin.SignInCallback;
import revolhope.splanes.com.mygrocery.ui.MainActivity;

public class FragmentSignIn extends Fragment {

    private MainActivity activity;

    private EditText editTextUsername;
    private EditText editTextEmail;
    private EditText editTextPwd;
    private EditText editTextDefaultTarget;
    private ProgressBar progressBarLoading;
    private LinearLayout inputsLayout;

    @Contract(" -> new")
    public static FragmentSignIn newInstance() {
        return new FragmentSignIn();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (MainActivity) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_signin, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        final SignInViewModel signInViewModel =
                ViewModelProviders.of(this, new SignInViewModelFactory())
                        .get(SignInViewModel.class);

        editTextUsername = view.findViewById(R.id.username);
        editTextEmail = view.findViewById(R.id.email);
        editTextPwd = view.findViewById(R.id.password);
        editTextDefaultTarget = view.findViewById(R.id.defaultTarget);
        final Button buttonLogin = view.findViewById(R.id.login);
        progressBarLoading = view.findViewById(R.id.loading);
        inputsLayout = view.findViewById(R.id.inputsLayout);

        signInViewModel.getLoginFormState().observe(this, new Observer<SignInFormState>() {
            @Override
            public void onChanged(@Nullable SignInFormState signInFormState) {
                if (signInFormState == null) return;
                buttonLogin.setEnabled(signInFormState.isDataValid());
                if (signInFormState.getUsernameError() != null) {
                    editTextUsername.setError(getString(signInFormState.getUsernameError()));
                }
                else if (signInFormState.getEmailError() != null) {
                    editTextEmail.setError(getString(signInFormState.getEmailError()));
                }
                else if (signInFormState.getPasswordError() != null) {
                    editTextPwd.setError(getString(signInFormState.getPasswordError()));
                }
                else if (signInFormState.getDefaultEmailTargetError() != null) {
                    editTextDefaultTarget.setError(getString(signInFormState.
                            getDefaultEmailTargetError()));
                }
            }
        });

        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }
            @Override
            public void afterTextChanged(Editable s) {
                signInViewModel.loginDataChanged(editTextUsername.getText().toString(),
                        editTextEmail.getText().toString(), editTextPwd.getText().toString());
            }
        };

        editTextUsername.addTextChangedListener(afterTextChangedListener);
        editTextEmail.addTextChangedListener(afterTextChangedListener);
        editTextPwd.addTextChangedListener(afterTextChangedListener);
        editTextDefaultTarget.addTextChangedListener(afterTextChangedListener);
        editTextDefaultTarget.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    signInViewModel.loginDataChanged(editTextUsername.getText().toString(),
                            editTextEmail.getText().toString(), editTextPwd.getText().toString());
                }
                return false;
            }
        });

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeViewsVisibility(true);
                SignInAsyncTask task = new SignInAsyncTask(getContext(),
                        new SignInCallback() {
                            @Override
                            public void onComplete(boolean success, Object... parameters) {
                                if (success) {
                                    activity.setAppUser((User) parameters[0]);
                                    activity.setPendingItems(new ArrayList<Item>());
                                    activity.showItemList();
                                }
                                else {
                                    changeViewsVisibility(false);
                                    String message = "Oops";
                                    if (parameters[0] instanceof String){
                                        message = (String) parameters[0];
                                    }
                                    else if (parameters[0] instanceof Exception) {
                                        message = ((Exception)parameters[0]).getMessage();
                                    }
                                    Toast.makeText(getContext(), message, Toast.LENGTH_LONG)
                                            .show();
                                }
                            }
                        });
                task.execute(editTextUsername.getText().toString(),
                        editTextEmail.getText().toString(),
                        editTextPwd.getText().toString(),
                        editTextDefaultTarget.getText().toString());
            }
        });
    }

    private void changeViewsVisibility(boolean hide) {
        inputsLayout.setVisibility(hide ? View.INVISIBLE : View.VISIBLE);
        progressBarLoading.setVisibility(hide ? View.VISIBLE : View.GONE);
    }
}
