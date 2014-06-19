<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="portal" uri="http://portal.archivesportaleurope.eu/tags"%>
<%@ taglib prefix="liferay-portlet" uri="http://liferay.com/tld/portlet"%>
<portlet:defineObjects />

<c:set var="element">
	<c:out value="${param['element']}" />
</c:set>
<c:set var="term">
	<c:out value="${param['term']}" />
</c:set>
<c:set var="repositoryCode">
	<c:out value="${param['repositoryCode']}" />
</c:set>
<c:set var="eaccpfIdentifier">
	<c:out value="${param['eaccpfIdentifier']}" />
</c:set>	

<portal:friendlyUrl var="aiCodeUrl" type="directory-institution-code"/>
<portal:friendlyUrl var="eacUrlBase" type="eac-display"/>
<portal:friendlyUrl var="eadUrl" type="eaddisplay-persistent-archdesc"/>

<script type='text/javascript'>
	$(document).ready(function() {
		initPrint();
	});
</script>

<div id="eacCpfDisplayPortlet">
		<div id="header">
			<div id="logo"></div>	
			<div class="left-header"></div>
			<div class="right-header"></div>
		</div>
		<div id="eaccpfcontent">
	   		<portal:eac type="eaccpfdetails" eacUrl="${eac.path}" repositoryCode="${repositoryCode}" eaccpfIdentifier="${eaccpfIdentifier}" aiCodeUrl="${aiCodeUrl}" eacUrlBase="${eacUrlBase}" eadUrl="${eadUrl}" searchFieldsSelectionId="${element}" searchTerms="${term}" />
	   </div> 

</div>
