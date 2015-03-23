package eu.archivesportaleurope.portal.tagcloud;

import java.text.NumberFormat;
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

import eu.apenet.commons.utils.Cache;
import eu.apenet.commons.utils.CacheManager;
import eu.apenet.persistence.dao.TopicDAO;
import eu.archivesportaleurope.portal.common.PortalDisplayUtil;
import eu.archivesportaleurope.portal.common.SpringResourceBundleSource;
import eu.archivesportaleurope.portal.search.common.FacetType;
import eu.archivesportaleurope.portal.search.common.SolrQueryParameters;
import eu.archivesportaleurope.portal.search.ead.EadSearcher;
import eu.archivesportaleurope.portal.search.ead.list.ListFacetSettings;

@Controller(value = "tagCloudController")
@RequestMapping(value = "VIEW")
public class TagCloudController {
	private static final int MAX_ALL_TOPICS = 300;
	private static final String ALL_ACTIVE_TOPICS = "all-active-topics";
	private static final String TAGS_KEY = "tags";
	private static final String TAGS_LEFT_KEY = "tagsLeft";
	private static final String TAGS_RIGHT_KEY = "tagsRight";
	private static final String ALL_TOPICS_KEY = "all-topics";
	private static final int NUMBER_OF_GROUPS = 5;
	private final static Logger LOGGER = Logger.getLogger(TagCloudController.class);
	private final static int MAX_NUMBER_OF_TAGS = 15;
	private final static Cache<String, List<TagCloudItem>> CACHE = CacheManager.getInstance().<String, List<TagCloudItem>>initCache("tagCloudCache");
	private TopicDAO topicDAO;
	
	private ResourceBundleMessageSource messageSource;
	private EadSearcher eadSearcher;

	public void setEadSearcher(EadSearcher eadSearcher) {
		this.eadSearcher = eadSearcher;
	}

	public void setMessageSource(ResourceBundleMessageSource messageSource) {
		this.messageSource = messageSource;
	}


	public void setTopicDAO(TopicDAO topicDAO) {
		this.topicDAO = topicDAO;
	}
	@RenderMapping()
	public String showTopics(RenderRequest request){
		String view = request.getPreferences().getValue("view", "tagcloud");
		if ("list".equals(view)){
			return showTopicsList(request);
		}else {
			return showTopicsCloud(request);
		}
	}
	private String showTopicsCloud(RenderRequest request) {
		List<TagCloudItem> activeTopics = CACHE.get(ALL_ACTIVE_TOPICS);
		if (activeTopics == null){
			activeTopics= getAllActiveTopics();
			CACHE.put(ALL_ACTIVE_TOPICS, activeTopics);
		}
		List<TagCloudItem> topicsToDisplay = null;
		if (activeTopics.size() > MAX_NUMBER_OF_TAGS){
			topicsToDisplay = getRandomTags(activeTopics,MAX_NUMBER_OF_TAGS);
			
		}else {
			topicsToDisplay = activeTopics;
		}
		/*
		 * display it
		 */
		displayTopics(request, topicsToDisplay);
		return "index";
	}
	public void displayTopics(RenderRequest request, List<TagCloudItem> tags){
		int numberOfTagsWithResults = tags.size();
//		if (numberOfTagsWithResults < MAX_NUMBER_OF_TAGS){
//			List<Topic> topics = topicDAO.getFirstTopics();
//			for (int i = 0; i < topics.size() && tags.size() < MAX_NUMBER_OF_TAGS; i++) {
//				TagCloudItem tagCloudItem = new TagCloudItem(0l, topics.get(i).getPropertyKey());
//				if (!tags.contains(tagCloudItem)) {
//					tags.add(tagCloudItem);
//				}
//			}
//		}
		Collections.sort(tags, new TagCloudCountComparator());
		int[] groups = numberPerGroup(numberOfTagsWithResults, tags.size() - numberOfTagsWithResults);
		int tagCloudItemIndex = 0;
		for (int i = 0; i < NUMBER_OF_GROUPS; i++) {
			int maxNumber = groups[i];
			for (int j = 0; j < maxNumber; j++) {
				tags.get(tagCloudItemIndex).setTagNumber((i + 1));
				tagCloudItemIndex++;
			}
		}
		List<TagCloudItem> translatedTags = new ArrayList<TagCloudItem>();
		SpringResourceBundleSource source = new SpringResourceBundleSource(messageSource, request.getLocale());
		for (TagCloudItem notTranslatedItem : tags){
			String translatedName = source.getString("topics." + notTranslatedItem.getKey());
			translatedTags.add(new TagCloudItem(notTranslatedItem, translatedName));
		}
		Collections.sort(translatedTags, new TagCloudComparator());
		request.setAttribute(TAGS_KEY, translatedTags);
	}

//	private static List<TagCloudItem> getAllActiveTopicsStub(){
//		List<TagCloudItem> tags= new ArrayList<TagCloudItem>();
//		tags.add(new TagCloudItem(100000l, "first.world.war"));
//		tags.add(new TagCloudItem(1000000l, "second.world.war"));
//		tags.add(new TagCloudItem(140l, "slavery"));
//		tags.add(new TagCloudItem(110l, "cold.war"));
//
//		tags.add(new TagCloudItem(110l, "state.collection"));
//		tags.add(new TagCloudItem(150l, "german.democratic.republic"));
//		tags.add(new TagCloudItem(150l, "germany.sed.fdgb"));
//		tags.add(new TagCloudItem(1220l, "medieval.period"));
//		tags.add(new TagCloudItem(14300l, "politics"));
//		tags.add(new TagCloudItem(14540l, "democracy"));
//		tags.add(new TagCloudItem(11340l, "transport"));
//		tags.add(new TagCloudItem(1234l, "genealogy"));
//		tags.add(new TagCloudItem(123l, "churches"));
//		tags.add(new TagCloudItem(1250l, "crime"));
//		tags.add(new TagCloudItem(1554500l, "taxation"));
//
//		tags.add(new TagCloudItem(15450l, "economics"));
//		tags.add(new TagCloudItem(16550l, "national.administration"));
//		tags.add(new TagCloudItem(153430l, "agriculture"));
//
//		return tags;
//	}
	private synchronized List<TagCloudItem> getAllActiveTopics(){
		List<TagCloudItem> tags= new ArrayList<TagCloudItem>();
		SolrQueryParameters solrQueryParameters = new SolrQueryParameters();
		solrQueryParameters.setTerm("*");
		solrQueryParameters.setTimeAllowed(false);
		List<ListFacetSettings> facetSettings = new ArrayList<ListFacetSettings>();
		facetSettings.add(new ListFacetSettings(FacetType.TOPIC, true, null, MAX_ALL_TOPICS));

		try {
			QueryResponse response = eadSearcher.performNewSearchForListView(solrQueryParameters, 0, facetSettings);
			FacetField facetField = response.getFacetFields().get(0);
			for (Count count : facetField.getValues()) {
				tags.add(new TagCloudItem(count.getCount(), count.getName()));
			}
			LOGGER.info("Topics loaded in topic cloud");
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
		return tags;
	}
	
	private String showTopicsList(RenderRequest request) {
		NumberFormat numberFormat = NumberFormat.getInstance(request.getLocale());
		List<TagCloudItem> tags = CACHE.get(ALL_TOPICS_KEY);
		if (tags == null){
			tags= new ArrayList<TagCloudItem>();
			SolrQueryParameters solrQueryParameters = new SolrQueryParameters();
			solrQueryParameters.setTerm("*");
			solrQueryParameters.setTimeAllowed(false);
			List<ListFacetSettings> facetSettings = new ArrayList<ListFacetSettings>();
			facetSettings.add(new ListFacetSettings(FacetType.TOPIC, true, null, 300));
	
			try {
				QueryResponse response = eadSearcher.performNewSearchForListView(solrQueryParameters, 0, facetSettings);
				FacetField facetField = response.getFacetFields().get(0);
				for (Count count : facetField.getValues()) {
					tags.add(new TagCloudItem(count.getCount(), count.getName()));
				}
				LOGGER.info("Topics loaded in topic list");
			} catch (Exception e) {
				LOGGER.error(e.getMessage());
			}
//			List<Topic> topics = topicDAO.findAll();
//			for (int i = 0; i < topics.size(); i++) {
//				TagCloudItem tagCloudItem = new TagCloudItem(0l, topics.get(i).getPropertyKey());
//				if (!tags.contains(tagCloudItem)) {
//					tags.add(tagCloudItem);
//				}
//			}
			CACHE.put(ALL_TOPICS_KEY, tags);
		}
		List<TagCloudItem> translatedTags = new ArrayList<TagCloudItem>();
		SpringResourceBundleSource source = new SpringResourceBundleSource(messageSource, request.getLocale());
		for (TagCloudItem notTranslatedItem : tags){
			String translatedName = source.getString("topics." + notTranslatedItem.getKey());
			translatedTags.add(new TagCloudItem(numberFormat, notTranslatedItem, translatedName));
		}
		Collections.sort(translatedTags, new TagCloudComparator());
		List<TagCloudItem> translatedTagsLeft = new ArrayList<TagCloudItem>();
		List<TagCloudItem> translatedTagsRight = new ArrayList<TagCloudItem>();
		double firstNumberOfTags = Math.ceil(((double)translatedTags.size()) /2d);
		int item = 0;
		for (TagCloudItem tag : translatedTags){
			if (item < firstNumberOfTags){
				translatedTagsLeft.add(tag);
			}else {
				translatedTagsRight.add(tag);
			}
			item++;
		}
		request.setAttribute(TAGS_LEFT_KEY, translatedTagsLeft);
		request.setAttribute(TAGS_RIGHT_KEY, translatedTagsRight);
		PortalDisplayUtil.setPageTitle(request, PortalDisplayUtil.TITLE_TOPICS);

		return "view-all";
	}
	private static int[] numberPerGroup(int numberOfItemsWithResults, int numberOfItemsWithoutResults) {
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
	
	private static List<TagCloudItem> getRandomTags(List<TagCloudItem> tags, int numberOfItems){
		List<TagCloudItem> temp = copy(tags);
		List<TagCloudItem> result = new ArrayList<TagCloudItem>();
		for (int i =0; i< numberOfItems;i++){
			int highestValue = temp.size() -1;
			int itemIndex = new Long(Math.round(Math.random()*highestValue)).intValue();
			TagCloudItem item = temp.get(itemIndex);
			result.add(item);
			temp.remove(itemIndex);
		}
		return result;
	}
	private static List<TagCloudItem> copy(List<TagCloudItem> source){
		List<TagCloudItem> tags= new ArrayList<TagCloudItem>();
		for (TagCloudItem tag: source){
			tags.add(tag);
		}
		return tags;
	}

	private static class TagCloudComparator implements Comparator<TagCloudItem> {

		@Override
		public int compare(TagCloudItem o1, TagCloudItem o2) {
			return o1.getName().compareTo(o2.getName());
		}

	}
	
	private static class TagCloudCountComparator implements Comparator<TagCloudItem> {

		@Override
		public int compare(TagCloudItem o1, TagCloudItem o2) {
			return new Long(o2.getCount()).compareTo(o1.getCount());
		}

	}
}
