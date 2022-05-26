package com.checkmate.backend.oauth.api.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.checkmate.backend.oauth.api.entity.AvatarImage;

public interface FileRepository extends JpaRepository<AvatarImage, Long> {
}
