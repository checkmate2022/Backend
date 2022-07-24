package com.checkmate.backend.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.checkmate.backend.entity.meeting.Meeting;
import com.checkmate.backend.entity.meeting.MeetingParticipant;
import com.checkmate.backend.entity.meeting.MeetingParticipantType;
import com.checkmate.backend.entity.user.User;
import com.checkmate.backend.repo.MeetingParticipantRepository;
import com.checkmate.backend.repo.MeetingRepository;
import com.checkmate.backend.service.UserService;

import io.openvidu.java.client.OpenVidu;
import io.openvidu.java.client.OpenViduHttpException;
import io.openvidu.java.client.OpenViduJavaClientException;
import io.openvidu.java.client.OpenViduRole;
import io.openvidu.java.client.Session;
import io.openvidu.java.client.TokenOptions;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "Session", description = "회의 API")
@RequestMapping(value = "/api/v1/session")
@Slf4j
@RequiredArgsConstructor
@RestController
public class SessionController {

	OpenVidu openVidu;

	@Autowired
	private MeetingRepository meetingRepository;

	@Autowired
	private UserService userService;

	@Autowired
	private MeetingParticipantRepository meetingParticipantRepository;

	private Map<String, Session> meetingIdSession = new ConcurrentHashMap<>();
	private Map<String, Map<Long, String>> sessionIdUserIdToken = new ConcurrentHashMap<>();

	private String OPENVIDU_URL;
	private String SECRET;

	public SessionController(@Value("${openvidu.secret}") String secret, @Value("${openvidu.url}") String openviduUrl) {
		this.SECRET = secret;
		this.OPENVIDU_URL = openviduUrl;
		this.openVidu = new OpenVidu(OPENVIDU_URL, SECRET);
	}

	@Operation(summary = "회의 만들기", security = {@SecurityRequirement(name = "bearer-key")})
	@RequestMapping(value = "/create-session", method = RequestMethod.POST)
	public ResponseEntity<JSONObject> createSession(@RequestParam("meetingId") String meetingId) {
		org.springframework.security.core.userdetails.User principal = (org.springframework.security.core.userdetails.User)SecurityContextHolder
			.getContext().getAuthentication().getPrincipal();

		User user = userService.getUser(principal.getUsername());

		// if (!user.hasRoleTeacher()) {
		// 	return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		// }

		Meeting c = meetingRepository.findById(meetingId).get();

		// if (!checkAuthorization(c, c.getHost())) {
		// 	return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		// }

		if (this.meetingIdSession.get(meetingId) != null) {
			// If there's already a valid Session object for this lesson,
			// it is not necessary to ask for a new one
			return new ResponseEntity<>(HttpStatus.OK);
		} else {
			try {
				Session session = this.openVidu.createSession();

				this.meetingIdSession.put(meetingId, session);
				this.sessionIdUserIdToken.put(session.getSessionId(), new HashMap<>());

				showMap();

				return new ResponseEntity<>(HttpStatus.OK);
			} catch (Exception e) {
				return getErrorResponse(e);
			}
		}
	}

	@Operation(summary = "회의 입장을 위한 토큰", security = {@SecurityRequirement(name = "bearer-key")})
	@RequestMapping(value = "/generate-token", method = RequestMethod.POST)
	public ResponseEntity<JSONObject> generateToken(@RequestParam("meetingId") String meetingId) {
		org.springframework.security.core.userdetails.User principal = (org.springframework.security.core.userdetails.User)SecurityContextHolder
			.getContext().getAuthentication().getPrincipal();

		User user = userService.getUser(principal.getUsername());

		MeetingParticipant meetingParticipant = meetingParticipantRepository.findMeetingParticipantByUser(user);

		Meeting c = meetingRepository.findById(meetingId).get();

		// if (!checkAuthorizationUsers(c, c.getParticipants())) {
		// 	System.out.println("Not authorized");
		// 	return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		// }
		//
		// if (this.lessonIdSession.get(id_lesson) == null) {
		// 	System.out.println("There's no Session fot this lesson");
		// 	return new ResponseEntity<>(HttpStatus.CONFLICT);
		// }

		Session session = this.meetingIdSession.get(meetingId);
		OpenViduRole role =
			meetingParticipant.getMeetingParticipantType() == MeetingParticipantType.HOST ? OpenViduRole.PUBLISHER :
				OpenViduRole.SUBSCRIBER;

		JSONObject responseJson = new JSONObject();
		TokenOptions tokenOpts = new TokenOptions.Builder().role(role)
			.data("SERVER=" + user.getUsername()).build();
		try {
			String token = this.meetingIdSession.get(meetingId).generateToken(tokenOpts);

			this.sessionIdUserIdToken.get(session.getSessionId()).put(user.getUserSeq(), token);
			responseJson.put(0, token);
			showMap();

			return new ResponseEntity<>(responseJson, HttpStatus.OK);
		} catch (OpenViduJavaClientException e1) {
			// If internal error generate an error message and return it to client
			return getErrorResponse(e1);
		} catch (OpenViduHttpException e2) {
			if (404 == e2.getStatus()) {
				// Invalid sessionId (user left unexpectedly). Session object is not valid
				// anymore. Must clean invalid session and create a new one
				try {
					this.sessionIdUserIdToken.remove(session.getSessionId());
					session = this.openVidu.createSession();
					this.meetingIdSession.put(meetingId, session);
					this.sessionIdUserIdToken.put(session.getSessionId(), new HashMap<>());
					String token = session.generateToken(tokenOpts);
					// END IMPORTANT STUFF

					this.sessionIdUserIdToken.get(session.getSessionId()).put(user.getUserSeq(), token);
					responseJson.put(0, token);
					showMap();

					return new ResponseEntity<>(responseJson, HttpStatus.OK);
				} catch (OpenViduJavaClientException | OpenViduHttpException e3) {
					return getErrorResponse(e3);
				}
			} else {
				return getErrorResponse(e2);
			}
		}
	}

	@Operation(summary = "나가기", security = {@SecurityRequirement(name = "bearer-key")})
	@RequestMapping(value = "/remove-user", method = RequestMethod.POST)
	public ResponseEntity<JSONObject> removeUser(@RequestParam("meetingId") String meetingId) throws Exception {
		org.springframework.security.core.userdetails.User principal = (org.springframework.security.core.userdetails.User)SecurityContextHolder
			.getContext().getAuthentication().getPrincipal();

		User user = userService.getUser(principal.getUsername());

		Meeting c = meetingRepository.findById(meetingId).get();

		String sessionId = this.meetingIdSession.get(meetingId).getSessionId();
		if (this.sessionIdUserIdToken.get(sessionId).remove(user.getUserSeq()) != null) {
			// This user has left the lesson
			if (this.sessionIdUserIdToken.get(sessionId).isEmpty()) {
				// The last user has left the lesson
				this.meetingIdSession.remove(meetingId);
				this.sessionIdUserIdToken.remove(sessionId);
			}
			showMap();

			return new ResponseEntity<>(HttpStatus.OK);
		} else {
			System.out.println("Problems in the app server: the user didn't have a valid token");
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private void showMap() {
		System.out.println("------------------------------");
		System.out.println(this.meetingIdSession.toString());
		System.out.println(this.sessionIdUserIdToken.toString());
		System.out.println("------------------------------");
	}

	private ResponseEntity<JSONObject> getErrorResponse(Exception e) {
		JSONObject json = new JSONObject();
		json.put("cause", e.getCause());
		json.put("error", e.getMessage());
		json.put("exception", e.getClass());
		return new ResponseEntity<>(json, HttpStatus.INTERNAL_SERVER_ERROR);
	}

}
