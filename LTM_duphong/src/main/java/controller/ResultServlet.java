package controller;

import java.io.*;
import java.util.List;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import model.bean.Account;
import model.bean.ImageTask;
import model.dao.ImageTaskDAO;

@WebServlet("/ResultServlet")
public class ResultServlet extends HttpServlet {
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        Account account = (Account) request.getSession().getAttribute("account");
        if (account == null) {
            response.sendRedirect("Login.jsp");
            return;
        }
        
        ImageTaskDAO taskDAO = new ImageTaskDAO();
        List<ImageTask> tasks = taskDAO.getTasksByUserId(account.getId());
        
        request.setAttribute("tasks", tasks);
        
        String message = request.getParameter("message");
        if ("uploaded".equals(message)) {
            request.setAttribute("success", "File uploaded successfully! Processing in background...");
        }
        
        request.getRequestDispatcher("Result.jsp").forward(request, response);
    }
}