package com.checkmate.backend.service;

import java.time.LocalDateTime;

import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.checkmate.backend.advice.exception.OAuthProviderMissMatchException;
import com.checkmate.backend.entity.user.User;
import com.checkmate.backend.repo.UserRepository;
import com.checkmate.backend.entity.oauth.ProviderType;
import com.checkmate.backend.entity.oauth.RoleType;
import com.checkmate.backend.entity.oauth.UserPrincipal;
import com.checkmate.backend.common.info.OAuth2UserInfo;
import com.checkmate.backend.common.info.OAuth2UserInfoFactory;

import lombok.RequiredArgsConstructor;

/**
 * @package : com.checkmate.backend.oauth.service
 * @name: CustomOAuth2UserService.java
 * @date : 2022/05/22 6:24 오후
 * @author : jifrozen
 * @version : 1.0.0
 * @description : 소셜로그인 처리하는 Service
 * @modified :
 **/
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

	private final UserRepository userRepository;

	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
		OAuth2User user = super.loadUser(userRequest);

		try {
			return this.process(userRequest, user);
		} catch (AuthenticationException ex) {
			throw ex;
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new InternalAuthenticationServiceException(ex.getMessage(), ex.getCause());
		}
	}

	private OAuth2User process(OAuth2UserRequest userRequest, OAuth2User user) {
		// naver google kakao 구분 짓는 코드
		ProviderType providerType = ProviderType.valueOf(
			userRequest.getClientRegistration().getRegistrationId().toUpperCase());

		OAuth2UserInfo userInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(providerType, user.getAttributes());
		// 회원가입 된 User 정보 찾음
		User savedUser = userRepository.findByUserId(userInfo.getId());

		if (savedUser != null) {//회원가입 o
			if (providerType != savedUser.getProviderType()) {
				throw new OAuthProviderMissMatchException(
					"Looks like you're signed up with " + providerType +
						" account. Please use your " + savedUser.getProviderType() + " account to login."
				);
			}
			updateUser(savedUser, userInfo);
		} else {//회원가입 x
			savedUser = createUser(userInfo, providerType);
		}

		return UserPrincipal.create(savedUser, user.getAttributes());
	}

	//첫 로그인시 회원가입
	private User createUser(OAuth2UserInfo userInfo, ProviderType providerType) {
		LocalDateTime now = LocalDateTime.now();
		User user = new User(
			userInfo.getId(),
			userInfo.getName(),
			"No_Password",
			providerType,
			RoleType.USER,
			now,
			now
		);

		return userRepository.saveAndFlush(user);
	}

	private User updateUser(User user, OAuth2UserInfo userInfo) {
		if (userInfo.getName() != null && !user.getUsername().equals(userInfo.getName())) {
			user.setUsername(userInfo.getName());
		}

		return user;
	}
}