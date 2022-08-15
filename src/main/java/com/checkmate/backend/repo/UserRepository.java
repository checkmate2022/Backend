package com.checkmate.backend.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.checkmate.backend.entity.user.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
	User findByUserId(String userId);

	Optional<User> findUserByUserId(String userId);

	User findByUsername(String username);

	int countByUsername(String username);

	int countByUserId(String userId);

	@Query("SELECT u from User u where "
		+ "u.userId like concat('%',:query,'%') "
		+ "or u.username like concat('%',:query,'%')")
	List<User> searchUsers(String query);

}
