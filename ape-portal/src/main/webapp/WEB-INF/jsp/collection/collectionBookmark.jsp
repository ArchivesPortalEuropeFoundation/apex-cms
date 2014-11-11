<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet_2_0"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ page contentType="text/html" isELIgnored="false"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="portal" uri="http://portal.archivesportaleurope.eu/tags"%>
<%@ taglib prefix="ape" uri="http://commons.archivesportaleurope.eu/tags"%>
<%-- <portal:friendlyUrl var="myCollectionUrl" type="saved-collection-overview"/> --%>
<div class="collectionSearchFields" id="collectionSearchFields">
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
		<table class="defaultlayout" id="currentSearch">
			<tr>
				<th class="collectionSearchCheckbox"><fmt:message key="collections.signal"/></th>
				<th class="collectionSearchId"><fmt:message key="savedsearch.id"/></th>
				<th class="collectionSearchTerms"><fmt:message key="advancedsearch.eaccpf.element.name"/></th>
				<th class="collectionSearchDate"><fmt:message key="advancedsearch.text.date"/></th>
			</tr>
			<c:forEach var="bookmark" items="${bookmarks}">
				<tr id="newCollectionBookmarks_<c:out value="${bookmark.id}" />">
					<td class="collectionSearchCheckbox"><input type="checkbox" name="new_bookmark_${bookmark.id}" id="new_bookmark_${bookmark.id}" /></td>
					<td class="collectionSearchId"><c:out value="${bookmark.id}" /></td>
					<td class="collectionSearchTerms"><c:out value="${bookmark.name}" /></td>
					<td class="collectionSearchDate2"><fmt:formatDate pattern="dd-MMM-yyyy HH:mm z" value="${bookmark.modifiedDate}" timeZone="${timeZone}"/></td>
				</tr>
			</c:forEach>
		</table>
		<br/>
	</c:if>
</div>