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
            // Load tasks and show error
            ImageTaskDAO taskDAO = new ImageTaskDAO();
            List<ImageTask> tasks = taskDAO.getTasksByUserId(account.getId());
            request.setAttribute("tasks", tasks);
            request.setAttribute("error", "Please select a file!");
            request.getRequestDispatcher("Upload.jsp").forward(request, response);
            return;
        }
        
        // Validate image file
        if (!isImageFile(fileName)) {
            // Load tasks and show error
            ImageTaskDAO taskDAO = new ImageTaskDAO();
            List<ImageTask> tasks = taskDAO.getTasksByUserId(account.getId());
            request.setAttribute("tasks", tasks);
            request.setAttribute("error", "Please select an image file!");
            request.getRequestDispatcher("Upload.jsp").forward(request, response);
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
                
                // Load tasks and show success message
                List<ImageTask> tasks = taskDAO.getTasksByUserId(account.getId());
                request.setAttribute("tasks", tasks);
                request.setAttribute("success", "File uploaded successfully! Processing in background...");
                request.getRequestDispatcher("Upload.jsp").forward(request, response);
            } else {
                // Load tasks and show error
                List<ImageTask> tasks = taskDAO.getTasksByUserId(account.getId());
                request.setAttribute("tasks", tasks);
                request.setAttribute("error", "Failed to create task!");
                request.getRequestDispatcher("Upload.jsp").forward(request, response);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            // Load tasks and show error
            ImageTaskDAO taskDAO = new ImageTaskDAO();
            List<ImageTask> tasks = taskDAO.getTasksByUserId(account.getId());
            request.setAttribute("tasks", tasks);
            request.setAttribute("error", "Upload failed: " + e.getMessage());
            request.getRequestDispatcher("Upload.jsp").forward(request, response);
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