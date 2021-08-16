package server;

public interface AuthService {
    String getNicknameByLoginAndPassword (String login, String password);
    Boolean registration (String login, String password, String nickname);
}
