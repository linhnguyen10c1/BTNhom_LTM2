package controller;

import java.io.*;
import java.util.List;
import javax.servlet.*;
import javax.servlet.annotation.*;
import javax.servlet.http.*;
import model.bean.Account;
import model.bean.ImageTask;
import model.dao.ImageTaskDAO;
import worker.BackgroundWorker;
import worker.TaskQueue;

@WebServlet("/UploadServlet")
@MultipartConfig(maxFileSize = 10 * 1024 * 1024) // 10MB
public class UploadServlet extends HttpServlet {
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Handle GET request - just show Upload.jsp with tasks list
        Account account = (Account) request.getSession().getAttribute("account");
        if (account == null) {
            response.sendRedirect("Login.jsp");
            return;
        }
        
        // Load tasks for display
        ImageTaskDAO taskDAO = new ImageTaskDAO();
        List<ImageTask> tasks = taskDAO.getTasksByUserId(account.getId());
        request.setAttribute("tasks", tasks);
        
        // Check for success/error messages from redirect
        String message = request.getParameter("message");
        if ("success".equals(message)) {
            request.setAttribute("success", "File uploaded successfully! Processing in background...");
        } else if ("error".equals(message)) {
            String errorDetail = request.getParameter("detail");
            request.setAttribute("error", errorDetail != null ? errorDetail : "Upload failed!");
        }
        
        request.getRequestDispatcher("Upload.jsp").forward(request, response);
    }
    
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        Account account = (Account) request.getSession().getAttribute("account");
        if (account == null) {
            response.sendRedirect("Login.jsp");
            return;
        }
        
        Part filePart = request.getPart("imageFile");
        String fileName = getFileName(filePart);
        
        if (fileName == null || fileName.isEmpty()) {
            response.sendRedirect("UploadServlet?message=error&detail=" + 
                java.net.URLEncoder.encode("Please select a file!", "UTF-8"));
            return;
        }
        
        // Validate image file
        if (!isImageFile(fileName)) {
            response.sendRedirect("UploadServlet?message=error&detail=" + 
                java.net.URLEncoder.encode("Please select an image file!", "UTF-8"));
            return;
        }
        
        try {
            // lưu file vào disk trước 
            String uploadPath = BackgroundWorker.getInstance().getUploadPath();
            String uniqueFileName = System.currentTimeMillis() + "_" + fileName;
            String filePath = uploadPath + uniqueFileName;
            
            // file đã tồn tại trên disk 
            filePart.write(filePath);
            
            // Create task in database
            ImageTask task = new ImageTask();
            task.setUserId(account.getId());
            task.setFilename(uniqueFileName);
            task.setOriginalPath(filePath);
            
            ImageTaskDAO taskDAO = new ImageTaskDAO();
            int taskId = taskDAO.createTask(task);
            
            if (taskId > 0) {
                TaskQueue.getInstance().enqueue(taskId);
                
                // ✅ FIX: Redirect instead of forward (PRG Pattern)
                response.sendRedirect("UploadServlet?message=success");
            } else {
                response.sendRedirect("UploadServlet?message=error&detail=" + 
                    java.net.URLEncoder.encode("Failed to create task!", "UTF-8"));
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("UploadServlet?message=error&detail=" + 
                java.net.URLEncoder.encode("Upload failed: " + e.getMessage(), "UTF-8"));
        }
    }
    
    private String getFileName(Part part) {
        String contentDisposition = part.getHeader("content-disposition");
        for (String content : contentDisposition.split(";")) {
            if (content.trim().startsWith("filename")) {
                return content.substring(content.indexOf('=') + 1).trim().replace("\"", "");
            }
        }
        return null;
    }
    
    private boolean isImageFile(String fileName) {
        String extension = fileName.toLowerCase();
        return extension.endsWith(".jpg") || extension.endsWith(".jpeg") || 
               extension.endsWith(".png") || extension.endsWith(".gif") || 
               extension.endsWith(".bmp");
    }
}