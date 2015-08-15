package outout.util;

import org.springframework.security.crypto.password.PasswordEncoder;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DataLoader {
    private DataSource dataSource;
    private PasswordEncoder passwordEncoder;

    public DataLoader(final DataSource dataSource, final PasswordEncoder passwordEncoder) {
        this.dataSource = dataSource;
        this.passwordEncoder = passwordEncoder;
    }

    public void insertUser(String username, String password) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement("insert into user(id,username,password) values(user_seq.nextval,?,?)")) {
            statement.setString(1, username);
            statement.setString(2, passwordEncoder.encode(password));
            statement.executeUpdate();
        }
        catch (SQLException exc) {
            exc.printStackTrace();
        }
    }
}
