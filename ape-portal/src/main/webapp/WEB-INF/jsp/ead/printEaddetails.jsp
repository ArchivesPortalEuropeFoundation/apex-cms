<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="portal" uri="http://portal.archivesportaleurope.eu/tags"%>
<%@ taglib prefix="ape" uri="http://commons.archivesportaleurope.eu/tags"%>
<%@ taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui"%>
<%@ taglib prefix="liferay-portlet" uri="http://liferay.com/tld/portlet"%>
<portlet:defineObjects />

<c:set var="element">
	<c:out value="${param['element']}" />
</c:set>
<c:set var="term">
	<c:out value="${param['term']}" />
</c:set>
<script type='text/javascript'>
	$(document).ready(function() {
		var title = "${documentTitle}";
		$("title").html($("<div />").html(title).text());
		self.print();
	});
</script>
<div id="eadDisplayPortlet">
		<div id="header">
			<div id="logo"></div>	
			<div class="left-header"></div>
			<div class="right-header"></div>
		</div>
	<c:choose>
		<c:when test="${empty c}">
			<div id="contextInformation">
				<ape:context eadContent="${eadContent}" onlyArchives="true" country="${localizedCountryName}" />
			</div>
			<div id="eadcontent">
				<portal:ead type="${type}" xml="${eadContent.xml}" />
			</div>
		</c:when>
		<c:otherwise>
			<div id="contextInformation">
				<ape:context clevel="${c}" country="${localizedCountryName}" />
			</div>
			<div id="eadcontent">

				<portal:ead type="cdetails" xml="${c.xml}" searchTerms="${term}" searchFieldsSelectionId="${element}" aiId="${aiId}" />
				<c:if test="${not c.leaf}">
					<div id="children" class="box">
						<div class="boxtitle"></div>
						<portal:ead type="cdetails-child" xml="${childXml}" />
					</div>
				</c:if>
			</div>
		</c:otherwise>
	</c:choose>

</div>
