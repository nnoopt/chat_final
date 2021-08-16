package com.example.client_next;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class RegController {
    @FXML
    private TextField loginField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField nicknameField;

    @FXML
    private TextArea textArea;

    private HelloController controller;


    public void setController(HelloController controller) {
        this.controller = controller;
    }

    public void tryToReg(ActionEvent actionEvent) {
        String login = loginField.getText().trim();
        String password = passwordField.getText().trim();
        String nickname = nicknameField.getText().trim();

        if (login.length()*password.length()*nickname.length()==0){
            textArea.appendText("Логин, пароль и ник не должны быть пустыми\n");
            return;
        }

        if (login.contains(" ") || password.contains(" ") || nickname.contains(" ") ){
            textArea.appendText("Логин, пароль и ник не должны содержать пробелов\n");
            return;
        }

        controller.tryToReg(login, password, nickname);

    }

    public void resultTryToReg (boolean flag){
        if (flag){
            textArea.appendText("Регистрация прошла успешно\n");
        } else {
            textArea.appendText("Регистрация не получилась\n" +
                    "Возможно логин или ник заняты\n");
        }
    }
}
