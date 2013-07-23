<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ taglib prefix="portal" uri="http://portal.archivesportaleurope.eu/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ page import="com.liferay.portal.kernel.portlet.LiferayWindowState" %>
<portlet:defineObjects />
<portlet:renderURL var="printEagDetailsUrl" windowState="<%=LiferayWindowState.POP_UP.toString()%>">
	<portlet:param name="myaction" value="printEagDetails" />
	<portlet:param name="id" value="${aiId}" />
</portlet:renderURL>
<div id="buttonsHeaderEag">
	<div class="linkButton right" id="printEagDetails">
		<a href="javascript:printEagByURL('${printEagDetailsUrl}')"><fmt:message key="label.print" /><span class="icon_print">&nbsp;</span></a>
	</div>
</div>
<div>
<portal:eag eagUrl="${eagUrl}"/>
</div>
