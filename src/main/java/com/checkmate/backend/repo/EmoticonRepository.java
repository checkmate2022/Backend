package com.checkmate.backend.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.checkmate.backend.entity.avatar.Avatar;
import com.checkmate.backend.entity.avatar.Emoticon;

public interface EmoticonRepository extends JpaRepository<Emoticon, Long> {

	List<Emoticon> findEmoticonsByAvatar(Avatar avatar);
}
