package hello.springmvc2.upload.controller;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriUtils;

import hello.springmvc2.domain.item.service.ItemService;
import hello.springmvc2.upload.file.FileStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 클래스 선언.
 * - 파일 다운로드와 이미지 미리보기 요청 처리
 * - @RestController 라서 메서드 반환 값은 HTTP 응답 바디로 바로 전달됨.
 * - @RequiredArgsConstruct : fileStore와 itemService가 생성자를 통해 바로 주입됨
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class FileController {

    private final FileStore fileStore;
    private final ItemService itemService;

    /**
     * 단일 첨부파일 다운로드
     * - [REQUEST] [/files/attach/50e978b9-7c09-40e3-ad67-f3093c73accf.jpg]
     */
    @GetMapping("/files/attach/{filename}")
    public ResponseEntity<Resource> downloadAttach(@PathVariable("filename") String storeFilename) {
        try {
        	/**
        	 * fileStore.getFullPath(filename) : 서버 파일 경로 전체를 반환 
        	 * 	-> C:/Users/CKIRUser/Pictures/01b4e228-2551-4f21-a596-1861e70bc190.jpg
        	 * "file:" + ... : 파일 시스템 경로를 URL 형태로 변환
        	 * UrlResource로 실제 파일 리소스를 만듦
        	 */
        	UrlResource resource = new UrlResource("file:" + fileStore.getFullPath(storeFilename));
           
        	// 파일 존재 여부 확인 - 존재하지 않거나 읽을 수 없으면, 404 Not-Found
        	if (!resource.exists() || !resource.isReadable()) {
                return ResponseEntity.notFound().build(); 
            }

            /**
             * 1. DB에서 첨부파일 원본 이름을 조회 : findOriginalUploadFilename();
             * - 서버에 저장된 파일 이름은 UUID로 바뀌었지만, 다운로드할 때는 원본 이름으로 제공
             * 
             * 2. 파일 이름을 URL-safe하게 UTF-8로 인코딩
             * - 공백, 한글, 특수문자 등 문제 없이 다운로드 가능하게 함
             */
            String originalUploadName = itemService.findOriginalUploadFilename(storeFilename);
            String encodedName = UriUtils.encode(originalUploadName, StandardCharsets.UTF_8);

            /**
             * HTTP 상태 200 OK
             * Header : Content-Disposition: attachment; filename="...encoded..."
             * Body : 실제 파일 내용 전송
             */
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + encodedName + "\"")
                    .body(resource);

        } catch (Exception e) {
            log.warn("[File download error] filename={}, message={}", storeFilename, e.getMessage());
            return ResponseEntity.status(500).body(null);
        }
    }

    /**
     * 다중 이미지 미리보기
     * - [REQUEST] [/files/images/8ceb52c2-bb81-490b-b6aa-368a9e0d814c.jpg]
     */
    @GetMapping("/files/images/{filename}")
    public ResponseEntity<Resource> serveImage(@PathVariable("filename") String filename) {
        try {
            UrlResource resource = new UrlResource("file:" + fileStore.getFullPath(filename));
            if (!resource.exists() || !resource.isReadable()) {
                return ResponseEntity.notFound().build();
            }

            /**
             * 파일 MIME 타입 추출
             * Files.probeContentType() -> 파일 확장자 기반으로 MIME 타입 추출 (image/jpeg, image/png)
             */
            String contentType = Files.probeContentType(resource.getFile().toPath());
            if (contentType == null) {
                contentType = "application/octet-stream"; // 기본값
            }

            /**
             * 상태 200 OK
             * Content-Disposition: inline : 브라우저가 다운로드하지 않고 바로 표시
             * Content-Type : image/jpeg 등 : 브라우저가 올바르게 렌더링
             * 파일 내용은 body(resource)
             */
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" +
                            UriUtils.encode(filename, StandardCharsets.UTF_8) + "\"")
                    .header(HttpHeaders.CONTENT_TYPE, contentType)
                    .body(resource);

        } catch (Exception e) {
            log.warn("[Image serve error] filename={}, message={}", filename, e.getMessage(), e);
            return ResponseEntity.status(500).body(null);
        }
    }

}
