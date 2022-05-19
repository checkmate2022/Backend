package com.checkmate.backend.common;

import lombok.Getter;
import lombok.Setter;

/**
 * @package : com.checkmate.backend.common
 * @name: SingleResult.java
 * @date : 2022/05/19 7:20 오후
 * @author : jifrozen
 * @version : 1.0.0
 * @description : 단일건 api 결과 처리
 * @modified :
 **/
@Getter
@Setter
public class SingleResult<T> extends CommonResult {
	private T data;
}