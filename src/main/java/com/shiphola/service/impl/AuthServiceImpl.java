package com.shiphola.service.impl;

import com.shiphola.constant.Role;
import com.shiphola.dto.request.LoginDTO;
import com.shiphola.dto.request.RegisterDTO;
import com.shiphola.entity.User;
import com.shiphola.repository.UserRepository;
import com.shiphola.service.auth.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public User register(RegisterDTO dto) {
        if (existsByUsername(dto.getUsername())) {
            throw new RuntimeException("Username đã tồn tại");
        }

        if (existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Email đã tồn tại");
        }

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setFullName(dto.getFullName());
        user.setPhone(dto.getPhone());
        user.setAddress(dto.getAddress());
        user.setRole(Role.GUEST);
        user.setEnabled(true);
        user.setLocked(false);

        return userRepository.save(user);
    }

    @Override
    public User login(LoginDTO dto) {
        User user = findByUsername(dto.getUsername());

        if (user == null) {
            throw new RuntimeException("Username không tồn tại");
        }

        if (!user.getEnabled()) {
            throw new RuntimeException("Tài khoản đã bị vô hiệu hóa");
        }

        if (user.getLocked()) {
            throw new RuntimeException("Tài khoản đã bị khóa");
        }

        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new RuntimeException("Password không đúng");
        }

        user.setLastLoginAt(LocalDateTime.now());
        return userRepository.save(user);
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
}
