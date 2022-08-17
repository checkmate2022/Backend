package com.checkmate.backend.service;

import java.io.File;
import java.io.IOException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class FileService {

	public File saveFile(MultipartFile file, String name) {
		StringBuilder sb = new StringBuilder();
		File dest = null;
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

			dest = new File(
				//클라우드 스토리지로 변경필요
				"/Users/jifrozen/project/checkmate/Backend-2/image/" + sb.toString());
			try {
				file.transferTo(dest);
			} catch (IllegalStateException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return dest;

	}

	public File updateFile(MultipartFile file, String orgin_name, String name) {

		File existFile = new File(orgin_name);

		if (existFile.exists()) {
			existFile.delete();
		}

		return saveFile(file, name);

	}

}
