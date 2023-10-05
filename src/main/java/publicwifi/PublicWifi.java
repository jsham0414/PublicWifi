package publicwifi;

import java.io.*;
import java.sql.*;

import javax.servlet.http.HttpServletRequest;

// https://www.w3schools.com/css/tryit.asp?filename=trycss_table_fancy
public interface PublicWifi {
    final static String key = "54526250576a7368313135506241634a";
    final static String dbName = "publicwifi";
    final static String fileName = dbName + ".db";
    final static String filePath = System.getenv("APPDATA") + "\\PublicWifi\\";
    final static String dbFile = "jdbc:sqlite:" + filePath + fileName;

    public class Packet {
        String list_total_count;
        Result RESULT;
        Row[] row;
    }

    public class Result {
        String CODE;
        String MESSAGE;
    }

    public class Row {
        String X_SWIFI_MGR_NO;
        String X_SWIFI_WRDOFC;
        String X_SWIFI_MAIN_NM;
        String X_SWIFI_ADRES1;
        String X_SWIFI_ADRES2;
        String X_SWIFI_INSTL_FLOOR;
        String X_SWIFI_INSTL_TY;
        String X_SWIFI_INSTL_MBY;
        String X_SWIFI_SVC_SE;
        String X_SWIFI_CMCWR;
        String X_SWIFI_CNSTC_YEAR;
        String X_SWIFI_INOUT_DOOR;
        String X_SWIFI_REMARS3;
        String LAT;
        String LNT;
        String WORK_DTTM;
        String DISTANCE;


        public String toString() {
            return "DISTANCE : " + DISTANCE +
                    " X_SWIFI_MGR_NO : " + X_SWIFI_MGR_NO +
                    " X_SWIFI_WRDOFC : " + X_SWIFI_WRDOFC +
                    " X_SWIFI_MAIN_NM : " + X_SWIFI_MAIN_NM +
                    " X_SWIFI_ADRES1 : " + X_SWIFI_ADRES1 +
                    " X_SWIFI_ADRES2 : " + X_SWIFI_ADRES2 +
                    " X_SWIFI_INSTL_FLOOR : " + X_SWIFI_INSTL_FLOOR +
                    " X_SWIFI_INSTL_TY : " + X_SWIFI_INSTL_TY +
                    " X_SWIFI_INSTL_MBY : " + X_SWIFI_INSTL_MBY +
                    " X_SWIFI_SVC_SE : " + X_SWIFI_SVC_SE +
                    " X_SWIFI_CMCWR : " + X_SWIFI_CMCWR +
                    " X_SWIFI_CNSTC_YEAR : " + X_SWIFI_CNSTC_YEAR +
                    " X_SWIFI_INOUT_DOOR : " + X_SWIFI_INOUT_DOOR +
                    " X_SWIFI_REMARS3 : " + X_SWIFI_REMARS3 +
                    " LAT : " + LAT +
                    " LNT : " + LNT +
                    " WORK_DTTM : " + WORK_DTTM;
        }
    }

    public class HistoryInfo {
        int id;
        String ip;
        String x;
        String y;
        String date;
    }

    default void connect() throws SQLException {
        ResultSet rs = null;
        Connection connection = null;
        Statement statement = null;

        try {
            Class.forName("org.sqlite.JDBC");

            File dir = new File(filePath);

            if (!dir.exists()) {
                dir.mkdir();
            }

            connection = DriverManager.getConnection(dbFile);

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
    }

    // 테이블이 있는지 확인
    default void createTable() throws SQLException {
        Connection connection = null;
        Statement statement = null;

        try {
            Class.forName("org.sqlite.JDBC");
            System.out.println(getTableName());

            connection = DriverManager.getConnection(dbFile);
            statement = connection.createStatement();

            if (tableExists(statement, getTableName())) {
                return;
            }
            

            String createQuery = getQuaryBuilder();
            statement.executeUpdate(createQuery.toString());

        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        } finally {
            if (connection != null) {
                connection.close();
            }
            if (statement != null) {
                statement.close();
            }
        }
    }
    

    default boolean tableExists(Statement statement, String tableName) throws SQLException {
        String query = "SELECT name FROM sqlite_master WHERE type='table' AND name='" + tableName + "';";
        ResultSet resultSet = statement.executeQuery(query);

        return resultSet.next();
    }
    
    default String getClientIP(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");

        if (ip == null) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null) {
            ip = request.getRemoteAddr();
        }

        return ip;
    }

    String getTableName();
    String getQuaryBuilder();
}

