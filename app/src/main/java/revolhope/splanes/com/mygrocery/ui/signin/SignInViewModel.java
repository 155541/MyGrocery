package revolhope.splanes.com.mygrocery.ui.signin;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import android.util.Patterns;

import revolhope.splanes.com.mygrocery.R;

class SignInViewModel extends ViewModel {

    private MutableLiveData<SignInFormState> loginFormState = new MutableLiveData<>();

    SignInViewModel() {
        super();
    }

    LiveData<SignInFormState> getLoginFormState() {
        return loginFormState;
    }

    void loginDataChanged(String username, String email, String password) {
        if (!isUserNameValid(username)) {
            loginFormState.setValue(new SignInFormState(R.string.invalid_username,
                    null, null, null));
        } else if (isEmailInvalid(email)) {
            loginFormState.setValue(new SignInFormState(null, null,
                    R.string.invalid_email, null));
        } else if (!isPasswordValid(password)) {
            loginFormState.setValue(new SignInFormState(null, R.string.invalid_password,
                    null, null));
        } else {
            loginFormState.setValue(new SignInFormState(true));
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
