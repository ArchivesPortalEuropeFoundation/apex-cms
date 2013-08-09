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
<portal:friendlyUrl var="friendlyUrl" type="featured-exhibition-details"/>
<script type='text/javascript'>
		var documentTitle = "${documentTitle}";
		if (documentTitle !=""){
			document.title = documentTitle;
		}
		
</script>
<div id="featuredExhibitionDetailsPortlet">
<c:if test="${!empty  articleDetails}">
<div id="featuredExhibitionDetails">
${articleDetails}
</div>
<div id="likeBar">
<span class='st_facebook_hcount' displayText='Facebook'></span>
<span class='st_googleplus_hcount' displayText='Google +'></span>
<span class='st_twitter_hcount' displayText='Tweet'></span>
<span class='st_linkedin_hcount' displayText='LinkedIn'></span>
<span class='st_pinterest_hcount' displayText='Pinterest'></span>
<span class='st_email_hcount' displayText='Email'></span>
</div>
</c:if>
<div id="featuredExhibitionDetailsList">
<h2>All Featured Exhibitions</h2>
<c:forEach var="featuredExhibitionSummary" items="${featuredExhibitionSummaries}">
	<a href="${friendlyUrl}/${featuredExhibitionSummary.classPk}">${featuredExhibitionSummary.title} - ${featuredExhibitionSummary.date}</a><br/>
</c:forEach>
</div>
</div>