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
    <title>Upload Image</title>
    <style>
        body { font-family:Arial; margin:20px; }
        .header { display:flex; justify-content:space-between; align-items:center; margin-bottom:30px; }
        .header h1 { margin:0; }
        .logout { background:#dc3545; padding:10px 20px; color:white; text-decoration:none; border-radius:5px; }
        
        .upload-section { text-align:center; margin-bottom:30px; }
        .upload-form { display:flex; justify-content:center; align-items:center; gap:15px; margin-top:20px; }
        input[type="file"] { padding:10px; border:1px solid #ddd; border-radius:5px; }
        button { padding:10px 20px; background:#007bff; color:white; border:none; border-radius:5px; cursor:pointer; }
        button:disabled { background:#ccc; cursor:not-allowed; }
        
    </style>
</head>
<body>
    <div class="header">
        <h1>Welcome <%=account.getUsername()%> to Image Processor</h1>
        <a href="LogoutServlet" class="logout-btn">Logout</a>
    </div>
     <div class ="upload-section">
      <p>Convert your images to ASCII art</p>
        <form action="UploadServlet" method="post" enctype="multipart/form-data">
            <div class="upload-form">
                <input type="file" name="imageFile" accept="image/*" required>
                <button type="submit">Convert Now!!!</button>
            </div>
        </form>
     </div>
        
    <div class="results-section">
        <h2 style="margin-top:0; color:#333;">Your Processing Results</h2>
        <div id="results">
            <!-- Results will be loaded here via AJAX -->
        </div>
    </div>
</body>
</html>
