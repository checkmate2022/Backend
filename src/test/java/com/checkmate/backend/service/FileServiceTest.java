package com.checkmate.backend.service;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

@ExtendWith(MockitoExtension.class)
@DisplayName("FileService 테스트")
class FileServiceTest {

	FileService fileService;

	@BeforeEach
	void setUp() {
		this.fileService = new FileService();
	}

	@Test
	@DisplayName("파일 저장")
	void saveFile() {
		MockMultipartFile mockFile
			= new MockMultipartFile(
			"file",
			"hello.jpg",
			MediaType.IMAGE_JPEG_VALUE,
			"Hello, World!".getBytes()
		);

		File file = fileService.saveFile(mockFile, "test");

		assertEquals("test.jpg", file.getName());
	}

	@Test
	void updateFile() {
		MockMultipartFile mockFile
			= new MockMultipartFile(
			"file",
			"hello.jpg",
			MediaType.IMAGE_JPEG_VALUE,
			"Hello, World!".getBytes()
		);
		File file = fileService.saveFile(mockFile, "test");
		File updateFile = fileService.updateFile(mockFile, file.getAbsolutePath(), "updateTest");

		assertEquals("updateTest.jpg", updateFile.getName());

	}

}