package hello.springmvc2.domain.item.service;

import java.util.List;
import java.util.function.Supplier;

import org.springframework.stereotype.Service;

import hello.springmvc2.domain.item.controller.form.ItemSaveForm;
import hello.springmvc2.domain.item.controller.form.ItemUpdateForm;
import hello.springmvc2.domain.item.controller.mapper.ItemMapper;
import hello.springmvc2.domain.item.entry.Item;
import hello.springmvc2.domain.item.exception.ItemNotFoundException;
import hello.springmvc2.domain.item.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemService {

	private final ItemRepository itemRepository;

	public Item findItemById(Long id) {
		return itemRepository.findById(id)
				.orElseThrow(() -> new ItemNotFoundException("아이템이 존재하지 않습니다. id : " + id));
	}

	public List<Item> findAllItems() {
		return itemRepository.findAll();
	}
	
	public Item saveItem(ItemSaveForm form) {
		Item item = ItemMapper.toEntity(form); // id 필드는 Repository
		return itemRepository.save(item);
	}

	public void updateItem(Long id, ItemUpdateForm form) {
		validateIdMatch(id, form::getId);
		Item updatedItem = ItemMapper.toEntity(form);
		
		boolean updated = itemRepository.update(updatedItem);
		if(!updated) {
			throw new ItemNotFoundException("수정할 아이템이 존재하지 않습니다. id : " + id);
		}
	}

	public void deleteItem(Long id) {
		boolean deleted = itemRepository.delete(id);
		if(!deleted) {
			throw new ItemNotFoundException("삭제할 아이템이 존재하지 않습니다. id : " + id);
		}
	}
	
	private void validateIdMatch(Long pathId, Supplier<Long> formIdSupplier) {
		if(!pathId.equals(formIdSupplier.get())) {
            throw new IllegalArgumentException("Path variable id와 form의 id가 일치하지 않습니다. pathId: " + pathId + ", formId: " + formIdSupplier.get());
		}
	}
	
	
}