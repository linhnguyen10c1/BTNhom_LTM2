package worker;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class TaskQueue {

    private static TaskQueue instance = new TaskQueue();

    private BlockingQueue<Integer> queue = new LinkedBlockingQueue<>();

    private TaskQueue() {}

    public static TaskQueue getInstance() {
        return instance;
    }

    public void enqueue(Integer taskId) {
        queue.add(taskId);
    }

    public Integer take() throws InterruptedException {
        return queue.take(); // blocking nếu rỗng
    }
}
