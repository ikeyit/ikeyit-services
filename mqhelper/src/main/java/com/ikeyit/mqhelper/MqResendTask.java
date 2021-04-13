package com.ikeyit.mqhelper;

import net.javacrumbs.shedlock.core.DefaultLockingTaskExecutor;
import net.javacrumbs.shedlock.core.LockConfiguration;
import net.javacrumbs.shedlock.core.LockProvider;
import net.javacrumbs.shedlock.core.LockingTaskExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.time.Instant;


@Component
public class MqResendTask {
    private static final Logger log = LoggerFactory.getLogger(MqResendTask.class);

    @Autowired
    MqSender mqSender;

    @Autowired
    LockProvider lockProvider;

    @Value("${mqhelper.lockName:mqresend}")
    private String lockName;

    @Value("${mqhelper.lockAtMostFor:PT5M}")
    private String lockAtMostFor;

    @Value("${mqhelper.lockAtLeastFor:PT5M}")
    private String lockAtLeastFor;

    Duration lockAtMostForDuration = null;

    Duration lockAtLeastForDuration = null;

    @PostConstruct
    private void init() {
        lockAtMostForDuration = Duration.parse(lockAtMostFor);
        lockAtLeastForDuration = Duration.parse(lockAtLeastFor);
    }

    @Scheduled(cron = "5 * * * * ?")
    public void execute() {
        log.info("本地消息表重发任务开始执行!锁名：{}, 最长锁定时间：{}", lockName, lockAtMostForDuration);
        LockingTaskExecutor executor = new DefaultLockingTaskExecutor(lockProvider);
        Instant createdAt = Instant.now();
        executor.executeWithLock((Runnable) mqSender::resend, new LockConfiguration(createdAt, lockName, lockAtMostForDuration, lockAtLeastForDuration));

    }
}
