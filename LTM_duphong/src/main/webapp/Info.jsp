<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="model.bean.Account" %>
<%
    Account account = (Account) session.getAttribute("account");
    if (account == null) {
        response.sendRedirect("Login.jsp");
        return;
    }
%>
<!DOCTYPE html>
<html>
<head>
    <title>Account Info</title>
</head>
<body style="text-align:center; margin-top:100px; font-family:Arial;">
    <h1>Welcome <%=account.getUsername()%> to Image Processor</h1>
    <p>Convert your images to ASCII art</p>
    <!-- ✅ FIX: Changed from Login.jsp to UploadServlet -->
    <a href="UploadServlet" style="padding:10px 20px; background:#007bff; color:white; text-decoration:none; border-radius:5px;">Upload</a>
</body>
</html>