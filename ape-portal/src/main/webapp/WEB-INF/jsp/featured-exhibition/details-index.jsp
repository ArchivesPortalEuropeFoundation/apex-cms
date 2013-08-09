<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="portal"
	uri="http://portal.archivesportaleurope.eu/tags"%>
<%@ taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui"%>
<%@ taglib prefix="liferay-portlet" uri="http://liferay.com/tld/portlet"%>
<portlet:defineObjects />
<portal:friendlyUrl var="friendlyUrl" type="featured-exhibition-details-article"/>
<div id="featuredExhibitionDetailsPortlet">
<div id="featuredExhibitionDetails">
${articleDetails}
</div>
<div id="featuredExhibitionDetailsList">
<h2>All Featured Exhibitions</h2>
<c:forEach var="featuredExhibitionSummary" items="${featuredExhibitionSummaries}">
	<a href="${friendlyUrl}/${featuredExhibitionSummary.articleId}">${featuredExhibitionSummary.title} - ${featuredExhibitionSummary.date}</a><br/>
</c:forEach>
</div>
</div>