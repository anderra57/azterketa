package ehu.isad.utils;

import com.google.gson.Gson;
import ehu.isad.model.PHPMyAdminModel;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.util.Properties;

public class Utils {

    public static Properties getProperties()  {
        Properties properties = null;

        try (InputStream in = Utils.class.getResourceAsStream("/setup.properties")) {
            properties = new Properties();
            properties.load(in);

        } catch (
                IOException e) {
            e.printStackTrace();
        }

        return properties;
    }

    public static boolean getStatus(String url) {
        // https://java2blog.com/how-to-ping-url-and-get-status-in-java/
        boolean result = false;
        try {
            URL urlObj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) urlObj.openConnection();
            con.setRequestMethod("GET");
            // Set connection timeout
            con.setConnectTimeout(3000);
            con.connect();

            int code = con.getResponseCode();
            if (code == 200) {
                result = true;
            }
        } catch (Exception e) {
            result = false;
        }
        return result;
    }

    public static String getMD5() {
        // https://howtodoinjava.com/java/io/sha-md5-file-checksum-hash/
        String checksum = null;
        try {
            File file = new File(Utils.getProperties().getProperty("pathToTempFiles")+"phpmyadmin.txt");
            MessageDigest md5Digest = null;
            md5Digest = MessageDigest.getInstance("MD5");
            checksum = getFileChecksum(md5Digest, file);
            file.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return checksum;
    }

    public static String getFileChecksum(MessageDigest digest, File file) throws IOException{
        // https://howtodoinjava.com/java/io/sha-md5-file-checksum-hash/
        FileInputStream fis = new FileInputStream(file);
        byte[] byteArray = new byte[1024];
        int bytesCount = 0;
        while ((bytesCount = fis.read(byteArray)) != -1) {
            digest.update(byteArray, 0, bytesCount);
        };
        fis.close();
        byte[] bytes = digest.digest();
        StringBuilder sb = new StringBuilder();
        for(int i=0; i< bytes.length ;i++)
        {
            sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
        }
        return sb.toString();
    }

    public static void getReadme(String url){
        InputStream in = null;
        try {
            in = new URL(url).openStream();
            Files.copy(in, Paths.get(Utils.getProperties().getProperty("pathToTempFiles")+"phpmyadmin.txt"), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}