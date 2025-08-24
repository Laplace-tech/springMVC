package hello.springmvc2.domain.item.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import hello.springmvc2.domain.item.entry.Item;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ItemToStringConverter implements Converter<Item, String> {

    @Override
    public String convert(Item item) {
        if (item == null) return "";
        log.info("ItemToStringConverter 호출: {}", item.getId());
        return String.format("%s (₩%,d원)", item.getItemName(), item.getPrice());
    }
}

