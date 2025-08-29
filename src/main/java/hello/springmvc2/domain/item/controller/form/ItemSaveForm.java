package hello.springmvc2.domain.item.controller.form;

import java.util.List;

import org.hibernate.validator.constraints.Range;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ItemSaveForm {
	
	@NotBlank(message = "{item.itemName.notBlank}")
	private String itemName;
	
	@NotNull(message = "{item.price.notNull}")
	@Range(min = 100, max = 1_000_000, message = "{item.price.range}")
	private Integer price;
	
	@NotNull(message = "{item.quantity.notNull}")
	@Range(min = 1, max = 9999, message = "{item.quantity.range}")
	private Integer quantity;
	
	private MultipartFile attachFile;
	
	private List<MultipartFile> imageFiles;
	
}
