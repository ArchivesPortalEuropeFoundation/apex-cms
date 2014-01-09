<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet_2_0"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ page contentType="text/html" isELIgnored="false"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="portal" uri="http://portal.archivesportaleurope.eu/tags"%>
<%@ taglib prefix="ape" uri="http://commons.archivesportaleurope.eu/tags"%>
<portal:friendlyUrl var="savedSearchUrl" type="saved-search"/>
<portal:friendlyUrl var="savedSearchPublicUrl" type="saved-search" noHttps="true"/>
<portal:friendlyUrl var="savedSearchPagingUrl" type="saved-search-overview" />
<div id="savedSearchesPortlet">
<c:choose>
	<c:when test="${not empty eadSavedSearches}">
		<h2><fmt:message key="savedsearches.overview.title"/></h2>
		<div id="children" class="box">
			<div class="boxtitle">
				<div class="numberOfPages">
					<ape:pageDescription numberOfItems="${totalNumberOfResults}" pageSize="${pageSize}" pageNumber="${pageNumber}" />
				</div>
				<div id="top-paging" class="paging">
					<ape:paging numberOfItems="${totalNumberOfResults}" pageSize="${pageSize}" pageNumber="${pageNumber}"
						refreshUrl="${savedSearchPagingUrl}/{pageNumber}" pageNumberId="pageNumber"  liferayFriendlyUrl="true"/>

				</div>
			</div>

			<table id="savedSearchesTable" class="defaultlayout">
				<tr>
					<th>ID</th><th class="searchTerms"><fmt:message key="savedsearch.searchterm"/></th><th class="description"><fmt:message key="savedsearch.description"/></th><th class="characteristics"><fmt:message key="savedsearch.characteristics"/></th><th class="modified"><fmt:message key="savedsearch.modified"/></th><th class="actions"><fmt:message key="savedsearches.overview.actions"/></th>
				</tr>
				<c:forEach var="eadSavedSearch" items="${eadSavedSearches}">
				<c:choose>
					<c:when test="${eadSavedSearch.publicSearch}"><c:set var="trClass" value="public"/></c:when>
					<c:otherwise><c:set var="trClass" value=""/></c:otherwise>
				</c:choose>
				<tr class="${trClass}">
					<td><c:out value="${eadSavedSearch.id}"/></td>
					<td><c:out value="${eadSavedSearch.searchTerm}"/></td>
					<td><c:out value="${eadSavedSearch.description}"/></td>
					<td>
						<c:if test="${eadSavedSearch.publicSearch}"><div class="characteristic public"><fmt:message key="savedsearch.publicaccessible"/></div></c:if>
						<c:if test="${eadSavedSearch.template}"><div class="characteristic template"><fmt:message key="savedsearch.template"/></div></c:if>
						<c:if test="${eadSavedSearch.containsSimpleSearchOptions}"><div class="characteristic"><fmt:message key="savedsearch.contains.simple.searchoptions"/></div></c:if>
						<c:if test="${eadSavedSearch.containsAdvancedSearchOptions}"><div class="characteristic"><fmt:message key="advancedsearch.title.advancedsearch"/></div></c:if>
						<c:if test="${eadSavedSearch.containsAlSearchOptions}"><div class="characteristic"><fmt:message key="advancedsearch.title.navigatedsearch"/></div></c:if>
						<c:if test="${eadSavedSearch.containsRefinements}"><div class="characteristic"><fmt:message key="savedsearch.contains.refinements"/></div></c:if>
					</td>
					<td><fmt:formatDate pattern="dd-MMM-yyyy HH:mm z"  value="${eadSavedSearch.modifiedDate}" timeZone="${timeZone}"/></td>
					<td>
						<c:choose>
							<c:when test="${eadSavedSearch.publicSearch}">
							<a href="${savedSearchPublicUrl}/${eadSavedSearch.id}" 
							onclick="return confirm('<fmt:message key="savedsearches.overview.public.areyousure"/>')"> <fmt:message key="savedsearches.overview.viewresults"/></a>
							<a href="${savedSearchPublicUrl}/${eadSavedSearch.id}/new" onclick="return confirm('<fmt:message key="savedsearches.overview.public.areyousure"/>')"><fmt:message key="savedsearches.overview.viewresults.new"/></a></c:when>
							<c:otherwise><a href="${savedSearchUrl}/${eadSavedSearch.id}"><fmt:message key="savedsearches.overview.viewresults"/></a><a href="${savedSearchUrl}/${eadSavedSearch.id}/new"><fmt:message key="savedsearches.overview.viewresults.new"/></a></c:otherwise>
						</c:choose>
						<portlet:renderURL var="editUrl">
							<portlet:param name="myaction" value="editSavedSearchForm" />
							<portlet:param name="id" value="${eadSavedSearch.id}" />
							<portlet:param name="overviewPageNumber" value="${pageNumber}" />
						</portlet:renderURL>
						<a
						href="${editUrl}"><fmt:message key="savedsearches.overview.edit"/></a>
					<a href="<portlet:actionURL>
							<portlet:param name="myaction" value="deleteSavedSearch" />
							<portlet:param name="id" value="${eadSavedSearch.id}" />
						</portlet:actionURL>					
					" onclick="return confirm('<fmt:message key="savedsearches.overview.delete.areyousure"/>')"><fmt:message key="savedsearches.overview.delete"/></a>
						</td>
					</tr>
				</c:forEach>
			</table>
		</div>

	</c:when>
	<c:otherwise>
		<h2><fmt:message key="savedsearches.overview.nothing"/></h2>
	</c:otherwise>
</c:choose>
</div>