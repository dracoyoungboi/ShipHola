package com.shiphola.service.impl;

import com.shiphola.constant.Role;
import com.shiphola.dto.request.UserDTO;
import com.shiphola.entity.User;
import com.shiphola.repository.UserRepository;
import com.shiphola.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAllActive();
    }

    @Override
    public List<User> getUsersByRole(Role role) {
        return userRepository.findByRoleAndNotDeleted(role);
    }

    @Override
    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .filter(user -> !user.getDeleted())
                .orElse(null);
    }

    @Override
    @Transactional
    public User createUser(UserDTO dto) {
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new RuntimeException("Username đã tồn tại");
        }

        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Email đã tồn tại");
        }

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword() != null ?
                passwordEncoder.encode(dto.getPassword()) :
                passwordEncoder.encode("123456"));
        user.setFullName(dto.getFullName());
        user.setPhone(dto.getPhone());
        user.setAddress(dto.getAddress());
        user.setRole(dto.getRole());
        user.setSubscriptionId(dto.getSubscriptionId());
        user.setEnabled(true);
        user.setLocked(false);

        return userRepository.save(user);
    }

    @Override
    @Transactional
    public User updateUser(Long userId, UserDTO dto) {
        User user = getUserById(userId);
        if (user == null) {
            throw new RuntimeException("Không tìm thấy user");
        }

        user.setFullName(dto.getFullName());
        user.setPhone(dto.getPhone());
        user.setAddress(dto.getAddress());
        user.setRole(dto.getRole());

        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        return userRepository.save(user);
    }

    @Override
    @Transactional
    public boolean deleteUser(Long userId) {
        User user = getUserById(userId);
        if (user == null) {
            return false;
        }

        user.setDeleted(true);
        userRepository.save(user);
        return true;
    }

    @Override
    @Transactional
    public boolean toggleUserStatus(Long userId) {
        User user = getUserById(userId);
        if (user == null) {
            return false;
        }

        user.setEnabled(!user.getEnabled());
        userRepository.save(user);
        return true;
    }

    @Override
    public long getTotalUsers() {
        return userRepository.countActive();
    }

    @Override
    public long getUsersByRoleCount(Role role) {
        return userRepository.countByRole(role);
    }

    @Override
    public User getLoggedInUser() {
        return null;
    }
}
