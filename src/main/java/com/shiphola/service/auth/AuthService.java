package com.shiphola.service.auth;

import com.shiphola.dto.request.LoginDTO;
import com.shiphola.dto.request.RegisterDTO;
import com.shiphola.entity.User;

public interface AuthService {

    User register(RegisterDTO dto);

    User login(LoginDTO dto);

    User findByUsername(String username);

    User findByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}
