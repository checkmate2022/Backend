package com.checkmate.backend.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.checkmate.backend.entity.avatar.AvatarImage;

public interface FileRepository extends JpaRepository<AvatarImage, Long> {
}
