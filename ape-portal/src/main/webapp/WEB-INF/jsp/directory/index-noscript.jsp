<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="portal" uri="http://portal.archivesportaleurope.eu/tags"%>
<%@ taglib prefix="liferay-portlet" uri="http://liferay.com/tld/portlet"%>
<portlet:defineObjects />
<portal:friendlyUrl var="countryUrl" type="directory-country"/>
<div id="directoryPortlet">
<h3><fmt:message key="advancedsearch.facet.title.country" /></h3>
			<div id="children" class="box">
				<div class="boxtitle">
				</div>
				<c:forEach var="countryUnit" items="${countries}">
				<div class="child"><a href="${countryUrl}/${countryUnit.country.isoname}">${countryUnit.localizedName}</a></div>
					
				</c:forEach>

			</div>
</div>