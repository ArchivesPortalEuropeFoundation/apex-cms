<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="portal" uri="http://portal.archivesportaleurope.eu/tags"%>
<%@ taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui"%>
<%@ taglib prefix="liferay-portlet" uri="http://liferay.com/tld/portlet"%>

<portlet:defineObjects />
<c:set var="element">
	<c:out value="${param['element']}" />
</c:set>
<c:set var="term">
	<c:out value="${param['term']}" />
</c:set>
<c:set var="databaseId">
	<c:out value="${param['databaseId']}" />
</c:set>
<%-- <c:set var="repositoryCode">
	<c:out value="${param['repositoryCode']}" />
</c:set>
<c:set var="eaccpfIdentifier">
	<c:out value="${param['eaccpfIdentifier']}" />
</c:set> --%>

<portlet:resourceURL var="displayEacUrl" id="displayEacDetails">
  <!--   <portlet:param name="solrId" value="${solrId}" /> -->
	<portlet:param name="databaseId" value="${databaseId}" />
<%-- 	<portlet:param name="repositoryCode" value="${repositoryCode}" />
	<portlet:param name="eaccpfIdentifier" value="${eaccpfIdentifier}" /> --%>
<!-- 	<portlet:param name="id" value="${id}" /> -->
	<portlet:param name="element" value="${element}" />
 	<portlet:param name="term" value="${term}" /> 
</portlet:resourceURL>
 
<script type="text/javascript">var switchTo5x=true;</script>
<script type="text/javascript" src="https://wd-edge.sharethis.com/button/buttons.js"></script>
<script type="text/javascript">stLight.options({publisher: "e059943f-766d-434b-84ea-1e0d4a91b7d4", doNotHash: true, doNotCopy: true, hashAddressBar: true, shorten:false});</script>

<script type='text/javascript'>
	$(document).ready(function() {
		init();	
	});		
</script>
<div id="eacCpfDisplayPortlet">
 	<div id="eaccpfcontent">
	   <portal:eac type="eaccpfdetails" eacUrl="${eac.path}" repositoryCode="${repositoryCode}" eaccpfIdentifier="${eaccpfIdentifier}" />
	</div> 
</div>