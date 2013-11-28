<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet_2_0"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ page contentType="text/html" isELIgnored="false"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<portlet:actionURL var="saveEadSavedSearchActionUrl">
	<portlet:param name="myaction" value="saveEditSavedSearch" />
</portlet:actionURL>
<portlet:renderURL var="homeUrl">
</portlet:renderURL>
<div id="savedSearchesPortlet">
<form:form name="editSavedSearchForm" commandName="savedSearch" method="post"
	action="${saveEadSavedSearchActionUrl}">
	<form:hidden path="id"/>
	<table class="defaultlayout">
		<tr><th><fmt:message key="savedsearch.id"/>:</th><td><c:out value="${savedSearch.id}"/></td></tr>
		<tr><th><fmt:message key="savedsearch.searchterm"/>:</th><td><c:out value="${savedSearch.searchTerm}"/></td></tr>
		<tr><th><fmt:message key="savedsearch.description"/>:</th><td><form:input path="description"  cssClass="longInput"/></td></tr>
		<tr><th><fmt:message key="savedsearch.contains.simple.searchoptions"/>:</th><td><c:out value="${savedSearch.containsSimpleSearchOptions}"/></td></tr>
		<tr><th><fmt:message key="advancedsearch.title.advancedsearch"/>:</th><td><c:out value="${savedSearch.containsAdvancedSearchOptions}"/></td></tr>
		<tr><th><fmt:message key="advancedsearch.title.navigatedsearch"/>:</th><td><c:out value="${savedSearch.containsAlSearchOptions}"/></td></tr>
		<tr><th><fmt:message key="savedsearch.contains.refinements"/>:</th><td><c:out value="${savedSearch.containsRefinements}"/></td></tr>
		<tr><th><fmt:message key="savedsearch.modified"/>:</th><td><fmt:formatDate type="both"  value="${savedSearch.modifiedDate}" timeZone="${timeZone}"/></td></tr>
		<tr><th><fmt:message key="savedsearch.publicaccessible"/>:</th><td>
			<c:choose><c:when test="${savedSearch.searchAllSearch}"><c:out value="${savedSearch.publicSearch}"/></c:when><c:otherwise><form:checkbox path="publicSearch" value="true"/></c:otherwise>
																			</c:choose></td></tr>
		<tr><th><fmt:message key="savedsearch.template"/>:</th><td><form:checkbox path="template" value="true"/></td></tr>
		<tr><td colspan="2"><input type="submit" class="mainButton" value="<fmt:message key="savedsearch.save"/>"></td>
		</tr>

	</table>
</form:form>
</div>