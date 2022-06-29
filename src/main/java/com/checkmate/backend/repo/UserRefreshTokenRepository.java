package com.checkmate.backend.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.checkmate.backend.entity.user.UserRefreshToken;

public interface UserRefreshTokenRepository extends JpaRepository<UserRefreshToken, Long> {
	UserRefreshToken findByUserId(String userId);

	UserRefreshToken findByUserIdAndRefreshToken(String userId, String refreshToken);
}
