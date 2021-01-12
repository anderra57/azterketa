package ehu.isad.controllers.ui;

import ehu.isad.controllers.db.PHPMyAdminDB;
import ehu.isad.model.PHPMyAdminModel;
import ehu.isad.utils.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.Callback;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.util.List;
import java.util.ResourceBundle;

public class PHPMyAdminController implements Initializable {

    ObservableList<PHPMyAdminModel> lista = FXCollections.observableArrayList();

    private static PHPMyAdminController instance=new PHPMyAdminController();

    private PHPMyAdminController() {}

    public static PHPMyAdminController getInstance() {
        return instance;
    }

    private PHPMyAdminDB phpMyAdminDB = PHPMyAdminDB.getInstance();

    @FXML
    private TableView<PHPMyAdminModel> table;

    @FXML
    private TableColumn<PHPMyAdminModel, String> colURL;

    @FXML
    private TableColumn<PHPMyAdminModel, String> colmd5;

    @FXML
    private TableColumn<PHPMyAdminModel, String> colversion;

    @FXML
    private TextField txt_url;

    @FXML
    private Button btn_check;

    @FXML
    private Label lbl_zegoen;

    private String pathToTempFiles = Utils.getProperties().getProperty("pathToTempFiles");

    @FXML
    void onClick(ActionEvent event) {
        String url = txt_url.getText()+"/README";
        if (Utils.getStatus(url)){
            String version = getVersion(url);
            if (version!=null){
                String md5 = Utils.getMD5();
                if (!phpMyAdminDB.md5listan(md5)){
                    PHPMyAdminModel model = new PHPMyAdminModel(txt_url.getText(),md5,"");
                    lbl_zegoen.setText("Ez da datubasean aurkitu.");
                    phpMyAdminDB.gehitu("",md5);
                    lista.add(model);
                } else {
                    PHPMyAdminModel model = new PHPMyAdminModel(txt_url.getText(),md5,version);
                    lbl_zegoen.setText("Datubasean zegoen.");
                    lista.add(model);
                }
            }else { lbl_zegoen.setText("README fitxategiarekin arazoa dago."); }
        } else { lbl_zegoen.setText("Ez da konexioa lortu edo ez da README fitxategia aurkitu."); }
    }

    private String getVersion(String url) {
        Utils.getReadme(url);
        String verline=null;
        try {
            List<String> allLines = Files.readAllLines(Paths.get(pathToTempFiles+"phpmyadmin.txt"));
            for (String line : allLines) {
                if (line.contains("Version")) verline=line.split("Version ")[1];
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return verline;
    }

    public void loadTabla(){
        table.setEditable(true);
        colURL.setCellValueFactory(new PropertyValueFactory<>("url"));
        colmd5.setCellValueFactory(new PropertyValueFactory<>("md5"));
        colversion.setCellValueFactory(new PropertyValueFactory<>("version"));
        Callback<TableColumn<PHPMyAdminModel, String>, TableCell<PHPMyAdminModel, String>> defaultTextFieldCellFactory = TextFieldTableCell.<PHPMyAdminModel>forTableColumn();
        colversion.setCellFactory(column -> {
            TableCell<PHPMyAdminModel, String> cell = defaultTextFieldCellFactory.call(column);
            cell.setOnMouseClicked(event -> {
                if (!cell.isEmpty() && cell.getTableView().getSelectionModel().getSelectedItem().getVersion().equals("")) { cell.setEditable(true); }
                else {cell.setEditable(false);}
            });
            return cell;
        });
        colversion.setCellFactory(TextFieldTableCell.forTableColumn());

        colversion.setOnEditCommit((TableColumn.CellEditEvent<PHPMyAdminModel, String> event) -> {
            TablePosition<PHPMyAdminModel, String> pos = event.getTablePosition();
            int row = pos.getRow();
            PHPMyAdminModel phpMyAdminModel = event.getTableView().getItems().get(row);
            String content = event.getNewValue();
            phpMyAdminModel.setVersion(content);
            phpMyAdminDB.editatu(phpMyAdminModel.getMd5(),phpMyAdminModel.getVersion());
            lbl_zegoen.setText("Datubasea eguneratu da.");
            colversion.setCellFactory(column -> {
                TableCell<PHPMyAdminModel, String> cell = defaultTextFieldCellFactory.call(column);
                cell.setEditable(false);
                return cell;
            });
        });

        table.setItems(lista);

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.loadTabla();
    }
}