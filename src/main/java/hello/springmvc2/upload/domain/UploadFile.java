package hello.springmvc2.upload.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
@AllArgsConstructor
public class UploadFile {

	private final String uploadFileName;
	private final String storeFileName;
	
}
