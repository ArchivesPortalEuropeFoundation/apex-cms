<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet_2_0"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="portal" uri="http://portal.archivesportaleurope.eu/tags"%>
<%@ taglib prefix="liferay-portlet" uri="http://liferay.com/tld/portlet"%>
<%@ taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme"%>
<c:set var="portletNamespace"><portlet:namespace/></c:set>
<portal:removeParameters  var="bookmarkUrl" namespace="${portletNamespace}" parameters="eadid,element,term,ecId,id,unitid,xmlTypeName,repoCode"><portlet:resourceURL id="bookmarkAction"/></portal:removeParameters>

<form:form id="bookmarkForm" name="bookmarkForm" commandName="bookmark" method="post" action="${bookmarkUrl}">
	<c:if test="${!loggedIn}">
		<div class="error">
			<fmt:message key="bookmarks.logged.ko"/>
		</div>
	</c:if>
	<c:if test="${loggedIn}">
		<c:if test="${!saved}">
			<script type="text/javascript">hasSaved(false, "${message}");</script>
		</c:if>
		<c:if test="${saved}">
			<c:if test="${showBox}">
				<portlet:resourceURL var="seeAvaiableCollectionsUrl" id="seeAvaiableCollections"/>
				<script type="text/javascript">showCollections("${bookmarkId}","${seeAvaiableCollectionsUrl}");</script>
			</c:if>
			<c:if test="${!showBox}">
				<script type="text/javascript">hasSaved(true, "${message}");</script>
			</c:if>
		</c:if>
	</c:if>
</form:form>