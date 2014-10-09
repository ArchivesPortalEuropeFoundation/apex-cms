<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet_2_0"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ page contentType="text/html" isELIgnored="false"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="portal" uri="http://portal.archivesportaleurope.eu/tags"%>
<%@ taglib prefix="ape" uri="http://commons.archivesportaleurope.eu/tags"%>


<div class="collectionSearchField" id="collectionSearchFields">
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
			<div class="collectionSearchesHeader" id="collectionSearchesHeader">
				<c:if test="${edit}">
					<div class="collectionSearchCheckbox"><fmt:message key="collections.signal"/></div>
				</c:if>
				<div class="collectionSearchId"><fmt:message key="savedsearch.id"/></div>
				<div class="<c:choose><c:when test="${edit}">collectionSearchTerms</c:when><c:otherwise>collectionSearchTerms2</c:otherwise></c:choose>"><fmt:message key="collections.terms"/></div>
				<div class="<c:choose><c:when test="${edit}">collectionSearchDate</c:when><c:otherwise>collectionSearchDate2</c:otherwise></c:choose>"><fmt:message key="advancedsearch.text.date"/></div>
			</div>
			<div id="collectionSearches" >
				<c:forEach var="currentSearch" items="${currentSearches}">
					<div class="collectionSearches" id="newCollectionSearch_<c:out value="${currentSearch.id}" />">
					<c:if test="${edit}">
						<div class="collectionSearchCheckbox"><input type="checkbox" name="selected_search_${currentSearch.id}" id="selected_search_${currentSearch.id}" checked="checked" /></div>
					</c:if>
						<div class="collectionSearchId"><c:out value="${currentSearch.eadSavedSearch.id}" /></div>
						<div class="<c:choose><c:when test="${edit}">collectionSearchTerms</c:when><c:otherwise>collectionSearchTerms2</c:otherwise></c:choose>"><c:out value="${currentSearch.eadSavedSearch.searchTerm}" /></div>
						<div class="<c:choose><c:when test="${edit}">collectionSearchDate</c:when><c:otherwise>collectionSearchDate2</c:otherwise></c:choose>"><fmt:formatDate pattern="dd-MMM-yyyy HH:mm z"  value="${currentSearch.eadSavedSearch.modifiedDate}" timeZone="${timeZone}"/></div>
						<c:if test="${!edit}">
							<div class="collectionSearchAction">
								<c:choose>
									<c:when test="${collectionSearch.eadSavedSearch.publicSearch}">
										<div>
											<a href="${savedSearchPublicUrl}/${collectionSearch.eadSavedSearch.id}" > <fmt:message key="savedsearches.overview.viewresults"/></a>
										</div>
										<div>
											<a href="${savedSearchPublicUrl}/${collectionSearch.eadSavedSearch.id}/new" ><fmt:message key="savedsearches.overview.viewresults.new"/></a>
										</div>
									</c:when>
									<c:otherwise>
										<div>
											<a href="${savedSearchUrl}/${collectionSearch.eadSavedSearch.id}"><fmt:message key="savedsearches.overview.viewresults"/></a>
										</div>
										<div>
											<a href="${savedSearchUrl}/${collectionSearch.eadSavedSearch.id}/new"><fmt:message key="savedsearches.overview.viewresults.new"/></a>
										</div>
									</c:otherwise>
								</c:choose>
							</div>
						</c:if>
					</div>
				</c:forEach>
			</div>
		</c:if>
	</div>
	</c:if>
</div>