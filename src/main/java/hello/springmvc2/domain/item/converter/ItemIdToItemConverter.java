package hello.springmvc2.domain.item.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import hello.springmvc2.domain.item.entry.Item;
import hello.springmvc2.domain.item.service.ItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ItemIdToItemConverter implements Converter<String, Item> {

    private final ItemService itemService;

    @Override
    public Item convert(String itemId) {
        try {
            Long id = Long.valueOf(itemId);
            Item item = itemService.findItemById(id);

            if (item == null) {
                log.warn("ItemIdToItemConverter: item not found for id={}", id);
            } else {
                log.info("ItemIdToItemConverter: 변환 완료 - itemId={}, itemName={}", id, item.getItemName());
            }

            return item;

        } catch (NumberFormatException ex) {
            log.error("ItemIdToItemConverter: 잘못된 ID 형식 - {}", itemId, ex);
            return null;
        }
    }
}
