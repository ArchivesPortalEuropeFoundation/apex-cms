package eu.archivesportaleurope.portal.bookmark;

import java.sql.Date;

import org.apache.log4j.Logger;
import org.springframework.context.support.ResourceBundleMessageSource;

import eu.apenet.persistence.vo.SavedBookmarks;
import eu.archivesportaleurope.persistence.jpa.dao.SavedBookmarksJpaDAO;
import eu.archivesportaleurope.util.ApeUtil;

public class BookmarkService {

	public BookmarkService(SavedBookmarksJpaDAO savedBookmarksDAO) {
		if (this.savedBookmarksDAO == null) {
			this.savedBookmarksDAO = savedBookmarksDAO;
		}
	}
	
	private final static Logger LOGGER = Logger.getLogger(BookmarkService.class);
	private SavedBookmarksJpaDAO savedBookmarksDAO;

	public void setEadSavedBookmarkDAO(SavedBookmarksJpaDAO savedBookmarksDAO) {
		this.savedBookmarksDAO = savedBookmarksDAO;
	}

	public void setMessageSource(ResourceBundleMessageSource messageSource) {
	}

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

	public SavedBookmarks getSavedBookmark(Long liferayUserId, int savedBookmarkId) {
		return null;
	}
	


}
