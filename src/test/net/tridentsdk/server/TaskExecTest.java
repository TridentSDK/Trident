package net.tridentsdk.server;

import net.tridentsdk.api.threads.TaskExecutor;
import net.tridentsdk.server.threads.ConcurrentTaskExecutor;

import java.util.Collection;

/*
Thread-1
Thread-3
Thread-0
Thread-2

Process finished with exit code 0
 */
public final class TaskExecTest {
    private TaskExecTest() {
    }

    public static void main(String... args) {
        ConcurrentTaskExecutor<String> concurrentTaskExecutor = new ConcurrentTaskExecutor<>(4);
        Collection<TaskExecutor> taskExecutors = concurrentTaskExecutor.threadList();
        for (TaskExecutor taskExecutor : taskExecutors) {
            final String name = taskExecutor.asThread().getName();
            TaskExecutor executor = concurrentTaskExecutor.getScaledThread();
            executor.addTask(new Runnable() {
                @Override
                public void run() {
                    System.out.println(name);
                }
            });
            concurrentTaskExecutor.assign(executor, name);
        }
        concurrentTaskExecutor.shutdown();
    }
}
