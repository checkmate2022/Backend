package com.checkmate.backend.oauth.info;

import java.util.Map;

import com.checkmate.backend.oauth.entity.ProviderType;

/**
 * @package : com.checkmate.backend.oauth.info
 * @name: OAuth2UserInfoFactory.java
 * @date : 2022/05/23 12:59 오전
 * @author : jifrozen
 * @version : 1.0.0
 * @description : ProviderType에 따라 UserInfo 나눔
 * @modified :
 **/
public class OAuth2UserInfoFactory {
	public static OAuth2UserInfo getOAuth2UserInfo(ProviderType providerType, Map<String, Object> attributes) {
		switch (providerType) {
			case GOOGLE:
				return new GoogleOAuth2UserInfo(attributes);
			case NAVER:
				return new NaverOAuth2UserInfo(attributes);
			case KAKAO:
				return new KakaoOAuth2UserInfo(attributes);
			default:
				throw new IllegalArgumentException("Invalid Provider Type.");
		}
	}
}
