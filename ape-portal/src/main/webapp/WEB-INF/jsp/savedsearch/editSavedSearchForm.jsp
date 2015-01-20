<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet_2_0"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ page contentType="text/html" isELIgnored="false"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="portal" uri="http://portal.archivesportaleurope.eu/tags"%>
<portal:friendlyUrl var="savedSearchUrl" type="saved-search"/>
<portlet:actionURL var="saveEadSavedSearchActionUrl">
	<portlet:param name="myaction" value="saveEditSavedSearch" />
	<portlet:param name="id" value="${savedSearch.id}" />
	<portlet:param name="pageNumber" value="${pageNumber}" />
	<portlet:param name="overviewPageNumber" value="${overviewPageNumber}" />
</portlet:actionURL>
<portlet:renderURL var="homeUrl">
</portlet:renderURL>
<div id="savedSearchesPortlet">
<form:form id="editSavedSearchForm" name="editSavedSearchForm" commandName="savedSearch" method="post"
	action="${saveEadSavedSearchActionUrl}">
	<form:hidden path="id"/>
	<form:hidden path="overviewPageNumber"/>
	<table class="defaultlayout">
		<tr><th><fmt:message key="savedsearch.id"/>:</th><td><c:out value="${savedSearch.id}"/></td></tr>
		<tr><th><fmt:message key="savedsearch.searchterm"/>:</th><td><c:out value="${savedSearch.searchTerm}"/></td></tr>
		<tr><th><fmt:message key="savedsearch.description"/>:</th><td><form:input path="description"  cssClass="longInput" maxlength="100"/></td></tr>
		<tr><th><fmt:message key="savedsearch.contains.simple.searchoptions"/>:</th><td><c:out value="${savedSearch.containsSimpleSearchOptions}"/></td></tr>
		<tr><th><fmt:message key="advancedsearch.title.advancedsearch"/>:</th><td><c:out value="${savedSearch.containsAdvancedSearchOptions}"/></td></tr>
		<tr><th><fmt:message key="advancedsearch.title.navigatedsearch"/>:</th><td><c:out value="${savedSearch.containsAlSearchOptions}"/></td></tr>
		<tr><th><fmt:message key="savedsearch.contains.refinements"/>:</th><td><c:out value="${savedSearch.containsRefinements}"/></td></tr>
		<tr><th><fmt:message key="savedsearch.modified"/>:</th><td><fmt:formatDate pattern="dd-MMM-yyyy HH:mm z"  value="${savedSearch.modifiedDate}" timeZone="${timeZone}"/></td></tr>
		<tr><th><fmt:message key="savedsearch.publicaccessible"/>:</th><td>
			<c:choose><c:when test="${savedSearch.searchAllSearch}"><c:out value="${savedSearch.publicSearch}"/></c:when><c:otherwise><form:checkbox path="publicSearch" value="true"/></c:otherwise>
																			</c:choose></td></tr>
		<tr><th><fmt:message key="savedsearch.template"/>:</th><td><form:checkbox path="template" value="true"/></td></tr>
		<tr><td  colspan="2"><a href="${savedSearchUrl}/${savedSearch.id}"><fmt:message key="savedsearches.overview.edit.parameters"/></a></td></tr>
		<tr>
			<td class="linkButton" colspan="2">
				<a href="javascript:completeUrlMyPages('${saveEadSavedSearchActionUrl}', 'savedsearch' , 'editSavedSearchForm')">
					<fmt:message key="savedsearch.save"/>
				</a>
			</td>
		</tr>

	</table>
</form:form>
</div>