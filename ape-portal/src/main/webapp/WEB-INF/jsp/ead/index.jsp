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

<portal:removeParameters  var="eadTreeUrl" namespace="${portletNamespace}" parameters="myaction,unitid,eadid,repoCode,element,term,pageNumber,databaseId">
    <portlet:resourceURL id="eadTree">
        <portlet:param name="solrId" value="${solrId}" />
        <portlet:param name="ecId" value="${eadContent.ecId}" />
        <portlet:param name="xmlTypeName" value="${xmlTypeName}" />
    </portlet:resourceURL>
</portal:removeParameters>

<portal:removeParameters  var="displayEadUrl" namespace="${portletNamespace}" parameters="myaction,xmlTypeName,unitid,eadid,repoCode,pageNumber,databaseId,preview">
    <portlet:resourceURL  id="displayEadDetails">
        <portlet:param name="ecId" value="${eadContent.ecId}" />
        <portlet:param name="previewDetails" value="${previewDetails}" />
        <portlet:param name="type" value="${xmlTypeName}" />
    </portlet:resourceURL>
</portal:removeParameters>

<portal:friendlyUrl var="eacUrl" type="eac-display"/>
<portal:friendlyUrl var="aiCodeUrl" type="directory-institution-code"/>

<c:if test="${previewDetails}">
    <header id="banner" role="banner">
        <div id="header">
            <div id="logo"></div>
            <div class="left-header"></div>
            <div class="right-header"> </div>
        </div>
    </header>
</c:if>

<div id="eadDisplayPortlet">
    <c:if test="${!empty errorMessage}">
        <div class="error errorHeader">
            <fmt:message key="${errorMessage}" />
        </div>
    </c:if>
    <h3 id="contextInformation">
        ${localizedCountryName}
        &gt; <c:choose><c:when test="${previewDetails}">${archivalInstitution.ainame}</c:when><c:otherwise><a href="${aiCodeUrl}/${archivalInstitution.encodedRepositorycode}">${archivalInstitution.ainame}</a></c:otherwise>
        </c:choose>
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
        initDAOs();
        initDAOs();
    </script>
    <c:if test="${not previewDetails}">
        <script defer="defer" type="text/javascript">
            window.onload = function () {
            initShareButtons();
            }
        </script>
    </c:if>	
</div>