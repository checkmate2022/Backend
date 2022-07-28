package com.checkmate.backend.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.checkmate.backend.entity.user.User;
import com.checkmate.backend.entity.user.UserDeviceToken;

public interface UserDeviceTokenRepository extends JpaRepository<UserDeviceToken, Long> {

	UserDeviceToken findUserDeviceTokenByUser(User user);
}
