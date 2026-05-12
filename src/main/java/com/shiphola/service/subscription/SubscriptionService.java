package com.shiphola.service.subscription;

import com.shiphola.constant.SubscriptionStatus;
import com.shiphola.dto.request.SubscriptionDTO;
import com.shiphola.entity.Subscription;
import com.shiphola.entity.User;

import java.util.List;

public interface SubscriptionService {

    List<Subscription> getAllSubscriptions();

    List<Subscription> getActiveSubscriptions();

    List<Subscription> getExpiredSubscriptions();

    Subscription getSubscriptionById(Long subscriptionId);

    Subscription createSubscription(SubscriptionDTO dto);

    Subscription updateSubscription(Long subscriptionId, SubscriptionDTO dto);

    boolean deleteSubscription(Long subscriptionId);

    boolean updateSubscriptionStatus(Long subscriptionId, SubscriptionStatus status);

    List<User> getUsersBySubscription(Long subscriptionId);

    long getTotalSubscriptions();

    long getActiveSubscriptionsCount();

    boolean checkSubscriptionLimit(Long subscriptionId);

    void expireExpiredSubscriptions();
}
