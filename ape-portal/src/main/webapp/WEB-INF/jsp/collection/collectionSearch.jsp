<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet_2_0"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ page contentType="text/html" isELIgnored="false"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="portal" uri="http://portal.archivesportaleurope.eu/tags"%>
<%@ taglib prefix="ape" uri="http://commons.archivesportaleurope.eu/tags"%>
<%-- <portal:friendlyUrl var="myCollectionUrl" type="saved-collection-overview"/> --%>
<div id="collectionSearch">
	<c:if test="${searches!=null}">
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
	
	<div class="collectionSearchesHeader" id="collectionSearchesHeader">
		<div class="collectionSearchCheckbox"><fmt:message key="collections.signal"/></div>
		<div class="collectionSearchId"><fmt:message key="savedsearch.id"/></div>
		<div class="collectionSearchTerms"><fmt:message key="collections.terms"/></div>
		<div class="collectionSearchDate"><fmt:message key="advancedsearch.text.date"/></div>
	</div>
	<c:forEach var="search" items="${searches}">
		<div class="collectionSearches" id="newCollectionSearches_<c:out value="${search.id}" />">
			<div class="collectionSearchCheckbox"><input type="checkbox" name="new_search_${search.id}" id="new_search_${search.id}" /></div>
			<div class="collectionSearchId"><c:out value="${search.id}" /></div>
			<div class="collectionSearchTerms"><c:out value="${search.searchTerm}" /></div>
			<div class="collectionSearchDate"><fmt:formatDate pattern="dd-MMM-yyyy HH:mm z"  value="${search.modifiedDate}" timeZone="${timeZone}"/></div>
		</div>
	</c:forEach>
	</c:if>
</div>