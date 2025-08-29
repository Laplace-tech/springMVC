package hello.springmvc2.upload.controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.UriUtils;

import hello.springmvc2.domain.item.entry.Item;
import hello.springmvc2.domain.item.service.ItemService;
import hello.springmvc2.upload.file.FileStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor
public class FileController {

	private final FileStore fileStore;
	private final ItemService itemService;
	
	@ResponseBody
	@GetMapping("/files/{filename}")
	public ResponseEntity<Resource> downloadAttach(@PathVariable("filename") String filename) throws MalformedURLException {

	    // 서버 저장용 파일명을 기준으로 Resource 생성
	    UrlResource resource = new UrlResource("file:" + fileStore.getFullPath(filename));

	    if (!resource.exists() || !resource.isReadable()) {
	        // 파일이 없거나 읽을 수 없으면 404 반환
	        return ResponseEntity.notFound().build();
	    }

	    // 업로드 시 원본 이름 찾기
	    Optional<Item> optionalItem = itemService.findAllItems().stream()
	            .filter(item -> item.getAttachFile() != null)
	            .filter(item -> filename.equals(item.getAttachFile().getStoreFileName()))
	            .findFirst();

	    String uploadFileName = optionalItem
	            .map(item -> item.getAttachFile().getUploadFileName())
	            .orElse("unknown");

	    // 한글 이름 안전하게 인코딩
	    String encodedUploadFileName = UriUtils.encode(uploadFileName, StandardCharsets.UTF_8);
	    String contentDisposition = "attachment; filename=\"" + encodedUploadFileName + "\"";

	    return ResponseEntity.ok()
	            .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
	            .body(resource);
	}



}
