package outout.view;

import java.util.List;

public class AccountCreationResult {
    private boolean successful;
    private List<String> errors;

    public boolean isSuccessful() {
        return successful;
    }

    public void setSuccessful(final boolean successful) {
        this.successful = successful;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(final List<String> errors) {
        this.errors = errors;
    }
}
