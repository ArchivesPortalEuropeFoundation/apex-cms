package eu.archivesportaleurope.portal.bookmark;

import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.bind.annotation.ResourceMapping;

import eu.archivesportaleurope.portal.common.SpringResourceBundleSource;
import eu.archivesportaleurope.portal.common.tree.AbstractJSONWriter;

@Controller(value = "SaveBookmarkJSONController")
@RequestMapping(value = "VIEW")
public class SaveBookmarkJsSONController extends AbstractJSONWriter {
	private static final int MAX_NUMBER_OF_AI = 20;
	private final static Logger LOGGER = Logger.getLogger(SaveBookmarkJsSONController.class);
	private SavedBookmarksService savedBookmarksService;

	public void setSavedBookmarkService(SavedBookmarksService savedBookmarksService) {
		this.savedBookmarksService = savedBookmarksService;
	}

	@ResourceMapping(value = "saveBookmark")
	public void saveBookmark(@ModelAttribute(value = "eaddisplay") SavedBookmarksObject bookmark,ResourceRequest resourceRequest, ResourceResponse resourceResponse) {
		SpringResourceBundleSource source = new SpringResourceBundleSource(this.getMessageSource(),
				resourceRequest.getLocale());
		String answerMessage = "";
		boolean saved = false;
		if (resourceRequest.getUserPrincipal() == null){
			answerMessage = source.getString("advancedbookmark.text.savebookmark.guest");
		}else {
			long liferayUserId = Long.parseLong(resourceRequest.getUserPrincipal().toString());
			try {
				savedBookmarksService.saveBookmark(liferayUserId, bookmark);
				answerMessage = source.getString("advancedbookmark.text.savebookmark.success");
				saved = true;
			}catch (Exception e) {
				LOGGER.error(e.getMessage());
				answerMessage = source.getString("advancedbookmark.text.savebookmark.error");
				saved = false;
			}
		}
		long startTime = System.currentTimeMillis();
		try {

			StringBuilder builder = new StringBuilder();
			builder.append(START_ITEM);
			builder.append("\"answerMessage\": \"" + answerMessage + "\"");
			builder.append(COMMA);
			builder.append("\"answerCode\": \"" +  saved + "\"");
			builder.append(END_ITEM);
			writeToResponseAndClose(builder, resourceResponse);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
		LOGGER.debug("Context bookmark time: " + (System.currentTimeMillis() - startTime));
	}

	@ResourceMapping(value = "saveBookmark")
	public void saveBookmark(ResourceRequest resourceRequest, ResourceResponse resourceResponse) {
		String answerMessage = "test";
		boolean saved = false;
		long startTime = System.currentTimeMillis();
		try {

			StringBuilder builder = new StringBuilder();
			builder.append(START_ITEM);
			builder.append("\"answerMessage\": \"" + answerMessage + "\"");
			builder.append(COMMA);
			builder.append("\"answerCode\": \"" +  saved + "\"");
			builder.append(END_ITEM);
			writeToResponseAndClose(builder, resourceResponse);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
		LOGGER.debug("Context bookmark time: " + (System.currentTimeMillis() - startTime));
	}
}
