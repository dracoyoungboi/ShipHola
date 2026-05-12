package com.shiphola.scheduler;

import com.shiphola.service.subscription.SubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class SubscriptionScheduler {

    @Autowired
    private SubscriptionService subscriptionService;

    @Scheduled(cron = "0 0 0 * * ?")
    public void expireSubscriptions() {
        subscriptionService.expireExpiredSubscriptions();
    }
}
