<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet_2_0"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ page contentType="text/html" isELIgnored="false"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="portal" uri="http://portal.archivesportaleurope.eu/tags"%>
<%@ taglib prefix="ape" uri="http://commons.archivesportaleurope.eu/tags"%>
<%-- <portal:friendlyUrl var="myCollectionUrl" type="saved-collection-overview"/> --%>
<div class="collectionSearchFields" id="collectionSearchFields">
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
		
		<table class="defaultlayout" id="currentSearch">
				<tr>
				<c:if test="${edit}">
					<th ><fmt:message key="collections.signal"/></th>
				</c:if>
					<th><fmt:message key="savedsearch.id"/></th>
					<th><fmt:message key="advancedsearch.eaccpf.element.name"/></th>
					<th class="<c:choose><c:when test="${edit}">collectionSearchDate</c:when><c:otherwise>collectionSearchDate2</c:otherwise></c:choose>"><fmt:message key="advancedsearch.text.date"/></th>
				<c:if test="${!edit}">
					<th><fmt:message key="savedsearches.overview.actions"/></th>
				</c:if>	
			</tr>
			<c:forEach var="collectionBookmark" items="${currentBookmarks}">
				<tr id="newCollectionBookmarks_<c:out value="${collectionBookmark.savedBookmarks.id}" />">
					<c:if test="${edit}">
						<td class="collectionSearchCheckbox"><input type="checkbox" name="collection_bookmark_${collectionBookmark.id}" id="collection_bookmark_${collectionBookmark.id}" checked="checked" /></td>
					</c:if>
					<td  class="collectionSearchId"><c:out value="${collectionBookmark.savedBookmarks.id}" /></td>
					<td class="<c:choose><c:when test="${edit}">collectionSearchTerms</c:when><c:otherwise>collectionSearchTerms2</c:otherwise></c:choose>"><c:out value="${collectionBookmark.savedBookmarks.name}" /></td>
					<td class="collectionSearchDate2"><fmt:formatDate pattern="dd-MMM-yyyy HH:mm z"  value="${collectionBookmark.savedBookmarks.modifiedDate}" timeZone="${timeZone}"/></td>
					<c:if test="${!edit}">
						<td class="collectionSearchAction">
							<a href="${collectionBookmark.savedBookmarks.link}"><fmt:message key="collections.bookmarks.see"/></a>
						</td>
					</c:if>
				</tr>
			</c:forEach>
		</table>
		<br/>
	</c:if>
</div>