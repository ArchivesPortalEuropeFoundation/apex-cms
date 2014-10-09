<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet_2_0"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ page contentType="text/html" isELIgnored="false"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="portal" uri="http://portal.archivesportaleurope.eu/tags"%>
<%@ taglib prefix="ape" uri="http://commons.archivesportaleurope.eu/tags"%>
<%-- <portal:friendlyUrl var="myCollectionUrl" type="saved-collection-overview"/> --%>
<div id="collectionBookmarks">
	<c:if test="${bookmarks!=null}">
	<portlet:resourceURL var="getNewBookmarksUrl" id="getNewBookmarks"></portlet:resourceURL>
	<div><fmt:message key="collections.bookmarks.out"/></div>
	
	<div class="collectionPaginator">
		<div class="boxtitle">
			<div class="numberOfPages">
				<ape:pageDescription numberOfItems="${totalNumberOfResults}" pageSize="${pageSize}" pageNumber="${pageNumber}" />
			</div>
			<div id="child-paging" class="paging">
				<ape:paging numberOfItems="${totalNumberOfResults}" pageSize="${pageSize}" pageNumber="${pageNumber}" refreshUrl="javascript:updatePageNumberCollectionBookmarks('${getNewBookmarksUrl}');" pageNumberId="pageNumber" />
			</div>
		</div>
	</div>
	
	<div class="collectionSearchesHeader" id="collectionBookmarksHeader">
		<div class="collectionSearchCheckbox"><fmt:message key="collections.signal"/></div>
		<div class="collectionSearchId"><fmt:message key="savedsearch.id"/></div>
		<div class="collectionSearchTerms"><fmt:message key="advancedsearch.eaccpf.element.name"/></div>
		<div class="collectionSearchDate"><fmt:message key="savedsearch.description"/></div>
	</div>
	<c:forEach var="bookmark" items="${bookmarks}">
		<div class="collectionSearches" id="newCollectionBookmarks_<c:out value="${bookmark.id}" />">
			<div class="collectionSearchCheckbox"><input type="checkbox" name="new_bookmark_${bookmark.id}" id="new_bookmark_${bookmark.id}" /></div>
			<div class="collectionSearchId"><c:out value="${bookmark.id}" /></div>
			<div class="collectionSearchTerms"><c:out value="${bookmark.name}" /></div>
			<div class="collectionSearchDate"><c:out value="${bookmark.description}" /></div>
		</div>
	</c:forEach>
	</c:if>
</div>