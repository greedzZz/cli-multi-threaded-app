package server.utility;

import common.content.*;
import server.CollectionManager;

import java.sql.*;

public class DataBaseManager {
    private Connection connection;
    private Statement statement;
    private final String URL = "jdbc:postgresql://localhost:5432/postgres"; // "jdbc:postgresql://pg:5432/studs";
    private String user = "postgres"; //"s312444";
    private String password = "databases"; //"";

    public void loadDriver() throws ClassNotFoundException {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new ClassNotFoundException("Failed to load driver for database management system.");
        }
    }

    public void connect() throws SQLException {
        try {
            connection = DriverManager.getConnection(URL, user, password);
        } catch (SQLException e) {
            throw new SQLException("Wrong login or password.");
        }
    }

    public void createMainTable() throws SQLException {
        try {
            statement = connection.createStatement();
            String sqlCommand = "CREATE TABLE IF NOT EXISTS marines (id INT PRIMARY KEY," +
                    " name VARCHAR(255) NOT NULL, coordinate_x INT NOT NULL," +
                    " coordinate_y INT NOT NULL, creation_date VARCHAR(63) NOT NULL," +
                    " health INT, astartes_category VARCHAR(255), weapon VARCHAR(255)," +
                    " melee_weapon VARCHAR(255), chapter_name VARCHAR(255) NOT NULL ," +
                    " chapter_world VARCHAR(255) NOT NULL);";
            statement.executeUpdate(sqlCommand);
        } catch (SQLException e) {
            throw new SQLException("A database access error has occurred or connection has closed.");
        }
    }

    public void fillCollection(CollectionManager cm) throws ClassNotFoundException, SQLException {
        loadDriver();
        connect();
        createMainTable();
        String sqlCommand = "SELECT * FROM marines;";
        ResultSet resultSet = statement.executeQuery(sqlCommand);
        while (resultSet.next()) {
            int id = resultSet.getInt("id");
            String name = resultSet.getString("name");
            int x = resultSet.getInt("coordinate_x");
            Integer y = resultSet.getInt("coordinate_y");

            String strDate = resultSet.getString("creation_date");
            CharSequence creationDate = strDate.subSequence(0, strDate.length());

            Integer health = resultSet.getInt("health");
            if (health == 0) {
                health = null;
            }

            String strCategory = resultSet.getString("astartes_category");
            AstartesCategory category;
            if (strCategory == null) {
                category = null;
            } else {
                category = AstartesCategory.valueOf(strCategory);
            }

            String strWeapon = resultSet.getString("weapon");
            Weapon weapon;
            if (strWeapon == null) {
                weapon = null;
            } else {
                weapon = Weapon.valueOf(strWeapon);
            }

            String strMelee = resultSet.getString("melee_weapon");
            MeleeWeapon meleeWeapon;
            if (strMelee == null) {
                meleeWeapon = null;
            } else {
                meleeWeapon = MeleeWeapon.valueOf(strMelee);
            }

            String chapName = resultSet.getString("chapter_name");
            String chapWorld = resultSet.getString("chapter_world");

            cm.put(new SpaceMarine(id, name, new Coordinates(x, y), creationDate, health, category, weapon, meleeWeapon, new Chapter(chapName, chapWorld)));
        }
        cm.setConnection(connection);
    }
}
