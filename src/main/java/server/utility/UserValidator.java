package server.utility;

import common.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserValidator {
    private final Connection connection;

    public UserValidator(Connection connection) {
        this.connection = connection;
    }

    public boolean validate(User user) throws SQLException {
        if (user.isNewbie()) {
            String sqlCommand = "SELECT login FROM users WHERE login=?;";
            PreparedStatement preparedStatement = connection.prepareStatement(sqlCommand);
            preparedStatement.setString(1, user.getLogin());
            ResultSet resultSet = preparedStatement.executeQuery();
            if (!resultSet.next()) {
                sqlCommand = "INSERT INTO users VALUES (?,?);";
                preparedStatement = connection.prepareStatement(sqlCommand);
                preparedStatement.setString(1, user.getLogin());
                preparedStatement.setString(2, user.getPassword());
                preparedStatement.executeUpdate();
                return true;
            } else {
                return false;
            }
        } else {
            String sqlCommand = "SELECT login FROM users WHERE login=? AND password=?;";
            PreparedStatement preparedStatement = connection.prepareStatement(sqlCommand);
            preparedStatement.setString(1, user.getLogin());
            preparedStatement.setString(2, user.getPassword());
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next();
        }
    }
}
