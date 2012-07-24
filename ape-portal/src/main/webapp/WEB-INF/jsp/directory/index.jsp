<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="portal"
	uri="http://portal.archivesportaleurope.eu/tags"%>
<%@ taglib prefix="liferay-portlet" uri="http://liferay.com/tld/portlet"%>
<portlet:defineObjects />
<portlet:resourceURL var="directoryTreeUrl" id="directoryTree" />
<portlet:resourceURL var="directoryTreeAiUrl" id="directoryTreeAi" />
<portlet:resourceURL var="aiDetailsUrl" id="aiDetails" />
<script type="text/javascript">
	$(document).ready(function() {
		initDirectory("${directoryTreeUrl}", "${directoryTreeAiUrl}", "${aiDetailsUrl}");
	});
</script>
<div id="directoryPortlet">
	<div class="directoryTree" id="directoryTree"></div>

	<div id="aiDetails">
		<div class="aiTitle"><fmt:message key="directory.message.noInstitutionSelected"/></div>
	</div>
</div>