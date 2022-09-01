package com.checkmate.backend.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.checkmate.backend.entity.user.User;
import com.checkmate.backend.model.response.ScheduleChatbotResponse;
import com.checkmate.backend.service.ScheduleService;
import com.checkmate.backend.service.UserService;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.dialogflow.v2beta1.model.GoogleCloudDialogflowV2Intent;
import com.google.api.services.dialogflow.v2beta1.model.GoogleCloudDialogflowV2WebhookRequest;
import com.google.api.services.dialogflow.v2beta1.model.GoogleCloudDialogflowV2WebhookResponse;

import lombok.RequiredArgsConstructor;

@RequestMapping("/chatbot")
@RestController
@RequiredArgsConstructor
public class ChatBotController {
	private static JacksonFactory jacksonFactory = JacksonFactory.getDefaultInstance();
	private final ScheduleService scheduleService;
	private final UserService userService;

	@RequestMapping(method = RequestMethod.POST, value = "/dialogFlowWebHook")
	public ResponseEntity<?> dialogFlowWebHook(@RequestBody String requestStr, HttpServletRequest servletRequest) throws
		IOException {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String username = authentication.getName();
		User user = userService.getUser(username);
		try {
			GoogleCloudDialogflowV2WebhookResponse response = new GoogleCloudDialogflowV2WebhookResponse(); // response 객체
			GoogleCloudDialogflowV2WebhookRequest request = jacksonFactory.createJsonParser(requestStr)
				.parse(GoogleCloudDialogflowV2WebhookRequest.class); // request 객체에서 파싱
			GoogleCloudDialogflowV2Intent intent=request.getQueryResult().getIntent();
			String name=intent.getDisplayName();

			System.out.println(name);

			String query=request.getQueryResult().getQueryText();
			System.out.println(query);
			String action=request.getQueryResult().getAction();
			System.out.println(action);

			Map<String, Object> params = request.getQueryResult().getParameters(); // 파라미터 받아서 map에다 저장
			StringBuilder sb=new StringBuilder();

			System.out.println(params.get("date-time"));
			switch(name){
				//날짜 일정 조회
				case "detail schedule":
					String time= (String)params.get("date-time");
					// DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
					// LocalDateTime dateTime = LocalDateTime.parse(time, formatter);
					StringTokenizer st=new StringTokenizer(time,"T");
					LocalDate date=LocalDate.parse(st.nextToken());
					LocalDateTime localTime = date.atStartOfDay();
					List<ScheduleChatbotResponse> chatbotResponses= scheduleService.getSchedulesForChatbot(localTime, user);
					sb.append(date.toString()+"일정은"+" ");
					for(int i=0;i<chatbotResponses.size();i++){
						sb.append(chatbotResponses.get(i).getScheduleName()+"  ");
					}
					sb.append("입니다.");
					response.setFulfillmentText(sb.toString());
			}
			//
			// if (params.size() > 0) {
			// 	System.out.println(params);
			// 	response.setFulfillmentText("다음과 같은 파라미터가 나왔습니다 " + "스프링에서 보내는 테스트입니다.");
			// } else {
			// 	response.setFulfillmentText("Sorry you didn't send enough to process");
			// }

			return new ResponseEntity<GoogleCloudDialogflowV2WebhookResponse>(response, HttpStatus.OK);
		} catch (Exception ex) {
			return new ResponseEntity<Object>(ex.getMessage(), HttpStatus.BAD_REQUEST); // 에러 발생 시 bad request 보내줌
		}
	}


}

