package hello.springmvc2.upload.controller;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.Part;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/servlet/v2")
public class ServletUploadControllerV2 {

	@Value("${file.dir}")
	private String fileDir;

	@GetMapping("/upload")
	public String newFile() {
		return "upload/upload-form";
	}

	@PostMapping("/upload")
	public String saveFile(HttpServletRequest request) throws IOException, ServletException {
		String itemName = request.getParameter("itemName");
		log.info("itemName={}", itemName);

		Collection<Part> parts = request.getParts();
		for (Part part : parts) {
			log.info("==== PART ====");
			log.info("name={}", part.getName());
			
			part.getHeaderNames().forEach(header -> log.info("[Header] {} : {}", header, part.getHeader(header)));
			log.info("submittedFileName={}", part.getSubmittedFileName());
			log.info("size={}", part.getSize()); // part body size

			// 파일 여부 확인
			String submittedFileName = part.getSubmittedFileName();
			if (StringUtils.hasText(submittedFileName)) {
				Path filePath = Paths.get(fileDir).resolve(submittedFileName);
				part.write(filePath.toString());
				log.info("파일 저장 fullPath={}", filePath.toAbsolutePath());
			} else {
				// 텍스트 필드 처리
				try (InputStream is = part.getInputStream()) {
					String body = StreamUtils.copyToString(is, StandardCharsets.UTF_8);
					log.info("body={}", body);
				}
			}
		}
		
		return "upload/upload-form";
	}
}
