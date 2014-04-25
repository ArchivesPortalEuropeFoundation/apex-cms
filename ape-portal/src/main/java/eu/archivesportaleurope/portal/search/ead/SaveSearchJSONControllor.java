package eu.archivesportaleurope.portal.search.ead;

import java.util.List;

import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.bind.annotation.ResourceMapping;

import eu.archivesportaleurope.portal.common.SpringResourceBundleSource;
import eu.archivesportaleurope.portal.common.al.AlType;
import eu.archivesportaleurope.portal.common.tree.AbstractJSONWriter;
import eu.archivesportaleurope.portal.search.saved.SavedSearchController;
import eu.archivesportaleurope.portal.search.saved.SavedSearchService;

@Controller(value = "saveSearchJSONControllor")
@RequestMapping(value = "VIEW")
public class SaveSearchJSONControllor extends AbstractJSONWriter {
	private static final int MAX_NUMBER_OF_AI = 20;
	private final static Logger LOGGER = Logger.getLogger(SavedSearchController.class);
	private SavedSearchService savedSearchService;

	public void setSavedSearchService(SavedSearchService savedSearchService) {
		this.savedSearchService = savedSearchService;
	}

	@ResourceMapping(value = "saveSearch")
	public void saveSearch(@ModelAttribute(value = "eadSearch") EadSearch eadSearch,ResourceRequest resourceRequest, ResourceResponse resourceResponse) {
		SpringResourceBundleSource source = new SpringResourceBundleSource(this.getMessageSource(),
				resourceRequest.getLocale());
		String answerMessage = "";
		boolean saved = false;
		if (resourceRequest.getUserPrincipal() == null){
			answerMessage = source.getString("advancedsearch.text.savesearch.guest");
		}else {
			boolean faHgSgFound = false;
			int numberOfArchivalInstitions = 0;
			List<String> selectedNodesList = eadSearch.getSelectedNodesList();
			if (selectedNodesList != null){
				for (int i = 0; i < selectedNodesList.size() && !faHgSgFound && numberOfArchivalInstitions < MAX_NUMBER_OF_AI; i++){
					String selectedNode = selectedNodesList.get(i);
					AlType alType = AlType.getAlType(selectedNode);
					if (AlType.ARCHIVAL_INSTITUTION.equals(alType)){
						numberOfArchivalInstitions++;
					}else if (!AlType.COUNTRY.equals(alType)){
						faHgSgFound = true;
					}
				}
			}
			List<String> aiList = eadSearch.getAiList();
			int refinementAiSize = 0;
			if (aiList != null){
				refinementAiSize = aiList.size();
			}
			if (faHgSgFound){
				answerMessage = source.getString("advancedsearch.text.savesearch.fahgsg");
			}else if (numberOfArchivalInstitions >= MAX_NUMBER_OF_AI || refinementAiSize >= MAX_NUMBER_OF_AI ){
				answerMessage = this.getMessageSource().getMessage("advancedsearch.text.savesearch.maxai", new String[]{MAX_NUMBER_OF_AI+""}, resourceRequest.getLocale());
			}else {
				long liferayUserId = Long.parseLong(resourceRequest.getUserPrincipal().toString());
				try {
					savedSearchService.saveSearch(liferayUserId, eadSearch);
					answerMessage = source.getString("advancedsearch.text.savesearch.success");
					saved = true;
				}catch (Exception e) {
					LOGGER.error(e.getMessage());
					answerMessage = source.getString("advancedsearch.text.savesearch.error");
					saved = false;
				}

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
		LOGGER.debug("Context search time: " + (System.currentTimeMillis() - startTime));
	}
}
