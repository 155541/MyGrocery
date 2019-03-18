package revolhope.splanes.com.mygrocery.ui.login;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleRegistry;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import revolhope.splanes.com.mygrocery.R;
import revolhope.splanes.com.mygrocery.data.login.LoginAsyncTask;
import revolhope.splanes.com.mygrocery.data.login.LoginCallback;
import revolhope.splanes.com.mygrocery.data.login.Result;
import revolhope.splanes.com.mygrocery.data.model.User;
import revolhope.splanes.com.mygrocery.ui.grocery.MainActivity;

public class LoginActivity extends AppCompatActivity implements LifecycleOwner {

    private LifecycleRegistry lifecycleRegistry;

    private EditText editTextUsername;
    private EditText editTextEmail;
    private EditText editTextPwd;
    private EditText editTextDefaultTarget;
    private ProgressBar progressBarLoading;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        lifecycleRegistry = new LifecycleRegistry(this);
        lifecycleRegistry.markState(Lifecycle.State.CREATED);

        final LoginViewModel loginViewModel =
                ViewModelProviders.of(this, new LoginViewModelFactory())
                        .get(LoginViewModel.class);

        editTextUsername = findViewById(R.id.username);
        editTextEmail = findViewById(R.id.email);
        editTextPwd = findViewById(R.id.password);
        editTextDefaultTarget = findViewById(R.id.defaultTarget);
        final Button buttonLogin = findViewById(R.id.login);
        progressBarLoading = findViewById(R.id.loading);

        loginViewModel.getLoginFormState().observe(this, new Observer<LoginFormState>() {
            @Override
            public void onChanged(@Nullable LoginFormState loginFormState) {
                if (loginFormState == null) return;
                buttonLogin.setEnabled(loginFormState.isDataValid());
                if (loginFormState.getUsernameError() != null) {
                    editTextUsername.setError(getString(loginFormState.getUsernameError()));
                }
                else if (loginFormState.getEmailError() != null) {
                    editTextEmail.setError(getString(loginFormState.getEmailError()));
                }
                else if (loginFormState.getPasswordError() != null) {
                    editTextPwd.setError(getString(loginFormState.getPasswordError()));
                }
                else if (loginFormState.getDefaultEmailTargetError() != null) {
                    editTextDefaultTarget.setError(getString(loginFormState.
                            getDefaultEmailTargetError()));
                }
            }
        });

        loginViewModel.getLoginResult().observe(this, new Observer<Result>() {
            @Override
            public void onChanged(@Nullable Result result) {
                if (result == null) {
                    return;
                }
                changeViewsVisibility(false);
                if (result instanceof Result.Error &&
                   ((Result.Error) result).getError() != null) {
                    showLoginFailed(((Result.Error) result).getError());
                }
                if (result instanceof Result.Success &&
                    ((Result.Success) result).getData() != null) {
                    User user = (((Result.Success) result).getData());
                    setResult(Activity.RESULT_OK);
                    // TODO: Init MainActivity!
                    finish();
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
                loginViewModel.loginDataChanged(editTextUsername.getText().toString(),
                        editTextEmail.getText().toString(), editTextPwd.getText().toString(),
                        editTextDefaultTarget.getText().toString());
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
                    loginViewModel.loginDataChanged(editTextUsername.getText().toString(),
                            editTextEmail.getText().toString(), editTextPwd.getText().toString(),
                            editTextDefaultTarget.getText().toString());
                }
                return false;
            }
        });

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeViewsVisibility(true);
                LoginAsyncTask task = new LoginAsyncTask(getApplicationContext(),
                        new LoginCallback() {
                            @Override
                            public void onComplete(boolean success, Object... parameters) {
                                if (success) {
                                    Intent i = new Intent(getApplicationContext(),
                                            MainActivity.class);
                                    setResult(Activity.RESULT_OK);
                                    startActivity(i);
                                    finish();
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
                                    Snackbar.make(findViewById(R.id.container),
                                            message, Snackbar.LENGTH_INDEFINITE)
                                            .setAction(R.string.action_ok,
                                                    new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {}
                                            })
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

    private void showLoginFailed(Exception e) {
        Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
    }

    private void changeViewsVisibility(boolean hide) {
        findViewById(R.id.inputsLayout).setVisibility(hide ? View.INVISIBLE : View.VISIBLE);
        progressBarLoading.setVisibility(hide ? View.VISIBLE : View.GONE);
    }

    @NonNull
    @Override
    public Lifecycle getLifecycle() {
        return lifecycleRegistry;
    }
}
