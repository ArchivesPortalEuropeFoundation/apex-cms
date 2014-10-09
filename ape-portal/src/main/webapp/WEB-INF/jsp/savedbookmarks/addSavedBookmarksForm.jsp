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
<portlet:renderURL var="createMyCollectionUrl" >
	<portlet:param name="action" value="createNewCollection" />
</portlet:renderURL>
<portlet:renderURL var="deleteMyCollectionUrl" >
	<portlet:param name="action" value="deleteSavedCollections" />
</portlet:renderURL>
<portlet:renderURL var="myCollections" />
<div id="mycollectionPortletDiv">
	<portlet:renderURL var="addBookmarksTo">
	<portlet:param name="myaction" value="addBookmarksTo" />
	<portlet:param name="savedBookmark_id" value="${savedBookmark.id}" />
	<portlet:param name="overviewPageNumber" value="${pageNumber}" />
	</portlet:renderURL>
	
	<form name="frm" method="post" action="${addBookmarksTo}">
		<c:choose>
		<c:when test="${not empty collections}">
			<div class="collectionPaginator">
				<div class="boxtitle">
					<div class="numberOfPages">
						<ape:pageDescription numberOfItems="${totalNumberOfResults}" pageSize="${pageSize}" pageNumber="${pageNumber}" />
					</div>
					<div id="child-paging" class="paging">
						<ape:paging numberOfItems="${totalNumberOfResults}" pageSize="${pageSize}" pageNumber="${pageNumber}"
							refreshUrl="${myCollections}" pageNumberId="pageNumber" />
					</div>
				</div>
			</div>
			<div class="collectionFieldHeader">
				<div class="collectionHeader"><fmt:message key="collections.signal"/></div>
				<div class="collectionFieldTitle collectionHeader"><fmt:message key="advancedsearch.text.title2"/></div>
				<div class="collectionFieldDescription collectionHeader"><fmt:message key="savedsearch.description"/></div>
			</div>
			
			<c:forEach var="collection" items="${collections}" varStatus="counter">
				<div class="collectionField" id="collectionField_${counter}">
					<div class="collectionSearchCheckbox"><input type="checkbox" name="collectionToAdd_${collection.id}" id="collectionToAdd_${newSearch.id}" /></div>
					<div class="collectionFieldTitle collectionContent"><c:out value="${collection.title}" /></div>
				</div>
			</c:forEach>
			
		</c:when>
		<c:otherwise>
			<h2>
				<fmt:message key="scollections.searches.no"/>
			</h2>
		</c:otherwise>
	</c:choose>
	
 	<div id="mycollectionCreateAction" class="collectionBigAction">
		<input type="submit" value="<fmt:message key="collections.bookmarkAddTo"/>" />
	</div>
	</form>
</div>