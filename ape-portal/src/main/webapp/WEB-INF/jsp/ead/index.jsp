<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="portal" uri="http://portal.archivesportaleurope.eu/tags"%>
<%@ taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %>
<%@ taglib prefix="liferay-portlet" uri="http://liferay.com/tld/portlet" %>
<portlet:defineObjects />

<c:choose>
	<c:when test="${!empty portletPreferences.map.advancedSearchPageId and !empty portletPreferences.map.advancedSearchPortletName}">
	<c:set var="element"><c:out value="${param['element']}" /></c:set>
	<c:set var="xmlTypeId" value="${xmlTypeId}"/>
	<c:set var="fileId" value="${ead.id}"/>
	<c:set var="solrId"><c:out value="${param['id']}" /></c:set>
	<c:set var="term"><c:out value="${param['term']}" /></c:set>
	<portlet:resourceURL var="eadTreeUrl" id="eadTree" >
		<portlet:param name="solrId" value="${solrId}"/>
		<portlet:param name="ecId" value="${ead.eadContent.ecId}"/>
		<portlet:param name="element" value="${element}"/>
		<portlet:param name="term" value="${term}"/>
	</portlet:resourceURL>
	<portlet:resourceURL var="displayEadUrl" id="displayEadDetails">
		<portlet:param name="ecId" value="${ead.eadContent.ecId}"/>
		<portlet:param name="element" value="${element}"/>
		<portlet:param name="term" value="${term}"/>
		<portlet:param name="aiId" value="${archivalInstitution.aiId}"/>
		<portlet:param  name="xmlTypeId" value="${xmlTypeId}"/>
	</portlet:resourceURL>
        <script type='text/javascript'>
        $(document).ready(function() {
       		initEadTree("${eadTreeUrl}", "${displayEadUrl}", "<portlet:namespace/>");
       		displayEadDetails("${displayEadUrl}", "${solrId}", null, "<portlet:namespace/>");
       		initPanes();       		
        	});
           </script>
		<c:set var="advancedSearchPageId" value="${portletPreferences.map.advancedSearchPageId[0]}"/>
		<c:set var="advancedSearchPortletName" value="${portletPreferences.map.advancedSearchPortletName[0]}"/>
		<liferay-portlet:renderURL var="displayAdvancedSearchUrl" plid="${advancedSearchPageId}" portletName="${advancedSearchPortletName}" windowState="normal" />
		<div id="eadDisplayPortlet">
			<div id="header">
                <div id="left-header-${fn:toLowerCase(country.isoname)}" class="left-header">
                </div>
                <div class="right-header">
                </div>
            </div>
            <div id="contextInformation">
            	<!-- <a href="${displayAdvancedSearchUrl}" title="<fmt:message key="page.alt.text.homepage" />" target="_blank"><fmt:message key="sitemap.menu.home" /></a> &gt; --><fmt:message key="country.${fn:toLowerCase(country.cname)}"/> &gt; ${archivalInstitution.ainame}
			</div>
			<div id="eadcontent"> 
				<div id="left-pane" class="pane">
					<div id="eadTree"> </div>
				</div>
				<div id="splitter" class="pane"> </div>
				<div id="right-pane" class="pane">
				
				</div>
			</div>
		</div>
	</c:when>
	<c:otherwise>
		<liferay-ui:message key="please-contact-the-administrator-to-setup-this-portlet" />
	</c:otherwise>
</c:choose>