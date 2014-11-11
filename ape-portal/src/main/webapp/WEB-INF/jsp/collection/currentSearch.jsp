<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet_2_0"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ page contentType="text/html" isELIgnored="false"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="portal" uri="http://portal.archivesportaleurope.eu/tags"%>
<%@ taglib prefix="ape" uri="http://commons.archivesportaleurope.eu/tags"%>
<portal:friendlyUrl var="savedSearchUrl" type="saved-search"/>
<portal:friendlyUrl var="savedSearchPublicUrl" type="saved-search" noHttps="true"/>

<div class="collectionSearchFields" id="collectionSearchFields">
	<c:if test="${currentSearches!=null}">
	<div class="collectionSearchField" id="collectionSearchFields">
		<c:if test="${currentSearches!=null && currentSearches.size() > 0}">
			<div><fmt:message key="advancedsearch.text.savedsearches"/>:</div>
			<c:choose>
				<c:when test="${edit}">
					<portlet:resourceURL var="getSearchesUrl" id="getSearches">
						<portlet:param name="edit" value="true" />
					</portlet:resourceURL>
				</c:when>
				<c:otherwise>
					<portlet:resourceURL var="getSearchesUrl" id="getSearches">
						<portlet:param name="edit" value="false" />
					</portlet:resourceURL>
				</c:otherwise>
			</c:choose>
			<div class="collectionPaginator">
				<div class="boxtitle">
					<div class="numberOfPages">
						<ape:pageDescription numberOfItems="${totalNumberOfResults}" pageSize="${pageSize}" pageNumber="${pageNumber}" />
					</div>
					<div id="child-paging" class="paging">
						<ape:paging numberOfItems="${totalNumberOfResults}" pageSize="${pageSize}" pageNumber="${pageNumber}" refreshUrl="javascript:updatePageNumberCurrentSearches('${getSearchesUrl}');" pageNumberId="pageNumber" />
					</div>
				</div>
			</div>
			<table class="defaultlayout" id="currentSearch">
				<tr>
 					<c:if test="${edit}">
						<th class="collectionSearchCheckbox"><fmt:message key="collections.signal"/></th>
					</c:if> 
						<th class="collectionSearchId"><fmt:message key="savedsearch.id"/></th>
						<th class="<c:choose><c:when test="${edit}">collectionSearchTerms</c:when><c:otherwise>collectionSearchTerms2</c:otherwise></c:choose>"><fmt:message key="advancedsearch.eaccpf.element.name"/></th>
						<th class="<c:choose><c:when test="${edit}">collectionSearchDate</c:when><c:otherwise>collectionSearchDate2</c:otherwise></c:choose>"><fmt:message key="advancedsearch.text.date"/></th>
					<c:if test="${!edit}">
						<th class="collectionSearchAction"><fmt:message key="savedsearches.overview.actions"/></th>
					</c:if>					
				</tr>
				<c:forEach var="currentSearch" items="${currentSearches}">
					<tr id="newCollectionSearches_<c:out value="${currentSearch.id}" />">
						<c:if test="${edit}">
							<td class="collectionSearchCheckbox"><input type="checkbox" name="selected_search_${currentSearch.id}" id="selected_search_${currentSearch.id}" checked="checked" /></td>
						</c:if>
						<td class="collectionSearchId"><c:out value="${currentSearch.eadSavedSearch.id}" /></td>
						<td class="<c:choose><c:when test="${edit}">collectionSearchTerms</c:when><c:otherwise>collectionSearchTerms2</c:otherwise></c:choose>"><c:out value="${currentSearch.eadSavedSearch.searchTerm}" /></td> 
						<td class="collectionSearchDate2"><fmt:formatDate pattern="dd-MMM-yyyy HH:mm z"  value="${currentSearch.eadSavedSearch.modifiedDate}" timeZone="${timeZone}"/></td>
						<c:if test="${!edit}">
							<td class="collectionSearchAction">
								<c:choose>
									<c:when test="${currentSearch.eadSavedSearch.publicSearch}">
										<a href="${savedSearchPublicUrl}/${currentSearch.eadSavedSearch.id}" onclick="return confirm('<fmt:message key="savedsearches.overview.public.areyousure"/>')"> <fmt:message key="savedsearches.overview.viewresults"/></a><br/>
										<a href="${savedSearchPublicUrl}/${currentSearch.eadSavedSearch.id}/new" onclick="return confirm('<fmt:message key="savedsearches.overview.public.areyousure"/>')"><fmt:message key="savedsearches.overview.viewresults.new"/></a>
									</c:when>
									<c:otherwise>
										<a href="${savedSearchUrl}/${currentSearch.eadSavedSearch.id}"><fmt:message key="savedsearches.overview.viewresults"/></a><br/>
										<a href="${savedSearchUrl}/${currentSearch.eadSavedSearch.id}/new"><fmt:message key="savedsearches.overview.viewresults.new"/></a>
									</c:otherwise>
								</c:choose>
							</td>
						</c:if>
					</tr>
				</c:forEach>
			</table>
			<br/>
		</c:if>
	</div>
	</c:if>
</div>