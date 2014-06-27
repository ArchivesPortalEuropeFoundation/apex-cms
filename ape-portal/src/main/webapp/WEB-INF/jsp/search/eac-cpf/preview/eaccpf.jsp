<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="com.liferay.portal.kernel.portlet.LiferayWindowState"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="portal" uri="http://portal.archivesportaleurope.eu/tags"%>
<%@ taglib prefix="liferay-portlet" uri="http://liferay.com/tld/portlet" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<portlet:defineObjects />
<c:set var="identifier"><c:out value="${param['identifier']}" /></c:set>
<c:set var="repositoryCode"><c:out value="${param['repositoryCode']}" /></c:set>
<c:set var="element"><c:out value="${param['element']}" /></c:set>
<c:set var="term"><c:out value="${param['term']}" /></c:set>
<portal:friendlyUrl var="aiCodeUrl" type="directory-institution-code"/>
<portal:friendlyUrl var="eacUrlBase" type="eac-display"/>
<portal:friendlyUrl var="eadUrl" type="eaddisplay-persistent-archdesc"/>
<script type='text/javascript'>
	$(document).ready(function() {
		eraseData();
	});
</script>
<div id="content">
	<div id="realcontent">
		<div class="ead-content" id="eaccpfcontent">
		  <portal:eac type="eaccpfdetailspreview" eacUrl="${eacCpf.path}" repositoryCode="${repositoryCode}" eaccpfIdentifier="${identifier}" aiCodeUrl="${aiCodeUrl}" eacUrlBase="${eacUrlBase}" eadUrl="${eadUrl}" searchFieldsSelectionId="${element}"  searchTerms="${term}"/>
		</div>
	</div>
</div>
<div id="more-line" class="hide-more-line">&nbsp;</div>
<div id="viewFullFond" class="linkButton">
	<portal:eacCpfPersistentLink var="url" repoCode="${repositoryCode}" id="${identifier}" searchFieldsSelectionId="${element}"  searchTerms="${term}"/>
	<a href="${url}" target="_blank"><fmt:message key="seconddisplay.view.eaccpf" /><span class="icon_new_window">&nbsp;</span></a>
</div>

