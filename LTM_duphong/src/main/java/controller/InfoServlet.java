package controller;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.bean.Account;

@WebServlet("/InfoServlet")
public class InfoServlet extends HttpServlet {
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        Account account = (Account) request.getSession().getAttribute("account");
        
        if (account == null) {
            response.sendRedirect("Login.jsp");
        } else {
            request.getRequestDispatcher("Info.jsp").forward(request, response);
        }
    }
}