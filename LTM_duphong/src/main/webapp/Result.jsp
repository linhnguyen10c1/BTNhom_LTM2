<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="model.bean.Account" %>
<%@ page import="model.bean.ImageTask" %>
<%@ page import="java.util.List" %>
<%@ page import="java.text.SimpleDateFormat" %>
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
    <title>Processing Results</title>
    <style>
        body { font-family:Arial; margin:20px; }
        .header { text-align:center; margin-bottom:30px; }
        .task { border:1px solid #ddd; margin:10px 0; padding:15px; border-radius:5px; }
        .status-pending { border-left:4px solid orange; }
        .status-processing { border-left:4px solid blue; }
        .status-done { border-left:4px solid green; }
        .status-error { border-left:4px solid red; }
        .ascii-result { background:#f5f5f5; padding:10px; font-family:monospace; white-space:pre-wrap; max-height:200px; overflow:auto; margin:10px 0; }
        .btn { padding:8px 16px; background:#007bff; color:white; text-decoration:none; border-radius:5px; margin:5px; display:inline-block; }
        .btn-success { background:#28a745; }
        .btn-danger { background:#dc3545; }
        .refresh-note { color:#666; font-style:italic; }
    </style>
    <script>
        // Auto refresh every 10 seconds if there are pending/processing tasks
        function checkAutoRefresh() {
            const pendingTasks = document.querySelectorAll('.status-pending, .status-processing');
            if (pendingTasks.length > 0) {
                setTimeout(function() {
                    location.reload();
                }, 10000);
            }
        }
        window.onload = checkAutoRefresh;
    </script>
</head>
<body>
    <div class="header">
        <h1>Processing Results for <%=account.getUsername()%></h1>
        <a href="Upload.jsp" class="btn">Upload New Image</a>
        <a href="javascript:location.reload()" class="btn">Refresh</a>
        <a href="LogoutServlet" class="btn btn-danger">Logout</a>
    </div>
    
    <% if (request.getAttribute("success") != null) { %>
        <div style="color:green; text-align:center; margin:10px;">
            <%=request.getAttribute("success")%>
        </div>
    <% } %>
    
    <p class="refresh-note">※ Page auto-refreshes every 10 seconds when tasks are processing</p>
    
    <%
        List<ImageTask> tasks = (List<ImageTask>) request.getAttribute("tasks");
        if (tasks != null && !tasks.isEmpty()) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            for (ImageTask task : tasks) {
    %>
        <div class="task status-<%=task.getStatus()%>">
            <h3><%=task.getFilename()%></h3>
            <p><strong>Status:</strong> <%=task.getStatus().toUpperCase()%></p>
            <p><strong>Uploaded:</strong> <%=sdf.format(task.getCreatedAt())%></p>
            
            <% if (task.getCompletedAt() != null) { %>
                <p><strong>Completed:</strong> <%=sdf.format(task.getCompletedAt())%></p>
            <% } %>
            
            <% if ("error".equals(task.getStatus())) { %>
                <p style="color:red;"><strong>Error:</strong> <%=task.getErrorMessage()%></p>
            <% } else if ("processing".equals(task.getStatus())) { %>
                <p style="color:blue;">🔄 Processing in progress...</p>
            <% } else if ("pending".equals(task.getStatus())) { %>
                <p style="color:orange;">⏳ Waiting in queue...</p>
            <% } else if ("done".equals(task.getStatus())) { %>
                <p style="color:green;">✅ Conversion completed!</p>
                
                <% if (task.getResultPath() != null) { %>
                    <a href="DownloadServlet?file=<%=task.getResultPath()%>" class="btn btn-success">Download ASCII File</a>
                <% } %>
                
                <% if (task.getAsciiResult() != null) { %>
                    <h4>ASCII Art Preview:</h4>
                    <div class="ascii-result"><%=task.getAsciiResult()%></div>
                <% } %>
            <% } %>
        </div>
    <%
            }
        } else {
    %>
        <div style="text-align:center; margin-top:50px;">
            <p>No tasks found. <a href="Upload.jsp">Upload an image</a> to get started!</p>
        </div>
    <% } %>
</body>
</html>