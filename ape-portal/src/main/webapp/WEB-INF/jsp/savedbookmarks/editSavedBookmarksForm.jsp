<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet_2_0"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ page contentType="text/html" isELIgnored="false"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<portlet:actionURL var="saveBookmarksActionUrl">
	<portlet:param name="myaction" value="saveEditSavedBookmarks" />
	<portlet:param name="id" value="${savedbookmarks.id}" />
	<portlet:param name="pageNumber" value="${pageNumber}" />
	<portlet:param name="overviewPageNumber" value="${overviewPageNumber}" />
</portlet:actionURL>
<portlet:renderURL var="homeUrl">
</portlet:renderURL>
<div id="savedBookmarksPortlet">
<form:form id="editSavedBookmarksForm" name="editSavedBookmarksForm" commandName="savedbookmarks" method="post"
	action="${saveBookmarksActionUrl}">
	<form:hidden path="id"/>
	<form:hidden path="overviewPageNumber"/>
	<table class="defaultlayout">
		<tr>
			<th><fmt:message key="savedsearch.id"/>:</th>
			<td><c:out value="${savedbookmarks.id}"/></td>
		</tr>
		<tr>
			<th class="modified"><fmt:message key="savedsearch.modified"/>:</th>
			<td><fmt:formatDate pattern="dd-MMM-yyyy HH:mm z"  value="${savedbookmarks.modifiedDate}" timeZone="${timeZone}"/></td>
		</tr>
		<tr>
			<th class="name"><fmt:message key="advancedsearch.eaccpf.element.name"/>:</th>
			<td><c:out value="${savedbookmarks.bookmarkName}"/></td>
		</tr>		
		<tr>
			<th class="description"><fmt:message key="savedsearch.description"/>:</th>
			<td><form:input path="description"  cssClass="longInput" maxlength="100"/></td>
		</tr>
		<tr>
			<th><fmt:message key="bookmarks.template"/>:</th>
			<td><c:out value="${savedbookmarks.typedocument}"/></td>
		</tr>
		<tr>
			<td class="linkButton" colspan="2">
				<a href="javascript:completeUrl('${saveBookmarksActionUrl}', 'savedbookmarks' , 'editSavedBookmarksForm')">
					<fmt:message key="bookmarks.save"/>
				</a>
			</td>
		</tr>
	</table>
</form:form>
</div>