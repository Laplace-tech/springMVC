package hello.springmvc2.domain.item.controller.mapper;

import hello.springmvc2.domain.item.controller.form.ItemSaveForm;
import hello.springmvc2.domain.item.controller.form.ItemUpdateForm;
import hello.springmvc2.domain.item.entry.Item;

public class ItemMapper {

	public static Item toEntity(ItemSaveForm form) {
		return Item.builder()
				.itemName(form.getItemName())
				.price(form.getPrice())
				.quantity(form.getQuantity())
				.build();
	}

	public static Item toEntity(ItemUpdateForm form) {
		return Item.builder()
				.id(form.getId())
				.itemName(form.getItemName())
				.price(form.getPrice())
				.quantity(form.getQuantity())
				.build();
	}
	
	public static ItemUpdateForm toForm(Item item) {
		return ItemUpdateForm.builder()
				.id(item.getId())
				.itemName(item.getItemName())
				.price(item.getPrice())
				.quantity(item.getQuantity())
				.build();
	}
	
}
