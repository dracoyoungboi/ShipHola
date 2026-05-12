package com.shiphola.service.user;

import com.shiphola.constant.Role;
import com.shiphola.dto.request.UserDTO;
import com.shiphola.entity.User;

import java.util.List;

public interface UserService {

    List<User> getAllUsers();

    List<User> getUsersByRole(Role role);

    User getUserById(Long userId);

    User createUser(UserDTO dto);

    User updateUser(Long userId, UserDTO dto);

    boolean deleteUser(Long userId);

    boolean toggleUserStatus(Long userId);

    long getTotalUsers();

    long getUsersByRoleCount(Role role);

    User getLoggedInUser();
}
