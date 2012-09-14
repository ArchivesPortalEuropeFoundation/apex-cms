<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="com.liferay.portal.kernel.portlet.LiferayWindowState"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="portal" uri="http://portal.archivesportaleurope.eu/tags"%>
<%@ taglib prefix="liferay-portlet" uri="http://liferay.com/tld/portlet" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<portlet:defineObjects />
<c:set var="element"><c:out value="${param['element']}" /></c:set>
<c:set var="term"><c:out value="${param['term']}" /></c:set>
<c:set var="eadDisplayFriendlyUrl" value="${portletPreferences.map.eadDisplayFriendlyUrl[0]}"/>
<c:set var="eadDisplayPortletName" value="${portletPreferences.map.eadDisplayPortletName[0]}"/>
<portal:page varPlId="eadDisplayPlId"  varPortletId="eadDisplayPortletId" portletName="${eadDisplayPortletName}" friendlyUrl="${eadDisplayFriendlyUrl}"/>
<liferay-portlet:renderURL var="displayEadUrl" plid="${eadDisplayPlId}" portletName="${eadDisplayPortletId}" windowState="<%= LiferayWindowState.POP_UP.toString() %>">
	<liferay-portlet:param name="id" value="C${c.clId}"/>
	<liferay-portlet:param  name="term" value="${term}"/>
	<liferay-portlet:param  name="element" value="${element}"/>
</liferay-portlet:renderURL>
<liferay-portlet:renderURL var="displayOtherEadUrl" plid="${eadDisplayPageId}" portletName="${eadDisplayPortletName}" windowState="<%= LiferayWindowState.POP_UP.toString() %>">
	<portlet:param name="aiId" value="${aiId}" />
	<portlet:param name="xmlTypeId" value="0" />
</liferay-portlet:renderURL>
<div id="content">
	<div id="realcontent">
<portal:ead type="cdetails" xml="${c.xml}" searchTerms="${term}"  searchFieldsSelectionId="${element}" aiId="${aiId}"
			secondDisplayUrl="${displayOtherEadUrl}" />
</div>
</div>
<div id="more-line" class="hide-more-line">&nbsp;</div>
<div id="viewFullFond" class="linkButton">
	<a href="${displayEadUrl}" target="_blank"><fmt:message key="seconddisplay.view.${xmlType.resourceName}" /><span class="icon_new_window">&nbsp;</span></a>
</div>

