package eu.archivesportaleurope.portal.bookmark;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.portlet.ResourceRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.ModelAndView;
import org.springframework.web.portlet.bind.annotation.ResourceMapping;

import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.User;

import eu.apenet.persistence.dao.CollectionContentDAO;
import eu.apenet.persistence.dao.CollectionDAO;
import eu.apenet.persistence.vo.Collection;
import eu.apenet.persistence.vo.CollectionContent;
import eu.apenet.persistence.vo.SavedBookmarks;
import eu.archivesportaleurope.persistence.jpa.dao.SavedBookmarksJpaDAO;
import eu.archivesportaleurope.portal.common.PortalDisplayUtil;
import eu.archivesportaleurope.portal.common.SpringResourceBundleSource;
import eu.archivesportaleurope.util.ApeUtil;

/**
 *
 * This is display ead controller
 *
 * @author bverhoef
 *
 */
@Controller(value = "BookmarkCollectionContoller")
@RequestMapping(value = "VIEW")
public class BookmarkCollectionContoller {
	private final static Logger LOGGER = Logger.getLogger(BookmarkCollectionContoller.class);

	private CollectionDAO collectionDAO;
	private MessageSource messageSource;
	private static final String COLLECTION_IN = "collectionToAdd_";
    private final static int PAGESIZE  = 20;
    private SavedBookmarksJpaDAO savedBookmarksDAO;
	private CollectionContentDAO collectionContentDAO;


	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}
	
	public void setCollectionDAO(CollectionDAO collectionDAO) {
		this.collectionDAO = collectionDAO;
	}
	
	public void setSavedBookmarksDAO(SavedBookmarksJpaDAO savedBookmarksDAO) {
		this.savedBookmarksDAO = savedBookmarksDAO;
	}
	
	public void setCollectionContentDAO(CollectionContentDAO collectionContentDAO) {
		this.collectionContentDAO = collectionContentDAO;
	}


	/***
	 * Return the modelAndView with the collections in which can be included a bookmark.
	 * @param resourceRequest
	 * @return modelAndView
	 */
	@ResourceMapping(value="seeAvaiableCollections")
	public ModelAndView seeAvaiableCollections(ResourceRequest resourceRequest) {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("seeAvaiableCollections");
		PortalDisplayUtil.setPageTitle(resourceRequest, PortalDisplayUtil.TITLE_SAVED_COLLECTIONS);
 		Principal principal = resourceRequest.getUserPrincipal();
 		//bookmark ID and search term
 		String bookmarkId=resourceRequest.getParameter("bookmarkId");
 		String searchTerm=resourceRequest.getParameter("criteria");
 		if (searchTerm==null)
 			searchTerm="";
 		if (principal != null){
			try {
	 			Long liferayUserId = Long.parseLong(principal.toString());
				modelAndView.getModelMap().addAttribute("collections",getCollectionsWithoutBookmark(searchTerm, bookmarkId, liferayUserId));	
			} catch (Exception e) {
				LOGGER.error(ApeUtil.generateThrowableLog(e));
			}
			modelAndView.getModelMap().addAttribute("loggedIn", true);
 		}
 		else{
 			modelAndView.getModelMap().addAttribute("loggedIn", false);
 		}
		modelAndView.getModelMap().addAttribute("bookmarkId",bookmarkId);
		return modelAndView;
	}
		
	/***
	 * Gets a list with the collections in which can include the selected bookmark. 
	 * If there is a criteria to search, it will return the list of collections that matches with criteria.
	 * @param searchTerm String with the name to search
	 * @param bookmarkId String the bookmark ID
	 * @param liferayUserId Long id Liferay's user
	 * @return List<Collection> collectionsWithoutBookmark
	 */
	private List<Collection> getCollectionsWithoutBookmark(String searchTerm, String bookmarkId, Long liferayUserId){
		List<Collection> collections = null;
		
		if (searchTerm=="")
			collections = this.collectionDAO.getCollectionsByUserId(liferayUserId, 1, 20,"collection.title",false);
		else
			collections = this.collectionDAO.getCollectionByName(liferayUserId, searchTerm, 20);
		
		List<Collection> collectionsWithoutBookmark=new ArrayList<Collection>();
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
					(collectionContent.getSavedBookmarks().getId()==Long.parseLong(bookmarkId))) {
					contains = true;
				}
			}
			if (!contains) {
					collectionsWithoutBookmark.add(collection);
			}
		}
		return collectionsWithoutBookmark;
	}
		
	/***
	 * Gets the list of the collections in which can be stored the bookmarks, if a collection alredy has the bookmark will not be shown
	 * @param request RenderRequest
	 * @param bookmark Bookmark object
	 * @return modelAndView
	 */
	@ResourceMapping(value="addBookmarksTo")
	public ModelAndView addBookmarksTo(ResourceRequest request/*, Bookmark bookmark*/) {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("bookmark");
		PortalDisplayUtil.setPageTitle(request, PortalDisplayUtil.TITLE_SAVED_COLLECTIONS);
 		Principal principal = request.getUserPrincipal();
 		Long liferayUserId = Long.parseLong(principal.toString());
 		SpringResourceBundleSource source = new SpringResourceBundleSource(messageSource, request.getLocale());
		Integer pageNumber = 1;
		if (StringUtils.isNotBlank(request.getParameter("pageNumber"))){
			pageNumber = Integer.parseInt(request.getParameter("pageNumber"));
		}	
 		if (principal != null){
			try {	
				//selected bookmark id
				Long savedBookmarkId = Long.parseLong(request.getParameter("bookmarkId"));
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
				//if bookmark id is not null and collections list is not empty call to the method
				if(!collectionsList.isEmpty() && savedBookmarkId !=null){
					if(addbookmarkToCollections(collectionsList, savedBookmarkId, liferayUserId)){
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
									(collectionContent.getSavedBookmarks().getId()==savedBookmarkId)) {
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
						modelAndView.getModelMap().addAttribute("message",source.getString("bookmarks.saved.ok"));
						modelAndView.getModelMap().addAttribute("collections",collectionsWithBookmark);	
					}else{
						modelAndView.getModelMap().addAttribute("saved", false);
						modelAndView.getModelMap().addAttribute("message",source.getString("bookmarks.saved.ko"));
					}
					modelAndView.getModelMap().addAttribute("loggedIn", true);
					modelAndView.getModelMap().addAttribute("showBox", false);
					return modelAndView;
				}else{
					modelAndView.getModelMap().addAttribute("loggedIn", true);
					modelAndView.getModelMap().addAttribute("showBox", false);
					modelAndView.getModelMap().addAttribute("saved", true);
					modelAndView.getModelMap().addAttribute("message",source.getString("bookmarks.saved.noCols"));
				} 
				
			} catch (Exception e) {
				LOGGER.error(ApeUtil.generateThrowableLog(e));
				modelAndView.getModelMap().addAttribute("saved", false);
				modelAndView.getModelMap().addAttribute("loggedIn", true);
				return modelAndView;
			}
		}
 		modelAndView.getModelMap().addAttribute("loggedIn", false);
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
		//get an bookmark object with bookmarkId to use it in the collections
		SavedBookmarks savedBookmark = savedBookmarksDAO.getSavedBookmark(liferayUserId, bookmarkId);
		//create a list with identifiers to iterate and to add the bookmarks
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
				newCollectionContent.setSavedBookmarks(savedBookmark);
				savedBookmark.setName(PortalDisplayUtil.replaceHTMLSingleQuotes(savedBookmark.getName()));
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
	
	//Create a new collection from second display
	
	@ResourceMapping(value="newCollection")
	public ModelAndView newCollection(ResourceRequest request) throws IOException {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("collection");
		modelAndView.getModelMap().addAttribute("edit",true);
		Principal principal = request.getUserPrincipal();
		Long id = request.getParameter("bookmarkId")!=null?Long.parseLong(request.getParameter("bookmarkId")):null;
		if(principal!=null && id!=null){ 
			modelAndView.getModelMap().addAttribute("bookmarkId",id);
			modelAndView.getModelMap().addAttribute("loggedIn", true);
		}
		else{
			modelAndView.getModelMap().addAttribute("loggedIn", false);
		}
		return modelAndView;
	}

	@ResourceMapping(value="saveNewCollection")
	public ModelAndView saveNewCollection(ResourceRequest request) throws IOException {
		Principal principal = request.getUserPrincipal();
		String title = request.getParameter("title");
		String description = request.getParameter("description");
		boolean public_ = (request.getParameter("isPublic").equals("true"));
		boolean edit = (request.getParameter("isEdit").equals("true"));
		Long id = request.getParameter("bookmarkId")!=null?Long.parseLong(request.getParameter("bookmarkId")):null;
		Collection newCollection = new Collection();
		if (principal != null && StringUtils.isNotBlank(title)){
			try {
				//collection
				Long liferayUserId = Long.parseLong(principal.toString());
				newCollection.setTitle(title);
				newCollection.setPublic_(public_);
				newCollection.setLiferayUserId(liferayUserId);
				newCollection.setDescription(description);
				newCollection.setEdit(edit);
				newCollection.setModified_date(new Date());
				newCollection = this.collectionDAO.store(newCollection);
				try {
					List<Long> collectionsList = new ArrayList<Long>();
					collectionsList.add(newCollection.getId());
					addbookmarkToCollections(collectionsList, id, liferayUserId);
				} catch (Exception e) {
					LOGGER.error(ApeUtil.generateThrowableLog(e));
				}
			} catch (Exception e) {
				LOGGER.error(ApeUtil.generateThrowableLog(e));
			}
		}
		return showSavedCollections(request);
	}		
	
	public ModelAndView showSavedCollections(ResourceRequest request) {
		SpringResourceBundleSource source = new SpringResourceBundleSource(messageSource, request.getLocale());
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("bookmark");
		boolean orderAsc=true;
		PortalDisplayUtil.setPageTitle(request, PortalDisplayUtil.TITLE_SAVED_COLLECTIONS);
		Principal principal = request.getUserPrincipal();
		if (principal != null){
			Long liferayUserId = Long.parseLong(principal.toString());
			Integer pageNumber = 1;
			if (StringUtils.isNotBlank(request.getParameter("pageNumber"))){
				pageNumber = Integer.parseInt(request.getParameter("pageNumber"));
			}
			if (request.getParameter("orderAsc")=="headerSortUp")
				orderAsc=true;
			else
				orderAsc=false;
				
			try {
				List<Collection> collections = this.collectionDAO.getCollectionsByUserId(liferayUserId, pageNumber, PAGESIZE, request.getParameter("column"),orderAsc);
				User user = (User) request.getAttribute(WebKeys.USER);
				modelAndView.getModelMap().addAttribute("timeZone", user.getTimeZone());
				modelAndView.getModelMap().addAttribute("pageNumber", pageNumber);
				modelAndView.getModelMap().addAttribute("totalNumberOfResults", (collections.size()>0)?this.collectionDAO.countCollectionsByUserId(liferayUserId):collections.size());
				modelAndView.getModelMap().addAttribute("pageSize", PAGESIZE);
				modelAndView.getModelMap().addAttribute("orderColumn", "none");
				modelAndView.getModelMap().addAttribute("orderAsc", "none");
				modelAndView.getModelMap().addAttribute("collections",collections);
				modelAndView.getModelMap().addAttribute("showBox", false);
				modelAndView.getModelMap().addAttribute("saved", true);
		 		modelAndView.getModelMap().addAttribute("loggedIn", true);
				modelAndView.getModelMap().addAttribute("message",source.getString("bookmarks.saved.ok"));
			} catch (Exception e) {
				modelAndView.getModelMap().addAttribute("saved", false);
		 		modelAndView.getModelMap().addAttribute("loggedIn", true);
				modelAndView.getModelMap().addAttribute("message",source.getString("bookmarks.saved.ko"));
			}
		}
		else{
	 		modelAndView.getModelMap().addAttribute("loggedIn", false);
		}
		return modelAndView;
	}
	//End creation a new colection from second display
}