package hello.springmvc2.domain.item.validator;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import hello.springmvc2.domain.item.controller.form.ItemSaveForm;
import hello.springmvc2.domain.item.controller.form.ItemUpdateForm;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ItemValidator implements Validator {

	private static final int MIN_TOTAL_PRICE = 10_000;

	@Override
	public boolean supports(Class<?> clazz) {
		return ItemSaveForm.class.isAssignableFrom(clazz) ||
				ItemUpdateForm.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		Integer price = null;
		Integer quantity = null;
		
		if(target instanceof ItemSaveForm saveForm) {
			price = saveForm.getPrice();
			quantity = saveForm.getQuantity();
		} else if (target instanceof ItemUpdateForm updateForm) {
			price = updateForm.getPrice();
			quantity = updateForm.getQuantity();
		} else {
            log.warn("지원하지 않는 폼 타입으로 검증 시도: {}", target.getClass());
            return;
        }
		
		 if (price != null && quantity != null) {
	            int totalPrice = price * quantity;
	            if (totalPrice < MIN_TOTAL_PRICE) {
	                errors.reject("totalPriceMin", new Object[] {MIN_TOTAL_PRICE, totalPrice},
	                        String.format("총 가격은 최소 %d원 이상이어야 합니다. 현재 : %d", MIN_TOTAL_PRICE, totalPrice));
	            }
	        }
	}
	
	
}
  