package eu.archivesportaleurope.portal.bookmark;

import java.sql.Date;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.bind.annotation.ResourceMapping;

import eu.apenet.persistence.dao.EadSavedSearchDAO;
import eu.apenet.persistence.vo.SavedBookmarks;
import eu.archivesportaleurope.persistence.jpa.dao.SavedBookmarksJpaDAO;
import eu.archivesportaleurope.util.ApeUtil;

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

	@ResourceMapping(value="saveBookmark")
	public void saveBookmark(Long liferayUserId, Bookmark bookmarkObject) {
		try {
			SavedBookmarks savedbookmark = new SavedBookmarks();
			savedbookmark.setLiferayUserId(liferayUserId);
			savedbookmark.setModifiedDate(new Date(System.currentTimeMillis()));
			savedbookmark.setLink(bookmarkObject.getPersistentLink());
			savedbookmark.setName(bookmarkObject.getBookmarkName());
			savedbookmark.setTypedocument(bookmarkObject.getTypedocument());
			savedBookmarksDAO.store(savedbookmark);
		} catch (Exception e) {
			LOGGER.error(ApeUtil.generateThrowableLog(e));
		}
	}
	@ResourceMapping(value="editBookmark")
	public void editBookmark(Long liferayUserId, String description, Long id, String name, String link, String typedocument) {
		try {
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
		} catch (Exception e) {
			LOGGER.error(ApeUtil.generateThrowableLog(e));
		}
	}
}
