package server;

import commands.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClientHandler {

    private static final Logger log = LoggerFactory.getLogger(ClientHandler.class);

    private Server server;
    private Socket socket;

    private DataInputStream in;
    private DataOutputStream out;

    private String nickname;
    private String login;

    public ClientHandler(Server server, Socket socket) {


        try {

            this.server = server;
            this.socket = socket;

            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());


            ExecutorService executorService = Executors.newCachedThreadPool();
            executorService.execute(() -> {

                        try {
                            try {
                                socket.setSoTimeout(120000);

                                //цикл аутентификации
                                while (true) {
                                    String str = in.readUTF();
                                    if (str.startsWith("/")) {
                                        if (str.equals(Command.END)) {
                                            log.info("client want to disconnected");
//                                            System.out.println("client want to disconnected");
                                            out.writeUTF(Command.END);
//                                            throw new RuntimeException("client want to disconnected");
                                            log.error("client want to disconnected");

                                        }
                                        if (str.startsWith(Command.AUTH)) {
                                            String[] token = str.split("\\s");
                                            String newNick = server.getAuthService()
                                                    .getNicknameByLoginAndPassword(token[1], token[2]);
                                            login = token[1];

                                            if (newNick != null) {
                                                if (!server.isLoginAuth(login)) {
                                                    nickname = newNick;
                                                    socket.setSoTimeout(0);
                                                    sendMsg(Command.AUTH_OK + " " + nickname);
                                                    server.subscribe(this);
                                                    break;
                                                } else {
                                                    sendMsg("С этим логином уже вошли");
                                                }
                                            } else {
                                                sendMsg("Неверный логин / пароль");
                                            }
                                        }

                                        if (str.startsWith(Command.REG)) {
                                            String[] token = str.split("\\s");
                                            if (token.length < 4) {
                                                continue;
                                            }
                                            boolean regSuccessful = server.getAuthService().
                                                    registration(token[1], token[2], token[3]);
                                            if (regSuccessful) {
                                                sendMsg(Command.REG_OK);
                                            } else {
                                                sendMsg(Command.REG_NO);
                                            }
                                        }
                                    }
                                }

                                //цикл работы
                                while (true) {
                                    String str = in.readUTF();
                                    if (str.startsWith("/")) {
                                        if (str.equals(Command.END)) {
                                            out.writeUTF(Command.END);
                                            break;
                                        }
                                        if (str.startsWith(Command.PRIVATE_MSG)) {
                                            String[] token = str.split("\\s+", 3);
                                            if (token.length < 3) {
                                                continue;
                                            }
                                            server.privateMsg(this, token[1], token[2]);
                                        }
                                    } else {
                                        server.broadcastMsg(this, str);
                                    }
                                }
                            } catch (SocketTimeoutException e) {
                                out.writeUTF(Command.END);
                            }

                        } catch (RuntimeException e) {
                            System.out.println(e.getMessage());
                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
//                            System.out.println("client disconnected");
                            log.info("client disconnected");
                            server.unsubscribe(this);
                            try {
                                socket.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                executorService.shutdown();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMsg (String msg){
        try {
            out.writeUTF(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getNickname() {
        return nickname;
    }

    public String getLogin() {
        return login;
    }
}
