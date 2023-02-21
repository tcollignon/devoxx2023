package org.tcollignon.user.schedule;

import io.quarkus.scheduler.Scheduled;
import org.tcollignon.user.service.UserService;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class UserScheduleService {

    @Inject
    UserService userService;
    
    @Scheduled(every = "5m")
    void deleteReinitPasswordRequest() {
        userService.deleteReinitPasswordRequestCreatedOverLastXminutes();
    }
    
}
