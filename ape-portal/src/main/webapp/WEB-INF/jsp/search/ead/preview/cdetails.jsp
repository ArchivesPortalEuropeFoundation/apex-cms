<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="portal" uri="http://portal.archivesportaleurope.eu/tags"%>
<%@ taglib prefix="liferay-portlet" uri="http://liferay.com/tld/portlet" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<portlet:defineObjects />
<c:set var="element"><c:out value="${param['element']}" /></c:set>
<portal:eadPersistentLink var="secondDisplayUrl" repoCode="${aiRepoCode}" xmlTypeName="fa" eadid=""/>		

<div id="content">
	<div id="realcontent">
<portal:ead type="cdetails" xml="${c.xml}" searchTerms="${term}"  searchFieldsSelectionId="${element}" aiId="${aiId}"
			secondDisplayUrl="${secondDisplayUrl}" preview="true"  xmlTypeName="${xmlType.resourceName}" />
</div>
</div>
<div id="more-line" class="hide-more-line">&nbsp;</div>
<div id="alwaysVisibleContainer">
</div>
<div id="viewFullFond" class="linkButton">
	<portal:eadPersistentLink var="url"  repoCode="${aiRepoCode}" xmlTypeName="${xmlType.resourceName}" eadid="${eadid}" searchId="${c.id}" unitid="${c.unitid}" searchFieldsSelectionId="${element}" searchTerms="${term}"/>

	<a href="${url}" target="_blank"><fmt:message key="seconddisplay.view.${xmlType.resourceName}" /></a>
</div>

