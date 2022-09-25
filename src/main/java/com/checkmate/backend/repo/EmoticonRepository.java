package com.checkmate.backend.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.checkmate.backend.entity.avatar.Emoticon;
import com.checkmate.backend.entity.user.User;

public interface EmoticonRepository extends JpaRepository<Emoticon, Long> {

	List<Emoticon> findAllByUser(User user);
}
