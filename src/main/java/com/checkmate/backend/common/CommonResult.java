package com.checkmate.backend.common;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
/**
* @package : com.checkmate.backend.common
* @name: CommonResult.java
* @date : 2022/05/19 7:18 오후
* @author : jifrozen
* @version : 1.0.0
* @description : REST API 결과 모델 생성
* @modified :
**/
@Getter
@Setter
public class CommonResult {

	@Schema(description = "응답 성공 여부 = true/false")
	private boolean success;

	@Schema(description = "응답 코드 번호")
	private int code;

	@Schema(description = "응답 메시지")
	private String msg;
}
