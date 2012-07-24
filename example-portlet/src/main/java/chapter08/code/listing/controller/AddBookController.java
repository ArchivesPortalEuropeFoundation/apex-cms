package chapter08.code.listing.controller;

import javax.portlet.ActionResponse;
import javax.portlet.RenderResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.bind.annotation.ActionMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;

import chapter08.code.listing.domain.Book;
import chapter08.code.listing.service.BookService;

/**
 * AddBookController shows the add book form and handles requests for adding a
 * book to the catalog.
 * 
 * @author asarin
 * 
 */
@Controller(value = "addBookController")
@RequestMapping(value = "VIEW")
public class AddBookController {
	@Autowired
	@Qualifier("myBookService")
	private BookService bookService;

	@Autowired
	private MyValidator myValidator;
	
	public void setBookService(BookService bookService) {
		this.bookService = bookService;
	}

	@ModelAttribute("book")
	public Book getCommandObject() {
		return new Book();
	}

	@RenderMapping(params = "myaction=addBookForm")
	public String showAddBookForm(RenderResponse response) {
		return "addBookForm";
	}

	@ExceptionHandler({ Exception.class })
	public String handleException() {
		return "errorPage";
	}

	@ActionMapping(params = "myaction=addBook")
	public void addBook( @ModelAttribute(value = "book") Book book,
			BindingResult bindingResult, ActionResponse response) {
		myValidator.validate(book, bindingResult);
		if (!bindingResult.hasErrors()) {
			bookService.addBook(book);
			response.setRenderParameter("myaction", "books");
			// --set the session status as complete to cleanup the model
			// attributes
			// --stored using @SessionAttributes, otherwise when you click
			// --'Add Book' button you'll see the book information pre-populated
			// -- because the getCommandObject method of the controller is not
			// --invoked
			//sessionStatus.setComplete();
		} else {
			response.setRenderParameter("myaction", "addBookForm");
		}
	}
}