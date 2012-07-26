<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="com.liferay.portal.kernel.portlet.LiferayWindowState"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="portal" uri="http://portal.archivesportaleurope.eu/tags"%>
<%@ taglib prefix="liferay-portlet" uri="http://liferay.com/tld/portlet" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<portlet:defineObjects />
<c:set var="eadDisplayPageId" value="${portletPreferences.map.eadDisplayPageId[0]}"/>
<c:set var="eadDisplayPortletName" value="${portletPreferences.map.eadDisplayPortletName[0]}"/>
<liferay-portlet:renderURL var="displayEadUrl" plid="${eadDisplayPageId}" portletName="${eadDisplayPortletName}" windowState="<%= LiferayWindowState.POP_UP.toString() %>">
	<liferay-portlet:param name="id" value="${param['id']}"/>
</liferay-portlet:renderURL>	
<div id="body">
<portal:ead type="frontpage" xml="${eadContent.xml}"/>
<div id="viewFullFA">
	<a href="${displayEadUrl}" target="_blank"><fmt:message key="seconddisplay.view.${xmlType.resourceName}" /><span class="icon_new_window">&gt;</span></a>
</div>
</div>