package com.checkmate.backend.oauth.api.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.checkmate.backend.oauth.api.entity.Avatar;

@Repository
public interface AvatarRepository extends JpaRepository<Avatar, Long> {

	//public List<Avatar> findByAvatar_name(String avatar_name);
}
