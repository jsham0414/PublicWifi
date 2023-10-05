package publicwifi;

import java.sql.*;
import java.util.ArrayList;

public class Information implements PublicWifi {
    @Override
	public String getTableName() {
		return "information";
	}

    public ArrayList<Row> selectData(String lat, String lnt) throws SQLException {
        if (Double.parseDouble(lat) == 0.0 && Double.parseDouble(lnt) == 0.0) {
            System.out.println("��ġ ������ ������ �� ��ȸ���ּ���.");
            return null;
        }

        Connection connection = null;
        Statement statement = null;
        ResultSet rs = null;

        ArrayList<Row> arrayList = new ArrayList<>();

        try {
            Class.forName("org.sqlite.JDBC");

            String selectQuery = String.format("SELECT 6371 * acos(cos(radians(%s)) * cos(radians(LAT)) * cos(radians(LNT) - radians(%s)) + sin(radians(%s)) * sin(radians(LAT))) AS distance, * FROM " + getTableName() + " ORDER BY distance LIMIT 20", lat, lnt, lat);

            connection = DriverManager.getConnection(dbFile);
            statement = connection.createStatement();
            rs = statement.executeQuery(selectQuery);
            while (rs.next()) {
                Row r = new Row();
                r.X_SWIFI_MGR_NO = rs.getString("X_SWIFI_MGR_NO");
                r.X_SWIFI_WRDOFC = rs.getString("X_SWIFI_WRDOFC");
                r.X_SWIFI_MAIN_NM = rs.getString("X_SWIFI_MAIN_NM");
                r.X_SWIFI_ADRES1 = rs.getString("X_SWIFI_ADRES1");
                r.X_SWIFI_ADRES2 = rs.getString("X_SWIFI_ADRES2");
                r.X_SWIFI_INSTL_FLOOR = rs.getString("X_SWIFI_INSTL_FLOOR");
                r.X_SWIFI_INSTL_TY = rs.getString("X_SWIFI_INSTL_TY");
                r.X_SWIFI_INSTL_MBY = rs.getString("X_SWIFI_INSTL_MBY");
                r.X_SWIFI_SVC_SE = rs.getString("X_SWIFI_SVC_SE");
                r.X_SWIFI_CMCWR = rs.getString("X_SWIFI_CMCWR");
                r.X_SWIFI_CNSTC_YEAR = rs.getString("X_SWIFI_CNSTC_YEAR");
                r.X_SWIFI_INOUT_DOOR = rs.getString("X_SWIFI_INOUT_DOOR");
                r.X_SWIFI_REMARS3 = rs.getString("X_SWIFI_REMARS3");
                r.LAT = rs.getString("LAT");
                r.LNT = rs.getString("LNT");
                r.WORK_DTTM = rs.getString("WORK_DTTM");
                r.DISTANCE = rs.getString("distance");
                
                arrayList.add(r);
            }

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

        return arrayList;
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


    public String getTable(ArrayList<Row> rowList) {
        StringBuilder sb = new StringBuilder();

        sb.append("\t<table id=\"customers\">\n");
        sb.append("\t\t<th>�Ÿ�(Km)</th>\n");
        sb.append("\t\t<th>������ȣ</th>\n");
        sb.append("\t\t<th>��ġ��</th>\n");
        sb.append("\t\t<th>�������̸�</th>\n");
        sb.append("\t\t<th>���θ��ּ�</th>\n");
        sb.append("\t\t<th>���ּ�</th>\n");
        sb.append("\t\t<th>��ġ��ġ(��)</th>\n");
        sb.append("\t\t<th>��ġ����</th>\n");
        sb.append("\t\t<th>��ġ���</th>\n");
        sb.append("\t\t<th>���񽺱���</th>\n");
        sb.append("\t\t<th>������</th>\n");
        sb.append("\t\t<th>��ġ�⵵</th>\n");
        sb.append("\t\t<th>�ǳ��ܱ���</th>\n");
        sb.append("\t\t<th>WIFI����ȯ��</th>\n");
        sb.append("\t\t<th>X��ǥ</th>\n");
        sb.append("\t\t<th>Y��ǥ</th>\n");
        sb.append("\t\t<th>�۾�����</th>\n");
        if (rowList == null || rowList.isEmpty()) {
            sb.append("\t\t<tr>\n");
            sb.append("\t\t\t<td align=\"center\" height=50px colspan=\"17\">��ġ ������ �Է��� �Ŀ� ��ȸ�� �ּ���.</td>\n");
            sb.append("\t\t</tr>\n");
            sb.append("\t</table>\n");
            return sb.toString();
        }

        for (Row r : rowList) {
            sb.append("\t\t<tr>\n");
            sb.append("\t\t\t<td>" + r.DISTANCE + "</td>\n");
            sb.append("\t\t\t<td>" + r.X_SWIFI_MGR_NO + "</td>\n");
            sb.append("\t\t\t<td>" + r.X_SWIFI_WRDOFC + "</td>\n");
            sb.append("\t\t\t<td>" + r.X_SWIFI_MAIN_NM + "</td>\n");
            sb.append("\t\t\t<td>" + r.X_SWIFI_ADRES1 + "</td>\n");
            sb.append("\t\t\t<td>" + r.X_SWIFI_ADRES2 + "</td>\n");
            sb.append("\t\t\t<td>" + r.X_SWIFI_INSTL_FLOOR + "</td>\n");
            sb.append("\t\t\t<td>" + r.X_SWIFI_INSTL_TY + "</td>\n");
            sb.append("\t\t\t<td>" + r.X_SWIFI_INSTL_MBY + "</td>\n");
            sb.append("\t\t\t<td>" + r.X_SWIFI_SVC_SE + "</td>\n");
            sb.append("\t\t\t<td>" + r.X_SWIFI_CMCWR + "</td>\n");
            sb.append("\t\t\t<td>" + r.X_SWIFI_CNSTC_YEAR + "</td>\n");
            sb.append("\t\t\t<td>" + r.X_SWIFI_INOUT_DOOR + "</td>\n");
            sb.append("\t\t\t<td>" + r.X_SWIFI_REMARS3 + "</td>\n");
            sb.append("\t\t\t<td>" + r.LAT + "</td>\n");
            sb.append("\t\t\t<td>" + r.LNT + "</td>\n");
            sb.append("\t\t\t<td>" + r.WORK_DTTM + "</td>\n");
            sb.append("\t\t</tr>\n");
        }

        sb.append("\t</table>\n");

        return sb.toString();
    }
}
