package outout.util;

public class TestApplicationPaths {
    public static final String BASE_PATH = "http://localhost:9000";
    public static final String CREATE_ACCOUNT_PATH = String.format("%s/account/create", BASE_PATH);
    public static final String AUTHENTICATION_PATH = String.format("%s/authenticate", BASE_PATH);
    public static final String SUGGESTION_PATH = String.format("%s/suggestion", BASE_PATH);
}
