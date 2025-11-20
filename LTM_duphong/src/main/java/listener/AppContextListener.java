package listener;

import javax.servlet.*;
import javax.servlet.annotation.WebListener;
import worker.BackgroundWorker;

@WebListener
public class AppContextListener implements ServletContextListener {
    
    private Thread workerThread;
    
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        // Start background worker thread
        BackgroundWorker worker = BackgroundWorker.getInstance();
        workerThread = new Thread(worker);
        workerThread.setDaemon(true);
        workerThread.start();
        
        System.out.println("Background Worker started successfully!");
    }
    
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        if (workerThread != null) {
            BackgroundWorker.getInstance().stop();
            workerThread.interrupt();
        }
        System.out.println("Background Worker stopped.");
    }
}