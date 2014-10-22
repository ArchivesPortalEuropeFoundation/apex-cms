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
<portal:removeParameters  var="eadTreeUrl" namespace="${portletNamespace}" parameters="myaction,xmlTypeName,unitid,eadid,repoCode,element,term"><portlet:resourceURL id="eadTree">
	<portlet:param name="solrId" value="${solrId}" />
	<portlet:param name="ecId" value="${ead.eadContent.ecId}" />
</portlet:resourceURL></portal:removeParameters>
<portal:removeParameters  var="displayEadUrl" namespace="${portletNamespace}" parameters="myaction,xmlTypeName,unitid,eadid,repoCode"><portlet:resourceURL  id="displayEadDetails">
	<portlet:param name="ecId" value="${ead.eadContent.ecId}" />
</portlet:resourceURL></portal:removeParameters>



<portal:friendlyUrl var="aiCodeUrl" type="directory-institution-code"/>

<div id="eadDisplayPortlet">
	<c:if test="${!empty errorMessage}">
	<div class="error errorHeader">
		<fmt:message key="${errorMessage}" />
	</div>
	</c:if>
	<h3 id="contextInformation">
		${localizedCountryName}
		&gt; <a href="${aiCodeUrl}/${archivalInstitution.encodedRepositorycode}">${archivalInstitution.ainame}</a>
	</h3>
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
</script>
<script defer="defer" type="text/javascript">
    window.onload=function(){
        var mycode;
        mycode=document.createElement("script");
        mycode.type="text/javascript";
        mycode.src="https://wd-edge.sharethis.com/button/buttons.js";
        document.getElementsByTagName("head")[0].appendChild(mycode);
        var switchTo5x=true;
        stLight.options({publisher: "e059943f-766d-434b-84ea-1e0d4a91b7d4", doNotHash: true, doNotCopy: true, hashAddressBar: true, shorten:false, onhover : true, tracking : 'google'});
		stButtons.locateElements();
    }
</script>
	
</div>