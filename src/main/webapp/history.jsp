<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="java.sql.*" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="publicwifi.PublicWifi" %>
<%@ page import="publicwifi.Information" %>
<%@ page import="publicwifi.History" %>
<%@ page import="publicwifi.PublicWifi.HistoryInfo" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8" name="viewport" content="width=device-width, initial-scale=1.0">
<style>
#customers table {
	text-align: center;
}

.group {
	line-height: 36px;
}

.navbar {
	white-space: nowrap;
}

#customers {
  font-family: Arial, Helvetica, sans-serif;
  border-collapse: collapse;
  width: 100%;
}

#customers td, #customers th {
  border: 1px solid #ddd;
  padding: 8px;
}

#customers tr:nth-child(even){background-color: #f2f2f2;}

#customers tr:hover {background-color: #ddd;}

#customers th {
  padding-top: 12px;
  padding-bottom: 12px;
  text-align: left;
  background-color: #04AA6D;
  color: white;
  text-align: center;
}
</style>
<title>와이파이 정보 구하기</title>
</head>
<body>
	<div class="top">
		<h1>위치 히스토리 목록</h1>
	</div>
	<div class="group">
		<div class="navbar">
			<a id="linkHome" href="index.jsp">홈</a>
			<label> | </label>
			<a id="linkHistory" href="history.jsp">위치 히스토리 기록</a>
			<label> | </label>
			<a id="linkOpenAPI" href="load-wifi.jsp">Open API 와이파이 정보 가져오기</a>
			<br>
		</div>
	</div>
	
	<%
		History history = new History();
		ArrayList<HistoryInfo> historyList = new ArrayList<>();
		
		String del = request.getParameter("del");

		history.connect();
		history.createTable();
		if (del != null) {
			history.deleteHistoryInfo(del);
		}

		String ip = history.getClientIP(request);
		historyList = history.selectData(ip);

	%>
	
	<%= history.getTable(historyList) %>
	
	<script src="./history.js"></script>
</body>
</html>