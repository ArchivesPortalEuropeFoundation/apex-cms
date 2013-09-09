<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ taglib prefix="portal" uri="http://portal.archivesportaleurope.eu/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib prefix="liferay-portlet" uri="http://liferay.com/tld/portlet"%>
<portlet:defineObjects />
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
		<iframe id="maps" width="970" height="400" frameborder="0" scrolling="no" marginheight="0" marginwidth="0" src="${embeddedMapUrl}${mapUrlCenterParameters}"></iframe>
	</div>
</div>
<script type="text/javascript" src="https://maps.google.com/maps/api/js?sensor=false"></script>
<script type="text/javascript">
	$(document).ready(function(){
		initPrint("${country}","${archivalInstitutionName}","${embeddedMapUrl}","${countryName}");
	});
</script>