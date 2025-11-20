<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <title>Login</title>
    <style>
        body { font-family:Arial; text-align:center; margin-top:100px; }
        .form { display:inline-block; padding:20px; border:1px solid #ddd; border-radius:5px; }
        input { padding:8px; margin:5px; width:200px; }
        button { padding:10px 20px; background:#007bff; color:white; border:none; border-radius:5px; cursor:pointer; }
        .error { color:red; }
    </style>
</head>
<body>
    <div class="form">
        <h2>Login</h2>
        
        <% if(request.getAttribute("error") != null) { %>
            <p class="error"><%=request.getAttribute("error")%></p>
        <% } %>
        
        <form action="LoginServlet" method="post">
            <div>
                <input type="text" name="username" placeholder="Username" required>
            </div>
            <div>
                <input type="password" name="password" placeholder="Password" required>
            </div>
            <div>
                <button type="submit">Login</button>
            </div>
        </form>
        
        <p>Test account: admin / admin123</p>
        <p><a href="index.jsp">Back to Home</a></p>
    </div>
</body>
</html>