package hello.springmvc2.domain.item.repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Repository;

import hello.springmvc2.domain.item.entry.Item;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
@RequiredArgsConstructor
public class MemoryItemRepository implements ItemRepository {

	private final Map<Long, Item> store = new ConcurrentHashMap<>();
	private final AtomicLong sequence = new AtomicLong(0L);

	@Override
	public Item save(Item item) {
		long id = sequence.incrementAndGet();
		item = setId(item, id);
		store.put(id, item);
		log.info("Item saved: {}", item);
		return item;
	}

	@Override
	public Optional<Item> findById(Long id) {
		return Optional.ofNullable(store.get(id));
	}

	@Override
	public List<Item> findAll() {
		return List.copyOf(store.values());
	}

	@Override
	public boolean update(Item updatedItem) {
		long id = updatedItem.getId();
		if (!store.containsKey(id)) {
			log.warn("Item update failed. id={} not found", id);
			return false;
		}
		store.put(id, updatedItem);
		log.info("Item updated: {}", updatedItem);
		return true;
	}

	@Override
	public boolean delete(Long id) {
		boolean removed = store.remove(id) != null;
		if (removed) {
			log.info("Item deleted: id={}", id);
		} else {
			log.warn("Item delete failed. id={} not found", id);
		}
		return removed;
	}

	private Item setId(Item item, long sequenceId) {
		return item.toBuilder()
				.id(sequenceId)
				.build();
	}

}
