package com.ikeyit.mqhelper;

import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


@Component
public class MqResendTask {
    private static final Logger log = LoggerFactory.getLogger(MqResendTask.class);


    @Autowired
    MqSender mqSender;

    @Scheduled(cron = "5 * * * * ?")
    @SchedulerLock(name = "MqResendTask")
    public void execute() {
        mqSender.resend();
    }
}
