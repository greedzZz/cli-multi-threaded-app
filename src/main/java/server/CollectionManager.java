package server;

import common.content.Chapter;
import common.content.SpaceMarine;
import server.utility.SpaceMarineDescriber;

import java.sql.*;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class CollectionManager {
    private final TreeMap<Integer, SpaceMarine> treeMap;
    private final Date date;
    private final HashMap<String, String> commandPool = new HashMap<>();
    private Connection connection;
    private PreparedStatement preparedStatement;

    {
        commandPool.put("help", "Displays information on available commands.");
        commandPool.put("info", "Displays information about the collection.");
        commandPool.put("show", "Displays information about elements of the collection.");
        commandPool.put("insert \"key\"", "Adds a new element with the given key.");
        commandPool.put("update \"id\"", "Updates the value of the collection element whose id is equal to the given.");
        commandPool.put("remove_key \"key\"", "Removes a collection element by its key.");
        commandPool.put("clear", "Clears the collection.");
        commandPool.put("execute_script \"file_name\"", "Reads and executes a script from the specified file.");
        commandPool.put("exit", "Ends the program.");
        commandPool.put("remove_greater", "Removes all items from the collection that are greater than the specified one.");
        commandPool.put("replace_if_greater \"key\"", "Replaces the value by key if the new value is greater than the old one.");
        commandPool.put("remove_greater_key \"key\"", "Removes from the collection all elements whose key exceeds the given one.");
        commandPool.put("group_counting_by_coordinates", "Groups the elements of the collection by the value of the coordinates field, display the number of elements in each group.");
        commandPool.put("filter_by_chapter \"chapter\"", "Displays elements whose chapter field is equal to the given.");
        commandPool.put("filter_starts_with_name \"name\"", "Displays elements whose name field value begins with a given substring.");
    }

    public CollectionManager() {
        this.date = new Date();
        this.treeMap = new TreeMap<>();
    }

    public String help() {
        return commandPool.keySet().stream()
                .map(com -> com + ": " + commandPool.get(com) + "\n")
                .collect(Collectors.joining());
    }

    public String info() {
        return "Collection type: " + treeMap.getClass() + "\n" +
                "Collection initialization date: " + date + "\n" +
                "Collection size: " + treeMap.size() + "\n";
    }

    public String show() {
        try {
            SpaceMarineDescriber smd = new SpaceMarineDescriber();
            if (treeMap.isEmpty()) {
                throw new Exception("The collection is empty.");
            }
            return treeMap.values().stream()
                    .map(smd::describe)
                    .map(str -> str + "\n\n")
                    .collect(Collectors.joining());
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    public String insert(Integer key, SpaceMarine sm) {
        try {
            sm.setID(key);
            sm.setCreationDate();
            String sqlCommand;
            if (treeMap.containsKey(key)) {
                sqlCommand = "UPDATE marines SET name=?, coordinate_x=?, coordinate_y=?," +
                        " creation_date=?, health=?, astartes_category=?," +
                        " weapon=?, melee_weapon=?, chapter_name=?, chapter_world=? WHERE id=?;";
                updateDataBase(sqlCommand, sm, key);
            } else {
                sqlCommand = "INSERT INTO marines VALUES (?,?,?,?,?,?,?,?,?,?,?);";
                preparedStatement = connection.prepareStatement(sqlCommand);

                preparedStatement.setInt(1, key);
                preparedStatement.setString(2, sm.getName());
                preparedStatement.setInt(3, sm.getCoordinateX());
                preparedStatement.setInt(4, sm.getCoordinateY());
                preparedStatement.setString(5, sm.getCreationDate());

                if (sm.getHealth() == null) {
                    preparedStatement.setNull(6, Types.INTEGER);
                } else {
                    preparedStatement.setInt(6, sm.getHealth());
                }

                if (sm.getCategory() == null) {
                    preparedStatement.setNull(7, Types.VARCHAR);
                } else {
                    preparedStatement.setString(7, sm.getCategory().toString());
                }

                if (sm.getWeaponType() == null) {
                    preparedStatement.setNull(8, Types.VARCHAR);
                } else {
                    preparedStatement.setString(8, sm.getWeaponType().toString());
                }

                if (sm.getMeleeWeapon() == null) {
                    preparedStatement.setNull(9, Types.VARCHAR);
                } else {
                    preparedStatement.setString(9, sm.getMeleeWeapon().toString());
                }

                preparedStatement.setString(10, sm.getChapterName());
                preparedStatement.setString(11, sm.getChapterWorld());
            }
            preparedStatement.executeUpdate();
            preparedStatement.close();
            return put(sm);
        } catch (SQLException e) {
            return "A database access error has occurred or connection has closed.\n";
        }
    }

    public String update(Integer id, SpaceMarine sm) {
        try {
            if (!treeMap.containsKey(id)) {
                throw new Exception("There is no element with such id in the collection.");
            } else {
                sm.setID(id);
                sm.setCreationDate();
                String sqlCommand = "UPDATE marines SET name=?, coordinate_x=?, coordinate_y=?," +
                        " creation_date=?, health=?, astartes_category=?," +
                        " weapon=?, melee_weapon=?, chapter_name=?, chapter_world=? WHERE id=?;";
                updateDataBase(sqlCommand, sm, id);
                preparedStatement.executeUpdate();
                preparedStatement.close();
                put(sm);
                return "Value of element with id " + id + " has been updated.\n";
            }
        } catch (SQLException s) {
            return "A database access error has occurred or connection has closed.\n";
        } catch (Exception e) {
            return e.getMessage() + "\n";
        }
    }

    public String removeKey(Integer key) {
        try {
            if (!treeMap.containsKey(key)) {
                throw new Exception("There is no such argument in the collection.");
            } else {
                String sqlCommand = "DELETE FROM marines WHERE id=?;";
                preparedStatement = connection.prepareStatement(sqlCommand);
                preparedStatement.setInt(1, key);
                preparedStatement.executeUpdate();
                preparedStatement.close();
                treeMap.remove(key);
                return "Element with " + key + " key has been deleted.\n";
            }
        } catch (SQLException s) {
            return "A database access error has occurred or connection has closed.\n";
        } catch (Exception e) {
            return e.getMessage() + "\n";
        }
    }

    public String clear() {
        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate("DELETE FROM marines;");
            statement.close();
            treeMap.clear();
            return "The collection has been cleared.\n";
        } catch (SQLException s) {
            return "A database access error has occurred or connection has closed.\n";
        }
    }

    public String executeScript() {
        return "Starting execution of the script.\n";
    }

    public String exit() {
        return "The program is finished.\n";
    }

    public String removeGreater(SpaceMarine sm) {
        try {
            if (treeMap.isEmpty()) {
                throw new Exception("The collection is empty.");
            } else {
                Iterator<SpaceMarine> iterator = treeMap.values().iterator();
                String name;
                String sqlCommand;
                StringBuilder sb = new StringBuilder();
                while (iterator.hasNext()) {
                    SpaceMarine next = iterator.next();
                    if (sm.compareTo(next) < 0) {
                        name = next.getName();
                        try {
                            sqlCommand = "DELETE FROM marines WHERE id=?;";
                            preparedStatement = connection.prepareStatement(sqlCommand);
                            preparedStatement.setInt(1, next.getID());
                            preparedStatement.executeUpdate();
                            preparedStatement.close();
                            iterator.remove();
                            sb.append("Space marine ").append(name).append(" has been removed from the collection.\n");
                        } catch (SQLException s) {
                            sb.append("Space marine ").append(name).append(" has not been removed from the collection" +
                                    " due to a database access error or a closed connection.\n");
                        }
                    }
                }
                if (sb.length() > 0) {
                    return sb.toString();
                } else {
                    return "There are no elements that are greater than the specified one.\n";
                }
            }
        } catch (Exception e) {
            return e.getMessage() + "\n";
        }
    }

    public String replaceIfGreater(Integer key, SpaceMarine sm) {
        try {
            if (!treeMap.containsKey(key)) {
                throw new Exception("There is no such argument in the collection.");
            } else {
                if (sm.compareTo(treeMap.get(key)) > 0) {
                    try {
                        sm.setID(key);
                        sm.setCreationDate();
                        String sqlCommand = "UPDATE marines SET name=?, coordinate_x=?, coordinate_y=?," +
                                " creation_date=?, health=?, astartes_category=?," +
                                " weapon=?, melee_weapon=?, chapter_name=?, chapter_world=? WHERE id=?;";
                        updateDataBase(sqlCommand, sm, key);
                        preparedStatement.executeUpdate();
                        preparedStatement.close();
                        treeMap.put(sm.getID(), sm);
                        return "Element with " + key + " key has been replaced.\n";
                    } catch (SQLException s) {
                        return "A database access error has occurred or connection has closed.\n";
                    }
                } else {
                    return "Value of the entered element does not exceed the value of the collection element.\n";
                }
            }
        } catch (Exception e) {
            return e.getMessage() + "\n";
        }
    }

    public String removeGreaterKey(Integer key) {
        try {
            if (treeMap.isEmpty()) {
                throw new Exception("The collection is empty.");
            } else {
                Iterator<SpaceMarine> iterator = treeMap.values().iterator();
                String sqlCommand;
                StringBuilder sb = new StringBuilder();
                while (iterator.hasNext()) {
                    SpaceMarine next = iterator.next();
                    Integer currentKey = next.getID();
                    if (currentKey > key) {
                        try {
                            sqlCommand = "DELETE FROM marines WHERE id=?;";
                            preparedStatement = connection.prepareStatement(sqlCommand);
                            preparedStatement.setInt(1, next.getID());
                            preparedStatement.executeUpdate();
                            preparedStatement.close();
                            iterator.remove();
                            sb.append("Element with key ").append(currentKey).append(" has been deleted.\n");
                        } catch (SQLException s) {
                            sb.append("Element with key ").append(currentKey).append(" has not been deleted from the collection" +
                                    " due to a database access error or a closed connection.\n");
                        }
                    }
                }
                if (sb.length() > 0) {
                    return sb.toString();
                } else {
                    return "There are no elements whose key exceeds the given one.\n";
                }
            }
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    public String groupCountingByCoordinates() {
        try {
            if (treeMap.isEmpty()) {
                throw new Exception("The collection is empty.");
            } else {
                long first = treeMap.values().stream()
                        .filter(sm -> sm.getCoordinateX() >= 0)
                        .filter(sm -> sm.getCoordinateY() >= 0)
                        .count();
                long second = treeMap.values().stream()
                        .filter(sm -> sm.getCoordinateX() < 0)
                        .filter(sm -> sm.getCoordinateY() >= 0)
                        .count();
                long third = treeMap.values().stream()
                        .filter(sm -> sm.getCoordinateX() < 0)
                        .filter(sm -> sm.getCoordinateY() < 0)
                        .count();
                long fourth = treeMap.values().stream()
                        .filter(sm -> sm.getCoordinateX() >= 0)
                        .filter(sm -> sm.getCoordinateY() < 0)
                        .count();
                return "There are " + first + " space marines in the first coordinate quarter, " +
                        "" + second + " in the second one, " + third +
                        " in the third one, " + fourth + " in the fourth one.\n";
            }
        } catch (Exception e) {
            return e.getMessage() + "\n";
        }
    }

    public String filterByChapter(Chapter chapter) {
        try {
            if (treeMap.isEmpty()) {
                throw new Exception("The collection is empty.");
            }
            String chapterName = chapter.getName();
            String chapterWorld = chapter.getWorld();
            SpaceMarineDescriber smd = new SpaceMarineDescriber();
            String marines = treeMap.values().stream()
                    .filter(sm -> sm.getChapterName().equals(chapterName) && sm.getChapterWorld().equals(chapterWorld))
                    .map(sm -> smd.describe(sm) + "\n\n")
                    .collect(Collectors.joining());
            if (marines.length() > 1) {
                return "Elements whose chapter value is equal to entered value:\n" + marines;
            } else {
                return "There are no elements whose chapter value is equal to entered value.\n";
            }
        } catch (Exception e) {
            return e.getMessage() + "\n";
        }
    }

    public String filterStartsWithName(String name) {
        try {
            if (treeMap.isEmpty()) {
                throw new Exception("The collection is empty.");
            }
            SpaceMarineDescriber smd = new SpaceMarineDescriber();
            String marines = treeMap.values().stream()
                    .filter(sm -> sm.getName().startsWith(name))
                    .map(sm -> smd.describe(sm) + "\n\n")
                    .collect(Collectors.joining());
            if (marines.length() > 1) {
                return "Elements whose starts with entered value:\n" + marines;
            } else {
                return "There are no elements whose starts with entered value.\n";
            }
        } catch (Exception e) {
            return e.getMessage() + "\n";
        }
    }

    public void updateDataBase(String sqlCommand, SpaceMarine sm, Integer key) throws SQLException {
        preparedStatement = connection.prepareStatement(sqlCommand);

        preparedStatement.setString(1, sm.getName());
        preparedStatement.setInt(2, sm.getCoordinateX());
        preparedStatement.setInt(3, sm.getCoordinateY());
        preparedStatement.setString(4, sm.getCreationDate());

        if (sm.getHealth() == null) {
            preparedStatement.setNull(5, Types.INTEGER);
        } else {
            preparedStatement.setInt(5, sm.getHealth());
        }

        if (sm.getCategory() == null) {
            preparedStatement.setNull(6, Types.VARCHAR);
        } else {
            preparedStatement.setString(6, sm.getCategory().toString());
        }

        if (sm.getWeaponType() == null) {
            preparedStatement.setNull(7, Types.VARCHAR);
        } else {
            preparedStatement.setString(7, sm.getWeaponType().toString());
        }

        if (sm.getMeleeWeapon() == null) {
            preparedStatement.setNull(8, Types.VARCHAR);
        } else {
            preparedStatement.setString(8, sm.getMeleeWeapon().toString());
        }

        preparedStatement.setString(9, sm.getChapterName());
        preparedStatement.setString(10, sm.getChapterWorld());
        preparedStatement.setInt(11, key);
    }

    public String put(SpaceMarine sm) {
        treeMap.put(sm.getID(), sm);
        return "Space marine " + sm.getName() + " has been added to the collection!" + "\n";
    }

    public void close() {
        try {
            connection.close();
        } catch (SQLException s) {
            System.exit(0);
        }
        System.exit(0);
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

}
