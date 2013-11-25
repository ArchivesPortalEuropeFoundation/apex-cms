package eu.archivesportaleurope.portal.search.advanced;

import java.util.Date;

import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.bind.annotation.ResourceMapping;

import eu.apenet.persistence.dao.EadSavedSearchDAO;
import eu.apenet.persistence.vo.EadSavedSearch;
import eu.archivesportaleurope.portal.common.SpringResourceBundleSource;
import eu.archivesportaleurope.portal.common.tree.AbstractJSONWriter;
import eu.archivesportaleurope.portal.search.saved.SavedSearchController;

@Controller(value = "saveSearchJSONControllor")
@RequestMapping(value = "VIEW")
public class SaveSearchJSONControllor extends AbstractJSONWriter {
	private final static Logger LOGGER = Logger.getLogger(SavedSearchController.class);
	private EadSavedSearchDAO eadSavedSearchDAO;
	
	
	public void setEadSavedSearchDAO(EadSavedSearchDAO eadSavedSearchDAO) {
		this.eadSavedSearchDAO = eadSavedSearchDAO;
	}


	@ResourceMapping(value = "saveSearch")
	public void saveSearch(@ModelAttribute(value = "advancedSearch") AdvancedSearch advancedSearch,ResourceRequest resourceRequest, ResourceResponse resourceResponse) {
		SpringResourceBundleSource source = new SpringResourceBundleSource(this.getMessageSource(),
				resourceRequest.getLocale());
		String answerMessage = "";
		boolean saved = false;
		LOGGER.info("saveSearch");
		if (resourceRequest.getUserPrincipal() == null){
			answerMessage = source.getString("advancedsearch.text.savesearch.guest");
		}else {
			EadSavedSearch eadSavedSearch = new EadSavedSearch();
			eadSavedSearch.setLiferayUserId(Long.parseLong(resourceRequest.getUserPrincipal().toString()));
			eadSavedSearch.setTerm(advancedSearch.getTerm());
			eadSavedSearch.setModifiedDate(new Date());
			eadSavedSearchDAO.store(eadSavedSearch);
			answerMessage = source.getString("advancedsearch.text.savesearch.success");
			saved = true;
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
		LOGGER.debug("Context search time: " + (System.currentTimeMillis() - startTime));
	}
}
