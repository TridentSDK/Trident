package net.tridentsdk.server;

import net.tridentsdk.api.threads.TaskExecutor;
import net.tridentsdk.server.threads.ConcurrentTaskExecutor;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

/*
Thread-1
Thread-3
Thread-0
Thread-2

Process finished with exit code 0
 */
@State(Scope.Benchmark)
public class TaskExecTest {
    public static void main3(String[] args) {
        ConcurrentTaskExecutor<String> concurrentTaskExecutor = new ConcurrentTaskExecutor<>(4);
        TaskExecutor executor = concurrentTaskExecutor.getScaledThread();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
            }
        };
        while (true) {
            executor.addTask(runnable);
        }
    }

    public static void main0(String... args) {
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

    private static final ConcurrentTaskExecutor<String> TASK_EXECUTOR = new ConcurrentTaskExecutor<>(4);
    private static final TaskExecutor EXECUTOR = TASK_EXECUTOR.getScaledThread();

    private static final Runnable RUNNABLE = new Runnable() {
        int anInt = 0;
        @Override
        public void run() {
            anInt++;
        }
    };

    //@Param({ "1", "4", "16", "256"}) private int threads;
    public static void main(String... args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(".*" + TaskExecTest.class.getSimpleName() + ".*")
                .timeUnit(TimeUnit.NANOSECONDS)
                .mode(Mode.AverageTime)
                .warmupIterations(25)
                .measurementIterations(25)
                .forks(1)
                .threads(4)
                .build();

        new Runner(opt).run();
        TASK_EXECUTOR.shutdown();
    }

    //@Benchmark
    //public void scale(Blackhole blackhole) {
    //    blackhole.consume(TASK_EXECUTOR.getScaledThread());
    //}

    //@Benchmark
    //public void assign() {
        //TASK_EXECUTOR.assign(EXECUTOR, "Lol");
    //}

    @Benchmark
    public void exec() {
        EXECUTOR.addTask(RUNNABLE);
    }
}
