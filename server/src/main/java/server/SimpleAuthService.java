package server;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SimpleAuthService implements AuthService {

    private static final String DB_URL = "jdbc:sqlite:/Users/nnoopt/IdeaProjects/chat_final/users.db";

    private class UserData {
        String login;
        String password;
        String nickname;


        public UserData(String login, String password, String nickname) {
            this.login = login;
            this.password = password;
            this.nickname = nickname;
        }
    }

    private List<UserData> users;

    public SimpleAuthService() {

        users = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(DB_URL)) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM users");

            while (resultSet.next()) {
                String login = resultSet.getString("login");
                String nickname = resultSet.getString("nickname");
                String password = resultSet.getString("password");
                users.add(new UserData(login, password, nickname));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getNicknameByLoginAndPassword(String login, String password) {
        for (UserData user : users) {
            if (user.login.equals(login) && user.password.equals(password)) {
                return user.nickname;
            }

        }
        return null;
    }

    @Override
    public Boolean registration(String login, String password, String nickname) {
        for (UserData user : users) {
            if (user.login.equals(login) || user.nickname.equals(nickname)) {
                return false;
            }

        }

        try (Connection connection = DriverManager.getConnection(DB_URL)) {

            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO users (login, password, nickname) VALUES(?, ?, ?)");
            preparedStatement.setString(1, login);
            preparedStatement.setString(2, password);
            preparedStatement.setString(3, nickname);
            preparedStatement.executeUpdate();

            users.add(new UserData(login, password, nickname));

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return true;

    }
}