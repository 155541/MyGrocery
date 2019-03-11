package revolhope.splanes.com.mygrocery.data.login;

import revolhope.splanes.com.mygrocery.data.model.User;

public class Result {
    // hide the private constructor to limit subclass types (Success, Error)
    private Result() {
    }

    // Success sub-class
    public final static class Success extends Result {
        private User user;

        public Success(User user) {
            this.user = user;
        }

        public User getData() {
            return this.user;
        }
    }

    // Error sub-class
    public final static class Error extends Result {
        private Exception error;

        public Error(Exception error) {
            this.error = error;
        }

        public Exception getError() {
            return this.error;
        }
    }
}
