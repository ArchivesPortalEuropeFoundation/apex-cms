<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ taglib prefix="portal" uri="http://portal.archivesportaleurope.eu/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib prefix="liferay-portlet" uri="http://liferay.com/tld/portlet"%>
<portlet:defineObjects />
<portlet:resourceURL var="directoryTreeMapsUrl" id="directoryTreeGMaps" />
<div id="directoryPortlet">
		<div id="header">
			<div id="logo"></div>	
			<div class="left-header"></div>
			<div class="right-header"></div>
		</div>
	<div id="directory-column-right-content" class="portlet-layout">
		<portal:eag eagUrl="${eagUrl}"/>
	</div>
	<div class="portlet-layout">
		<div id="map_div" style="width: 1000px; height: 400px">	</div>
	</div>
</div>
<script src="https://maps.googleapis.com/maps/api/js?v=3&amp;sensor=false" ></script>
<script src="https://google-maps-utility-library-v3.googlecode.com/svn/tags/markerclustererplus/2.1.2/src/markerclusterer_packed.js"></script>
<script src="https://www.google.com/jsapi"></script>  <!-- type="text/javascript" -->
<script type="text/javascript">
	$(document).ready(function(){
		initPrint("${countryCode}","${directoryTreeMapsUrl}", "${selectedAiId}");
	});
</script>