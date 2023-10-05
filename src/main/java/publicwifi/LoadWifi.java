package publicwifi;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;

public class LoadWifi implements PublicWifi {
	@Override
	public String getTableName() {
		return "information";
	}

    public String bindURL(int start, int end) throws UnsupportedEncodingException {
        StringBuilder sb = new StringBuilder("http://openapi.seoul.go.kr:8088/");
        sb.append(URLEncoder.encode(key, "UTF-8") + "/");
        sb.append(URLEncoder.encode("json", "UTF-8") + "/");
        sb.append(URLEncoder.encode("TbPublicWifiInfo", "UTF-8") + "/");
        sb.append(start + "/" + end + "/");

        return sb.toString();
    }

    public int getCount() throws UnsupportedEncodingException {
        // 받아올 데이터의 개수를 구하기 위해 더미 패킷을 보낸다.
        Packet packet = getPacket(1, 1);
        if (packet == null)
            return -1;

        return Integer.parseInt(packet.list_total_count);
    }

    public int getDBCount() throws SQLException {
        ResultSet rs = null;
        Connection connection = null;
        Statement statement = null;
        int count = -1;

        try {
            String checkQuery = "SELECT COUNT(*) FROM information;";

            connection = DriverManager.getConnection(dbFile);
            statement = connection.createStatement();

            rs = statement.executeQuery(checkQuery);

            if (rs.next()) {
                count = rs.getInt(1);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            if (statement != null) {
                statement.close();
            }
            if (connection != null) {
                connection.close();
            }
        }

        return count;
    }

    public Packet getPacket(int start, int end) throws UnsupportedEncodingException {
        if (end < start)
            return null;

        String tempURL = bindURL(start, end);

        StringBuilder tempJson = new StringBuilder();

        try {
            URL url = new URL(tempURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-type", "application/json");
            connection.setDoOutput(true);

            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
            while (br.ready()) {
                tempJson.append(br.readLine());
            }
            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Gson gson = new Gson();

        JsonElement element = JsonParser.parseString(tempJson.toString()).getAsJsonObject().get("TbPublicWifiInfo");
        Packet packet = gson.fromJson(element, Packet.class);

        return packet;
    }

    public void dropData() throws SQLException {
        Connection connection = null;
        Statement statement = null;

        try {
            Class.forName("org.sqlite.JDBC");

            connection = DriverManager.getConnection(dbFile);
            statement = connection.createStatement();

            String deleteDataSQL = "DELETE FROM information;";
            statement.executeUpdate(deleteDataSQL);

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

    public ArrayList<Row> loadData() throws UnsupportedEncodingException {
        ArrayList<Row> rowList = new ArrayList<>();

        int count = getCount();
        final int limit = 1000;
        int startIndex = 1;

        int page = (int) (Math.ceil((double) count / (double) limit));

        System.out.println(page);

        for (int i = 0; i < page; i++) {
            Packet packet = getPacket(startIndex, Math.min(startIndex + limit - 1, count));

            if (packet == null)
                return null;

            System.out.println(startIndex + "遺��꽣 " + Math.min(startIndex + limit, count) + "源뚯� " + packet.row.length);

            rowList.addAll(Arrays.asList(packet.row));

            startIndex += limit;
        }

        return rowList;
    }

    public void insertData(ArrayList<Row> rowList) throws SQLException {
        ResultSet rs = null;
        Connection connection = null;
        Statement statement = null;

        try {
            Class.forName("org.sqlite.JDBC");

            String checkQuery = "SELECT COUNT(*) FROM information;";

            connection = DriverManager.getConnection(dbFile);
            statement = connection.createStatement();

            rs = statement.executeQuery(checkQuery);

            int count = -1;
            if (rs.next()) {
                count = rs.getInt(1);
            }

            if (count == rowList.size())
                return;
            else if (count == -1)
                throw new SQLException();

            statement.close();

            String insertQuery = "INSERT INTO information (X_SWIFI_MGR_NO, X_SWIFI_WRDOFC, X_SWIFI_MAIN_NM, X_SWIFI_ADRES1, X_SWIFI_ADRES2, X_SWIFI_INSTL_FLOOR, X_SWIFI_INSTL_TY, X_SWIFI_INSTL_MBY, X_SWIFI_SVC_SE, X_SWIFI_CMCWR, X_SWIFI_CNSTC_YEAR, X_SWIFI_INOUT_DOOR, X_SWIFI_REMARS3, LAT, LNT, WORK_DTTM) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            PreparedStatement ps = connection.prepareStatement(insertQuery);

            int inserted = 0;
            for (int i = 0; i < rowList.size(); i++) {
                Row r = rowList.get(i);
                ps.setString(1, r.X_SWIFI_MGR_NO);
                ps.setString(2, r.X_SWIFI_WRDOFC);
                ps.setString(3, r.X_SWIFI_MAIN_NM);
                ps.setString(4, r.X_SWIFI_ADRES1);
                ps.setString(5, r.X_SWIFI_ADRES2);
                ps.setString(6, r.X_SWIFI_INSTL_FLOOR);
                ps.setString(7, r.X_SWIFI_INSTL_TY);
                ps.setString(8, r.X_SWIFI_INSTL_MBY);
                ps.setString(9, r.X_SWIFI_SVC_SE);
                ps.setString(10, r.X_SWIFI_CMCWR);
                ps.setString(11, r.X_SWIFI_CNSTC_YEAR);
                ps.setString(12, r.X_SWIFI_INOUT_DOOR);
                ps.setString(13, r.X_SWIFI_REMARS3);
                ps.setString(14, r.LNT);
                ps.setString(15, r.LAT);
                ps.setString(16, r.WORK_DTTM);
                ps.addBatch();

                if (i != 0 && (i % 1000 == 0)) {
                    inserted += ps.executeBatch().length;
                    ps.clearBatch();
                }
            }

            inserted += ps.executeBatch().length;

            if (inserted != rowList.size()) {
                System.out.println("�쟾�넚 �삤瑜�");
            }

            ps.close();

        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        } finally {
            if (statement != null) {
                statement.close();
            }
            if (connection != null) {
                connection.close();
            }
        }

    }

    @Override
    public String getQuaryBuilder() {
        StringBuilder createQuery = new StringBuilder();
        createQuery.append("CREATE TABLE " + getTableName() + " (");
        createQuery.append("X_SWIFI_MGR_NO STRING PRIMARY KEY,");
        createQuery.append("X_SWIFI_WRDOFC STRING,");
        createQuery.append("X_SWIFI_MAIN_NM STRING,");
        createQuery.append("X_SWIFI_ADRES1 STRING,");
        createQuery.append("X_SWIFI_ADRES2 STRING,");
        createQuery.append("X_SWIFI_INSTL_FLOOR STRING,");
        createQuery.append("X_SWIFI_INSTL_TY STRING,");
        createQuery.append("X_SWIFI_INSTL_MBY STRING,");
        createQuery.append("X_SWIFI_SVC_SE STRING,");
        createQuery.append("X_SWIFI_CMCWR STRING,");
        createQuery.append("X_SWIFI_CNSTC_YEAR STRING,");
        createQuery.append("X_SWIFI_INOUT_DOOR STRING,");
        createQuery.append("X_SWIFI_REMARS3 STRING,");
        createQuery.append("LAT STRING,");
        createQuery.append("LNT STRING,");
        createQuery.append("WORK_DTTM STRING");
        createQuery.append(");");

        return createQuery.toString();
    }
}
