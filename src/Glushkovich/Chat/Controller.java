package Glushkovich.Chat;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import Glushkovich.AuthException;
import Glushkovich.Chat.Network.IncomeMessageHandler;
import Glushkovich.Chat.Network.Network;

import java.io.IOException;
import java.util.List;


public class Controller implements IncomeMessageHandler {

    private Alert errorAlert;
    private Network network;
    private boolean isAuthorized;

    public static int MESSAGES_TO_SHOW_COUNT = 100;  // предполагаем, что это можно будет менять в настройках

    @FXML
    private ListView<Message> listMessages;

    @FXML
    private TextField txtMessageInput;

    @FXML
    private Button bttnSendMessage;

    @FXML
    private VBox panelLogin;

    @FXML
    private VBox panelMessages;

    @FXML
    private Button bttnLogIn;

    @FXML
    private Button bttnSignUp;

    @FXML
    private Button bttnCancel;

    @FXML
    private TextField txtLogin;

    @FXML
    private PasswordField txtPassword;

    @FXML
    private VBox mainLayout;

    @FXML
    private ListView<String> usersOnLine;

    private ObservableList<String> users = FXCollections.observableArrayList("all");

    private Stage stage;

    private ObservableList<Message> messages = FXCollections.observableArrayList();


    @FXML
    private void initialize() {
        errorAlert = new Alert(Alert.AlertType.ERROR);

        usersOnLine.setItems(users);
        usersOnLine.getSelectionModel().select(0);

        bttnSendMessage.setOnAction(this::sendMessage);
        txtMessageInput.setOnAction(this::sendMessage);
        bttnLogIn.setOnAction(this::okBttnCLicked);
        bttnSignUp.setOnAction(this::okBttnCLicked);
//        txtPassword.setOnAction(this::okBttnCLicked);
        bttnCancel.setOnAction(this::cancelBttnClicked);

        setAuthorized(false);

        listMessages.setCellFactory(messageListView -> new MessageCell());
        listMessages.setItems(messages);



    }

    private void sendMessage(ActionEvent actionEvent) {

        String message = txtMessageInput.getText();
        String to = usersOnLine.getSelectionModel().getSelectedItem();
        network.sendMessage(to, message);
        txtMessageInput.clear();
        txtMessageInput.requestFocus();
    }


    public void setAuthorized(boolean authorized) {
        isAuthorized = authorized;
        panelLogin.setVisible(!isAuthorized);
        panelLogin.setManaged(!isAuthorized);
        panelMessages.setVisible(isAuthorized);
        panelMessages.setManaged(isAuthorized);
    }

    public boolean authorize(String login, String password, String action) {
        try {
            network.authorize(login, password, action);


        } catch (AuthException e) {
            showError("Authorization ERROR", e.getMessage());
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private void okBttnCLicked(ActionEvent event) {

        try {
            network = new Network("localhost", 5354);

            network.setIncomeMessageHandler(this);

            //TODO: проверка на пустые значения
            String login = txtLogin.getText();
            String password = txtPassword.getText();

            String action = ((Button) event.getTarget()).getText();

            setAuthorized(authorize(login, password, action));

            network.requestOnlineUsersList();

            stage = (Stage) mainLayout.getScene().getWindow();
            stage.setTitle(stage.getTitle() + " " + login);

            txtMessageInput.requestFocus();

        } catch (IOException e) {
            showError("Network ERROR", "No network connection");
        }
        txtPassword.clear();

    }

    private void cancelBttnClicked(ActionEvent event) {
        System.exit(1);
    }

    private void showError(String title, String errorMessage) {
        errorAlert.setTitle(title);
        errorAlert.setContentText(errorMessage);
        errorAlert.setHeaderText(null);
        errorAlert.showAndWait();
    }

    @Override
    public void handleMessage(Message message) {
        if (message.getUser().equals(network.getLogin())) {
            message.setUser("Вы");
        }
        Platform.runLater(() -> {
            messages.add(message);
            listMessages.scrollTo(messages.size()-1);
        });
    }

    @Override
    public void addOnlineUser(String user) {
        Platform.runLater(() -> users.add(user));
    }

    @Override
    public void removeOnlineUser(String user) {
        Platform.runLater(() -> users.remove(user));
    }

    @Override
    public void setOnlineUsersList(List<String> usersList) {
        if (!usersList.isEmpty())
            Platform.runLater(() -> users.addAll(usersList));
    }
}