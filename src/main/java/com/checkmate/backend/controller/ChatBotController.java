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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.checkmate.backend.common.ListResult;
import com.checkmate.backend.entity.schedule.Schedule;
import com.checkmate.backend.entity.schedule.ScheduleType;
import com.checkmate.backend.entity.user.User;
import com.checkmate.backend.model.request.ScheduleRequest;
import com.checkmate.backend.model.response.ScheduleChatbotResponse;
import com.checkmate.backend.service.ResponseService;
import com.checkmate.backend.service.ScheduleService;
import com.checkmate.backend.service.TeamService;
import com.checkmate.backend.service.UserService;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.dialogflow.v2beta1.model.GoogleCloudDialogflowV2Intent;
import com.google.api.services.dialogflow.v2beta1.model.GoogleCloudDialogflowV2WebhookRequest;
import com.google.api.services.dialogflow.v2beta1.model.GoogleCloudDialogflowV2WebhookResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;

@RequestMapping("/chatbot")
@RestController
@RequiredArgsConstructor
public class ChatBotController {
	private static JacksonFactory jacksonFactory = JacksonFactory.getDefaultInstance();
	private final ScheduleService scheduleService;
	private final UserService userService;
	private final TeamService teamService;
	private final ResponseService responseService;

	@Operation(summary = "챗봇 알림 일정 조회", security = {
		@SecurityRequirement(name = "bearer-key")})
	@GetMapping("")
	public ListResult<ScheduleChatbotResponse> getSchedulesforChatBot() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String name = authentication.getName();

		User user = userService.getUser(name);
		return responseService.getListResult(scheduleService.getSchedulesForChatBotNotification(user));
	}

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

			Map<String, Object> params = request.getQueryResult().getParameters(); // 파라미터 받아서 map에다 저장
			StringBuilder sb = new StringBuilder();
			if (name.equals("ask - scheduleDate")) {
				//날짜 일정 조회
				String time = (String)params.get("date-time");
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
			} else if (name.equals("register - scheduleDateTime")) {
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
			} else if (name.equals("notification - notificationTime")) {

				int notificationTime = Integer.parseInt(
					(String)request.getOriginalDetectIntentRequest().getPayload().get("notificationTime"));
				Long scheduleId = Long.parseLong(
					(String)request.getOriginalDetectIntentRequest().getPayload().get("scheduleSeq"));

				scheduleService.updateNotification(scheduleId, notificationTime, user);

				sb.append("알람 설정이 완료되었습니다.");

			} else {
				sb.append("서버 연결 불안정");
			}
			response.setFulfillmentText(sb.toString());

			return new ResponseEntity<GoogleCloudDialogflowV2WebhookResponse>(response, HttpStatus.OK);
		} catch (Exception ex) {
			return new ResponseEntity<Object>(ex.getMessage(), HttpStatus.BAD_REQUEST); // 에러 발생 시 bad request 보내줌
		}
	}

}
