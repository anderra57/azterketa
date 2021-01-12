package ehu.isad.controllers.db;

import ehu.isad.model.PHPMyAdminModel;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PHPMyAdminDB {

    private static final PHPMyAdminDB instance = new PHPMyAdminDB();
    private static final DBController dbcontroller = DBController.getController();

    private PHPMyAdminDB() {}

    public static PHPMyAdminDB getInstance() {
        return instance;
    }


    public boolean md5listan(String md5) {
        String query = "SELECT md5 FROM checksums where md5='"+md5+"'";
        ResultSet rs = dbcontroller.execSQL(query);
        Boolean listan= false;
        try {
            if (rs.next()) listan=true;
        } catch (SQLException e) { }
        return listan;
    }

    public void gehitu(String version, String md5) {
        String query = "INSERT INTO checksums VALUES (1,'"+version+"','"+md5+"','README')";
        dbcontroller.execSQL(query);
    }

    public void editatu(String md5, String version) {
        String query = "UPDATE checksums SET version='"+version+"' WHERE md5='"+md5+"'";
        dbcontroller.execSQL(query);
    }
}
