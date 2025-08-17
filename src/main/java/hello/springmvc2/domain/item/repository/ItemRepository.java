package hello.springmvc2.domain.item.repository;

import java.util.List;
import java.util.Optional;

import hello.springmvc2.domain.item.entry.Item;

public interface ItemRepository {

	public Item save(Item item);
	public Optional<Item> findById(Long id);
	public List<Item> findAll();
	public boolean update(Item updatedItem);
	public boolean delete(Long id);
	
}
