<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet_2_0"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ page contentType="text/html" isELIgnored="false"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="portal" uri="http://portal.archivesportaleurope.eu/tags"%>
<%@ taglib prefix="ape" uri="http://commons.archivesportaleurope.eu/tags"%>

<portlet:renderURL var="myCollectionUrl" >
	<portlet:param name="action" value="viewCollection" />
</portlet:renderURL>

<portlet:renderURL var="myCollections" />

<portlet:renderURL var="addSearchesTo">
	<portlet:param name="myaction" value="addSearchesTo" />
	<portlet:param name="eadSavedSearches_id" value="${savedSearch.id}" />
	<portlet:param name="overviewPageNumber" value="${pageNumber}" />
	<portlet:param name="listChecked" value="${listChecked}"/>
</portlet:renderURL>

<portlet:renderURL var="addSavedSearchesFormUrl">
	<portlet:param name="myaction" value="addSavedSearchesForm" />
	<portlet:param name="id" value="${eadSavedSearch.id}" />
	<portlet:param name="overviewPageNumber" value="${pageNumber}" />
	<portlet:param name="listChecked" value="${listChecked}"/>
</portlet:renderURL>

<script type="text/javascript">
	$(document).ready(function(){
		showCheckedValuesInCollections("${listChecked}");
	});
</script>

<div id="mycollectionPortletDiv">
	<form id="frm" name="frm" method="post" action="${addSearchesTo}">
		<c:choose>
			<c:when test="${not empty collections}">
				<h2><fmt:message key="collections.searchAddTo"/></h2>
				<div id="noCollectionsSelected" class="error">
					<c:if test="${isNoCollectionsSelected}">
						<fmt:message key="collections.select.one"/>
					</c:if>
				</div>
				<div class="collectionPaginator">
					<div class="boxtitle">
						<div class="numberOfPages">
							<ape:pageDescription numberOfItems="${totalNumberOfResults}" pageSize="${pageSize}" pageNumber="${pageNumber}" />
						</div>		
						<div id="child-paging" class="paging">
							<ape:paging numberOfItems="${totalNumberOfResults}" 
							pageSize="${pageSize}" pageNumber="${pageNumber}" 
							refreshUrl="${addSavedSearchesFormUrl}" pageNumberId="pageNumber" />							 
						</div>
					</div>
				</div>
				<table class="defaultlayout" id="currentSearch">
					<tr>
						<th class="collectionSearchCheckbox"><fmt:message key="collections.signal"/></th>
						<th class="collectionSearchId"><fmt:message key="savedsearch.id"/></th>
						<th class="collectionSearchTerms2"><fmt:message key="advancedsearch.eaccpf.element.name"/></th>
						<th class="collectionSearchDate"><fmt:message key="advancedsearch.text.date"/></th>
					</tr>
					<c:forEach var="collection" items="${collections}">
						<tr id="CollectionSearches_<c:out value="${collection.id}" />">
							<td class="collectionSearchCheckbox">
								<input type="checkbox" name="collectionToAdd_${collection.id}" id="collectionToAdd_${collection.id}" 
								onclick="javascript:updateSelectedCollections(this.id,'${listChecked}', 'search');"/>
							</td>
							<td class="collectionSearchId"><c:out value="${collection.id}" /></td>
							<td class="collectionSearchTerms2"><c:out value="${collection.title}" /></td>
							<td class="collectionSearchDate2"><fmt:formatDate pattern="dd-MMM-yyyy HH:mm z" value="${collection.modified_date}" timeZone="${timeZone}"/></td>
						</tr>
					</c:forEach>
				</table>
				<div id="storeCollectionIds" class="hidden">
					<input id="storeCollectionIdsInput" type="hidden"/>
				</div>
				<br/>
			 	<div id="mycollectionCreateAction" class="collectionBigAction">
					<input type="submit" value="<fmt:message key="collections.searchAddTo"/>" />
				</div>
			</c:when>
			<c:otherwise>
				<h2>
					<fmt:message key="collections.searches.no.free"/>
				</h2>
			</c:otherwise>
		</c:choose>
	</form>
</div>