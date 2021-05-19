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
            String sqlCommand = "CREATE TABLE IF NOT EXISTS \"SpaceMarines\" (\"ID\" INT PRIMARY KEY," +
                    " \"Name\" VARCHAR(255) NOT NULL, \"Coordinate X\" INT NOT NULL," +
                    " \"Coordinate Y\" INT NOT NULL, \"Creation date\" VARCHAR(63) NOT NULL," +
                    " \"Health\" INT, \"Astartes category\" VARCHAR(255), \"Weapon\" VARCHAR(255)," +
                    " \"Melee weapon\" VARCHAR(255), \"Chapter name\" VARCHAR(255) NOT NULL ," +
                    " \"Chapter world\" VARCHAR(255) NOT NULL);";
            statement.executeUpdate(sqlCommand);
        } catch (SQLException e) {
            throw new SQLException("A database access error has occurred or connection has closed.");
        }
    }

    public void fillCollection(CollectionManager cm) throws ClassNotFoundException, SQLException {
        loadDriver();
        connect();
        createMainTable();
        String sqlCommand = "SELECT * FROM \"SpaceMarines\";";
        ResultSet resultSet = statement.executeQuery(sqlCommand);
        while (resultSet.next()) {
            int id = resultSet.getInt("ID");
            String name = resultSet.getString("Name");
            int x = resultSet.getInt("Coordinate X");
            Integer y = resultSet.getInt("Coordinate Y");

            String strDate = resultSet.getString("Creation date");
            CharSequence creationDate = strDate.subSequence(0, strDate.length());

            Integer health = resultSet.getInt("Health");
            if (health == 0) {
                health = null;
            }

            String strCategory = resultSet.getString("Astartes category");
            AstartesCategory category;
            if (strCategory == null) {
                category = null;
            } else {
                category = AstartesCategory.valueOf(strCategory);
            }

            String strWeapon = resultSet.getString("Weapon");
            Weapon weapon;
            if (strWeapon == null) {
                weapon = null;
            } else {
                weapon = Weapon.valueOf(strWeapon);
            }

            String strMelee = resultSet.getString("Melee weapon");
            MeleeWeapon meleeWeapon;
            if (strMelee == null) {
                meleeWeapon = null;
            } else {
                meleeWeapon = MeleeWeapon.valueOf(strMelee);
            }

            String chapName = resultSet.getString("Chapter name");
            String chapWorld = resultSet.getString("Chapter world");

            cm.put(new SpaceMarine(id, name, new Coordinates(x, y), creationDate, health, category, weapon, meleeWeapon, new Chapter(chapName, chapWorld)));
        }
        cm.setConnection(connection);
    }
}
