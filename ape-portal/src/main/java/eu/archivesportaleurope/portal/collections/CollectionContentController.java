package eu.archivesportaleurope.portal.collections;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.portlet.ActionRequest;
import javax.portlet.RenderRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.bind.annotation.ActionMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;
import org.springframework.web.servlet.ModelAndView;

import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.User;

import eu.apenet.persistence.dao.CollectionContentDAO;
import eu.apenet.persistence.dao.CollectionDAO;
import eu.apenet.persistence.dao.EadSavedSearchDAO;
import eu.apenet.persistence.vo.Collection;
import eu.apenet.persistence.vo.CollectionContent;
import eu.apenet.persistence.vo.EadSavedSearch;
import eu.apenet.persistence.vo.SavedBookmarks;
import eu.archivesportaleurope.persistence.jpa.dao.SavedBookmarksJpaDAO;
import eu.archivesportaleurope.portal.bookmark.Bookmark;
import eu.archivesportaleurope.portal.common.PortalDisplayUtil;
import eu.archivesportaleurope.portal.search.saved.SavedSearch;
import eu.archivesportaleurope.portal.search.saved.SavedSearchController;
import eu.archivesportaleurope.util.ApeUtil;


@Controller(value = "CollectionContentController")
@RequestMapping(value = "VIEW")
public class CollectionContentController {

	private final static Logger LOGGER = Logger.getLogger(SavedSearchController.class);
    private SavedBookmarksJpaDAO savedBookmarksDAO;
	
	private final static int PAGESIZE  = 10;
	private EadSavedSearchDAO eadSavedSearchDAO;
	private CollectionDAO collectionDAO;
	private CollectionContentDAO collectionContentDAO;
	private static final String BOOKMARK = "bookmark";
	private static final String SAVEDSEARCH = "savedsearch";
	
	public void setSavedBookmarksDAO(SavedBookmarksJpaDAO savedBookmarksDAO) {
		this.savedBookmarksDAO = savedBookmarksDAO;
	}
	
	public void setEadSavedSearchDAO(EadSavedSearchDAO eadSavedSearchDAO) {
		this.eadSavedSearchDAO = eadSavedSearchDAO;
	}

	public void setCollectionDAO(CollectionDAO collectionDAO) {
		this.collectionDAO = collectionDAO;
	}	
	
	public void setCollectionContentDAO(CollectionContentDAO collectionContentDAO) {
		this.collectionContentDAO = collectionContentDAO;
	}

	//DELETE
	
	@ActionMapping(params="myaction=deleteSavedBookmark")
	public void deleteSavedBookmark(ActionRequest request) {
		deleteSavedElemet(request, BOOKMARK);
	}
	
	@ActionMapping(params="myaction=deleteSavedSearch")
	public void deleteSavedSearch(ActionRequest request) {
		deleteSavedElemet(request, SAVEDSEARCH);
	}
	
	private void deleteSavedElemet(ActionRequest request, String type){
		Principal principal = request.getUserPrincipal();
		if (principal != null){
			Long liferayUserId = Long.parseLong(principal.toString());
			Long id = Long.parseLong(request.getParameter("id"));
			try {
				if (type==BOOKMARK){
					SavedBookmarks savedBookmark = savedBookmarksDAO.getSavedBookmark(liferayUserId, id);
					if (savedBookmark.getLiferayUserId() == liferayUserId)
						savedBookmarksDAO.delete(savedBookmark);
				}else{
					EadSavedSearch eadSavedSearch = eadSavedSearchDAO.getEadSavedSearch(id, liferayUserId);
					if (eadSavedSearch.getLiferayUserId() == liferayUserId)
						eadSavedSearchDAO.delete(eadSavedSearch);
				}
			} catch (Exception e) {
				LOGGER.error(ApeUtil.generateThrowableLog(e));
			}
		}
	}

	//----------------------------------------------------------------
	
	//ADD SAVED ELEMENT
	
	@RenderMapping(params="myaction=addSavedSearchesForm")
	public ModelAndView showaddSavedSearchesForm(RenderRequest request, SavedSearch savedSearch) {
		return showAddSavedElement(request, savedSearch, null, SAVEDSEARCH);
	}
	
	@RenderMapping(params="myaction=addSavedBookmarksForm")
	public ModelAndView showAddSavedBookmarksForm(RenderRequest request, Bookmark bookmark) {
		return showAddSavedElement(request, null, bookmark, BOOKMARK);
	}
	
	private ModelAndView showAddSavedElement(RenderRequest request, SavedSearch savedSearch, Bookmark bookmark, String type){
		ModelAndView modelAndView = new ModelAndView();
		
		if (type!=BOOKMARK){
			modelAndView.setViewName("addSavedSearchesForm");
			modelAndView.getModelMap().addAttribute("eadSavedSearch",savedSearch);
		}else{
			modelAndView.setViewName("addSavedBookmarksForm");
			modelAndView.getModelMap().addAttribute("savedBookmark", bookmark);
		}
		
		PortalDisplayUtil.setPageTitle(request, PortalDisplayUtil.TITLE_SAVED_COLLECTIONS);
 		Principal principal = request.getUserPrincipal();
 		if (principal != null){
 			Integer pageNumber = 1;
 			if (StringUtils.isNotBlank(request.getParameter("pageNumber"))){
 				pageNumber = Integer.parseInt(request.getParameter("pageNumber"));
 			}
			try {
				Long liferayUserId = Long.parseLong(principal.toString());
	 			User user = (User) request.getAttribute(WebKeys.USER);				
	 			//get the list of collectionContent that contains the elemet (saved search or saved bookmark)
	 			String id = "";
	 			if (request.getParameter("id") != null) {
	 				id = request.getParameter("id");
	 			} else  if(request.getParameter("eadSavedSearches_id") != null) {
	 				id = request.getParameter("eadSavedSearches_id");
	 			} else {
	 				id = request.getParameter("savedBookmark_id");
	 			}
	 			List<CollectionContent> restCollectionBookmarks=null;
	 			restCollectionBookmarks = collectionContentDAO.getCollectionContentByElementId(type, id);
				//iterate the list to get collectionContent Ids
	 			List<Long> collectionIdsWithElement=new ArrayList<Long>();
				for (CollectionContent content: restCollectionBookmarks) {
					collectionIdsWithElement.add(content.getCollection().getId());
				}
				//get paged list of user collections that NOT contais the elements of the list
				List<Collection> collectionsWithoutElements = collectionDAO.getUserCollectionsWithoutIds(liferayUserId, collectionIdsWithElement, pageNumber, PAGESIZE);
				//count the number of user collections that NOT contains the element
				Long totalNumberOfResults = collectionDAO.countUserCollectionsWithoutIds(liferayUserId, collectionIdsWithElement);
				modelAndView.getModelMap().addAttribute("timeZone", user.getTimeZone());
				modelAndView.getModelMap().addAttribute("pageNumber", pageNumber);
				modelAndView.getModelMap().addAttribute("totalNumberOfResults", totalNumberOfResults);
				modelAndView.getModelMap().addAttribute("pageSize", PAGESIZE);
				modelAndView.getModelMap().addAttribute("collections",collectionsWithoutElements);
				String listChecked = null;
				if (request.getParameter("listChecked") == null) {
					listChecked = "";
				} else {
					listChecked = request.getParameter("listChecked");
				}
				modelAndView.getModelMap().addAttribute("listChecked", listChecked);
					
			} catch (Exception e) {
				LOGGER.error(ApeUtil.generateThrowableLog(e));
			}
 		}
		return modelAndView;
	}
	
	/***
	 * Gets the list of the collectionsin My Pages space in which can be stored the searches, 
	 * if a collection alredy has the search will not be shown
	 * @param request RenderRequest
	 * @param savedSearch SavedSearch object
	 * @return modelAndView
	 */
	@RenderMapping(params="myaction=addSearchesTo")
	public ModelAndView addSearchesTo(RenderRequest request, SavedSearch savedSearch) {
		return addElementsTo(request, null, savedSearch, SAVEDSEARCH);
	}
	
	/***
	 * Gets the list of the collections in which can be stored the bookmarks, 
	 * if a collection alredy has the bookmark will not be shown
	 * @param request RenderRequest
	 * @param bookmark Bookmark object
	 * @return modelAndView
	 */
	@RenderMapping(params="myaction=addBookmarksTo")
	public ModelAndView addBookmarksTo(RenderRequest request, Bookmark bookmark) {
		return addElementsTo(request, bookmark, null, BOOKMARK);
	}
	
	public ModelAndView addElementsTo(RenderRequest request, Bookmark bookmark, SavedSearch savedSearch, String type) {
		ModelAndView modelAndView = new ModelAndView();
		Long elementId;
		
		if(type==BOOKMARK){
			modelAndView.setViewName("viewBookmarksInCollectionsForm");
			elementId = Long.parseLong(request.getParameter("savedBookmark_id"));
		}else{
			modelAndView.setViewName("viewSearchesInCollectionsForm");
			elementId = Long.parseLong(request.getParameter("eadSavedSearches_id"));
		}
		
		PortalDisplayUtil.setPageTitle(request, PortalDisplayUtil.TITLE_SAVED_COLLECTIONS);
 		Principal principal = request.getUserPrincipal();
		Integer pageNumber = 1;
		if (StringUtils.isNotBlank(request.getParameter("pageNumber"))){
			pageNumber = Integer.parseInt(request.getParameter("pageNumber"));
		}
 		if (principal != null){
			try {	
				//recover selected collections from the request
				List<Long> collectionsList = new ArrayList<Long>();
				String[] listChecked = {""};
				if (request.getParameter("listChecked") == null || request.getParameter("listChecked").isEmpty()) {
					listChecked = null;
				} else {
					listChecked = request.getParameter("listChecked").split(",");
					for (int i = 0; i < listChecked.length; i++){
						collectionsList.addAll(Arrays.asList(Long.parseLong(listChecked[i])));
					}
					modelAndView.getModelMap().addAttribute("listChecked", listChecked);
				}
				
				//if bookmark id or saved search id is not null and collections list is not empty call to the method
				if(!collectionsList.isEmpty() && elementId !=null){
					Long liferayUserId = Long.parseLong(principal.toString());
					
					boolean isAdded;
					if(type==BOOKMARK){
						isAdded = addbookmarkToCollections(collectionsList, elementId, liferayUserId);
					}else{
						isAdded = addSearchToCollections(collectionsList, elementId, liferayUserId);
					}
					
					if(isAdded){
			 			List<Collection> collections = this.collectionDAO.getCollectionsByUserId(liferayUserId, pageNumber, PAGESIZE,"none",false);
			 			List<Collection> collectionsWithBookmark=new ArrayList<Collection>();
			 			//irterate collection to check if bookmark exists
			 			Iterator<Collection> itcollections = collections.iterator();
						while(itcollections.hasNext()){
							Collection collection = itcollections.next();
							boolean contains = false;
							Set<CollectionContent> collectioncontentSet = collection.getCollectionContents();
				 			Iterator<CollectionContent> itcollectionContents = collectioncontentSet.iterator();
							while(!contains && itcollectionContents.hasNext()){
								CollectionContent collectionContent = itcollectionContents.next();
								if ((collectionContent.getSavedBookmarks()!=null) && 
									(collectionContent.getSavedBookmarks().getId()==elementId)) {
									contains = true;
								}
							}
							if (contains) {
								collectionsWithBookmark.add(collection);
							}
						}
			 			User user = (User) request.getAttribute(WebKeys.USER);
						modelAndView.getModelMap().addAttribute("timeZone", user.getTimeZone());
						modelAndView.getModelMap().addAttribute("pageNumber", pageNumber);
						modelAndView.getModelMap().addAttribute("totalNumberOfResults", collectionsWithBookmark.size());
						modelAndView.getModelMap().addAttribute("pageSize", PAGESIZE);
						modelAndView.getModelMap().addAttribute("saved", true);
						modelAndView.getModelMap().addAttribute("loggedIn", true);
						modelAndView.getModelMap().addAttribute("savedBookmark", bookmark);
						modelAndView.getModelMap().addAttribute("collections",collectionsWithBookmark);	
						return modelAndView;
					}
				} else if (listChecked == null) {
					modelAndView = showAddSavedElement(request, savedSearch, bookmark, type);
					modelAndView.getModelMap().addAttribute("isNoCollectionsSelected", true);

					if (BOOKMARK.equalsIgnoreCase(type)) {
						bookmark.setId(Long.toString(elementId));
						modelAndView.getModelMap().addAttribute("savedBookmark", bookmark);
					} else {
						savedSearch.setId(Long.toString(elementId));
						modelAndView.getModelMap().addAttribute("savedSearch", savedSearch);
					}

					return modelAndView;
				}
			} catch (Exception e) {
				LOGGER.error(ApeUtil.generateThrowableLog(e));
				modelAndView.getModelMap().addAttribute("loggedIn", false);
				modelAndView.getModelMap().addAttribute("saved", false);
				return modelAndView;
			}
		}
		modelAndView.getModelMap().addAttribute("loggedIn", false);
		modelAndView.getModelMap().addAttribute("saved", false);
		return modelAndView;
	}
	
	/***
	 * Adds a bookmark in a list of collections
	 * @param collectionIds the list of the collections in which will be stored the bookmark
	 * @param bookmarkId the id of the bookmark 
	 * @param liferayUserId the user ID
	 * @return true if the bookmark is stores, false if not.
	 */
	private boolean addbookmarkToCollections(List<Long> collectionIds, Long bookmarkId, Long liferayUserId) {
		return addElementToCollections(collectionIds, null, bookmarkId, liferayUserId, BOOKMARK);
	}
	
	/***
	 * Adds a saved search in a list of collections
	 * @param collectionIds the list of the collections in which will be stored the saved search
	 * @param searchId the id of the saved search 
	 * @param liferayUserId the user ID
	 * @return true if the saved search is stored, false if not.
	 */
	private boolean addSearchToCollections(List<Long> collectionIds, Long searchId, Long liferayUserId) {
		return addElementToCollections(collectionIds, searchId, null, liferayUserId, SAVEDSEARCH);
	}
	
	private boolean addElementToCollections(List<Long> collectionIds, Long searchId, Long bookmarkId, Long liferayUserId, String type) {
		boolean isStored = false;
		//iterator iterate and set values
		Iterator<Long> itCollectionIds = collectionIds.iterator();
		try {
			while(itCollectionIds.hasNext()){
				// get Id form element
				Collection col = collectionDAO.findById(itCollectionIds.next());
				CollectionContent newCollectionContent = new CollectionContent();
				//get collections that contains selected IDs
				newCollectionContent.setCollection(col);
				if (type==BOOKMARK){
					newCollectionContent.setSavedBookmarks(savedBookmarksDAO.getSavedBookmark(liferayUserId, bookmarkId));
				}else{
					newCollectionContent.setEadSavedSearch(eadSavedSearchDAO.getEadSavedSearch(searchId, liferayUserId));
				}

				// Store current content.
				this.collectionContentDAO.store(newCollectionContent);

				// Update current collection.
				col.setModified_date(new Date());
				this.collectionDAO.update(col);

				// Set stored.
				isStored = true;
			}
		} catch (Exception e) {
			LOGGER.error(ApeUtil.generateThrowableLog(e));
			isStored = false;
		}

		return isStored;
	}
}
