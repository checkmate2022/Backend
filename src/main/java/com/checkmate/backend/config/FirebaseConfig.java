package com.checkmate.backend.config;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

@Configuration
public class FirebaseConfig {
	private final Logger logger = LoggerFactory.getLogger(FirebaseConfig.class);

	@Value("${firebase-sdk-path}")
	private String firebaseSdkPath;

	@PostConstruct
	public void initialize() {
		try {
			ClassPathResource resource = new ClassPathResource(firebaseSdkPath);
			InputStream serviceAcount = resource.getInputStream();
			FirebaseOptions options = FirebaseOptions.builder()
				.setCredentials(GoogleCredentials.fromStream(serviceAcount))
				.build();
			FirebaseApp.initializeApp(options);
		} catch (FileNotFoundException e) {
			logger.error("Firebase ServiceAccountKey FileNotFoundException" + e.getMessage());
		} catch (IOException e) {
			logger.error("FirebaseOptions IOException" + e.getMessage());
		}
	}
}
