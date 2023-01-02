package pl.coderslab.users;

import org.mindrot.jbcrypt.BCrypt;
import pl.coderslab.utils.DbUtil;

import java.sql.*;
import java.util.Arrays;

public class UserDao {

    private static final String CREATE_DATABASE_QUERY = "create database if not exists workshop_2\n" +
            "character set utf8mb4\n" +
            "collate utf8mb4_unicode_ci;";

    private static final String CREATE_USERS_TABLE_QUERY = "create table if not exists workshop_2.users(\n" +
            "id int(11) not null primary key auto_increment,\n" +
            "email varchar(255) not null unique,\n" +
            "username varchar(255) not null,\n" +
            "password varchar(60) not null\n" +
            ");";

    private static final String CREATE_USER_QUERY = "insert into workshop_2.users(username, email, password) values (?,?,?)";

    private static final String READ_USER_QUERY = "select * from workshop_2.users where id = ?";

    private static final String UPDATE_USER_QUERY = "update workshop_2.users set username = ? , email = ? , password = ? where id = ?;";

    private static final String DELETE_USER_QUERY = "delete from workshop_2.users where id = ?;";

    private static final String FINDALL_USER_QUERY= "select * from workshop_2.users;";

    public static void CreatedDataBase(){
        try (Connection connection = DbUtil.getConnection();
             Statement statement = connection.createStatement()) {

                statement.execute(CREATE_DATABASE_QUERY);
                statement.execute(CREATE_USERS_TABLE_QUERY);



        } catch (SQLException e) {
            e.printStackTrace();

        }



    }
    public String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    public User create(User user) {
        try (Connection conn = DbUtil.getConnection()) {
            PreparedStatement statement =
                    conn.prepareStatement(CREATE_USER_QUERY, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, user.getUserName());
            statement.setString(2, user.getEmail());
            statement.setString(3, hashPassword(user.getPassword()));
            statement.executeUpdate();
            //Pobieramy wstawiony do bazy identyfikator, a następnie ustawiamy id obiektu user.
            ResultSet resultSet = statement.getGeneratedKeys();
            if (resultSet.next()) {
                user.setId(resultSet.getInt(1));

            }
            return user;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    public User read (int userId){
        User readuser= new User();
        try (Connection conn = DbUtil.getConnection()){
            try (PreparedStatement statement = conn.prepareStatement(READ_USER_QUERY)) {
                statement.setInt(1, userId);
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()){
                        readuser.setId(resultSet.getInt(1));
                        readuser.setEmail(resultSet.getString(2));
                        readuser.setUserName(resultSet.getString(3));
                        readuser.setPassword(resultSet.getString(4));
                    }
                    return readuser;
                }
            }
             } catch (SQLException e) {
                 e.printStackTrace();
        }
        return null;
    }
    public void update(User user) {
        try (Connection conn = DbUtil.getConnection()) {
            PreparedStatement statement = conn.prepareStatement(UPDATE_USER_QUERY);
            statement.setString(1, user.getUserName());
            statement.setString(2,user.getEmail());
            statement.setString(3,hashPassword(user.getPassword()));
            statement.setInt(4,user.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }


    }
    public void delete (int usetId){
        try (Connection conn = DbUtil.getConnection()) {
            PreparedStatement statement = conn.prepareStatement(DELETE_USER_QUERY);
            statement.setInt(1, usetId);
            statement.executeUpdate();
        } catch (SQLException e) {
           e.printStackTrace();
        }
    }
    public User[] findAll(){
        User[] findUser = new User[0];
        try (Connection conn = DbUtil.getConnection();
            Statement statement = conn.createStatement()){
            try (ResultSet resultSet = statement.executeQuery(FINDALL_USER_QUERY)){
                while (resultSet.next()){
                    User nextUser = new User();
                    nextUser.setId(resultSet.getInt(1));
                    nextUser.setEmail(resultSet.getString(2));
                    nextUser.setUserName(resultSet.getString(3));
                    nextUser.setPassword(resultSet.getString(4));
                    findUser = addToArray(nextUser,findUser);
                }
            }return findUser;


        } catch (SQLException e) {
            e.printStackTrace();
        }return null;

    }



    private User[] addToArray(User u, User[] users) {
        User[] tmpUsers = Arrays.copyOf(users, users.length + 1); // Tworzymy kopię tablicy powiększoną o 1.
        tmpUsers[users.length] = u; // Dodajemy obiekt na ostatniej pozycji.
        return tmpUsers; // Zwracamy nową tablicę.
    }



}






