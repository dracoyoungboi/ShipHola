package com.shiphola.service.impl;

import com.shiphola.constant.SubscriptionStatus;
import com.shiphola.dto.request.SubscriptionDTO;
import com.shiphola.entity.Package;
import com.shiphola.entity.Subscription;
import com.shiphola.entity.User;
import com.shiphola.repository.PackageRepository;
import com.shiphola.repository.SubscriptionRepository;
import com.shiphola.repository.UserRepository;
import com.shiphola.service.subscription.SubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SubscriptionServiceImpl implements SubscriptionService {

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PackageRepository packageRepository;

    @Override
    public List<Subscription> getAllSubscriptions() {
        return subscriptionRepository.findAllActive();
    }

    @Override
    public List<Subscription> getActiveSubscriptions() {
        return subscriptionRepository.findActiveSubscriptions(LocalDateTime.now());
    }

    @Override
    public List<Subscription> getExpiredSubscriptions() {
        return subscriptionRepository.findExpiredSubscriptions(LocalDateTime.now());
    }

    @Override
    public Subscription getSubscriptionById(Long subscriptionId) {
        return subscriptionRepository.findById(subscriptionId)
                .filter(sub -> !sub.getDeleted())
                .orElse(null);
    }

    @Override
    @Transactional
    public Subscription createSubscription(SubscriptionDTO dto) {
        Subscription subscription = new Subscription();
        subscription.setCompanyName(dto.getCompanyName());
        subscription.setTaxCode(dto.getTaxCode());
        subscription.setPlan(dto.getPlan());
        subscription.setStatus(SubscriptionStatus.ACTIVE);
        subscription.setMaxPackages(dto.getMaxPackages());
        subscription.setMaxUsers(dto.getMaxUsers());
        subscription.setMonthlyFee(dto.getMonthlyFee());
        subscription.setStartDate(dto.getStartDate() != null ? dto.getStartDate() : LocalDateTime.now());
        subscription.setEndDate(dto.getEndDate() != null ? dto.getEndDate() : subscription.getStartDate().plusMonths(1));

        return subscriptionRepository.save(subscription);
    }

    @Override
    @Transactional
    public Subscription updateSubscription(Long subscriptionId, SubscriptionDTO dto) {
        Subscription subscription = getSubscriptionById(subscriptionId);
        if (subscription == null) {
            throw new RuntimeException("Không tìm thấy subscription");
        }

        subscription.setCompanyName(dto.getCompanyName());
        subscription.setTaxCode(dto.getTaxCode());
        subscription.setPlan(dto.getPlan());
        subscription.setMaxPackages(dto.getMaxPackages());
        subscription.setMaxUsers(dto.getMaxUsers());
        subscription.setMonthlyFee(dto.getMonthlyFee());

        if (dto.getStartDate() != null) {
            subscription.setStartDate(dto.getStartDate());
        }
        if (dto.getEndDate() != null) {
            subscription.setEndDate(dto.getEndDate());
        }

        return subscriptionRepository.save(subscription);
    }

    @Override
    @Transactional
    public boolean deleteSubscription(Long subscriptionId) {
        Subscription subscription = getSubscriptionById(subscriptionId);
        if (subscription == null) {
            return false;
        }

        subscription.setDeleted(true);
        subscriptionRepository.save(subscription);
        return true;
    }

    @Override
    @Transactional
    public boolean updateSubscriptionStatus(Long subscriptionId, SubscriptionStatus status) {
        Subscription subscription = getSubscriptionById(subscriptionId);
        if (subscription == null) {
            return false;
        }

        subscription.setStatus(status);
        subscriptionRepository.save(subscription);
        return true;
    }

    @Override
    public List<User> getUsersBySubscription(Long subscriptionId) {
        return userRepository.findBySubscriptionId(subscriptionId);
    }

    @Override
    public long getTotalSubscriptions() {
        return subscriptionRepository.count();
    }

    @Override
    public long getActiveSubscriptionsCount() {
        return subscriptionRepository.countActive();
    }

    @Override
    public boolean checkSubscriptionLimit(Long subscriptionId) {
        Subscription subscription = getSubscriptionById(subscriptionId);
        if (subscription == null || !subscription.isActive()) {
            return false;
        }

        long currentPackages = packageRepository.count();
        return currentPackages < subscription.getMaxPackages();
    }

    @Override
    @Transactional
    public void expireExpiredSubscriptions() {
        List<Subscription> expired = getExpiredSubscriptions();
        for (Subscription sub : expired) {
            sub.setStatus(SubscriptionStatus.EXPIRED);
            subscriptionRepository.save(sub);
        }
    }
}
