package outout.util;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DatabaseCleanup {
    private DataSource dataSource;

    public DatabaseCleanup(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void deleteUsers() {
        try(Connection connection = dataSource.getConnection()){
            PreparedStatement statement = connection.prepareStatement("delete from user");
            statement.execute();
        }
        catch(SQLException exc) {
            exc.printStackTrace();
        }
    }

    public void deleteSuggestions() {
        try(Connection connection = dataSource.getConnection()){
            PreparedStatement statement = connection.prepareStatement("delete from suggestion");
            statement.execute();
        }
        catch(SQLException exc) {
            exc.printStackTrace();
        }
    }
}
