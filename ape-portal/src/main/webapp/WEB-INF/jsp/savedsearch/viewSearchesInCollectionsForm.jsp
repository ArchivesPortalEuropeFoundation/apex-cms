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

<portlet:renderURL var="addSearchesTo">
	<portlet:param name="myaction" value="addSearchesTo" />
	<portlet:param name="eadSavedSearches_id" value="${savedSearch.id}" />
	<portlet:param name="overviewPageNumber" value="${pageNumber}" />
</portlet:renderURL>

<portlet:renderURL var="myCollections" />

<div id="mycollectionPortletDiv">
	<h2><fmt:message key="collections.searchAddTo"/></h2>
	<c:if test="${!loggedIn}">
          	<div id="answerMessageSaved" class="failure">
			<fmt:message key="collections.searches.logged.ko"/>
		</div>
       </c:if>
	<c:if test="${loggedIn}">
		<c:if test="${!saved}">
           	<div id="answerMessageSaved" class="failure">
				<fmt:message key="collections.searches.no.free"/>
			</div>
        </c:if>
    		<c:if test="${saved}">
           	<div id="answerMessageSaved"class="success">
				<fmt:message key="collections.searches.saved.ok"/>
			</div>
        </c:if>
	</c:if>
</div>