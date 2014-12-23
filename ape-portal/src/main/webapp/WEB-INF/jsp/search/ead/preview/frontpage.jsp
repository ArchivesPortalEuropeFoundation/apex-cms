<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="com.liferay.portal.kernel.portlet.LiferayWindowState"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="portal" uri="http://portal.archivesportaleurope.eu/tags"%>
<%@ taglib prefix="liferay-portlet" uri="http://liferay.com/tld/portlet" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<portlet:defineObjects />
<c:set var="element"><c:out value="${param['element']}" /></c:set>
<c:set var="id"><c:out value="${param['id']}" /></c:set>

<div id="content">	
	<div id="realcontent">
	<portal:ead type="frontpage" xml="${eadContent.xml}" searchTerms="${term}"  searchFieldsSelectionId="${element}"  preview="true" xmlTypeName="${xmlType.resourceName}"/>
	</div>
</div>
<div id="more-line" class="hide-more-line">&nbsp;</div>
<div id="alwaysVisibleContainer">
</div>
<div id="viewFullFond" class="linkButton">
	<portal:eadPersistentLink var="url" repoCode="${aiRepoCode}" xmlTypeName="${xmlTypeName}" eadid="${eadid}" searchFieldsSelectionId="${element}" searchTerms="${term}"/>	
	<a href="${url}" target="_blank"><fmt:message key="seconddisplay.view.${xmlType.resourceName}" /></a>
</div>