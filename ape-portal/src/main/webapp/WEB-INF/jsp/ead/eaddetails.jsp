<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="com.liferay.portal.kernel.portlet.LiferayWindowState"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="ape" uri="http://commons.archivesportaleurope.eu/tags"%>
<%@ taglib prefix="portal" uri="http://portal.archivesportaleurope.eu/tags"%>
<%@ taglib prefix="liferay-portlet" uri="http://liferay.com/tld/portlet"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<portlet:defineObjects />
<c:set var="element">
	<c:out value="${param['element']}" />
</c:set>
<c:set var="term">
	<c:out value="${param['term']}" />
</c:set>
<c:set var="id">
	<c:out value="${param['id']}" />
</c:set>
<c:set var="ecId">
	<c:out value="${param['ecId']}" />
</c:set>
<portal:friendlyUrl var="eadDisplaySearchUrl" type="eaddisplay-search"/>
<portal:friendlyUrl var="eadDisplayDirectUrl" type="eaddisplay-frontpage"/>
<portlet:resourceURL var="displayChildrenUrl" id="displayEadDetails">
	<portlet:param name="id" value="${id}" />
	<portlet:param name="element" value="${element}" />
	<portlet:param name="term" value="${term}" />
</portlet:resourceURL>
<portlet:renderURL var="printEadDetailsUrl" windowState="<%=LiferayWindowState.POP_UP.toString()%>">
	<portlet:param name="myaction" value="printEadDetails" />
	<portlet:param name="id" value="${id}" />
	<portlet:param name="ecId" value="${ecId}" />
	<portlet:param name="element" value="${element}" />
	<portlet:param name="term" value="${term}" />
	<portlet:param name="type" value="${type}" />
	<portlet:param name="pageNumber" value="${pageNumber}" />
</portlet:renderURL>
<script type='text/javascript'>
	$(document).ready(function() {
		document.title = "${documentTitle}";
		initExpandableParts();
	    stLight.options({
	        publisher: 'e059943f-766d-434b-84ea-1e0d4a91b7d4',
	        onhover: true
	    });
	    stButtons.locateElements();
	});
</script>
<div id="buttonsHeader">
<div id="printEadDetails" class="linkButton">
	<a href="javascript:printEadDetails('${printEadDetailsUrl}')"><fmt:message key="label.print" /><span class="icon_print">&nbsp;</span></a>
</div>

	<c:choose>
		<c:when test="${empty c}">
			<c:set var="url" value="${eadDisplayDirectUrl}/${aiRepoCode}/${xmlTypeName}/${eadContent.eadid}"/>
		</c:when>
			<c:when test="${empty advancedSearch.term }">
				<c:set var="url" value="${eadDisplaySearchUrl}/${id}"/>
			</c:when>
			<c:otherwise>
				<c:set var="url" value="${eadDisplaySearchUrl}/${id}/${element}/${term}"/>
			</c:otherwise>
	</c:choose>	
<div id="shareButton" class="linkButton">
<span class="st_sharethis_button" displayText="<fmt:message key="label.share" />" st_title="${documentTitle}" st_url="${url}"></span>
</div>
</div>
<div id="eaddetailsContent">
<c:choose>
	<c:when test="${empty c}">
		<portal:ead type="${type}" xml="${eadContent.xml}" />
	</c:when>
	<c:otherwise>
		<portal:ead type="cdetails" xml="${c.xml}" searchTerms="${term}" searchFieldsSelectionId="${element}" aiId="${aiId}"
			secondDisplayUrl="${eadDisplayDirectUrl}/${aiRepoCode}/fa" />
		<c:if test="${not c.leaf}">
			<div id="children" class="box">
				<div class="boxtitle">
					<div class="numberOfPages">
						<ape:pageDescription numberOfItems="${totalNumberOfChildren}" pageSize="${pageSize}" pageNumber="${pageNumber}" />
					</div>
					<div id="child-paging" class="paging">
						<ape:paging numberOfItems="${totalNumberOfChildren}" pageSize="${pageSize}" pageNumber="${pageNumber}"
							refreshUrl="javascript:updatePageNumber('${displayChildrenUrl}')" pageNumberId="pageNumber" />

					</div>
				</div>
				<portal:ead type="cdetails-child" xml="${childXml}" />
			</div>
		</c:if>
	</c:otherwise>
</c:choose>
</div>