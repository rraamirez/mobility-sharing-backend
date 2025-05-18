package com.ramirezabril.mobility_sharing.util;

import com.ramirezabril.mobility_sharing.repository.UserRepository;
import com.ramirezabril.mobility_sharing.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SchedulingTasks {
    @Autowired
    private UserService userService;

    @Autowired
    UserRepository userRepository;

    @Scheduled(cron = "0 0 3 * * *")
    public void updateUsersRupees() {
        List<Integer> userIds = userRepository.getUserIds();
        userIds.forEach(userId -> userService.updateRupeeWallet(100, userId));
    }

    @Scheduled(cron = "0 0 5 * * *")
    public void updateUsersRatings() {
        userService.computeUserRatings();
    }
}
