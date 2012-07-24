package chapter08.code.listing.controller;

import javax.portlet.ActionResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.portlet.bind.annotation.ActionMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;

import chapter08.code.listing.domain.Book;
import chapter08.code.listing.service.BookService;

/**
 * EditBookController shows the Edit Book form and does request processing of the
 * edit book action.
 * 
 * @author asarin
 *
 */
@Controller(value="editBookController")
@RequestMapping(value="VIEW")
public class EditBookController {
	@Autowired
	@Qualifier("myBookService")
	private BookService bookService;
	@Autowired
	private MyValidator myValidator;
	
	@RenderMapping(params="myaction=editBookForm")
	public String showEditBookForm() {
		return "editBookForm";
	}

	@ActionMapping(params="myaction=editBook")
	public void editBook(@ModelAttribute("book") Book book, BindingResult bindingResult, ActionResponse response)  {
		myValidator.validate(book, bindingResult);
		if (!bindingResult.hasErrors()) {
			bookService.editBook(book);
			response.setRenderParameter("myaction", "books");
		} else {
			//--this is required. the getBook method is not invoked but the @RequestParam
			//--is still evaluated
			response.setRenderParameter("isbnNumber", book.getIsbnNumber().toString());
			response.setRenderParameter("myaction", "editBookForm");
		}
	}
	
	@ModelAttribute("book")
	public Book getBook(@RequestParam String isbnNumber) {
		return bookService.getBook(isbnNumber);
	}
	
	@ExceptionHandler({ Exception.class })
	public String handleException() {
		return "errorPage";
	}
}