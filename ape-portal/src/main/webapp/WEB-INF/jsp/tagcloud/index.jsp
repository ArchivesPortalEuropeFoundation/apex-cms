<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui"%>
<%@ taglib prefix="liferay-portlet" uri="http://liferay.com/tld/portlet" %>
<%@ taglib prefix="portal" uri="http://portal.archivesportaleurope.eu/tags"%>
<portlet:defineObjects />
<div id="tagcloudPortlet">
<portal:friendlyUrl var="searchUrl" type="topic-search"/>
<h2><fmt:message key="tagcloud.topics.title"/>:</h2>
<div id="tags">
	<ul>
		<c:forEach var="tag" items="${tags}">
			<c:choose>
				<c:when test="${tag.enabled}">
					<li class="${tag.cssClass}"><a href="${searchUrl}/${tag.key}">${tag.name}</a></li>				
				</c:when>
				<c:otherwise><li class="${tag.cssClass}"><span>${tag.name}</span></li></c:otherwise>
			</c:choose>
		</c:forEach>	
	</ul>
</div>
</div>