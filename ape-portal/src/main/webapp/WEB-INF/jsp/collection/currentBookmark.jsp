<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet_2_0"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ page contentType="text/html" isELIgnored="false"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="portal" uri="http://portal.archivesportaleurope.eu/tags"%>
<%@ taglib prefix="ape" uri="http://commons.archivesportaleurope.eu/tags"%>
<%-- <portal:friendlyUrl var="myCollectionUrl" type="saved-collection-overview"/> --%>
<div id="collectionBookmarks">
	<c:if test="${currentBookmarks!=null}">
		<c:choose>
			<c:when test="${edit}">
				<portlet:resourceURL var="getBookmarksUrl" id="getBookmarks">
					<portlet:param name="edit" value="true" />
				</portlet:resourceURL>
			</c:when>
			<c:otherwise>
				<portlet:resourceURL var="getBookmarksUrl" id="getBookmarks">
					<portlet:param name="edit" value="false" />
				</portlet:resourceURL>
			</c:otherwise>
		</c:choose>
		<div><fmt:message key="collections.bookmarks.saved"/></div>
		
		<div class="collectionPaginator">
			<div class="boxtitle">
				<div class="numberOfPages">
					<ape:pageDescription numberOfItems="${totalNumberOfResults}" pageSize="${pageSize}" pageNumber="${pageNumber}" />
				</div>
				<div id="child-paging" class="paging">
					<ape:paging numberOfItems="${totalNumberOfResults}" pageSize="${pageSize}" pageNumber="${pageNumber}" refreshUrl="javascript:updatePageNumberCurrentBookmarks('${getBookmarksUrl}');" pageNumberId="pageNumber" />
				</div>
			</div>
		</div>
		
		<div class="collectionSearchesHeader" id="collectionBookmarksHeader">
			<c:if test="${edit}">
				<div class="collectionSearchCheckbox"><fmt:message key="collections.signal"/></div>
			</c:if>
			<div class="collectionSearchId"><fmt:message key="savedsearch.id"/></div>
			<div class="<c:choose><c:when test="${edit}">collectionSearchTerms</c:when><c:otherwise>collectionSearchTerms2</c:otherwise></c:choose>">Name</div>
			<div class="<c:choose><c:when test="${edit}">collectionSearchDate</c:when><c:otherwise>collectionSearchDate2</c:otherwise></c:choose>">Description</div>
		</div>
		
		<c:forEach var="collectionBookmark" items="${currentBookmarks}">
			<div class="collectionSearches" id="newCollectionBookmarks_<c:out value="${collectionBookmark.id}" />">
				<c:if test="${edit}">
					<div class="collectionSearchCheckbox"><input type="checkbox" name="collection_bookmark_${collectionBookmark.id}" id="collection_bookmark_${collectionBookmark.id}" checked="checked" /></div>
				</c:if>
				<div class="collectionSearchId"><c:out value="${collectionBookmark.savedBookmarks.id}" /></div>
				<div class="<c:choose><c:when test="${edit}">collectionSearchTerms</c:when><c:otherwise>collectionSearchTerms2</c:otherwise></c:choose>"><c:out value="${collectionBookmark.savedBookmarks.name}" /></div>
				<div class="<c:choose><c:when test="${edit}">collectionSearchDate</c:when><c:otherwise>collectionSearchDate2</c:otherwise></c:choose>"><c:out value="${collectionBookmark.savedBookmarks.description}" /></div>
				<c:if test="${!edit}">
				<div class="collectionSearchAction">
					<a href="${collectionBookmark.savedBookmarks.link}"><fmt:message key="collections.bookmarks.see"/></a>
				</div>
				</c:if>
			</div>
		</c:forEach>
	</c:if>
</div>