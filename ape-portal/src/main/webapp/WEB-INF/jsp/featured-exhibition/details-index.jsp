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
<script type="text/javascript">var switchTo5x=true;</script>
<script type="text/javascript" src="https://wd-edge.sharethis.com/button/buttons.js"></script>
<script type="text/javascript">stLight.options({publisher: "e059943f-766d-434b-84ea-1e0d4a91b7d4", doNotHash: true, doNotCopy: true, hashAddressBar: true, shorten:false});</script>
<div id="featuredExhibitionDetailsPortlet">
<c:if test="${!empty  articleDetails}">
<div id="featuredExhibitionDetails">
${articleDetails}
</div>

	<script type="text/javascript">
	$(document).ready(function($){
		$('#multiplezoom').addimagezoom({ // multi-zoom: options same as for previous Featured Image Zoomer's addimagezoom unless noted as '- new'
			descArea: '#description', // description selector (optional - but required if descriptions are used) - new
			speed: 1500, // duration of fade in for new zoomable images (in milliseconds, optional) - new
			descpos: true, // if set to true - description position follows image position at a set distance, defaults to false (optional) - new
			imagevertcenter: true, // zoomable image centers vertically in its container (optional) - new
			magvertcenter: true, // magnified area centers vertically in relation to the zoomable image (optional) - new
			zoomrange: [3, 15],
			magnifiersize: [600,350],
			magnifierpos: 'right',
			cursorshadecolor: '#fdffd5',
			cursorshade: true //<-- No comma after last option!
		});

		$(document).bind("contextmenu", function(e) {
		    return false;
		});
	});
	</script>
<div id="likeBar">
<span class='st_facebook_large' st_image="http://development.archivesportaleurope.net/documents/10179/23708/adalen1_350.jpg" displayText='Facebook'></span>
<span class='st_googleplus_large' displayText='Google +'></span>
<span class='st_twitter_large' displayText='Tweet'></span>
<span class='st_linkedin_large' displayText='LinkedIn'></span>
<span class='st_pinterest_large' displayText='Pinterest'></span>
<span class='st_email_large' displayText='Email'></span>
</div>
</c:if>
<div id="featuredExhibitionDetailsList">
<h2><fmt:message key="featuredexhibition.all" /></h2>
<c:forEach var="featuredExhibitionSummary" items="${featuredExhibitionSummaries}">
	<a href="${friendlyUrl}/${featuredExhibitionSummary.classPk}">${featuredExhibitionSummary.title} - ${featuredExhibitionSummary.date}</a><br/>
</c:forEach>
</div>

</div>