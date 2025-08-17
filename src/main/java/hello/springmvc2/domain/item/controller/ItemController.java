package hello.springmvc2.domain.item.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import hello.springmvc2.domain.item.controller.form.ItemSaveForm;
import hello.springmvc2.domain.item.controller.form.ItemUpdateForm;
import hello.springmvc2.domain.item.controller.mapper.ItemMapper;
import hello.springmvc2.domain.item.entry.Item;
import hello.springmvc2.domain.item.service.ItemService;
import hello.springmvc2.domain.item.validator.ItemValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {

	private final ItemService itemService;
	private final ItemValidator itemValidator;
	
	@InitBinder("form")
	public void initItemUpdateFormValidator(WebDataBinder binder) {
		binder.addValidators(itemValidator);
	}
	
	@GetMapping
	public String listItems(Model model) {
		List<Item> items = itemService.findAllItems();
		model.addAttribute("items", items);
		return "items/list"; // templates/items/list.html
	}
	
	@GetMapping("/{itemId}")
	public String getItem(@PathVariable("itemId") Long itemId, Model model) {
		Item item = itemService.findItemById(itemId);
		model.addAttribute("item", item);
		return "items/detail"; // templates/items/detail.html
	}
	
	@GetMapping("/add")
	public String addForm(@ModelAttribute("form") ItemSaveForm form) {
		return "items/addForm";
	}
	
	
	@PostMapping("/add")
	public String saveItem(@Validated @ModelAttribute("form") ItemSaveForm form, 
						   BindingResult bindingResult,
						   RedirectAttributes redirectAttributes) {
		
		// 개별 Validator에서 validateTotalPrice도 수행함
		if(bindingResult.hasErrors()) {
			log.warn("상품 등록 유효성 실패 : {}", bindingResult);
			return "items/addForm";
		}
		
		Item savedItem = itemService.saveItem(form);
		redirectAttributes.addAttribute("itemId", savedItem.getId());
		redirectAttributes.addAttribute("status", true);
		return "redirect:/items/{itemId}";
	}
	
	@GetMapping("/{itemId}/edit")
	public String editForm(@PathVariable("itemId") Long itemId, Model model) {
		Item item = itemService.findItemById(itemId);
		ItemUpdateForm form = ItemMapper.toForm(item);
		model.addAttribute("form", form);
		return "items/editForm";
	}
	
    @PostMapping("/{itemId}/edit")
    public String updateItem(@PathVariable("itemId") Long itemId,
                             @Validated @ModelAttribute("form") ItemUpdateForm form,
                             BindingResult bindingResult) {
    	
    	// 개별 Validator에서 validateTotalPrice도 수행함
        if (bindingResult.hasErrors()) {
            log.warn("상품 수정 유효성 실패: {}", bindingResult);
            return "items/editForm";
        }
        
        itemService.updateItem(itemId, form);
        return "redirect:/items/{itemId}";
    }
    
    @PostMapping("/{itemId}/delete")
    public String deleteItem(@PathVariable("itemId") Long itemId) {
    	itemService.deleteItem(itemId);
    	return "redirect:/items";
    }
    
    
	
}