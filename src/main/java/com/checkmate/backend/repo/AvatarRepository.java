package com.checkmate.backend.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.checkmate.backend.entity.avatar.Avatar;
import com.checkmate.backend.entity.user.User;

@Repository
public interface AvatarRepository extends JpaRepository<Avatar, Long> {

	int countByAvatarName(String avatarName);

	List<Avatar> findAllByUser(User user);

	Optional<Avatar> findAvatarByUserAndIsBasicTrue(User user);
}
