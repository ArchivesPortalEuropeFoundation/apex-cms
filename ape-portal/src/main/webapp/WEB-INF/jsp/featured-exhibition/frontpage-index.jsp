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
<div id="featuredExhibitionPortlet">
	<div class="slider-wrapper theme-light">
		<div id="slider" class="nivoSlider">
			<c:forEach var="featuredExhibition" items="${featuredExhibitions}">
				<a href="${friendlyUrl}/${featuredExhibition.detailsClassPk}">${featuredExhibition.content}</a>
                </c:forEach>
		</div>
		<div id="slider-captions">
		</div>


	</div>
	<script type="text/javascript">
		$(document).ready(function() {
			initSlider();
		});	
		function initSlider() {
			$(".nivo-html-caption-container").each(function(index, value) {
				$("#slider-captions").append($(this).html());
				$(this).remove();
			   
			});
	        $('#slider').nivoSlider({
	        	 pauseTime: 5000,
	        	 pauseOnHover: false
	        	});
		};
	</script>
</div>