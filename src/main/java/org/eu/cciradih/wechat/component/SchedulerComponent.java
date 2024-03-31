package org.eu.cciradih.wechat.component;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.config.CronTask;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;

@Component
@RequiredArgsConstructor
public class SchedulerComponent {
    private final Map<String, ScheduledFuture<?>> scheduledTaskMap = Collections.synchronizedMap(new HashMap<>());

    private final TaskScheduler taskScheduler;

    public void start(String taskName, CronTask cronTask) {
        ScheduledFuture<?> scheduledFuture = taskScheduler.schedule(cronTask.getRunnable(), cronTask.getTrigger());
        this.scheduledTaskMap.put(taskName, scheduledFuture);
    }

    public void stop(String taskName) {
        this.scheduledTaskMap.get(taskName).cancel(true);
    }

    public ScheduledFuture<?> getScheduledFuture(String taskName) {
        return this.scheduledTaskMap.get(taskName);
    }
}
