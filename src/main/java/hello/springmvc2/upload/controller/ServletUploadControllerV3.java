package hello.springmvc2.upload.controller;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/spring")
public class ServletUploadControllerV3 {

	@Value("${file.dir}")
	private String fileDir;
	
	@GetMapping("/upload")
	public String newFile() {
		return "upload/upload-form";
	}
	
	@PostMapping("/upload")
	public String saveFile(@RequestParam("itemName") String itemName,
						   @RequestParam("file") MultipartFile file) throws IllegalStateException, IOException {
		
		String fileName = file.getOriginalFilename();
		log.info("itemName={}", itemName);
		log.info("multipartFile name={}, size={}", fileName, file.getSize());
		
	    if (!file.isEmpty()) {
	        Path filePath = Paths.get(fileDir).resolve(fileName);
	        log.info("파일 저장 fullPath={}", filePath.toAbsolutePath());
	        file.transferTo(filePath.toFile());
	    } else {
	        log.info("업로드된 파일이 없습니다.");
	    }
	    
	    return "upload/upload-form";
	}
	
}
