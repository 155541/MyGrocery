package revolhope.splanes.com.mygrocery.ui.login;

import androidx.annotation.Nullable;

class LoginFormState {
    @Nullable
    private Integer usernameError;
    @Nullable
    private Integer passwordError;
    @Nullable
    private Integer emailError;
    @Nullable
    private Integer defaultEmailTargetError;
    private boolean isDataValid;

    LoginFormState(@Nullable Integer usernameError, @Nullable Integer passwordError,
                   @Nullable Integer emailError, @Nullable Integer defaultEmailTargetError) {
        this.usernameError = usernameError;
        this.passwordError = passwordError;
        this.emailError = emailError;
        this.defaultEmailTargetError = defaultEmailTargetError;
        this.isDataValid = false;
    }

    LoginFormState(boolean isDataValid) {
        this.usernameError = null;
        this.passwordError = null;
        this.emailError = null;
        this.defaultEmailTargetError = null;
        this.isDataValid = isDataValid;
    }

    @Nullable
    Integer getUsernameError() {
        return usernameError;
    }

    @Nullable
    Integer getPasswordError() {
        return passwordError;
    }

    @Nullable
    Integer getEmailError() {
        return emailError;
    }

    @Nullable
    Integer getDefaultEmailTargetError() {
        return defaultEmailTargetError;
    }

    boolean isDataValid() {
        return isDataValid;
    }
}
