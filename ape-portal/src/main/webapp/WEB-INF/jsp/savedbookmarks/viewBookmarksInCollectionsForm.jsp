<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet_2_0"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ page contentType="text/html" isELIgnored="false"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="portal" uri="http://portal.archivesportaleurope.eu/tags"%>
<%@ taglib prefix="ape" uri="http://commons.archivesportaleurope.eu/tags"%>

<portlet:renderURL var="addSavedBookmarksToUrl">
	<portlet:param name="myaction" value="addSavedBookmarksTo" />
	<portlet:param name="id" value="${savedBookmark.id}" />
</portlet:renderURL>

 <portlet:renderURL var="addBookmarksTo">
	<portlet:param name="myaction" value="addBookmarksTo" />
	<portlet:param name="savedBookmark_id" value="${savedBookmark.id}" />
	<portlet:param name="overviewPageNumber" value="${pageNumber}" />
</portlet:renderURL>
		
<portlet:resourceURL var="seeAvaiableCollectionsUrl" id="seeAvaiableCollections"/>

<div id="mycollectionPortletDiv">
	<h2><fmt:message key="collections.bookmarkAddTo"/></h2>
	<c:if test="${!loggedIn}">
	         	<div id="answerMessageSaved" class="failure">
			<fmt:message key="collections.bookmarks.logged.ko"/>
		</div>
	      </c:if>
	<c:if test="${loggedIn}">
		<c:if test="${!saved}">
	          	<div id="answerMessageSaved" class="failure">
				<fmt:message key="collections.bookmarks.no.free"/>
			</div>
	       </c:if>
	   		<c:if test="${saved}">
	          	<div id="answerMessageSaved" class="success">
				<fmt:message key="collections.bookmarks.saved.ok"/>
			</div>
	       </c:if>
	</c:if>
</div>