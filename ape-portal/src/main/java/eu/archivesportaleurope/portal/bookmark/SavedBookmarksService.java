package eu.archivesportaleurope.portal.bookmark;

import java.sql.Date;
import org.apache.log4j.Logger;
import org.springframework.context.support.ResourceBundleMessageSource;

import eu.apenet.persistence.dao.SavedBookmarksDAO;
import eu.apenet.persistence.vo.SavedBookmarks;

public class SavedBookmarksService {
	private final static Logger LOGGER = Logger.getLogger(SavedBookmarksService.class);
	private SavedBookmarksDAO savedBookmarksDAO;

	public void setEadSavedBookmarkDAO(SavedBookmarksDAO savedBookmarksDAO) {
		this.savedBookmarksDAO = savedBookmarksDAO;
	}

	public void setMessageSource(ResourceBundleMessageSource messageSource) {
	}

	public void saveBookmark(Long liferayUserId, SavedBookmarksObject savedBookmarksObject) {
		try {
			SavedBookmarks savedbookmark = new SavedBookmarks();
			savedbookmark.setLiferayUserId(liferayUserId);
			savedbookmark.setModifiedDate(new Date(liferayUserId));
			savedbookmark.setLink(savedBookmarksObject.getPersistentLink());
			savedbookmark.setName(savedBookmarksObject.getBookmarkName());
			savedbookmark.setTypedocument(savedBookmarksObject.getTypedocument());
			savedBookmarksDAO.store(savedbookmark);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
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
			savedBookmarksDAO.store(savedbookmark);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
	}

	public SavedBookmarks getSavedBookmarks(Long liferayUserId, Long savedBookmarkId) {
//		return SavedBookmarks.getSavedBookmarks(savedBookamrkId, liferayUserId);
		return null;

	}


}
