package com.checkmate.backend.oauth.info;

import java.util.Map;

/**
 * @package : com.checkmate.backend.oauth.info
 * @name: OAuth2UserInfo.java
 * @date : 2022/05/23 12:59 오전
 * @author : jifrozen
 * @version : 1.0.0
 * @description :
 * @modified :
 **/
public abstract class OAuth2UserInfo {
	protected Map<String, Object> attributes;

	public OAuth2UserInfo(Map<String, Object> attributes) {
		this.attributes = attributes;
	}

	public Map<String, Object> getAttributes() {
		return attributes;
	}

	public abstract String getId();

	public abstract String getName();

	public abstract String getEmail();

}