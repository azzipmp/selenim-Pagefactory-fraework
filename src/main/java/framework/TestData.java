package framework;

public final class TestData {

    private TestData() {
    }

    public static String url() {
        return ConfigReader.getString("url", "https://practicetestautomation.com/practice-test-login/");
    }

    public static String username() {
        return ConfigReader.getString("username", "student");
    }

    public static String password() {
        return ConfigReader.getString("password", "Password123");
    }
}
