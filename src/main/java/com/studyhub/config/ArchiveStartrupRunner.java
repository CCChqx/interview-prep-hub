package com.studyhub.config;

import com.studyhub.service.CheckinService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class ArchiveStartrupRunner implements ApplicationRunner {

    @Autowired
    private CheckinService checkinService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        checkinService.checkin();
    }
}
