package eu.archivesportaleurope.portal.tagcloud;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import eu.apenet.commons.utils.DisplayUtils;
import eu.apenet.persistence.dao.TopicDAO;
import eu.apenet.persistence.vo.Topic;
import eu.archivesportaleurope.portal.common.PortalDisplayUtil;
import eu.archivesportaleurope.portal.common.SpringResourceBundleSource;
import eu.archivesportaleurope.portal.search.common.FacetType;
import eu.archivesportaleurope.portal.search.common.SolrQueryParameters;
import eu.archivesportaleurope.portal.search.ead.EadSearcher;
import eu.archivesportaleurope.portal.search.ead.list.ListFacetSettings;

@Controller(value = "tagCloudController")
@RequestMapping(value = "VIEW")
public class TagCloudController {
	private static final Pattern WORD_PATTERN = Pattern.compile("([\\p{L}\\p{Digit}\\s]+)");
	private static final String TAGS_KEY = "tags";
	private static final String TAGS_LEFT_KEY = "tagsLeft";
	private static final String TAGS_RIGHT_KEY = "tagsRight";
	private static final String ALL_TAGS_KEY = "all-tags";
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
		List<TagCloudItem> tags = CACHE.get(TAGS_KEY);
		if (tags == null){
			tags= new ArrayList<TagCloudItem>();
			SolrQueryParameters solrQueryParameters = new SolrQueryParameters();
			List<String> blockedTopics = getBlockedTopics(request);
			solrQueryParameters.setTerm("*");
			List<ListFacetSettings> facetSettings = new ArrayList<ListFacetSettings>();
			facetSettings.add(new ListFacetSettings(FacetType.TOPIC, true, null, MAX_NUMBER_OF_TAGS+ blockedTopics.size()));
	
			try {
				QueryResponse response = eadSearcher.performNewSearchForListView(solrQueryParameters, 0, facetSettings);
				FacetField facetField = response.getFacetFields().get(0);
				int numberAdded = 0;
				for (Count count : facetField.getValues()) {
					if (numberAdded < MAX_NUMBER_OF_TAGS && !blockedTopics.contains(count.getName())){
						tags.add(new TagCloudItem(count.getCount(), count.getName()));
						numberAdded++;
					}
				}
			} catch (Exception e) {
				LOGGER.error(e.getMessage());
			}
			int numberOfTagsWithResults = tags.size();
			List<Topic> topics = topicDAO.getFirstTopics();
			for (int i = 0; i < topics.size() && tags.size() < MAX_NUMBER_OF_TAGS; i++) {
				TagCloudItem tagCloudItem = new TagCloudItem(0l, topics.get(i).getPropertyKey());
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
			CACHE.put(TAGS_KEY, tags);
		}
		List<TagCloudItem> translatedTags = new ArrayList<TagCloudItem>();
		SpringResourceBundleSource source = new SpringResourceBundleSource(messageSource, request.getLocale());
		for (TagCloudItem notTranslatedItem : tags){
			String translatedName = source.getString("topics." + notTranslatedItem.getKey());
			translatedTags.add(new TagCloudItem(notTranslatedItem, clean(translatedName)));
		}
		Collections.sort(translatedTags, new TagCloudComparator());
		request.setAttribute(TAGS_KEY, translatedTags);
		return "index";
	}
	private List<String> getBlockedTopics(RenderRequest request){
		String blockedTopics = request.getPreferences().getValue("blockedTopics", "");
		String[] blockedTopicsList = blockedTopics.split("\\|");
		return Arrays.asList(blockedTopicsList);
	}
	private String showTopicsList(RenderRequest request) {
		NumberFormat numberFormat = NumberFormat.getInstance(request.getLocale());
		List<TagCloudItem> tags = CACHE.get(ALL_TAGS_KEY);
		if (tags == null){
			tags= new ArrayList<TagCloudItem>();
			SolrQueryParameters solrQueryParameters = new SolrQueryParameters();
			solrQueryParameters.setTerm("*");
			List<ListFacetSettings> facetSettings = new ArrayList<ListFacetSettings>();
			facetSettings.add(new ListFacetSettings(FacetType.TOPIC, true, null, 300));
	
			try {
				QueryResponse response = eadSearcher.performNewSearchForListView(solrQueryParameters, 0, facetSettings);
				FacetField facetField = response.getFacetFields().get(0);
				for (Count count : facetField.getValues()) {
					tags.add(new TagCloudItem(count.getCount(), count.getName()));
				}
			} catch (Exception e) {
				LOGGER.error(e.getMessage());
			}
			List<Topic> topics = topicDAO.findAll();
			for (int i = 0; i < topics.size(); i++) {
				TagCloudItem tagCloudItem = new TagCloudItem(0l, topics.get(i).getPropertyKey());
				if (!tags.contains(tagCloudItem)) {
					tags.add(tagCloudItem);
				}
			}
			CACHE.put(ALL_TAGS_KEY, tags);
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
	
	private static String clean(String string){
		String result = "";
		Matcher matcher = WORD_PATTERN.matcher(string);
		if (matcher.find()) {
			result = DisplayUtils.substring(matcher.group().trim(), 20);
		}
		return result;

	}

	private static class TagCloudComparator implements Comparator<TagCloudItem> {

		@Override
		public int compare(TagCloudItem o1, TagCloudItem o2) {
			return o1.getName().compareTo(o2.getName());
		}

	}
}
