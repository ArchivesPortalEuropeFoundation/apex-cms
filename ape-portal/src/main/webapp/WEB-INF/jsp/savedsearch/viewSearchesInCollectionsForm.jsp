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
	<portlet:renderURL var="addSearchesTo">
	<portlet:param name="myaction" value="addSearchesTo" />
	<portlet:param name="eadSavedSearches_id" value="${savedSearch.id}" />
	<portlet:param name="overviewPageNumber" value="${pageNumber}" />
	</portlet:renderURL>

	<form name="frm" method="post" action="${addSearchesTo}">
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
				<div class="collectionFieldTitle collectionHeader"><fmt:message key="advancedsearch.text.title2"/></div>
				<div class="collectionFieldDescription collectionHeader"><fmt:message key="savedsearch.description"/></div>
			</div>

			<c:forEach var="collection" items="${collections}" varStatus="counter">
				<div class="collectionField" id="collectionField_${counter}">
					<div class="collectionFieldTitle collectionContent">
						<c:out value="${collection.title}"/>
					</div>
					<div class="collectionFieldTitle collectionContent">
						<c:out value="${collection.description}"/>
					</div>
				</div>
			</c:forEach>
		</c:when>
		</c:choose>

		<c:if test="${!loggedIn}">
           	<div id="answerMessageSaved" class="error">
				<fmt:message key="collections.bookmarks.logged.ko"/>
			</div>
        </c:if>
		<c:if test="${loggedIn}">
			<c:if test="${!saved}">
            	<div id="answerMessageSaved" class="error">
					<fmt:message key="collections.bookmarks.saved.ko"/>
				</div>
	        </c:if>
     		<c:if test="${saved}">
            	<div id="answerMessageSaved">
					<fmt:message key="collections.bookmarks.saved.ok"/>
				</div>
	        </c:if>
		</c:if>


<!-- 	keep this div in case it will be needed a button there -->
<%-- 	<div id="mycollectionCreateAction" class="collectionBigAction">
		<input type="submit" value="<fmt:message key="collections.bookmarkAddTo"/>" />
	</div> --%>
	</form>
</div>