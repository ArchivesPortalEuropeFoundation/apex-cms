<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet_2_0"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ page contentType="text/html" isELIgnored="false"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="portal" uri="http://portal.archivesportaleurope.eu/tags"%>
<%@ taglib prefix="ape" uri="http://commons.archivesportaleurope.eu/tags"%>
<div class="collectionSearchFields" id="collectionSearchFields">
	<c:if test="${searches!=null}">
	<br/>
	<portlet:resourceURL var="getNewSearchesUrl" id="getNewSearches"></portlet:resourceURL>
	<div><fmt:message key="collections.searches.out"/></div>
	
	<div class="collectionPaginator">
		<div class="boxtitle">
			<div class="numberOfPages">
				<ape:pageDescription numberOfItems="${totalNumberOfResults}" pageSize="${pageSize}" pageNumber="${pageNumber}" />
			</div>
			<div id="child-paging" class="paging">
				<ape:paging numberOfItems="${totalNumberOfResults}" pageSize="${pageSize}" pageNumber="${pageNumber}" refreshUrl="javascript:updatePageNumberCollectionSearches('${getNewSearchesUrl}');" pageNumberId="pageNumber" />
			</div>
		</div>
	</div>
		<table class="defaultlayout" id="currentSearch">
			<tr>
				<th class="collectionSearchCheckbox"><fmt:message key="collections.signal"/></th>
				<th class="collectionSearchId"><fmt:message key="savedsearch.id"/></th>
				<th class="collectionSearchTerms"><fmt:message key="advancedsearch.eaccpf.element.name"/></th>
				<th class="collectionSearchDescription"><fmt:message key="savedsearch.description"/></th>
				<th class="collectionSearchDate"><fmt:message key="advancedsearch.text.date"/></th>
			</tr>
			<c:forEach var="search" items="${searches}">
				<tr id="newCollectionSearches_<c:out value="${search.id}" />">
					<td class="collectionSearchCheckbox"><input type="checkbox" name="new_search_${search.id}" id="new_search_${search.id}" /></td>
					<td class="collectionSearchId"><c:out value="${search.id}" /></td>
					<td class="collectionSearchTerms"><c:out value="${search.searchTerm}" /></td>
					<td class="collectionSearchDescription2"><c:out value="${search.description}" /></td>
					<td class="collectionSearchDate2"><fmt:formatDate pattern="dd-MMM-yyyy HH:mm z" value="${search.modifiedDate}" timeZone="${timeZone}"/></td>
				</tr>
			</c:forEach>
		</table>
		<br/>
	</c:if>
</div>