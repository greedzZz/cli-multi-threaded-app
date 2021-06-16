package server.utility;

import common.content.*;
import server.CollectionManager;

import java.sql.*;

public class DataBaseManager {
    private Connection connection;
    private Statement statement;
    private final String URL;
    private final String user;
    private final String password;

    public DataBaseManager(String host, String database, String user, String password) {
        this.user = user;
        this.password = password;
        this.URL = "jdbc:postgresql://" + host + ":5432/" + database;
    }

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
            throw new SQLException("Wrong login/password or database host/name.");
        }
    }

    public void createMainTable() throws SQLException {
        try {
            statement = connection.createStatement();
            String sqlCommand = "CREATE TABLE IF NOT EXISTS marines (owner TEXT NOT NULL, id INT PRIMARY KEY," +
                    " name TEXT NOT NULL, coordinate_x INT NOT NULL," +
                    " coordinate_y INT NOT NULL, creation_date VARCHAR(29) NOT NULL," +
                    " health INT, astartes_category VARCHAR(8), weapon VARCHAR(16)," +
                    " melee_weapon VARCHAR(11), chapter_name TEXT NOT NULL ," +
                    " chapter_world TEXT NOT NULL);";
            statement.executeUpdate(sqlCommand);
        } catch (SQLException e) {
            throw new SQLException("A database access error has occurred or connection has closed.");
        }
    }

    public void createUserTable() throws SQLException {
        try {
            statement = connection.createStatement();
            String sqlCommand = "CREATE TABLE IF NOT EXISTS users (login TEXT PRIMARY KEY, password TEXT NOT NULL);";
            statement.executeUpdate(sqlCommand);
        } catch (SQLException e) {
            throw new SQLException("A database access error has occurred or connection has closed.");
        }
    }

    public void fillCollection(CollectionManager cm) throws ClassNotFoundException, SQLException {
        loadDriver();
        connect();
        createMainTable();
        createUserTable();
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
            String owner = resultSet.getString("owner");

            cm.put(new SpaceMarine(id, name, new Coordinates(x, y), creationDate, health, category, weapon, meleeWeapon, new Chapter(chapName, chapWorld), owner));
        }
        cm.setConnection(connection);
        statement.close();
    }

    public Connection getConnection() {
        return connection;
    }
}
