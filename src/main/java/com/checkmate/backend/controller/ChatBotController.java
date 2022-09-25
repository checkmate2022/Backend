package com.checkmate.backend.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.checkmate.backend.entity.schedule.Schedule;
import com.checkmate.backend.entity.schedule.ScheduleType;
import com.checkmate.backend.entity.user.User;
import com.checkmate.backend.model.request.ScheduleRequest;
import com.checkmate.backend.model.response.ScheduleChatbotResponse;
import com.checkmate.backend.service.ScheduleService;
import com.checkmate.backend.service.TeamService;
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
	private final TeamService teamService;

	@RequestMapping(method = RequestMethod.POST, value = "/dialogFlowWebHook")
	public ResponseEntity<?> dialogFlowWebHook(@RequestBody String requestStr, HttpServletRequest servletRequest) throws
		IOException {

		try {
			GoogleCloudDialogflowV2WebhookResponse response = new GoogleCloudDialogflowV2WebhookResponse(); // response 객체
			GoogleCloudDialogflowV2WebhookRequest request = jacksonFactory.createJsonParser(requestStr)
				.parse(GoogleCloudDialogflowV2WebhookRequest.class); // request 객체에서 파싱
			GoogleCloudDialogflowV2Intent intent = request.getQueryResult().getIntent();
			String name = intent.getDisplayName();
			String userId = (String)request.getOriginalDetectIntentRequest().getPayload().get("userId");
			User user = userService.getUser(userId);
			String query = request.getQueryResult().getQueryText();
			String action = request.getQueryResult().getAction();

			Map<String, Object> params = request.getQueryResult().getParameters(); // 파라미터 받아서 map에다 저장
			StringBuilder sb = new StringBuilder();
			System.out.println((String)request.getOriginalDetectIntentRequest().getPayload().get("scheduleType"));
			System.out.println((String)request.getOriginalDetectIntentRequest().getPayload().get("teamSeq"));
			System.out.println((String)request.getOriginalDetectIntentRequest().getPayload().get("userId"));
			System.out.println((String)request.getOriginalDetectIntentRequest().getPayload().get("scheduleTitle"));
			System.out.println((String)request.getOriginalDetectIntentRequest().getPayload().get("scheduleStartDate"));
			System.out.println((String)request.getOriginalDetectIntentRequest().getPayload().get("scheduleEndDate"));
			System.out.println(name);
			System.out.println(name.equals("register_schedule_scheduleDateTime"));
			if (name.equals("detail schedule")) {
				//날짜 일정 조회
				// case "detail schedule":
				String time = (String)params.get("date-time");
				// DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
				// LocalDateTime dateTime = LocalDateTime.parse(time, formatter);
				StringTokenizer st = new StringTokenizer(time, "T");
				LocalDate date = LocalDate.parse(st.nextToken());
				LocalDateTime localTime = date.atStartOfDay();
				List<ScheduleChatbotResponse> chatbotResponses = scheduleService.getSchedulesForChatbot(localTime,
					user);
				sb.append(date.toString() + "일정은" + " ");
				if (chatbotResponses.size() == 0)
					sb.append("없습니다.");
				for (int i = 0; i < chatbotResponses.size(); i++) {
					sb.append(chatbotResponses.get(i).getScheduleName() + "입니다");
				}

			}
			// case "register_schedule_scheduleDateTime":
			if (name.equals("register_schedule_scheduleDateTime")) {
				String scheduleTypeName = (String)request.getOriginalDetectIntentRequest()
					.getPayload()
					.get("scheduleType");

				Long teamId = Long.parseLong(
					(String)request.getOriginalDetectIntentRequest().getPayload().get("teamSeq"));

				String scheduleTitle = (String)request.getOriginalDetectIntentRequest()
					.getPayload()
					.get("scheduleTitle");

				LocalDateTime startTime = LocalDateTime.parse((String)request.getOriginalDetectIntentRequest()
					.getPayload()
					.get("scheduleStartDate"));

				LocalDateTime endTime = LocalDateTime.parse((String)request.getOriginalDetectIntentRequest()
					.getPayload()
					.get("scheduleEndDate"));

				List<String> participantsName = teamService.findParticipantsByTeam(teamId, userId);

				ScheduleRequest scheduleRequest = new ScheduleRequest(scheduleTitle, scheduleTitle,
					ScheduleType.CONFERENCE, startTime,
					endTime, participantsName, teamId);
				Schedule schedule = scheduleService.make(scheduleRequest, user);
				sb.append(
					"( " + schedule.getScheduleType().getDisplayName() + " )" + schedule.getScheduleName() +
						" " + schedule.getScheduleStartdate() + " 일정이 등록되었습니다.");
			}
			response.setFulfillmentText(sb.toString());
			//
			// if (params.size() > 0) {
			//    System.out.println(params);
			//    response.setFulfillmentText("다음과 같은 파라미터가 나왔습니다 " + "스프링에서 보내는 테스트입니다.");
			// } else {
			//    response.setFulfillmentText("Sorry you didn't send enough to process");
			// }

			return new ResponseEntity<GoogleCloudDialogflowV2WebhookResponse>(response, HttpStatus.OK);
		} catch (Exception ex) {
			return new ResponseEntity<Object>(ex.getMessage(), HttpStatus.BAD_REQUEST); // 에러 발생 시 bad request 보내줌
		}
	}

}
