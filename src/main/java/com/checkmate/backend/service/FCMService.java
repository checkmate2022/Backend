package com.checkmate.backend.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import com.checkmate.backend.entity.schedule.Notification;
import com.checkmate.backend.entity.user.User;
import com.checkmate.backend.entity.user.UserDeviceToken;
import com.checkmate.backend.model.FcmMessage;
import com.checkmate.backend.repo.NotificationRepository;
import com.checkmate.backend.repo.UserDeviceTokenRepository;
import com.checkmate.backend.repo.UserRepository;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;

import lombok.RequiredArgsConstructor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@Component
@RequiredArgsConstructor
public class FCMService {

	private final String API_URL = "https://fcm.googleapis.com/v1/projects/fcm-checkmate/messages:send";
	private final ObjectMapper objectMapper;
	private final UserRepository userRepository;
	private final UserDeviceTokenRepository userDeviceTokenRepository;
	private final NotificationRepository notificationRepository;

	public List<Notification> findNotificationByUser(String userId) {
		return notificationRepository.findNotificationsByUserIdAndIsNoticeTrue(userId);
	}

	public void registerDeviceToken(String token, String userId) throws Throwable {
		User user = (User)userRepository.findByUserId(userId);
		Optional<UserDeviceToken> userDeviceToken = userDeviceTokenRepository.findUserDeviceTokenByUser(user);
		UserDeviceToken newUserDeviceToken;
		if (userDeviceToken.isEmpty()) {
			newUserDeviceToken = UserDeviceToken.builder()
				.token(token)
				.user(user)
				.build();
			userDeviceTokenRepository.save(newUserDeviceToken);
		} else {
			newUserDeviceToken = userDeviceToken.get();
			newUserDeviceToken.update(token);
		}
	}

	public void sendMessageTo(String userId, String title, String body) throws IOException {
		User user = (User)userRepository.findByUserId(userId);
		Optional<UserDeviceToken> targetToken = userDeviceTokenRepository.findUserDeviceTokenByUser(user);
		String message = makeMessage(targetToken.get().getToken(), title, body);

		OkHttpClient client = new OkHttpClient();
		RequestBody requestBody = RequestBody.create(message,
			MediaType.get("application/json; charset=utf-8"));
		Request request = new Request.Builder()
			.url(API_URL)
			.post(requestBody)
			.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + getAccessToken())
			.addHeader(HttpHeaders.CONTENT_TYPE, "application/json; UTF-8")
			.build();

		Response response = client.newCall(request).execute();
		Notification notification = new Notification(title, body, userId, LocalDateTime.now(), true);
		notificationRepository.save(notification);

		System.out.println(response.body().string());
	}

	private String makeMessage(String targetToken, String title, String body) throws
		JsonParseException,
		JsonProcessingException {
		FcmMessage fcmMessage = FcmMessage.builder()
			.message(FcmMessage.Message.builder()
				.token(targetToken)
				.notification(FcmMessage.Notification.builder()
					.title(title)
					.body(body)
					.image(null)
					.build()
				).build()).validateOnly(false).build();

		return objectMapper.writeValueAsString(fcmMessage);
	}

	private String getAccessToken() throws IOException {
		String firebaseConfigPath = "firebase/fcm-checkmate-firebase-adminsdk-x38ww-489686dc75.json";

		GoogleCredentials googleCredentials = GoogleCredentials
			.fromStream(new ClassPathResource(firebaseConfigPath).getInputStream())
			.createScoped(List.of("https://www.googleapis.com/auth/cloud-platform"));

		googleCredentials.refreshIfExpired();
		return googleCredentials.getAccessToken().getTokenValue();
	}
}