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
<portlet:resourceURL var="directoryTreeMapsUrl" id="directoryTreeGMaps" />
<portlet:resourceURL var="getAndClickOnParentsAction" id="directoryTreeArchivalInstitution" />

<input type="hidden" id="getAndClickOnParentsAction" value="${getAndClickOnParentsAction}" />

<script src="${google_maps_url}${google_maps_license}&sensor=false"></script>

<script type="text/javascript">
	$(document).ready(function() {
		initDirectory("${directoryTreeUrl}", "${directoryTreeAiUrl}", "${aiDetailsUrl}", "${embeddedMapUrl}", "${mapUrl}", "${directoryTreeMapsUrl}");
	});
</script>

<div id="directoryPortlet">
	<div class="portlet-layout">
		<div id="map_div" style="width: 1000px; height: 500px">	</div>
  	</div>
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

</div>