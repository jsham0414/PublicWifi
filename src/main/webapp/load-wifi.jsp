<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="java.sql.*" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="publicwifi.PublicWifi" %>
<%@ page import="publicwifi.LoadWifi" %>
<%@ page import="publicwifi.PublicWifi.Row" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8" name="viewport" content="width=device-width, initial-scale=1.0">
<style>
body {
	text-align: center;
}

</style>
<title>와이파이 정보 구하기</title>
</head>
<body>
	<%
		ArrayList<Row> rowList = null;
		LoadWifi loadWifi = new LoadWifi();	
        
		loadWifi.connect();
		loadWifi.createTable();

		loadWifi.dropData();
		loadWifi.createTable();
		
		rowList = loadWifi.loadData();
		loadWifi.insertData(rowList);

		String msg = "";
		int apiCount = loadWifi.getCount();
		int dbCount = loadWifi.getDBCount();
		if (apiCount != dbCount) {
			msg = "문제가 발생했습니다. 다시 시도해주세요.";
		} else {
			msg = dbCount + "개의 WIFI 정보를 정상적으로 저장했습니다.";
		}
	%>
	
	<div class="top">
		<h1><%=msg%></h1>
	</div>
	<a href="index.jsp">홈으로 가기</a>
	
</body>
</html>