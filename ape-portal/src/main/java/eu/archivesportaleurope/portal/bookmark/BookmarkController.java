package eu.archivesportaleurope.portal.bookmark;

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
import javax.portlet.ResourceRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.bind.annotation.ActionMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;
import org.springframework.web.portlet.bind.annotation.ResourceMapping;
import org.springframework.web.servlet.ModelAndView;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.User;
import com.liferay.portal.util.PortalUtil;

import eu.apenet.persistence.dao.CollectionContentDAO;
import eu.apenet.persistence.dao.CollectionDAO;
import eu.apenet.persistence.vo.Collection;
import eu.apenet.persistence.vo.CollectionContent;
import eu.apenet.persistence.vo.SavedBookmarks;
import eu.archivesportaleurope.persistence.jpa.dao.SavedBookmarksJpaDAO;
import eu.archivesportaleurope.portal.bookmark.Bookmark;
import eu.archivesportaleurope.portal.bookmark.BookmarkService;
import eu.archivesportaleurope.portal.common.FriendlyUrlUtil;
import eu.archivesportaleurope.portal.common.PortalDisplayUtil;
import eu.archivesportaleurope.portal.common.SpringResourceBundleSource;
import eu.archivesportaleurope.util.ApeUtil;

@Controller(value = "bookmarkController")
@RequestMapping(value = "VIEW")
public class BookmarkController {

    private static final Logger LOGGER = Logger.getLogger(BookmarkController.class);
    private SavedBookmarksJpaDAO savedBookmarksDAO;
    private BookmarkService bookmarkService;
    
	private CollectionDAO collectionDAO;
	private CollectionContentDAO collectionContentDAO;

	private static final String COLLECTION_IN = "collectionToAdd_";
    private final static int PAGESIZE  = 10;
	private ResourceBundleMessageSource messageSource;

	public void setMessageSource(ResourceBundleMessageSource messageSource) {
		this.messageSource = messageSource;
	}
	
	public void setSavedBookmarksDAO(SavedBookmarksJpaDAO savedBookmarksDAO) {
		this.savedBookmarksDAO = savedBookmarksDAO;
	}
	
	public void setCollectionDAO(CollectionDAO collectionDAO) {
		this.collectionDAO = collectionDAO;
	}	
	
	 public void setBookmarkService (BookmarkService bookmarkService) {
         this.bookmarkService = bookmarkService;
     }

	public void setCollectionContentDAO(CollectionContentDAO collectionContentDAO) {
		this.collectionContentDAO = collectionContentDAO;
	}
	
	@ResourceMapping(value="bookmark")
    public ModelAndView showInitialPage(@ModelAttribute("bookmark") Bookmark bookmark, ResourceRequest request) throws PortalException, SystemException {
		boolean loggedIn = request.getUserPrincipal() != null;
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("bookmark");  
        modelAndView.getModelMap().addAttribute("loggedIn",loggedIn);
		SpringResourceBundleSource source = new SpringResourceBundleSource(messageSource, request.getLocale());
    	if (loggedIn){
    		User currentUser = (User) PortalUtil.getUser(request);
    		String description = bookmark.getDescription() + " " + currentUser.getEmailAddress();
    		bookmark.setDescription(description);
			if (saveBookmark(bookmark, request, modelAndView)){
				modelAndView.getModelMap().addAttribute("saved",true);
				modelAndView.getModelMap().addAttribute("showBox", true);
				modelAndView.getModelMap().addAttribute("bookmarkId",bookmark.getId());
				modelAndView.getModelMap().addAttribute("searchTerm",request.getParameter("searchTerm"));
			}
			else{
				modelAndView.getModelMap().addAttribute("saved",false);
				modelAndView.getModelMap().addAttribute("message",source.getString("bookmarks.saved.ko"));
			}
    	}
    	else{
			modelAndView.getModelMap().addAttribute("message",source.getString("bookmarks.logged.ko"));
    	}
        return modelAndView;
    }

	public boolean saveBookmark(Bookmark bookmark,ResourceRequest resourceRequest, ModelAndView modelAndView) {
		boolean saved = false;
		if (resourceRequest.getUserPrincipal() != null){
			long liferayUserId = Long.parseLong(resourceRequest.getUserPrincipal().toString());
			try {
		        SpringResourceBundleSource source = new SpringResourceBundleSource(messageSource, resourceRequest.getLocale());

				if (checkIfExist(bookmark, resourceRequest)){
			        modelAndView.getModelMap().addAttribute("message",source.getString("bookmarks.saved.already"));
					saved = true;
					//when you click over bookmark this and the current element is already bookmarked.
					//show the list of the collections in which can be added
					modelAndView.getModelMap().addAttribute("bookmarkId",bookmark.getId());				
				}
				else{
					this.bookmarkService.saveBookmark(liferayUserId, bookmark);
					modelAndView.getModelMap().addAttribute("message",source.getString("bookmarks.saved.ok"));
					saved = true;
				}
			}catch (Exception e) {
				LOGGER.error(ApeUtil.generateThrowableLog(e));
				saved = false;
			}
		}
		return saved;
	}
	
	private boolean checkIfExist(Bookmark bookmark, ResourceRequest resourceRequest){
		boolean exist = false;
		long liferayUserId = Long.parseLong(resourceRequest.getUserPrincipal().toString());
		try {
			List<SavedBookmarks> savedBookmarks = savedBookmarksDAO.getSavedBookmarks(liferayUserId, 1, PAGESIZE);
			Iterator<SavedBookmarks> itBookmarks = savedBookmarks.iterator();
			while(itBookmarks.hasNext() && !exist){
				SavedBookmarks sb = itBookmarks.next();
				if(sb.getLink().compareTo(bookmark.getPersistentLink())==0){
					bookmark.setId(Long.toString(sb.getId()));
			        exist = true;
				}
			}
		} catch (Exception e) {
			LOGGER.error(ApeUtil.generateThrowableLog(e));
		}
		return exist;
	}
	
    @ModelAttribute("bookmark")
    public Bookmark getCommandObject() {
        return new Bookmark();
    }
    	
	@ModelAttribute("savedbookmarks")
	public Bookmark getSavedBookmark(PortletRequest request) {
		Principal principal = request.getUserPrincipal();
		String id = request.getParameter("id");
		Integer pageNumber = 1;
		if (StringUtils.isNotBlank(request.getParameter("pageNumber"))){
			pageNumber = Integer.parseInt(request.getParameter("pageNumber"));
		}
		Bookmark bookmark = new Bookmark();
		bookmark.setOverviewPageNumber(pageNumber.toString());
		if (principal != null && StringUtils.isNotBlank(id)){
			Long liferayUserId = Long.parseLong(principal.toString());
			SavedBookmarks savedBookmark = savedBookmarksDAO.getSavedBookmark(liferayUserId, Long.parseLong(id));
			if (savedBookmark.getLiferayUserId() == liferayUserId){
				bookmark.setLiferay_user_id(Long.toString(liferayUserId));
				bookmark.setBookmarkName(savedBookmark.getName());
				bookmark.setDescription(savedBookmark.getDescription());
				bookmark.setPersistentLink(savedBookmark.getLink());
				bookmark.setTypedocument(savedBookmark.getTypedocument());
				bookmark.setModifiedDate(savedBookmark.getModifiedDate());
				bookmark.setId(savedBookmark.getId() +"");
			}
		}		
		return bookmark;
	}
	
 // --maps the incoming portlet request to this method
 	@RenderMapping
 	public ModelAndView showSavedBookmarks(RenderRequest request) {
 		ModelAndView modelAndView = new ModelAndView();
 		modelAndView.setViewName("home");
 		PortalDisplayUtil.setPageTitle(request, PortalDisplayUtil.TITLE_SAVED_BOOKMARK);
 		Principal principal = request.getUserPrincipal();
 		if (principal != null){
 			Integer pageNumber = 1;
 			if (StringUtils.isNotBlank(request.getParameter("pageNumber"))){
 				pageNumber = Integer.parseInt(request.getParameter("pageNumber"));
 			}
			try {
	 			Long liferayUserId = Long.parseLong(principal.toString());
				List<SavedBookmarks> savedBookmarks = savedBookmarksDAO.getSavedBookmarks(liferayUserId, pageNumber, PAGESIZE);
				User user = (User) request.getAttribute(WebKeys.USER);
	 			modelAndView.getModelMap().addAttribute("timeZone", user.getTimeZone());
	 			modelAndView.getModelMap().addAttribute("pageNumber", pageNumber);
	 			modelAndView.getModelMap().addAttribute("totalNumberOfResults", savedBookmarksDAO.countSavedBookmarks(liferayUserId));
	 			modelAndView.getModelMap().addAttribute("pageSize", PAGESIZE);
	 			modelAndView.getModelMap().addAttribute("savedBookmarks",savedBookmarks);
			} catch (Exception e) {
				LOGGER.error(ApeUtil.generateThrowableLog(e));
			}
 		}
 		return modelAndView;
 	}
 		
    @ResourceMapping(value="bookmarkAction")
    public ModelAndView showResult(@ModelAttribute("bookmark") Bookmark bookmark, BindingResult result, ResourceRequest request) throws PortalException, SystemException {
        ModelAndView modelAndView = new ModelAndView();
    	boolean loggedIn = request.getUserPrincipal() != null;
    	User currentUser = null;
    	if (loggedIn){
    		currentUser = (User) PortalUtil.getUser(request);
    		modelAndView.getModelMap().addAttribute("currentUser", currentUser);
    	}
		modelAndView.setViewName("success");
		return modelAndView;
    }

	@RenderMapping(params="myaction=editSavedBookmarksForm")
	public String showEditSavedBookmarksForm() {
		return "editSavedBookmarksForm";
	}
		
	@RenderMapping(params="myaction=addSavedBookmarksForm")
	public ModelAndView showAddSavedBookmarksForm(RenderRequest request, Bookmark bookmark) {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("addSavedBookmarksForm");
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
				modelAndView.getModelMap().addAttribute("timeZone", user.getTimeZone());
				modelAndView.getModelMap().addAttribute("pageNumber", pageNumber);
				modelAndView.getModelMap().addAttribute("totalNumberOfResults", getCollectionsWithoutBookmark(request, liferayUserId).size());
				modelAndView.getModelMap().addAttribute("pageSize", PAGESIZE);
				modelAndView.getModelMap().addAttribute("savedBookmark", bookmark);
				modelAndView.getModelMap().addAttribute("collections",getCollectionsWithoutBookmark(request, liferayUserId));	
			} catch (Exception e) {
				LOGGER.error(ApeUtil.generateThrowableLog(e));
			}
 		}
		return modelAndView;
	}
	
	private List<Collection> getCollectionsWithoutBookmark(RenderRequest request, Long liferayUserId){
		List<Collection> collections = this.collectionDAO.getCollectionsByUserId(liferayUserId, 1, PAGESIZE,"none",false);
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
					(collectionContent.getSavedBookmarks().getId()==Long.parseLong(request.getParameter("id")))) {
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
	@RenderMapping(params="myaction=addBookmarksTo")
	public ModelAndView addBookmarksTo(RenderRequest request, Bookmark bookmark) {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("viewBookmarksInCollectionsForm");
		PortalDisplayUtil.setPageTitle(request, PortalDisplayUtil.TITLE_SAVED_COLLECTIONS);
 		Principal principal = request.getUserPrincipal();
 		Long liferayUserId = Long.parseLong(principal.toString());
		Integer pageNumber = 1;
		if (StringUtils.isNotBlank(request.getParameter("pageNumber"))){
			pageNumber = Integer.parseInt(request.getParameter("pageNumber"));
		}	
 		if (principal != null){
			try {	
				//selected bookmark id
				Long savedBookmarkId = Long.parseLong(request.getParameter("savedBookmark_id"));
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
						modelAndView.getModelMap().addAttribute("loggedIn", true);
						modelAndView.getModelMap().addAttribute("savedBookmark", bookmark);
						modelAndView.getModelMap().addAttribute("collections",collectionsWithBookmark);	
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

	@ActionMapping(params="myaction=saveEditSavedBookmarks")
	public void saveSavedBookmark(@ModelAttribute("savedBookmark") Bookmark bookmark, BindingResult bindingResult,ActionRequest request, ActionResponse response) throws IOException  {
		Principal principal = request.getUserPrincipal();
		if (principal != null){
			Long liferayUserId = Long.parseLong(principal.toString());
			SavedBookmarks savedBookmark = savedBookmarksDAO.getSavedBookmark(liferayUserId, Long.parseLong(bookmark.getId()));
			if (savedBookmark.getLiferayUserId() == liferayUserId){
				savedBookmark.setDescription(bookmark.getDescription());
				try {
					savedBookmarksDAO.store(savedBookmark);
					response.sendRedirect(FriendlyUrlUtil.getRelativeUrl(FriendlyUrlUtil.SAVED_BOOKMARKS_OVERVIEW) + FriendlyUrlUtil.SEPARATOR + bookmark.getOverviewPageNumber());
				} catch (Exception e) {
					LOGGER.error(ApeUtil.generateThrowableLog(e));
				}
			}
		}	
	}
	
	@ActionMapping(params="myaction=deleteSavedBookmark")
	public void deleteSavedBookmark(ActionRequest request) {
		Principal principal = request.getUserPrincipal();
		if (principal != null){
			Long liferayUserId = Long.parseLong(principal.toString());
			Long id = Long.parseLong(request.getParameter("id"));
			SavedBookmarks savedBookmark = savedBookmarksDAO.getSavedBookmark(liferayUserId, id);
			if (savedBookmark.getLiferayUserId() == liferayUserId){
				try {
					savedBookmarksDAO.delete(savedBookmark);
				} catch (Exception e) {
					LOGGER.error(ApeUtil.generateThrowableLog(e));
				}
			}
		}
	}
}
