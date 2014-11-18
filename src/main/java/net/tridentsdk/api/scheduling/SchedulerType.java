package net.tridentsdk.api.scheduling;

public enum SchedulerType {
    /**
     * Represents a Scheduler which is Asynchronous, which will run a task immediately.
     */
    ASYNC_RUN,

    /**
     * Represents a Scheduler which is Asynchronous, which will run a task later.
     */
    ASYNC_LATER,

    /**
     * Represents a Scheduler which is Asynchronous, which repeats a task.
     */
    ASYNC_REPEAT,

    /**
     * Represents a Scheduler which is Synchronous, which will run a task immediately.
     */
    SYNC_RUN,

    /**
     * Represents a Scheduler which is Synchronous, which will run a task later.
     */
    SYNC_LATER,

    /**
     * Represents a Scheduler which is Synchronous, which repeats a task.
     */
    SYNC_REPEAT
}
