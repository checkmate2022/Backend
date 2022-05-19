package com.checkmate.backend.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.checkmate.backend.common.CommonResult;
import com.checkmate.backend.common.ListResult;
import com.checkmate.backend.common.SingleResult;

/**
 * @package : com.checkmate.backend.service
 * @name: ResponseService.java
 * @date : 2022/05/19 7:50 오후
 * @author : jifrozen
 * @version : 1.0.0
 * @description : 결과 모델을 처리할 서비스
 * @modified :
 **/
@Service
public class ResponseService {
	// 단일건 결과 처리
	public <T> SingleResult<T> getSingleResult(T data) {
		SingleResult<T> result = new SingleResult<>();
		result.setData(data);
		setSuccessResult(result);
		return result;
	}

	// 다중건 결과 처리
	public <T> ListResult<T> getListResult(List<T> list) {
		ListResult<T> result = new ListResult<>();
		result.setList(list);
		setSuccessResult(result);
		return result;
	}

	// 성공 결과만 처리
	public CommonResult getSuccessResult() {
		CommonResult result = new CommonResult();
		setSuccessResult(result);
		return result;
	}

	// 성공하면 api 성공 데이터 세팅
	private void setSuccessResult(CommonResult result) {
		result.setSuccess(true);
		result.setCode(CommonResponse.SUCCESS.getCode());
		result.setMsg(CommonResponse.SUCCESS.getMsg());
	}

	// 실패 결과만 처리
	public CommonResult getFailResult(int code, String msg) {
		CommonResult result = new CommonResult();
		result.setSuccess(false);
		result.setCode(code);
		result.setMsg(msg);
		return result;
	}

	public enum CommonResponse {
		SUCCESS(0, "성공"),
		FAIL(-1, "실패");

		int code;
		String msg;

		CommonResponse(int code, String msg) {
			this.code = code;
			this.msg = msg;
		}

		public int getCode() {
			return code;
		}

		public String getMsg() {
			return msg;
		}
	}

}
