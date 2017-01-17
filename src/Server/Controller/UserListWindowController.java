package Server.Controller;

import Server.Model.UserKey;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;

import java.util.ArrayList;

public class UserListWindowController {
    @FXML
    private Button buttonZapisz;
    @FXML
    private Button buttonPrzywroc;
    @FXML
    private Button buttonDodaj;
    @FXML
    private Button buttonUsun;
    @FXML
    private TextField textName;
    @FXML
    private TextField textKey;
    @FXML
    private TextField textPower;
    @FXML
    private TableView<UserKey> tableUsers = new TableView<>();
    private ArrayList<UserKey> userList;
    private ObservableList<UserKey> data;


    public void iniWindow(ArrayList<UserKey> userList) {
        this.userList = userList;
        ini();
    }

    private void ini() {
        buttonDodaj.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                data.add(new UserKey(textName.getText(), textKey.getText(), textPower.getText()));
                textName.setText("Nazwa");
                textKey.setText("Klucz publiczny");
                textPower.setText("Poziom uprawnień");
            }
        });

        buttonZapisz.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                userList.clear();
                for (UserKey aData : data) {
                    userList.add(aData);
                }
            }
        });

        buttonPrzywroc.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                data.clear();
                for (UserKey anUserList : userList) {
                    data.add(anUserList);
                }
            }
        });

        buttonUsun.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                if (!tableUsers.getSelectionModel().isEmpty()) {
                    data.remove(tableUsers.getSelectionModel().getSelectedItem());
                }
            }
        });


        if (!userList.isEmpty()) {
            data = FXCollections.observableArrayList(userList.get(0));
            for (int i = 1; i < userList.size(); i++) {
                data.add(userList.get(i));
            }
        }

        tableUsers.setEditable(true);
        TableColumn nameColumn = new TableColumn("Nazwa");
        nameColumn.setCellValueFactory(
                new PropertyValueFactory<UserKey, String>("name"));
        nameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        nameColumn.setOnEditCommit(
                new EventHandler<TableColumn.CellEditEvent<UserKey, String>>() {
                    @Override
                    public void handle(TableColumn.CellEditEvent<UserKey, String> t) {
                        t.getTableView().getItems().get(
                                t.getTablePosition().getRow()).setName(t.getNewValue());
                    }
                }
        );
        TableColumn pKeyColumn = new TableColumn("Klucz publiczny");
        pKeyColumn.setCellValueFactory(
                new PropertyValueFactory<UserKey, String>("publicKey"));
        pKeyColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        pKeyColumn.setOnEditCommit(
                new EventHandler<TableColumn.CellEditEvent<UserKey, String>>() {
                    @Override
                    public void handle(TableColumn.CellEditEvent<UserKey, String> t) {
                        t.getTableView().getItems().get(
                                t.getTablePosition().getRow()).setPublicKey(t.getNewValue());
                    }
                }
        );
        TableColumn powerColumn = new TableColumn("Poziom uprawnien");
        powerColumn.setCellValueFactory(
                new PropertyValueFactory<UserKey, String>("power"));
        powerColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        powerColumn.setOnEditCommit(
                new EventHandler<TableColumn.CellEditEvent<UserKey, String>>() {
                    @Override
                    public void handle(TableColumn.CellEditEvent<UserKey, String> t) {
                        t.getTableView().getItems().get(
                                t.getTablePosition().getRow()).setPower(t.getNewValue());
                    }
                }
        );

        tableUsers.setItems(data);
        tableUsers.getColumns().addAll(nameColumn, powerColumn, pKeyColumn);
    }
}
