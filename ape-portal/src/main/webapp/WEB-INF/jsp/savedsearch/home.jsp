<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet_2_0"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ page contentType="text/html" isELIgnored="false"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="portal" uri="http://portal.archivesportaleurope.eu/tags"%>
<%@ taglib prefix="ape" uri="http://commons.archivesportaleurope.eu/tags"%>
<portal:friendlyUrl var="savedSearchUrl" type="saved-search"/>
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
						refreshUrl="test/{pageNumber}" pageNumberId="pageNumber"  liferayFriendlyUrl="true"/>

				</div>
			</div>


			<table id="savedSearchesTable" class="defaultlayout">
				<tr>
					<th><fmt:message key="savedsearch.id"/></th><th><fmt:message key="savedsearch.term"/></th><th><fmt:message key="savedsearch.description"/></th><th><fmt:message key="savedsearch.modified"/></th><th><fmt:message key="savedsearches.overview.actions"/></th>
				</tr>
				<c:forEach var="eadSavedSearch" items="${eadSavedSearches}">
				<tr>
					<td>${eadSavedSearch.id}</td>
					<td><a href="${savedSearchUrl}/${eadSavedSearch.id}"><c:out value="${eadSavedSearch.term}"/></a></td>
					<td><c:out value="${eadSavedSearch.label}"/></td>
					<td><fmt:formatDate type="both"  value="${eadSavedSearch.modifiedDate}" timeZone="${timeZone}"/></td>
					<td>
						<a
						href="
						<portlet:renderURL>
							<portlet:param name="myaction" value="editSavedSearchForm" />
							<portlet:param name="id" value="${eadSavedSearch.id}" />
						</portlet:renderURL>					
					"><fmt:message key="savedsearches.overview.edit"/></a>
					<a href="
						<portlet:actionURL>
							<portlet:param name="myaction" value="deleteSavedSearch" />
							<portlet:param name="id" value="${eadSavedSearch.id}" />
						</portlet:actionURL>					
					" onclick="javascript: return confirmRemove()"><fmt:message key="savedsearches.overview.delete"/></a>
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