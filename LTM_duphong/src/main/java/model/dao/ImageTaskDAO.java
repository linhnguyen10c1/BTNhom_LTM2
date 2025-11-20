package model.dao;

import java.sql.*;
import java.util.*;
import model.bean.ImageTask;
import config.DatabaseConnection;

public class ImageTaskDAO {
    
    public int createTask(ImageTask task) {
        String sql = "INSERT INTO image_tasks (user_id, filename, original_path, status, created_at) VALUES (?, ?, ?, 'pending', NOW())";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setInt(1, task.getUserId());
            ps.setString(2, task.getFilename());
            ps.setString(3, task.getOriginalPath());
            
            int result = ps.executeUpdate();
            if (result > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
    
    public void updateTaskResult(int taskId, String status, String asciiResult, String resultPath, String errorMessage) {
        String sql = "UPDATE image_tasks SET status=?, ascii_result=?, result_path=?, error_message=?, completed_at=NOW() WHERE id=?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, status);
            ps.setString(2, asciiResult);
            ps.setString(3, resultPath);
            ps.setString(4, errorMessage);
            ps.setInt(5, taskId);
            
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public List<ImageTask> getTasksByUserId(int userId) {
        List<ImageTask> tasks = new ArrayList<>();
        String sql = "SELECT * FROM image_tasks WHERE user_id=? ORDER BY created_at DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                ImageTask task = new ImageTask();
                task.setId(rs.getInt("id"));
                task.setUserId(rs.getInt("user_id"));
                task.setFilename(rs.getString("filename"));
                task.setOriginalPath(rs.getString("original_path"));
                task.setStatus(rs.getString("status"));
                task.setAsciiResult(rs.getString("ascii_result"));
                task.setResultPath(rs.getString("result_path"));
                task.setCreatedAt(rs.getTimestamp("created_at"));
                task.setCompletedAt(rs.getTimestamp("completed_at"));
                task.setErrorMessage(rs.getString("error_message"));
                tasks.add(task);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tasks;
    }
    
    public ImageTask getPendingTask() {
        String sql = "SELECT * FROM image_tasks WHERE status='pending' ORDER BY created_at ASC LIMIT 1";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                ImageTask task = new ImageTask();
                task.setId(rs.getInt("id"));
                task.setUserId(rs.getInt("user_id"));
                task.setFilename(rs.getString("filename"));
                task.setOriginalPath(rs.getString("original_path"));
                task.setStatus(rs.getString("status"));
                task.setCreatedAt(rs.getTimestamp("created_at"));
                return task;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    public ImageTask getTaskById(int id) {
        ImageTask task = null;
        String sql = "SELECT * FROM image_tasks WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                task = new ImageTask();
                task.setId(rs.getInt("id"));
                task.setUserId(rs.getInt("user_id"));
                task.setFilename(rs.getString("filename"));
                task.setOriginalPath(rs.getString("original_path"));
                task.setStatus(rs.getString("status"));
                task.setCreatedAt(rs.getTimestamp("created_at"));
               
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return task;
    }

}