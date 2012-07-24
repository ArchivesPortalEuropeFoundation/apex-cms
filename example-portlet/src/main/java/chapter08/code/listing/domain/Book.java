package chapter08.code.listing.domain;

/**
 * Book Domain object which defines properties of a book in the catalog.
 * 
 * @author asarin
 */
public class Book {
	private String name;
	private String author;
	private String isbnNumber;
	
	public Book(String name, String author, String isbnNumber) {
		this.name = name;
		this.author = author;
		this.isbnNumber = isbnNumber;
	}
	public Book() {
	}
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getIsbnNumber() {
		return isbnNumber;
	}

	public void setIsbnNumber(String isbnNumber) {
		this.isbnNumber = isbnNumber;
	}
	
	@Override
	public boolean equals(Object otherObject) {
		Book otherBook = (Book)otherObject;
		if(otherBook.getIsbnNumber().equals(this.isbnNumber)) {
			return true;
		} else {
			return false;
		}
	}
}
