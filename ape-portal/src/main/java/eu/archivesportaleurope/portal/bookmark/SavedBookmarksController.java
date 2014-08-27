package eu.archivesportaleurope.portal.bookmark;

import java.io.IOException;
import java.security.Principal;
import java.util.List;

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

import eu.apenet.persistence.dao.SavedBookmarksDAO;
import eu.apenet.persistence.vo.SavedBookmarks;
import eu.archivesportaleurope.portal.common.FriendlyUrlUtil;
import eu.archivesportaleurope.portal.common.PortalDisplayUtil;

@Controller(value = "BookmarkController")
@RequestMapping(value = "VIEW")
public class SavedBookmarksController {
	private final static Logger LOGGER = Logger.getLogger(SavedBookmarksController.class);
	private final static int PAGESIZE  = 10;
	private SavedBookmarksDAO savedBookmarksDAO;
	public void setBookmarkDAO(SavedBookmarksDAO savedBookmarksDAO) {
		this.savedBookmarksDAO = savedBookmarksDAO;
	}

	public void setMessageSource(ResourceBundleMessageSource messageSource) {
	}

	// --maps the incoming portlet request to this method
	@RenderMapping
	public ModelAndView showBookmarkes(RenderRequest request) {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("home");
		PortalDisplayUtil.setPageTitle(request, PortalDisplayUtil.TITLE_SAVED_BOOKMARK);
		Principal principal = request.getUserPrincipal();
		if (principal != null){
			Long liferayUserId = Long.parseLong(principal.toString());
			Integer pageNumber = 1;
			if (StringUtils.isNotBlank(request.getParameter("pageNumber"))){
				pageNumber = Integer.parseInt(request.getParameter("pageNumber"));
			}
			List<SavedBookmarks> Bookmarkes = savedBookmarksDAO.getSavedBookmarks(liferayUserId, pageNumber, PAGESIZE);
			User user = (User) request.getAttribute(WebKeys.USER);
			boolean loggedIn = request.getUserPrincipal() != null;
			modelAndView.getModelMap().addAttribute("timeZone", user.getTimeZone());
			modelAndView.getModelMap().addAttribute("pageNumber", pageNumber);
			modelAndView.getModelMap().addAttribute("totalNumberOfResults", savedBookmarksDAO.countSavedBookmarks(liferayUserId));
			modelAndView.getModelMap().addAttribute("pageSize", PAGESIZE);
			modelAndView.getModelMap().addAttribute("Bookmarkes",Bookmarkes);
	        modelAndView.getModelMap().addAttribute("loggedIn",loggedIn);

		}
		return modelAndView;
	}

	@ActionMapping(params="myaction=deleteBookmark")
	public void deleteBookmark(ActionRequest request) {
		Principal principal = request.getUserPrincipal();
		if (principal != null){
			Long liferayUserId = Long.parseLong(principal.toString());
			Long id = Long.parseLong(request.getParameter("id"));
			SavedBookmarks bookmark = savedBookmarksDAO.getSavedBookmark(id, liferayUserId);
			if (bookmark.getLiferayUserId() == liferayUserId){
				savedBookmarksDAO.delete(bookmark);
			}
		}
	}
	@RenderMapping(params="myaction=editBookmarkForm")
	public String showEditBookmarkForm() {
		return "editBookmarkForm";
	}

	@ActionMapping(params="myaction=saveEditBookmark")
	public void saveBookmark(@ModelAttribute("Bookmark") SavedBookmarksController bookmark, BindingResult bindingResult,ActionRequest request, ActionResponse response) throws IOException  {
		Principal principal = request.getUserPrincipal();
		if (principal != null){
			Long liferayUserId = Long.parseLong(principal.toString());
			SavedBookmarks bookmark1 = savedBookmarksDAO.getSavedBookmark(Long.parseLong(bookmark.getId()), liferayUserId);
			if (bookmark1.getLiferayUserId() == liferayUserId){
				bookmark1.setDescription(bookmark1.getDescription());
				savedBookmarksDAO.store(bookmark1);
				response.sendRedirect(FriendlyUrlUtil.getRelativeUrl(FriendlyUrlUtil.SAVED_BOOKMARKS_OVERVIEW));
			}
		}	
	}
	
	@ModelAttribute("bookmark")
	public SavedBookmarks getBookmark(PortletRequest request) {
		Principal principal = request.getUserPrincipal();
		String id = request.getParameter("id");
		SavedBookmarks bookmark = new SavedBookmarks();
		if (principal != null && StringUtils.isNotBlank(id)){
			Long liferayUserId = Long.parseLong(principal.toString());
			bookmark = savedBookmarksDAO.getSavedBookmark(Long.parseLong(id), liferayUserId);
			if (bookmark.getLiferayUserId() == liferayUserId){
				bookmark.setDescription(bookmark.getDescription());
				bookmark.setModifiedDate(bookmark.getModifiedDate());
				bookmark.setId(bookmark.getId());
			}
		}		
		
		return bookmark;
	}
	
	public String getDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getId() {
		// TODO Auto-generated method stub
		return null;
	}
}
