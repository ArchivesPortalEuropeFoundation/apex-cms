<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui"%>
<%@ taglib prefix="liferay-portlet" uri="http://liferay.com/tld/portlet" %>
<%@ taglib prefix="portal" uri="http://portal.archivesportaleurope.eu/tags"%>
<portlet:defineObjects />
<portlet:renderURL var="viewAllTopicsUrl" windowState='maximized'/>
<div id="tagcloudPortlet">
<portal:friendlyUrl var="searchUrl" type="topic-search"/>
<h2><fmt:message key="tagcloud.topics.title"/>:</h2>
<div id="tags">
<table id="allTagsTable" class="defaultlayout">
<tr><th><fmt:message key="advancedsearch.facet.title.topic" /></th><th><fmt:message key="tagcloud.topics.view.all.results" /></th></tr>
			<c:forEach var="tag" items="${tags}">
				<tr><td>
				<c:choose>
					<c:when test="${tag.enabled}">
						<a href="${searchUrl}/${tag.key}">${tag.name}</a>				
					</c:when>
					<c:otherwise><span>${tag.name}</span></c:otherwise>
				</c:choose>
				<td class="tagCount">${tag.displayCount}</td></tr>
			</c:forEach>
				
</table>
</div>
</div>