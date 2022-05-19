package com.checkmate.backend.common;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
* @package : com.checkmate.backend.common
* @name: ListResult.java
* @date : 2022/05/19 7:20 오후
* @author : jifrozen
* @version : 1.0.0
* @description : 여러건 api 결과 처리
* @modified : 
**/
@Getter
@Setter
public class ListResult<T> extends CommonResult {
	private List<T> list;
}
