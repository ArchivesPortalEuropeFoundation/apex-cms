package eu.archivesportaleurope.portal.tagcloud;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.portlet.RenderRequest;

import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.FacetField.Count;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;

import eu.archivesportaleurope.portal.search.common.FacetType;
import eu.archivesportaleurope.portal.search.common.SolrQueryParameters;
import eu.archivesportaleurope.portal.search.ead.EadSearcher;
import eu.archivesportaleurope.portal.search.ead.list.ListFacetSettings;

@Controller(value = "tagCloudController")
@RequestMapping(value = "VIEW")
public class TagCloudController {

	private static final int NUMBER_OF_GROUPS = 5;
	private final static Logger LOGGER = Logger.getLogger(TagCloudController.class);
	private final static int MAX_NUMBER_OF_TAGS = 15;

	private ResourceBundleMessageSource messageSource;
	private EadSearcher eadSearcher;

	public void setEadSearcher(EadSearcher eadSearcher) {
		this.eadSearcher = eadSearcher;
	}

	public void setMessageSource(ResourceBundleMessageSource messageSource) {
		this.messageSource = messageSource;
	}

	// --maps the incoming portlet request to this method
	@RenderMapping
	public String showTagCloud(RenderRequest request) {
		List<TagCloudItem> tags = new ArrayList<TagCloudItem>();
		SolrQueryParameters solrQueryParameters = new SolrQueryParameters();
		solrQueryParameters.setTerm("*");
		List<ListFacetSettings> facetSettings = new ArrayList<ListFacetSettings>();
		facetSettings.add(new ListFacetSettings(FacetType.TOPIC, true, null, MAX_NUMBER_OF_TAGS));

		try {
			QueryResponse response = eadSearcher.performNewSearchForListView(solrQueryParameters, 0, facetSettings);
			FacetField facetField = response.getFacetFields().get(0);
			for (Count count : facetField.getValues()) {
				tags.add(new TagCloudItem(count.getCount(), count.getName(), messageSource.getMessage(
						"topic." + count.getName(), null, request.getLocale())));
			}
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
		int numberOfTagsWithResults = tags.size();
		String preferences = "first.world.war=First World War;second.world.war=Second World War;";
		preferences += "middle.ages=Middle Ages;cold.war=Cold War;slavery=Slavery;";
		preferences += "genealogy=Genealogy;church.records=Church records;";
		preferences += "state.collection=State Collection;gdr=German Democratic Republic;";
		preferences += "politics=Politics;democracy=Democracy;communism=Communism;";
		preferences += "industry=Industry;transport=Transport;thirty.years.war=30-years war;";
		String[] items = preferences.split(";");
		for (int i = 0; i < items.length && tags.size() < MAX_NUMBER_OF_TAGS; i++) {
			String[] keyValue = items[i].split("=");
			String description = messageSource.getMessage(keyValue[0], null, keyValue[1], request.getLocale());
			TagCloudItem tagCloudItem = new TagCloudItem(0l, keyValue[0], description);
			if (!tags.contains(tagCloudItem)) {
				tags.add(tagCloudItem);
			}
		}
		int[] groups = numberPerGroup(numberOfTagsWithResults, tags.size() - numberOfTagsWithResults);
		int tagCloudItemIndex = 0;
		for (int i = 0; i < NUMBER_OF_GROUPS; i++) {
			int maxNumber = groups[i];
			for (int j = 0; j < maxNumber; j++) {
				tags.get(tagCloudItemIndex).setTagNumber((i + 1));
				tagCloudItemIndex++;
			}
		}
		Collections.sort(tags, new TagCloudComparator());
		request.setAttribute("url", "/search/-/s/n/topic/");
		request.setAttribute("tags", tags);
		return "index";
	}

	public static int[] numberPerGroup(int numberOfItemsWithResults, int numberOfItemsWithoutResults) {
		int numberOfGroups = NUMBER_OF_GROUPS;
		int numberOfItems = numberOfItemsWithResults + numberOfItemsWithoutResults;
		int[] groups = new int[numberOfGroups];
		int extra = numberOfItems % numberOfGroups;
		int minimalNumber = numberOfItems / numberOfGroups;
		for (int i = 0; i < groups.length; i++) {
			if (extra > 0) {
				groups[i] = minimalNumber + 1;
			} else {
				groups[i] = minimalNumber;
			}
			extra--;
		}
		if (numberOfItemsWithResults > 0 && numberOfItemsWithoutResults > 0) {
			int value = groups[0] + groups[1];
			boolean first = true;
			if (numberOfItemsWithResults <= value && value > 2) {
				while (numberOfItemsWithResults <= value && value > 2) {
					if (first && groups[0] > 1) {
						groups[0] = groups[0] - 1;
						groups[4] = groups[4] + 1;
					} else if (!first && groups[1] > 1) {
						groups[1] = groups[1] - 1;
						groups[3] = groups[3] + 1;
					}
					first = !first;
					value = groups[0] + groups[1];
				}
			}

		}
		return groups;
	}

	private static class TagCloudComparator implements Comparator<TagCloudItem> {

		@Override
		public int compare(TagCloudItem o1, TagCloudItem o2) {
			return o1.getName().compareTo(o2.getName());
		}

	}
}
