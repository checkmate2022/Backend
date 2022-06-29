package com.checkmate.backend.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.checkmate.backend.entity.oauth.UserPrincipal;
import com.checkmate.backend.entity.user.User;
import com.checkmate.backend.repo.UserRepository;

import lombok.RequiredArgsConstructor;

/**
 * @package : com.checkmate.backend.oauth.service
 * @name: CustomUserDetailService.java
 * @date : 2022/05/23 1:26 오전
 * @author : jifrozen
 * @version : 1.0.0
 * @description : local 로그인을 위함 유저의 정보를 가져옴
 * @modified :
 **/
@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {

	private final UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
		User user = userRepository.findByUserId(userId);
		if (user == null) {
			throw new UsernameNotFoundException("Can not find username.");
		}
		return UserPrincipal.create(user);
	}
}
