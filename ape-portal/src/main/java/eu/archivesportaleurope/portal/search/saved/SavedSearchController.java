package eu.archivesportaleurope.portal.search.saved;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletRequest;
import javax.portlet.RenderRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.ModelAndView;
import org.springframework.web.portlet.bind.annotation.ActionMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;

import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.User;

import eu.apenet.persistence.dao.CollectionContentDAO;
import eu.apenet.persistence.dao.CollectionDAO;
import eu.apenet.persistence.dao.EadSavedSearchDAO;
import eu.apenet.persistence.vo.Collection;
import eu.apenet.persistence.vo.CollectionContent;
import eu.apenet.persistence.vo.EadSavedSearch;
import eu.archivesportaleurope.persistence.jpa.dao.EadSavedSearchJpaDAO;
import eu.archivesportaleurope.portal.common.FriendlyUrlUtil;
import eu.archivesportaleurope.portal.common.PortalDisplayUtil;
import eu.archivesportaleurope.portal.search.ead.EadSearch;
import eu.archivesportaleurope.util.ApeUtil;

@Controller(value = "savedSearchController")
@RequestMapping(value = "VIEW")
public class SavedSearchController {
	private final static Logger LOGGER = Logger.getLogger(SavedSearchController.class);
	private final static int PAGESIZE  = 10;
	private EadSavedSearchDAO eadSavedSearchDAO;
	private EadSavedSearchJpaDAO eadSavedSearchJpaDAO;
	private ResourceBundleMessageSource messageSource;
	private static final String COLLECTION_IN = "collectionToAdd_";
	private CollectionDAO collectionDAO;
	private CollectionContentDAO collectionContentDAO;

	
	public void setEadSavedSearchDAO(EadSavedSearchDAO eadSavedSearchDAO) {
		this.eadSavedSearchDAO = eadSavedSearchDAO;
	}

	public void setMessageSource(ResourceBundleMessageSource messageSource) {
		this.messageSource = messageSource;
	}
	
	public void setCollectionDAO(CollectionDAO collectionDAO) {
		this.collectionDAO = collectionDAO;
	}	
	
	public void setCollectionContentDAO(CollectionContentDAO collectionContentDAO) {
		this.collectionContentDAO = collectionContentDAO;
	}
	
	public void setEadSavedSearchJpaDAO(EadSavedSearchJpaDAO eadSavedSearchJpaDAO){
		this.eadSavedSearchJpaDAO = eadSavedSearchJpaDAO;
	}

	// --maps the incoming portlet request to this method
	@RenderMapping
	public ModelAndView showSavedSearches(RenderRequest request) {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("home");
		PortalDisplayUtil.setPageTitle(request, PortalDisplayUtil.TITLE_SAVED_SEARCH);
		Principal principal = request.getUserPrincipal();
		if (principal != null){
			Long liferayUserId = Long.parseLong(principal.toString());
			Integer pageNumber = 1;
			if (StringUtils.isNotBlank(request.getParameter("pageNumber"))){
				pageNumber = Integer.parseInt(request.getParameter("pageNumber"));
			}
			List<EadSavedSearch> eadSavedSearches = eadSavedSearchDAO.getEadSavedSearches(liferayUserId, pageNumber, PAGESIZE);
			User user = (User) request.getAttribute(WebKeys.USER);
			modelAndView.getModelMap().addAttribute("timeZone", user.getTimeZone());
			modelAndView.getModelMap().addAttribute("pageNumber", pageNumber);
			modelAndView.getModelMap().addAttribute("totalNumberOfResults", eadSavedSearchDAO.countEadSavedSearches(liferayUserId));
			modelAndView.getModelMap().addAttribute("pageSize", PAGESIZE);
			modelAndView.getModelMap().addAttribute("eadSavedSearches",eadSavedSearches);

		}
		return modelAndView;
	}

	@ActionMapping(params="myaction=deleteSavedSearch")
	public void deleteSavedSearch(ActionRequest request) {
		Principal principal = request.getUserPrincipal();
		if (principal != null){
			Long liferayUserId = Long.parseLong(principal.toString());
			Long id = Long.parseLong(request.getParameter("id"));
			EadSavedSearch eadSavedSearch = eadSavedSearchDAO.getEadSavedSearch(id, liferayUserId);
			if (eadSavedSearch.getLiferayUserId() == liferayUserId){
				eadSavedSearchDAO.delete(eadSavedSearch);
			}
		}
	}
	
	@RenderMapping(params="myaction=editSavedSearchForm")
	public String showEditSavedSearchForm() {
		return "editSavedSearchForm";
	}

	@ActionMapping(params="myaction=saveEditSavedSearch")
	public void saveSavedSearch(@ModelAttribute("savedSearch") SavedSearch savedSearch, BindingResult bindingResult,ActionRequest request, ActionResponse response) throws IOException  {
		Principal principal = request.getUserPrincipal();
		if (principal != null){
			Long liferayUserId = Long.parseLong(principal.toString());
			EadSavedSearch eadSavedSearch = eadSavedSearchDAO.getEadSavedSearch(Long.parseLong(savedSearch.getId()), liferayUserId);
			if (eadSavedSearch.getLiferayUserId() == liferayUserId){
				eadSavedSearch.setDescription(SavedSearchService.removeEmptyString(savedSearch.getDescription()));
				if (! EadSearch.SEARCH_ALL_STRING.equals(eadSavedSearch.getSearchTerm())){
					eadSavedSearch.setPublicSearch(savedSearch.isPublicSearch());
				}
				eadSavedSearch.setTemplate(savedSearch.isTemplate());
				eadSavedSearchDAO.store(eadSavedSearch);
				response.sendRedirect(FriendlyUrlUtil.getRelativeUrl(FriendlyUrlUtil.SAVED_SEARCH_OVERVIEW) + FriendlyUrlUtil.SEPARATOR + savedSearch.getOverviewPageNumber());
			}
			
		}	

	}
	
	@ModelAttribute("savedSearch")
	public SavedSearch getEadSavedSearch(PortletRequest request) {
		Principal principal = request.getUserPrincipal();
		String id = request.getParameter("id");
		String overviewPageNumber = request.getParameter("overviewPageNumber");
		SavedSearch savedSearch = new SavedSearch();
		savedSearch.setOverviewPageNumber(overviewPageNumber);
		if (principal != null && StringUtils.isNotBlank(id)){
			Long liferayUserId = Long.parseLong(principal.toString());
			EadSavedSearch eadSavedSearch = eadSavedSearchDAO.getEadSavedSearch(Long.parseLong(id), liferayUserId);
			if (eadSavedSearch.getLiferayUserId() == liferayUserId){
				savedSearch.setDescription(eadSavedSearch.getDescription());
				savedSearch.setSearchTerm(eadSavedSearch.getSearchTerm());
				savedSearch.setModifiedDate(eadSavedSearch.getModifiedDate());
				savedSearch.setPublicSearch(eadSavedSearch.isPublicSearch());
				savedSearch.setTemplate(eadSavedSearch.isTemplate());
				savedSearch.setContainsSimpleSearchOptions(eadSavedSearch.isContainsSimpleSearchOptions());
				savedSearch.setContainsAdvancedSearchOptions(eadSavedSearch.isContainsAdvancedSearchOptions());
				savedSearch.setContainsAlSearchOptions(eadSavedSearch.isContainsAlSearchOptions());
				savedSearch.setContainsRefinements(eadSavedSearch.isContainsRefinements());
				savedSearch.setId(eadSavedSearch.getId() +"");

			}
		}		
		return savedSearch;
	}
	
	@RenderMapping(params="myaction=addSavedSearchesForm")
	public ModelAndView showaddSavedSearchesForm(RenderRequest request, SavedSearch savedSearch) {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("addSavedSearchesForm");
		PortalDisplayUtil.setPageTitle(request, PortalDisplayUtil.TITLE_SAVED_COLLECTIONS);
 		Principal principal = request.getUserPrincipal();
 		if (principal != null){
 			Integer pageNumber = 1;
 			if (StringUtils.isNotBlank(request.getParameter("pageNumber"))){
 				pageNumber = Integer.parseInt(request.getParameter("pageNumber"));
 			}
			try {
	 			Long liferayUserId = Long.parseLong(principal.toString());
	 			List<Collection> collections = this.collectionDAO.getCollectionsByUserId(liferayUserId, pageNumber, PAGESIZE,"none",false);
	 			List<Collection> collectionsWithoutSearch=new ArrayList<Collection>();
	 			//irterate collection to check if bookmark exists
	 			Iterator<Collection> itcollections = collections.iterator();
				while(itcollections.hasNext()){
					Collection collection = itcollections.next();
					
					boolean contains = false;
					Set<CollectionContent> collectioncontentSet = collection.getCollectionContents();
		 			Iterator<CollectionContent> itcollectionContents = collectioncontentSet.iterator();
					while(!contains && itcollectionContents.hasNext()){
						CollectionContent collectionContent = itcollectionContents.next();
						if ((collectionContent.getEadSavedSearch()!=null) && 
							(collectionContent.getEadSavedSearch().getId()==Long.parseLong(request.getParameter("id")))) {
							contains = true;
						}
					}
					if (!contains) {
						collectionsWithoutSearch.add(collection);
					}
				}	
//				List<EadSavedSearch> eadSavedSearches = eadSavedSearchDAO.getEadSavedSearches(liferayUserId, pageNumber, PAGESIZE);
				User user = (User) request.getAttribute(WebKeys.USER);
				modelAndView.getModelMap().addAttribute("timeZone", user.getTimeZone());
				modelAndView.getModelMap().addAttribute("pageNumber", pageNumber);
				modelAndView.getModelMap().addAttribute("totalNumberOfResults", eadSavedSearchDAO.countEadSavedSearches(liferayUserId));
				modelAndView.getModelMap().addAttribute("pageSize", PAGESIZE);
				modelAndView.getModelMap().addAttribute("eadSavedSearch",savedSearch);
				modelAndView.getModelMap().addAttribute("collections",collectionsWithoutSearch);
				//-----------------------------------
				
			} catch (Exception e) {
				LOGGER.error(ApeUtil.generateThrowableLog(e));
			}
 		}
		return modelAndView;
	}
	
	/***
	 * Gets the list of the collections in which can be stored the searches, if a collection alredy has the search will not be shown
	 * @param request RenderRequest
	 * @param savedSearch SavedSearch object
	 * @return modelAndView
	 */
	@RenderMapping(params="myaction=addSearchesTo")
	public ModelAndView addSearchesTo(RenderRequest request, SavedSearch savedSearch) {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("viewSearchesInCollectionsForm");
		PortalDisplayUtil.setPageTitle(request, PortalDisplayUtil.TITLE_SAVED_COLLECTIONS);
 		Principal principal = request.getUserPrincipal();
 		Long liferayUserId = Long.parseLong(principal.toString());
		Integer pageNumber = 1;
		if (StringUtils.isNotBlank(request.getParameter("pageNumber"))){
			pageNumber = Integer.parseInt(request.getParameter("pageNumber"));
		}	
 		if (principal != null){
			try {	
				//selected saved search id
				Long id = Long.parseLong(request.getParameter("eadSavedSearches_id"));
				//recover selected collections from the request
				Enumeration<String> parametersNames = request.getParameterNames();
				List<Long> collectionsList = new ArrayList<Long>();
				while(parametersNames.hasMoreElements()){
					String parameterName = parametersNames.nextElement();
					if(parameterName!=null){
						if(parameterName.contains(COLLECTION_IN)){
							if(request.getParameter(parameterName).equalsIgnoreCase("on")){
								collectionsList.add(Long.parseLong(parameterName.substring(COLLECTION_IN.length())));
							}
						}
					}
				}
				//if saved search id is not null and collections list is not empty call to the method
				if(!collectionsList.isEmpty() && id !=null){
					if(addSearchToCollections(collectionsList, id, liferayUserId)){
			 			List<Collection> collections = this.collectionDAO.getCollectionsByUserId(liferayUserId, pageNumber, PAGESIZE,"none",false);
			 			List<Collection> collectionsWithSearch=new ArrayList<Collection>();
			 			//irterate collection to check if the search exists
			 			Iterator<Collection> itcollections = collections.iterator();
						while(itcollections.hasNext()){
							Collection collection = itcollections.next();
							boolean contains = false;
							Set<CollectionContent> collectioncontentSet = collection.getCollectionContents();
				 			Iterator<CollectionContent> itcollectionContents = collectioncontentSet.iterator();
							while(!contains && itcollectionContents.hasNext()){
								CollectionContent collectionContent = itcollectionContents.next();
								if ((collectionContent.getEadSavedSearch()!=null) && 
									(collectionContent.getEadSavedSearch().getId()==id)) {
									contains = true;
								}
							}
							if (contains) {
								collectionsWithSearch.add(collection);
							}
						}
						//-----------------------------------
						User user = (User) request.getAttribute(WebKeys.USER);
						modelAndView.getModelMap().addAttribute("timeZone", user.getTimeZone());
						modelAndView.getModelMap().addAttribute("pageNumber", pageNumber);
						modelAndView.getModelMap().addAttribute("totalNumberOfResults", eadSavedSearchDAO.countEadSavedSearches(liferayUserId));
						modelAndView.getModelMap().addAttribute("pageSize", PAGESIZE);
						modelAndView.getModelMap().addAttribute("saved", true);
						modelAndView.getModelMap().addAttribute("loggedIn", true);
						modelAndView.getModelMap().addAttribute("savedSearch", savedSearch);
						modelAndView.getModelMap().addAttribute("collections",collectionsWithSearch);
						//-----------------------------------					
						return modelAndView;
					}
				} 
			} catch (Exception e) {
				LOGGER.error(ApeUtil.generateThrowableLog(e));
				modelAndView.getModelMap().addAttribute("saved", false);
				return modelAndView;
			}
		}
 		modelAndView.getModelMap().addAttribute("loggedIn", false);
		return modelAndView;
	}
		
	/***
	 * Adds a saved search in a list of collections
	 * @param collectionIds the list of the collections in which will be stored the saved search
	 * @param searchId the id of the saved search 
	 * @param liferayUserId the user ID
	 * @return true if the saved search is stored, false if not.
	 */
	private boolean addSearchToCollections(List<Long> collectionIds, Long searchId, Long liferayUserId) {
		//get an saved search object with searchId to use it in the collections
		EadSavedSearch savedSearch = eadSavedSearchDAO.getEadSavedSearch(searchId, liferayUserId);
		//create a list with identifiers to iterate and to add the searches
		List<CollectionContent> newCollectionContentList = new ArrayList<CollectionContent>();
		//iterator iterate and set values
		Iterator<Long> itCollectionIds = collectionIds.iterator();
		try {
			while(itCollectionIds.hasNext()){
				// get Id form element
				Collection col = collectionDAO.findById(itCollectionIds.next());
				CollectionContent newCollectionContent = new CollectionContent();
				//get collections that contains selected IDs
				newCollectionContent.setCollection(col);
				newCollectionContent.setEadSavedSearch(savedSearch);
				//add item to the list
				newCollectionContentList.add(newCollectionContent);
			}
			//add content
			if(newCollectionContentList.size()>0){
				this.collectionContentDAO.store(newCollectionContentList);
				return true;
			}
		} catch (Exception e) {
			LOGGER.error(ApeUtil.generateThrowableLog(e));
		}
		return false;
	}
}
