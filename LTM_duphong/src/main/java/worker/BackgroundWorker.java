package worker;

import java.io.File;
import model.bean.ImageTask;
import model.dao.ImageTaskDAO;
import util.ImageProcessor;

public class BackgroundWorker implements Runnable {
    
    private static BackgroundWorker instance;
    private ImageTaskDAO taskDAO;
    private boolean running = true;
    private String uploadPath;
    private String resultPath;
    
    private BackgroundWorker() {
        this.taskDAO = new ImageTaskDAO();
        this.uploadPath = System.getProperty("java.io.tmpdir") + File.separator + "uploads" + File.separator;
        this.resultPath = System.getProperty("java.io.tmpdir") + File.separator + "results" + File.separator;
        
        // Tạo thư mục nếu chưa có
        new File(uploadPath).mkdirs();
        new File(resultPath).mkdirs();
    }
    
    public static synchronized BackgroundWorker getInstance() {
        if (instance == null) {
            instance = new BackgroundWorker();
        }
        return instance;
    }
    
    @Override
    public void run() {
        System.out.println("BackgroundWorker started...");
        
        while (running) {
            try {
            	int taskId = TaskQueue.getInstance().take();
//                ImageTask task = taskDAO.getPendingTask();
            	ImageTask task = taskDAO.getTaskById(taskId);
                
                if (task != null) {
                    processTask(task);
                } else {
                    Thread.sleep(5000); // Wait 5 seconds
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        
        System.out.println("BackgroundWorker stopped.");
    }
    
    private void processTask(ImageTask task) {
        try {
            System.out.println("Processing task: " + task.getFilename());
            
            // Update status to processing
            taskDAO.updateTaskResult(task.getId(), "processing", null, null, null);
            
            // Convert image to ASCII
            String imagePath = uploadPath + task.getFilename();
            String ascii = ImageProcessor.convertToAscii(imagePath);
            
            // Save ASCII result to file
            String asciiFileName = "ascii_" + task.getId() + ".txt";
            String asciiFilePath = resultPath + asciiFileName;
            ImageProcessor.saveAsciiToFile(ascii, asciiFilePath);
            
            // Update task with result
            taskDAO.updateTaskResult(task.getId(), "done", ascii, asciiFileName, null);
            
            System.out.println("Task completed: " + task.getFilename());
            
        } catch (Exception e) {
            e.printStackTrace();
            taskDAO.updateTaskResult(task.getId(), "error", null, null, e.getMessage());
        }
    }
    
    public void stop() {
        running = false;
    }
    
    public String getUploadPath() { return uploadPath; }
    public String getResultPath() { return resultPath; }
}