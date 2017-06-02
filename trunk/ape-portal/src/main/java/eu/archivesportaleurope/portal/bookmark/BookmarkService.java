package eu.archivesportaleurope.portal.bookmark;

import java.sql.Date;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.bind.annotation.ResourceMapping;

import eu.apenet.persistence.vo.SavedBookmarks;
import eu.archivesportaleurope.persistence.jpa.dao.SavedBookmarksJpaDAO;
import eu.archivesportaleurope.portal.common.PortalDisplayUtil;
import eu.archivesportaleurope.util.ApeUtil;

/***
 * This is the Bookmark service class
 */
@Controller(value = "bookmarkService")
@RequestMapping(value = "VIEW")
public class BookmarkService {

	private final static Logger LOGGER = Logger.getLogger(BookmarkService.class);
	private SavedBookmarksJpaDAO savedBookmarksDAO;
	
	public void setSavedBookmarksDAO(SavedBookmarksJpaDAO savedBookmarksDAO) {
		this.savedBookmarksDAO = savedBookmarksDAO;
	}
	
	/**
	 * @return the savedBookmarksDAO
	 */
	public SavedBookmarksJpaDAO getSavedBookmarksDAO() {
		return savedBookmarksDAO;
	}

	@RequestMapping
	public void showDefault(){
		
	}

	/***
	 * This method saves a page into a bookmark in the second display
	 * 
	 * @param liferayUserId {@link Long} current user id
	 * @param bookmark {@link Bookmark} bookmark object
	 */
	@ResourceMapping(value="saveBookmark")
	public void saveBookmark(Long liferayUserId, Bookmark bookmarkObject) {
		try {
			if (LOGGER.isDebugEnabled()) 
				LOGGER.debug("Enter in method \"saveBookmark\"");
			
			SavedBookmarks savedbookmark = new SavedBookmarks();
			savedbookmark.setLiferayUserId(liferayUserId);
			savedbookmark.setModifiedDate(new Date(System.currentTimeMillis()));
			savedbookmark.setLink(bookmarkObject.getPersistentLink());
			savedbookmark.setName(PortalDisplayUtil.replaceHTMLSingleQuotes(bookmarkObject.getBookmarkName()));
			savedbookmark.setTypedocument(bookmarkObject.getTypedocument());
			SavedBookmarks bookmark = savedBookmarksDAO.store(savedbookmark);
			bookmarkObject.setId(Long.toString(bookmark.getId()));
			if (LOGGER.isDebugEnabled()) 
				LOGGER.debug("Exit in method \"saveBookmark\"");
			
		} catch (Exception e) {
			LOGGER.error(ApeUtil.generateThrowableLog(e));
		}
	}
	
	/***
	 * This function saves and edited bikmark
	 * 
	 * @param liferayUserId {@link Long} current user id
	 * @param description {@link String} bookmark description
	 * @param id {@link Long} bookmark id
	 * @param name {@link String} bookmarks name
	 * @param link {@link String} bookmark url link
	 * @param typedocument {@link String} type of the bookmark
	 */
	@ResourceMapping(value="editBookmark")
	public void editBookmark(Long liferayUserId, String description, Long id, String name, String link, String typedocument) {
		try {
			if (LOGGER.isDebugEnabled()) 
				LOGGER.debug("Enter in method \"editBookmark\"");
			
			SavedBookmarks savedbookmark = new SavedBookmarks();
			savedbookmark.setLiferayUserId(liferayUserId);
			savedbookmark.setModifiedDate(new Date(liferayUserId));
			savedbookmark.setDescription(description);
			savedbookmark.setId(id);
			savedbookmark.setLink(link);
			savedbookmark.setName(name);
			savedbookmark.setTypedocument(typedocument);
			savedbookmark.setLiferayUserId(liferayUserId);
			savedBookmarksDAO.store(savedbookmark);
			if (LOGGER.isDebugEnabled()) 
				LOGGER.debug("Exit in method \"editBookmark\"");
			
		} catch (Exception e) {
			LOGGER.error(ApeUtil.generateThrowableLog(e));
		}
	}
}
