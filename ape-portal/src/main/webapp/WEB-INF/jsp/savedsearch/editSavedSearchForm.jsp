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
	<table>
		<tr><th><fmt:message key="savedsearch.id"/>:</th><td><c:out value="${savedSearch.id}"/></td><td></td></tr>
		<tr><th><fmt:message key="savedsearch.term"/>:</th><td><c:out value="${savedSearch.term}"/></td><td></td></tr>
		<tr><th><fmt:message key="savedsearch.description"/>:</th><td><form:input path="description"  cssClass="longInput"/></td><td><span class="error"><form:errors
				path="description" /></span></td></tr>
		<tr><th><fmt:message key="savedsearch.modified"/>:</th><td><fmt:formatDate type="both"  value="${savedSearch.modifiedDate}" timeZone="${timeZone}"/></td><td></td></tr>
		<tr><td><input type="submit" class="mainButton" value="<fmt:message key="savedsearch.save"/>"></td>
		</tr>

	</table>
</form:form>
</div>