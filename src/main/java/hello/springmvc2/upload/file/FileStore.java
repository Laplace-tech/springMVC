package hello.springmvc2.upload.file;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import hello.springmvc2.upload.domain.UploadFile;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Component
public class FileStore {

	@Value("${file.dir}")
	private String fileDir;

	public String getFullPath(String filename) {
		return fileDir + filename;
	}
	
	/**
	 * 여러 파일 저장
	 */
	public List<UploadFile> storeFiles(List<MultipartFile> multipartFiles) {
		return multipartFiles.stream()
				.filter(file -> !file.isEmpty())
				.map(file -> {
					try {
						return storeFile(file);
					} catch (IOException e) {
						throw new RuntimeException("파일 저장 실패: " + file.getOriginalFilename(), e);
					}
				})
				.collect(Collectors.toList());
	}
	
	/**
	 * 단일 파일 저장
	 */
	public UploadFile storeFile(MultipartFile multipartFile) throws IllegalStateException, IOException {
		if(multipartFile.isEmpty()) {
			return null;
		}
	
		String originalFilename = StringUtils.cleanPath(multipartFile.getOriginalFilename());
		String storeFilename = createStoreFilename(originalFilename);
		
		Path fullPath = Paths.get(fileDir, storeFilename);
		multipartFile.transferTo(fullPath.toFile());
		
		return new UploadFile(originalFilename, storeFilename);
	}
	
    /**
     * 서버 저장용 파일명 생성 (UUID + 확장자)
     */
	private String createStoreFilename(String originalFilename) {
		String ext = extractExt(originalFilename);
		String uuid = UUID.randomUUID().toString();
		return uuid + "." + ext;
	}
	
    /**
     * 확장자 추출
     */
	private String extractExt(String originalFilename) {
		int pos = originalFilename.lastIndexOf(".");
		return originalFilename.substring(pos + 1);
	}
	
}
