package eu.archivesportaleurope.portal.bookmark;

import java.io.IOException;
import java.security.Principal;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletRequest;
import javax.portlet.RenderRequest;
import javax.portlet.ResourceRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
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

import eu.apenet.persistence.vo.SavedBookmarks;
import eu.archivesportaleurope.persistence.jpa.dao.SavedBookmarksJpaDAO;
import eu.archivesportaleurope.portal.common.FriendlyUrlUtil;
import eu.archivesportaleurope.portal.common.PortalDisplayUtil;
import eu.archivesportaleurope.util.ApeUtil;

/***
 * This is the controller for the Bookmark Class
 * 
 */
@Controller(value = "bookmarkController")
@RequestMapping(value = "VIEW")
public class BookmarkController {

    private static final Logger LOGGER = Logger.getLogger(BookmarkController.class);
    private SavedBookmarksJpaDAO savedBookmarksDAO;
    private final static int PAGESIZE  = 10;
	
	public void setSavedBookmarksDAO(SavedBookmarksJpaDAO savedBookmarksDAO) {
		this.savedBookmarksDAO = savedBookmarksDAO;
	}

	/***
	 * Maps the incoming portlet request to this method from My pages space<br/>
	 * This method is called from My Bookmarks in My Pages space to show the bookmarks the user has<br/>
	 * This method is called from "save bookmark" button when the user clicks in the "edit" button
	 * 
	 * @param request {@link RenderRequest} is empty when it cames from My bookmarks, [pageNumber] when it comes from edit bookmark
	 * 
	 * @return modelAndView {@link ModelAndView} ModelAndView: reference to view with name 'home'; model is {timeZone,transitions,lastRule, pageNumber, totalNumberOfResults, pageSize, savedBookmarks[]}
	 */
 	@RenderMapping
 	public ModelAndView showSavedBookmarks(RenderRequest request) {
 		if (LOGGER.isDebugEnabled()) 
 			LOGGER.debug("Enter in method \"showSavedBookmarks\"");
 		
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
 		if (LOGGER.isDebugEnabled()) 
 			LOGGER.debug("Exit in method \"showSavedBookmarks\"");
 		
 		return modelAndView;
 	}

 	/***
 	 * maps the incoming portlet request to this method from Edit Bookmark in My pages space
 	 * 
 	 * @return "editSavedBookmarksForm"
 	 */
	@RenderMapping(params="myaction=editSavedBookmarksForm")
	public String showEditSavedBookmarksForm() {
		if (LOGGER.isDebugEnabled()) 
			LOGGER.debug("Enter in method \"showEditSavedBookmarksForm\"");
		return "editSavedBookmarksForm";
	}
	
	/***
	 * This method saves a bookmark in the My Bookmarks in the My Pages space
	 * 
	 * @param bookmark {@link Bookmark} bookmark object
	 * @param bindingResult {@link BindingResult} bindingResult object name: "savedBookmark"
	 * @param request {@link ActionRequest} request parameters: {id, description, myaction, overviewPageNumber}
	 * @param response {@link ActionResponse} response builds the friendlyurl and gets the location to the redirect
	 * 
	 * @throws IOException
	 */
	@ActionMapping(params="myaction=saveEditSavedBookmarks")
	public void saveSavedBookmark(@ModelAttribute("savedBookmark") Bookmark bookmark, BindingResult bindingResult,ActionRequest request, ActionResponse response) throws IOException  {
		if (LOGGER.isDebugEnabled()) 
			LOGGER.debug("Enter in method \"saveSavedBookmark\"");
		
		Principal principal = request.getUserPrincipal();
		if (principal != null){
			Long liferayUserId = Long.parseLong(principal.toString());
			SavedBookmarks savedBookmark = savedBookmarksDAO.getSavedBookmark(liferayUserId, Long.parseLong(bookmark.getId()));
			if (savedBookmark.getLiferayUserId() == liferayUserId){
				savedBookmark.setDescription(bookmark.getDescription());
				try {
					savedBookmarksDAO.store(savedBookmark);
					response.sendRedirect(FriendlyUrlUtil.getRelativeUrl(FriendlyUrlUtil.SAVED_BOOKMARKS_OVERVIEW) + FriendlyUrlUtil.SEPARATOR + bookmark.getOverviewPageNumber());
					if (LOGGER.isDebugEnabled()) 
						LOGGER.debug("Exit in method \"saveSavedBookmark\"");
					
				} catch (Exception e) {
					LOGGER.error(ApeUtil.generateThrowableLog(e));
				}
			}
		}	
	}

	/***
	 * This method gets a bookmark object in the Saved Bookmarks page in My Pages space
	 * 
	 * @param request {@link PortletRequest}, request has not value vhen comes from My Collections, it has values: {id, myaction, overviewPageNumber} when it cames from "edit bookmark"
	 * 
	 * @return Bookmark {@link Bookmark}, void when is calld from My Collections, full bookmark object when it is called from "edit bookmark"
	 */
	@ModelAttribute("savedbookmarks")
	public Bookmark getSavedBookmark(PortletRequest request) { 
		if (LOGGER.isDebugEnabled()) 
			LOGGER.debug("Enter in method \"getSavedBookmark\"");
		
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
		if (LOGGER.isDebugEnabled()) 
			LOGGER.debug("Exit in method \"getSavedBookmark\"");
		
		return bookmark;
	}
	
	/***
	 * This method gets the bookmark object to show in the saved bookmarks list in My Space
	 * 
	 * @return Bookmark {@link Bookmark} object
	 */
    @ModelAttribute("bookmark")
    public Bookmark getCommandObject() {
    	if (LOGGER.isDebugEnabled()) 
    		LOGGER.debug("Enter in method \"getCommandObject\"");
    	
        return new Bookmark();
    }
    	
    /***
     * This method gets the current user
     * 
     * @param bookmark{@link Bookmark} bookmark object
     * @param result {@link BindingResult} result
	 * @param request {@link ResourceRequest} request has not value vhen comes from My Collections, it has values: {id, myaction, overviewPageNumber}
     * 
     * @return modelAndView {@link ModelAndView} ModelAndView: reference to view with name 'home'; model is {timeZone,transitions,lastRule, pageNumber, totalNumberOfResults, pageSize, savedBookmarks[]}
     * 
     * @throws PortalException
     * @throws SystemException
     */
    @ResourceMapping(value="bookmarkAction")
    public ModelAndView showResult(@ModelAttribute("bookmark") Bookmark bookmark, BindingResult result, ResourceRequest request) throws PortalException, SystemException { 
    	if (LOGGER.isDebugEnabled()) 
    		LOGGER.debug("Enter in method \"showResult\"");
    	
        ModelAndView modelAndView = new ModelAndView();
    	boolean loggedIn = request.getUserPrincipal() != null;
    	User currentUser = null;
    	if (loggedIn){
    		currentUser = (User) PortalUtil.getUser(request);
    		modelAndView.getModelMap().addAttribute("currentUser", currentUser);
    	}
		modelAndView.setViewName("success");
		if (LOGGER.isDebugEnabled()) 
			LOGGER.debug("Exit in method \"showResult\"");
		
		return modelAndView;
    }
	
}
