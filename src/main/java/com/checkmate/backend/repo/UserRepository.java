package com.checkmate.backend.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.checkmate.backend.entity.user.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
	User findByUserId(String userId);

	User findByUsername(String username);

	int countByUsername(String username);

	int countByUserId(String userId);

}
