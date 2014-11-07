<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet_2_0"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ page contentType="text/html" isELIgnored="false"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="portal" uri="http://portal.archivesportaleurope.eu/tags"%>
<%@ taglib prefix="ape" uri="http://commons.archivesportaleurope.eu/tags"%>
<link rel="stylesheet" href="//code.jquery.com/ui/1.11.2/themes/smoothness/jquery-ui.css">
<script src="//code.jquery.com/jquery-1.10.2.js"></script>
<script src="//code.jquery.com/ui/1.11.2/jquery-ui.js"></script>
<!-- <link rel="stylesheet" href="/resources/demos/style.css"> -->
<script>
	$(function() {
		$( "#mycollectionPortletDiv" ).dialog({
			resizable: false/* ,modal: true */
		 });
		return false;
	});
</script>

<div id="mycollectionPortletDiv" title="<fmt:message key="collections.bookmarkAddTo"/>">
	<portlet:resourceURL var="addBookmarksToUrl" id="addBookmarksTo">
		<portlet:param name="bookmarkId" value="${bookmarkId}" />
	</portlet:resourceURL>
	<form name="frm" id="frm" method="post" action="${addBookmarksToUrl}">		
		<div>
			<input id="searchTerm" tabindex="1" maxlength="30"/>
			<portlet:resourceURL var="seeAvaiableCollectionsUrl" id="seeAvaiableCollections"/>
			<div id="searchButton" class="linkButton" style="display: inline-flex">
	 			<a href="javascript:showCollections('${bookmarkId}','${seeAvaiableCollectionsUrl}')"><fmt:message key="advancedsearch.message.search" /></a>
			</div>
		</div>	
		
		<c:choose>
		<c:when test="${not empty collections}">
			<table class="defaultlayout">
				<tr>
					<th class="defaultlayout"><fmt:message key="collections.signal"/></th>
					<th class="defaultlayout"><fmt:message key="advancedsearch.text.title2"/></th>
				</tr>
				<c:forEach var="collection" items="${collections}" varStatus="counter">
					<tr id="collectionField_${counter}">
						<td><input type="checkbox" name="collectionToAdd_${collection.id}" id="collectionToAdd_${newSearch.id}" /></td>
						<td><c:out value="${collection.title}" /></td>
					</tr>
				</c:forEach>
			</table>
			<div id="mycollectionCreateAction" class="linkButton" style="display: inline-flex">
	 			<a href="javascript:saveBookmarkInCollections('${addBookmarksToUrl}')"><fmt:message key="collections.bookmarkAddTo" /></a>
			</div>
		</c:when>
		<c:otherwise>
			<div>
				<fmt:message key="collections.searches.no.mach"/>
			</div>
		</c:otherwise>
		</c:choose>	
 	</form>
</div>