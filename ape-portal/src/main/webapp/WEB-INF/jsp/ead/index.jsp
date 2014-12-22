<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="portal" uri="http://portal.archivesportaleurope.eu/tags"%>
<%@ taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui"%>
<%@ taglib prefix="liferay-portlet" uri="http://liferay.com/tld/portlet"%>
<script type="text/javascript" src="${recaptchaAjaxUrl}"></script>
<portlet:defineObjects />
<c:set var="portletNamespace"><portlet:namespace/></c:set>
<portal:removeParameters  var="eadTreeUrl" namespace="${portletNamespace}" parameters="myaction,xmlTypeName,unitid,eadid,repoCode,element,term,pageNumber,databaseId"><portlet:resourceURL id="eadTree">
	<portlet:param name="solrId" value="${solrId}" />
	<portlet:param name="ecId" value="${eadContent.ecId}" />
</portlet:resourceURL></portal:removeParameters>
<portal:removeParameters  var="displayEadUrl" namespace="${portletNamespace}" parameters="myaction,xmlTypeName,unitid,eadid,repoCode,pageNumber,databaseId"><portlet:resourceURL  id="displayEadDetails">
	<portlet:param name="ecId" value="${eadContent.ecId}" />
	<portlet:param name="preview" value="${preview}" />
</portlet:resourceURL></portal:removeParameters>

<portal:friendlyUrl var="aiCodeUrl" type="directory-institution-code"/>

<div id="eadDisplayPortlet">
	<c:if test="${!empty errorMessage}">
	<div class="error errorHeader">
		<fmt:message key="${errorMessage}" />
	</div>
	</c:if>
	<c:if test="${not preview}">
	<h3 id="contextInformation">
		${localizedCountryName}
		&gt; <a href="${aiCodeUrl}/${archivalInstitution.encodedRepositorycode}">${archivalInstitution.ainame}</a>
	</h3>
	</c:if>
	<div id="eadcontent">
		<div id="left-pane" class="pane">
			<div id="eadTree"></div>
		</div>
		<div id="splitter" class="pane"></div>
		<div id="right-pane" class="pane">
			<jsp:include page="eaddetails.jsp" />
		</div>
	</div>

<script type='text/javascript' defer="defer">
initEadTree("${eadTreeUrl}", "${displayEadUrl}", "<portlet:namespace/>");
initPanes();
initDAOs();
initDAOs();
</script>
<c:if test="${not preview}">
<script defer="defer" type="text/javascript">
    window.onload=function(){
    	initShareButtons();
    }
</script>
</c:if>	
</div>