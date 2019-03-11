package revolhope.splanes.com.mygrocery.ui.login;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import android.util.Patterns;

import revolhope.splanes.com.mygrocery.R;
import revolhope.splanes.com.mygrocery.data.login.Result;

class LoginViewModel extends ViewModel {

    private MutableLiveData<LoginFormState> loginFormState = new MutableLiveData<>();
    private MutableLiveData<Result> loginResult = new MutableLiveData<>();

    LoginViewModel() {
        super();
    }

    LiveData<LoginFormState> getLoginFormState() {
        return loginFormState;
    }

    LiveData<Result> getLoginResult() {
        return loginResult;
    }

    void loginDataChanged(String username, String email, String password,
                                 String defaultEmailTarget) {
        if (!isUserNameValid(username)) {
            loginFormState.setValue(new LoginFormState(R.string.invalid_username,
                    null, null, null));
        } else if (isEmailInvalid(email)) {
            loginFormState.setValue(new LoginFormState(null, null,
                    R.string.invalid_email, null));
        } else if (!isPasswordValid(password)) {
            loginFormState.setValue(new LoginFormState(null, R.string.invalid_password,
                    null, null));
        } else {
            loginFormState.setValue(new LoginFormState(true));
        }
    }

    private boolean isUserNameValid(String username) {
        if (username == null) {
            return false;
        }
        return !username.trim().isEmpty() && username.length() > 4;
    }

    private boolean isEmailInvalid(String email) {
        if (email == null) {
            return true;
        }
        if (!email.contains("@")) {
            return true;
        }
        else {
            return !Patterns.EMAIL_ADDRESS.matcher(email).matches();
        }
    }

    private boolean isPasswordValid(String password) {
        return password != null && password.trim().length() > 7;
    }
}
