package Klient.Controller;

import Klient.Model.ServerElement;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Stage;

import java.util.ArrayList;

public class ServerListWindowController {
    @FXML
    private Button buttonWybierz;
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
    private TextField textIP;
    @FXML
    private TextField textPort;
    @FXML
    private TableView<ServerElement> tableServers = new TableView<>();
    private ArrayList<ServerElement> serverList;
    private ObservableList<ServerElement> data;
    private ActionEvent parentEvent;
    private LoginController loginController;


    public void iniWindow(ArrayList<ServerElement> serverList, ActionEvent e, LoginController loginController) {
        this.serverList = serverList;
        this.parentEvent = e;
        this.loginController = loginController;
        ini();
    }

    private void ini() {
        buttonDodaj.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                data.add(new ServerElement(textName.getText(), textIP.getText(), textPort.getText()));
                textName.setText("Nazwa serwera");
                textIP.setText("Adres IP");
                textPort.setText("Port");
            }
        });

        buttonZapisz.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                serverList.clear();
                for (ServerElement aData : data) {
                    serverList.add(aData);
                }
            }
        });

        buttonPrzywroc.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                data.clear();
                for (ServerElement aServerList : serverList) {
                    data.add(aServerList);
                }
            }
        });

        buttonUsun.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                if (!tableServers.getSelectionModel().isEmpty()) {
                    data.remove(tableServers.getSelectionModel().getSelectedItem());
                }
            }
        });

        buttonWybierz.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                if (!tableServers.getSelectionModel().isEmpty()) {
                    loginController.setServer(tableServers.getSelectionModel().getSelectedItem());
                    Stage parentStage = ((Stage) ((Node) parentEvent.getSource()).getScene().getWindow());
                    parentStage.show();
                    buttonWybierz.getScene().getWindow().hide();
                }
            }
        });

        if (!serverList.isEmpty()) {
            data = FXCollections.observableArrayList(serverList.get(0));
            for (int i = 1; i < serverList.size(); i++) {
                data.add(serverList.get(i));
            }
        }

        tableServers.setEditable(true);
        TableColumn nameColumn = new TableColumn("Nazwa serwera");
        nameColumn.setCellValueFactory(
                new PropertyValueFactory<ServerElement, String>("name"));
        nameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        nameColumn.setOnEditCommit(
                new EventHandler<TableColumn.CellEditEvent<ServerElement, String>>() {
                    @Override
                    public void handle(TableColumn.CellEditEvent<ServerElement, String> t) {
                        t.getTableView().getItems().get(
                                t.getTablePosition().getRow()).setName(t.getNewValue());
                    }
                }
        );
        TableColumn IPColumn = new TableColumn("Adres IP");
        IPColumn.setCellValueFactory(
                new PropertyValueFactory<ServerElement, String>("IP"));
        IPColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        IPColumn.setOnEditCommit(
                new EventHandler<TableColumn.CellEditEvent<ServerElement, String>>() {
                    @Override
                    public void handle(TableColumn.CellEditEvent<ServerElement, String> t) {
                        t.getTableView().getItems().get(
                                t.getTablePosition().getRow()).setIP(t.getNewValue());
                    }
                }
        );
        TableColumn portColumn = new TableColumn("Numer portu");
        portColumn.setCellValueFactory(
                new PropertyValueFactory<ServerElement, String>("port"));
        portColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        portColumn.setOnEditCommit(
                new EventHandler<TableColumn.CellEditEvent<ServerElement, String>>() {
                    @Override
                    public void handle(TableColumn.CellEditEvent<ServerElement, String> t) {
                        t.getTableView().getItems().get(
                                t.getTablePosition().getRow()).setPort(t.getNewValue());
                    }
                }
        );

        tableServers.setItems(data);
        tableServers.getColumns().addAll(nameColumn, IPColumn, portColumn);
    }
}
