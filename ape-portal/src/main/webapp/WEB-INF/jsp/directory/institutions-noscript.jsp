<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="portal" uri="http://portal.archivesportaleurope.eu/tags"%>
<%@ taglib prefix="liferay-portlet" uri="http://liferay.com/tld/portlet"%>
<portlet:defineObjects />
<portal:friendlyUrl var="aiIdUrl" type="directory-institution-id"/>
<portal:friendlyUrl var="aiCodeUrl" type="directory-institution-code"/>
<div id="directoryPortlet">
<h3>${parent}</h3>
			<div id="children" class="box">
				<c:forEach var="archivalInstitutionUnit" items="${archivalInstitutionUnits}">
					<c:choose>
						<c:when test="${empty archivalInstitutionUnit.repoCode }">
							<c:set var="url" value="${aiIdUrl}/${archivalInstitutionUnit.aiId}"/>
						</c:when>
						<c:otherwise>
							<c:set var="url" value="${aiCodeUrl}/${archivalInstitutionUnit.repoCode}"/>
						</c:otherwise>
					</c:choose>	
				<div class="child"><a href="${url}">${archivalInstitutionUnit.ainame}</a></div>
					
				</c:forEach>

			</div>
</div>