package com.checkmate.backend.service;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.Optional;
import java.util.Scanner;

import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import com.checkmate.backend.entity.user.User;
import com.checkmate.backend.entity.user.UserDeviceToken;
import com.checkmate.backend.model.FcmMessage;
import com.checkmate.backend.repo.UserDeviceTokenRepository;
import com.checkmate.backend.repo.UserRepository;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@Slf4j
@Service
@RequiredArgsConstructor
public class FCMService {
	public static final String MESSAGE_KEY = "message";
	private static final String PROJECT_ID = "fcm-checkmate";
	private static final String BASE_URL = "https://fcm.googleapis.com";
	private static final String FCM_SEND_ENDPOINT = "/v1/projects/" + PROJECT_ID + "/messages:send";
	private static final String MESSAGING_SCOPE = "https://www.googleapis.com/auth/firebase.messaging";
	private static final String[] SCOPES = {MESSAGING_SCOPE};
	private final ObjectMapper objectMapper;
	private final UserRepository userRepository;
	private final UserDeviceTokenRepository userDeviceTokenRepository;

	private static HttpURLConnection getConnection() throws IOException {
		// [START use_access_token]
		URL url = new URL(BASE_URL + FCM_SEND_ENDPOINT);
		HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
		httpURLConnection.setRequestProperty("Authorization", "Bearer " + getAccessToken());
		httpURLConnection.setRequestProperty("Content-Type", "application/json; UTF-8");
		return httpURLConnection;
		// [END use_access_token]
	}

	private static String getAccessToken() throws IOException {
		GoogleCredentials googleCredential = GoogleCredentials
			.fromStream(new ClassPathResource(
				"resources/fcm-checkmate-firebase-adminsdk-x38ww-489686dc75.json").getInputStream())
			.createScoped(Arrays.asList(SCOPES));
		googleCredential.refreshIfExpired();
		return googleCredential.getAccessToken().getTokenValue();
	}

	private static String inputstreamToString(InputStream inputStream) throws IOException {
		StringBuilder stringBuilder = new StringBuilder();
		Scanner scanner = new Scanner(inputStream);
		while (scanner.hasNext()) {
			stringBuilder.append(scanner.nextLine());
		}
		return stringBuilder.toString();
	}

	public static void sendMessage(JsonObject fcmMessage) throws IOException {
		HttpURLConnection connection = getConnection();
		connection.setDoOutput(true);
		DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
		outputStream.writeBytes(fcmMessage.toString());
		outputStream.flush();
		outputStream.close();

		int responseCode = connection.getResponseCode();
		if (responseCode == 200) {
			String response = inputstreamToString(connection.getInputStream());
			System.out.println("Message sent to Firebase for delivery, response:");
			System.out.println(response);
		} else {
			System.out.println("Unable to send message to Firebase:");
			String response = inputstreamToString(connection.getErrorStream());
			System.out.println(response);
		}
	}

	private static JsonObject buildNotificationMessage(String title, String body) {
		JsonObject jNotification = new JsonObject();
		jNotification.addProperty("title", title);
		jNotification.addProperty("body", body);

		JsonObject jMessage = new JsonObject();
		jMessage.add("notification", jNotification);
        /*
            firebase
            1. topic
            2. token
            3. condition -> multiple topic
         */
		jMessage.addProperty("topic", "news");
		//jMessage.addProperty("token", /* your test device token */);

		JsonObject jFcm = new JsonObject();
		jFcm.add(MESSAGE_KEY, jMessage);

		return jFcm;
	}

	public static void sendCommonMessage(String title, String body) throws IOException {
		JsonObject notificationMessage = buildNotificationMessage(title, body);
		System.out.println("FCM request body for message using common notification object:");
		prettyPrint(notificationMessage);
		sendMessage(notificationMessage);
	}

	private static void prettyPrint(JsonObject jsonObject) {
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		System.out.println(gson.toJson(jsonObject) + "\n");
	}

	public void registerDeviceToken(String token, String userId) throws Throwable {
		User user = (User)userRepository.findByUserId(userId);
		Optional<UserDeviceToken> userDeviceToken = userDeviceTokenRepository.findUserDeviceTokenByUser(user);
		UserDeviceToken newUserDeviceToken;
		if(userDeviceToken.isEmpty()){
			newUserDeviceToken = UserDeviceToken.builder()
				.token(token)
				.user(user)
				.build();
		}else{
			newUserDeviceToken=userDeviceToken.get();
			newUserDeviceToken.update(token);
		}
		userDeviceTokenRepository.save(newUserDeviceToken);


	}

	public String getToken(String userName) {
		User user = (User)userRepository.findByUsername(userName);
		UserDeviceToken userDeviceToken = userDeviceTokenRepository.findUserDeviceTokenByUser(user).orElseThrow(
			() -> new IllegalArgumentException("토큰이 존재하지 않습니다.")
		);
		return userDeviceToken.getToken();
	}

	public void sendMessageTo(String userName, String title, String body) throws IOException {
		System.out.println("getToken 문제");

		String message = makeMessage(getToken(userName), title, body);
		System.out.println("1");
		OkHttpClient client = new OkHttpClient();
		RequestBody requestBody = RequestBody.create(message, MediaType.get("application/json; charset=utf-8"));
		System.out.println("연결 문제");
		Request request = new Request.Builder()
			.url(BASE_URL + FCM_SEND_ENDPOINT)
			.post(requestBody)
			.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + getAccessToken())
			.addHeader(HttpHeaders.CONTENT_TYPE, "application/json; UTF-8")
			.build();

		Response response = client.newCall(request).execute();

		System.out.println(response.body().string());
	}

	private String makeMessage(String targetToken, String title, String body) throws JsonParseException,
		JsonProcessingException {
		System.out.println("makeMessage");
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
}
