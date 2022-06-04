package com.checkmate.backend.oauth.api.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.checkmate.backend.oauth.api.entity.Avatar;
import com.checkmate.backend.oauth.api.entity.Participant;
import com.checkmate.backend.oauth.api.entity.Schedule;
import com.checkmate.backend.oauth.api.entity.User;

@Repository
public interface AvatarRepository extends JpaRepository<Avatar, Long> {

	public List<Avatar> findByAvatarName(String avatarName);
	List<Avatar> findAllByUser(User user);
}
