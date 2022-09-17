package com.checkmate.backend.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.checkmate.backend.entity.avatar.Emoticon;

public interface EmoticonRepository extends JpaRepository<Emoticon, Long> {
}
