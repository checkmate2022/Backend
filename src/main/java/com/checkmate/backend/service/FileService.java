package com.checkmate.backend.service;

import java.io.File;
import java.io.IOException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import com.checkmate.backend.repo.FileRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class FileService {
	private final FileRepository fileRepository;

	public File saveOriginFile(MultipartFile file, String name) {
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
				}else if (contentType.contains("multipart/jpeg")) {
					originalFileExtension = ".jpeg";
				} else if (contentType.contains("multipart/jpg")) {
					originalFileExtension = ".jpg";
				}else if (contentType.contains("multipart/png")) {
					originalFileExtension = ".png";
				}
				sb.append(name + "_origin" + originalFileExtension);
			}

			dest = new File(
				"C:/Users/user/Desktop/졸업프로젝트/user완료/Backend/images" + "/" + sb.toString());
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

	public File saveCreatedFile(MultipartFile file, String name) {
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
				}else if (contentType.contains("multipart/jpeg")) {
					originalFileExtension = ".jpeg";
				} else if (contentType.contains("multipart/jpg")) {
					originalFileExtension = ".jpg";
				}else if (contentType.contains("multipart/png")) {
					originalFileExtension = ".png";
				}
				sb.append(name + "_created" + originalFileExtension);
			}

			dest = new File(
				"C:/Users/user/Desktop/졸업프로젝트/user완료/Backend/images" + "/" + sb.toString());
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

	public File updateOriginFile(MultipartFile file, String orgin_name, String name) {

		String path = "C:/Users/user/Desktop/졸업프로젝트/user완료/Backend/images/" + orgin_name + "_origin";

		File existfile = new File(path);

		if (existfile.exists() == true) {

			existfile.delete();

		}

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
				}else if (contentType.contains("multipart/jpeg")) {
					originalFileExtension = ".jpeg";
				} else if (contentType.contains("multipart/jpg")) {
					originalFileExtension = ".jpg";
				}else if (contentType.contains("multipart/png")) {
					originalFileExtension = ".png";
				}
				sb.append(name + "_origin" + originalFileExtension);
			}

			dest = new File(
				"C:/Users/user/Desktop/졸업프로젝트/user완료/Backend/images" + "/" + sb.toString());
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

	public File updateCreatedFile(MultipartFile file, String origin_name, String name) {

		String path = "C:/Users/user/Desktop/졸업프로젝트/user완료/Backend/images/" + origin_name + "_created";

		File existfile = new File(path);

		if (existfile.exists() == true) {

			existfile.delete();

		}

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
				}else if (contentType.contains("multipart/jpeg")) {
					originalFileExtension = ".jpeg";
				} else if (contentType.contains("multipart/jpg")) {
					originalFileExtension = ".jpg";
				}else if (contentType.contains("multipart/png")) {
					originalFileExtension = ".png";
				}
				sb.append(name + "_created" + originalFileExtension);
			}

			dest = new File(
				"C:/Users/user/Desktop/졸업프로젝트/user완료/Backend/images" + "/" + sb.toString());
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
}
