<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="portal" uri="http://portal.archivesportaleurope.eu/tags"%>
<%@ taglib prefix="liferay-portlet" uri="http://liferay.com/tld/portlet"%>
<portlet:defineObjects />
<portlet:resourceURL var="directoryTreeUrl" id="directoryTree" />
<portlet:resourceURL var="directoryTreeAiUrl" id="directoryTreeAi" />
<portlet:resourceURL var="aiDetailsUrl" id="aiDetails" />
	<script type="text/javascript" src="https://maps.google.com/maps/api/js?sensor=false"></script>
<script type="text/javascript">
	$(document).ready(function() {
		initDirectory("${directoryTreeUrl}", "${directoryTreeAiUrl}", "${aiDetailsUrl}", "${embeddedMapUrl}", "${mapUrl}", '<fmt:message key="label.print" />');
	});
</script>
<div id="directoryPortlet">
	<div class="portlet-layout">
		<div id="directory-column-left" class="aui-w50 portlet-column portlet-column-first">
			<div id="directory-column-left-content" class="portlet-column-content portlet-column-content-first">
				<div class="directoryTree" id="directoryTree"></div>
				&nbsp;
			</div>
		</div>
		<div id="directory-column-right" class="aui-w50 portlet-column portlet-column-last">
			<div id="directory-column-right-content" class="portlet-column-content portlet-column-content-last">
				<div class="arrow_box">
					<p>
						<fmt:message key="directory.message.noInstitutionSelected" />
					</p>
				</div>
				&nbsp;
			</div>
		</div>
	</div>
	<div class="portlet-layout">
		<iframe id="maps" width="1000" height="400" frameborder="0" scrolling="no" marginheight="0" marginwidth="0"
			src="${embeddedMapUrl}${mapUrlCenterParameters}"></iframe>
		<div id="viewLargerMap">
		<a id="externalMap" href="${mapUrl}${mapUrlCenterParameters}" target="_blank"><fmt:message key="directory.text.largermap" /></a>
		</div>
	</div>

</div>