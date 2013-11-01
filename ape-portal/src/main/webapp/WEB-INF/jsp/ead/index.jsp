<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="portal" uri="http://portal.archivesportaleurope.eu/tags"%>
<%@ taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui"%>
<%@ taglib prefix="liferay-portlet" uri="http://liferay.com/tld/portlet"%>
<portlet:defineObjects />
<c:set var="element">
	<c:out value="${param['element']}" />
</c:set>
<c:set var="term">
	<c:out value="${param['term']}" />
</c:set>
<portlet:resourceURL var="eadTreeUrl" id="eadTree">
	<portlet:param name="solrId" value="${solrId}" />
	<portlet:param name="ecId" value="${ead.eadContent.ecId}" />
	<portlet:param name="element" value="${element}" />
	<portlet:param name="term" value="${term}" />
</portlet:resourceURL>
<portlet:resourceURL var="displayEadUrl" id="displayEadDetails">
	<portlet:param name="ecId" value="${ead.eadContent.ecId}" />
	<portlet:param name="element" value="${element}" />
	<portlet:param name="term" value="${term}" />
</portlet:resourceURL>
<portal:friendlyUrl var="aiCodeUrl" type="directory-institution-code"/>
<script type='text/javascript'>
	$(document).ready(function() {
		initEadTree("${eadTreeUrl}", "${displayEadUrl}", "<portlet:namespace/>");
		displayEadDetails("${displayEadUrl}", "${solrId}", null, "<portlet:namespace/>");
		initPanes();
	});		
</script>
<div id="eadDisplayPortlet">
	<h3 id="contextInformation">
		${localizedCountryName}
		&gt; <a href="${aiCodeUrl}/${archivalInstitution.repositorycodeForUrl}">${archivalInstitution.ainame}</a>
	</h3>
	<div id="eadcontent">
		<div id="left-pane" class="pane">
			<div id="eadTree"></div>
		</div>
		<div id="splitter" class="pane"></div>
		<div id="right-pane" class="pane"></div>
	</div>
</div>