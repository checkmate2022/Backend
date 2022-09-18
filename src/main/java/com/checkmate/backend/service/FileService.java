package com.checkmate.backend.service;

import java.io.IOException;
import java.io.InputStream;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class FileService {

	private AmazonS3 s3Client;

	@Value("${cloud.aws.credentials.accessKey}")
	private String accessKey;

	@Value("${cloud.aws.credentials.secretKey}")
	private String secretKey;

	@Value("${cloud.aws.s3.bucket}")
	private String bucket;

	@Value("${cloud.aws.region.static}")
	private String region;

	@PostConstruct
	public void setS3Client() {
		s3Client = AmazonS3ClientBuilder.standard()
			.withRegion(region)
			.withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(accessKey, secretKey)))
			.build();
	}

	public String saveFile(MultipartFile file, String name, String folderName) throws IOException {
		StringBuilder sb = new StringBuilder();
		String fullBucketName = bucket + folderName;
		String fileName = "";

		// file image 가 없을 경우
		if (file.isEmpty()) {
			sb.append("none");
		}
		if (!file.isEmpty()) {
			// jpeg, png, gif 파일들만 받아서 처리할 예정
			String contentType = file.getContentType();
			String originalFileExtension = null;
			// 확장자 명이 없으면 이 파일은 잘 못 된 것이다
			if (ObjectUtils.isEmpty(contentType)) {
				sb.append("none");
			}
			if (!ObjectUtils.isEmpty(contentType)) {
				if (contentType.contains("image/jpeg")) {
					originalFileExtension = ".jpg";
				} else if (contentType.contains("image/png")) {
					originalFileExtension = ".png";
				} else if (contentType.contains("image/gif")) {
					originalFileExtension = ".gif";
				} else if (contentType.contains("multipart/jpeg")) {
					originalFileExtension = ".jpeg";
				} else if (contentType.contains("multipart/jpg")) {
					originalFileExtension = ".jpg";
				} else if (contentType.contains("multipart/png")) {
					originalFileExtension = ".png";
				}
				sb.append(name + originalFileExtension);
			}
			fileName = sb.toString();
			fileUpload(file, fullBucketName, fileName);
		}
		String fullFileName = s3Client.getUrl(fullBucketName, fileName).toString();
		System.out.println(fullFileName);
		return fullFileName;

	}

	private void fileUpload(MultipartFile multipartFile, String fullBucketName, String newFileName) throws IOException {
		InputStream input = multipartFile.getInputStream();
		ObjectMetadata metadata = new ObjectMetadata();

		try {
			PutObjectRequest putObjectRequest = new PutObjectRequest(fullBucketName, newFileName, input,
				metadata);
			putObjectRequest.setCannedAcl(CannedAccessControlList.PublicRead);
			s3Client.putObject(putObjectRequest);
		} catch (AmazonServiceException ase) {
			ase.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} finally {
			input.close();
		}
	}
	//
	// public File updateFile(MultipartFile file, String orgin_name, String name) {
	//
	// 	File existFile = new File(orgin_name);
	//
	// 	if (existFile.exists()) {
	// 		existFile.delete();
	// 	}
	//
	// 	return saveFile(file, name);
	//
	// }

}
