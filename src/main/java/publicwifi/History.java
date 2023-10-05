package publicwifi;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import publicwifi.PublicWifi.Row;

public class History implements PublicWifi {
	@Override
	public String getTableName() {
		return "history";
	}

	public void insertData(String ip, String lat, String lnt) throws SQLException {
		ResultSet rs = null;
		Connection connection = null;
		Statement statement = null;

		try {
			Class.forName("org.sqlite.JDBC");

			connection = DriverManager.getConnection(dbFile);
			LocalDateTime now = LocalDateTime.now();
			String formatedNow = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));

			statement = connection.createStatement();
			
			String insertQuery = "INSERT INTO " + getTableName() + " (ip, x, y, date) " + "VALUES (\"" + ip + "\", \"" + lat + "\", \"" + lnt + "\", \"" + formatedNow + "\");";
			
			System.out.println(insertQuery);

			statement.executeUpdate(insertQuery);

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
	
	public ArrayList<HistoryInfo> selectData(String ip) throws SQLException {
		ArrayList<HistoryInfo> historyList = new ArrayList<>();
		ResultSet rs = null;
		Connection connection = null;
		Statement statement = null;

		try {
			Class.forName("org.sqlite.JDBC");

			connection = DriverManager.getConnection(dbFile);

			statement = connection.createStatement();
			
			String selectQuery = "SELECT * FROM " + getTableName() + " WHERE ip = \"" + ip + "\" ORDER BY date DESC LIMIT 20;";
			
			System.out.println(selectQuery);

			rs = statement.executeQuery(selectQuery);

			while (rs.next()) {
				HistoryInfo info = new HistoryInfo();

				info.id = rs.getInt("id");
				info.ip = rs.getString("ip");
				info.x = rs.getString("x");
				info.y = rs.getString("y");
				info.date = rs.getString("date");
				
				historyList.add(info);
			}

		} catch (SQLException | ClassNotFoundException e) {
			throw new RuntimeException(e);
		} finally {
			if (statement != null) {
				statement.close();
			}
			if (connection != null) {
				connection.close();
			}
			if (rs != null) {
				rs.close();
			}
		}
		
		return historyList;
	}
	
	public void deleteHistoryInfo(String id) throws SQLException {
		Connection connection = null;
		Statement statement = null;

		try {
			Class.forName("org.sqlite.JDBC");

			connection = DriverManager.getConnection(dbFile);

			statement = connection.createStatement();
			
			String deleteQuery = "DELETE FROM " + getTableName() + " WHERE id = " + Integer.parseInt(id) + ";";

			System.out.println(deleteQuery);

			statement.executeUpdate(deleteQuery);

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
		createQuery.append("id INTEGER PRIMARY KEY AUTOINCREMENT,");
		createQuery.append("ip STRING,");
		createQuery.append("x STRING,");
		createQuery.append("y STRING,");
		createQuery.append("date STRING");
		createQuery.append(");");

		return createQuery.toString();
	}

    public String getTable(ArrayList<HistoryInfo> historyList) {
        StringBuilder sb = new StringBuilder();

        sb.append("\t<table id=\"customers\">\n");
        sb.append("\t\t<th>ID</th>\n");
        sb.append("\t\t<th>X좌표</th>\n");
        sb.append("\t\t<th>Y좌표</th>\n");
        sb.append("\t\t<th>조회일자</th>\n");
        sb.append("\t\t<th>비고</th>\n");
        if (historyList == null || historyList.isEmpty()) {
            sb.append("\t\t<tr>\n");
            sb.append("\t\t\t<td align=\"center\" height=50px colspan=\"5\">데이터가 없습니다.</td>\n");
            sb.append("\t\t</tr>\n");
            sb.append("\t</table>\n");
            return sb.toString();
        }

        for (int i = 0; i < historyList.size(); i++) {
        	HistoryInfo info = historyList.get(i);
            sb.append("\t\t<tr>\n");
            sb.append("\t\t\t<td>" + (i + 1) + "</td>\n");
            sb.append("\t\t\t<td>" + info.x + "</td>\n");
            sb.append("\t\t\t<td>" + info.y + "</td>\n");
            sb.append("\t\t\t<td>" + info.date + "</td>\n");
            sb.append("\t\t\t<td>\n");
            sb.append("\t\t\t\t<center>\n");
            sb.append("\t\t\t\t\t<input type=\"button\" class=\"delete\" del-id=\"" + info.id + "\"value=\"삭제\">");
            sb.append("\t\t\t\t</center>\n");
            sb.append("\t\t\t</td>\n");
            sb.append("\t\t</tr>\n");
        }

        sb.append("\t</table>\n");

        return sb.toString();
    }
}
