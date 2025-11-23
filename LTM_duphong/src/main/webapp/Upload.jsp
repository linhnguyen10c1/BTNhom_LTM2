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
    
    // Get tasks list from request attribute (set by UploadServlet or on page load)
    List<ImageTask> tasks = (List<ImageTask>) request.getAttribute("tasks");
    String successMessage = (String) request.getAttribute("success");
    String errorMessage = (String) request.getAttribute("error");
%>
<!DOCTYPE html>
<html>
<head>
    <title>Upload Image - Image Processor</title>
    <style>
        body { font-family:Arial; margin:20px; }
        .header { display:flex; justify-content:space-between; align-items:center; margin-bottom:30px; }
        .header h1 { margin:0; }
        .logout { background:#dc3545; padding:10px 20px; color:white; text-decoration:none; border-radius:5px; }
        
        /* Message styles */
        .message { text-align:center; margin:15px auto; padding:10px; border-radius:5px; max-width:600px; }
        .error-message { background:#f8d7da; color:#721c24; border:1px solid #f5c6cb; }
        .success-message { background:#d4edda; color:#155724; border:1px solid #c3e6cb; }
        
        /* Upload section */
        .upload-section { text-align:center; margin-bottom:30px; padding-bottom:30px; border-bottom:2px solid #e0e0e0; }
        .upload-form { display:flex; justify-content:center; align-items:center; gap:15px; margin-top:20px; }
        input[type="file"] { padding:10px; border:1px solid #ddd; border-radius:5px; }
        button { padding:10px 20px; background:#007bff; color:white; border:none; border-radius:5px; cursor:pointer; }
        button:disabled { background:#ccc; cursor:not-allowed; }
        
        /* Results section */
        .results-section { margin-top:30px; }
        .results-header { text-align:center; margin-bottom:20px; }
        .task { border:1px solid #ddd; margin:10px 0; padding:15px; border-radius:5px; }
        .status-pending { border-left:4px solid orange; }
        .status-processing { border-left:4px solid blue; }
        .status-done { border-left:4px solid green; }
        .status-error { border-left:4px solid red; }
        
        /* Image and ASCII comparison layout */
        .comparison-container {
            display: flex;
            gap: 20px;
            margin: 15px 0;
            align-items: flex-start;
        }
        
        .comparison-item {
            flex: 1;
            text-align: center;
        }
        
        .comparison-item h4 {
            margin: 0 0 10px 0;
            color: #333;
            font-size: 14px;
        }
        
        .original-image-container {
            background: #f9f9f9;
            padding: 10px;
            border-radius: 5px;
            border: 1px solid #ddd;
        }
        
        .original-image-container img {
            max-width: 300px;
            max-height: 300px;
            width: auto;
            height: auto;
            display: block;
            margin: 0 auto;
            border: 1px solid #ccc;
        }
        
        .ascii-result-container {
            background: #f5f5f5;
            padding: 10px;
            border-radius: 5px;
            border: 1px solid #ddd;
        }
        
        .ascii-result {
            background: #fff;
            padding: 10px;
            font-family: monospace;
            white-space: pre;
            overflow: auto;
            font-size: 5px;
            line-height: 5px;
            text-align: left;
            border: 1px solid #ccc;
        }
        
        .btn { padding:8px 16px; background:#007bff; color:white; text-decoration:none; border-radius:5px; margin:5px; display:inline-block; }
        .btn-success { background:#28a745; }
        .btn-danger { background:#dc3545; }
        .refresh-note { color:#666; font-style:italic; text-align:center; }
        .no-tasks { text-align:center; margin-top:50px; color:#666; }
        
        .task-info { margin-bottom: 15px; }
        .task-info p { margin: 5px 0; }
    </style>
    <script>
        // Auto refresh every 0.5 seconds if there are pending/processing tasks
        function checkAutoRefresh() {
            const pendingTasks = document.querySelectorAll('.status-pending, .status-processing');
            if (pendingTasks.length > 0) {
                setTimeout(function() {
                    window.location.href = 'UploadServlet';
                }, 500);
            }
        }
        window.onload = checkAutoRefresh;
    </script>
</head>
<body>
    <div class="header">
        <h1>Welcome <%=account.getUsername()%> - Image Processor</h1>
        <a href="LogoutServlet" class="logout">Logout</a>
    </div>
    
    <!-- Error/Success Messages -->
    <% if (errorMessage != null) { %>
        <div class="message error-message">
            <strong>× Error:</strong> <%=errorMessage%>
        </div>
    <% } %>
    
    <% if (successMessage != null) { %>
        <div class="message success-message">
            <strong>✓ Success:</strong> <%=successMessage%>
        </div>
    <% } %>
    
    <!-- Upload Section -->
    <div class="upload-section">
        <h2 style="margin-top:0; color:#333;">Upload & Convert Image to ASCII Art</h2>
        <p>Select an image file (JPG, PNG, GIF, BMP) to convert</p>
        <form action="UploadServlet" method="post" enctype="multipart/form-data">
            <div class="upload-form">
                <input type="file" name="imageFile" accept="image/*" required>
                <button type="submit">Convert Now!!!</button>
            </div>
        </form>
    </div>
    
    <!-- Processing Results Section -->
    <div class="results-section">
        <div class="results-header">
            <h2 style="margin-top:0; color:#333;">Your Processing Results</h2>
            <a href="UploadServlet" class="btn">🔄 Refresh</a>
        </div>
        
        <% if (tasks != null && !tasks.isEmpty()) { %>
            <p class="refresh-note">※ Page auto-refreshes every 0.5 seconds when tasks are processing</p>
            
            <%
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                for (ImageTask task : tasks) {
            %>
                <div class="task status-<%=task.getStatus()%>">
                    <div class="task-info">
                        <h3 style="margin:0 0 10px 0;"><%=task.getFilename()%></h3>
                        <p><strong>Status:</strong> <%=task.getStatus().toUpperCase()%></p>
                        <p><strong>Uploaded:</strong> <%=sdf.format(task.getCreatedAt())%></p>
                        
                        <% if (task.getCompletedAt() != null) { %>
                            <p><strong>Completed:</strong> <%=sdf.format(task.getCompletedAt())%></p>
                        <% } %>
                    </div>
                    
                    <% if ("error".equals(task.getStatus())) { %>
                        <p style="color:red;"><strong>Error:</strong> <%=task.getErrorMessage()%></p>
                    <% } else if ("processing".equals(task.getStatus())) { %>
                        <p style="color:blue;">🔄 Processing in progress...</p>
                    <% } else if ("pending".equals(task.getStatus())) { %>
                        <p style="color:orange;">⏳ Waiting in queue...</p>
                    <% } else if ("done".equals(task.getStatus())) { %>
                        <p style="color:green; margin-bottom:15px;">✓ Conversion completed!</p>
                        
                        <% if (task.getResultPath() != null) { %>
                            <a href="DownloadServlet?file=<%=task.getResultPath()%>" class="btn btn-success">📥 Download ASCII File</a>
                        <% } %>
                        
                        <!-- Image and ASCII Comparison -->
                        <div class="comparison-container">
                            <!-- Original Image -->
                            <div class="comparison-item">
                                <h4>🖼️ Original Image</h4>
                                <div class="original-image-container">
                                    <img src="ImageServlet?file=<%=task.getFilename()%>" alt="Original Image">
                                </div>
                            </div>
                            
                            <!-- ASCII Result -->
                            <div class="comparison-item">
                                <h4>🎨 ASCII Art Result</h4>
                                <div class="ascii-result-container">
                                    <% if (task.getAsciiResult() != null) { %>
                                        <div class="ascii-result"><%=task.getAsciiResult()%></div>
                                    <% } else { %>
                                        <p style="color:#999;">ASCII result not available</p>
                                    <% } %>
                                </div>
                            </div>
                        </div>
                    <% } %>
                </div>
            <%
                }
            %>
        <% } else { %>
            <div class="no-tasks">
                <p>📂 No processing history yet. Upload an image above to get started!</p>
            </div>
        <% } %>
    </div>
</body>
</html>