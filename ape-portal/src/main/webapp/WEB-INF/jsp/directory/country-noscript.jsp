<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="portal" uri="http://portal.archivesportaleurope.eu/tags"%>
<%@ taglib prefix="liferay-portlet" uri="http://liferay.com/tld/portlet"%>
<portlet:defineObjects />
<portal:friendlyUrl var="aiUrl" type="directory-institution"/>
<div id="directoryPortlet">
<h4><fmt:message key="advancedsearch.facet.title.country" /></h4>
			<div id="children" class="box">
				<div class="boxtitle">${countryUnit.country.isoname}</div>
				<c:forEach var="archivalInstitutionUnit" items="${archivalInstitutionUnits}">
				<div class="child"><a href="${aiUrl}/${archivalInstitutionUnit.repoCode}">${archivalInstitutionUnit.ainame}</a></div>
					
				</c:forEach>

			</div>
</div>