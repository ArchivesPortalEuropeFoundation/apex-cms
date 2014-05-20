<%@page contentType="text/html" pageEncoding="UTF-8"%>
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
<div id="content">
	<div id="realcontent">
			<div class="eadid"><c:out value="${eacCpf.identifier}"/></div>
				<h1 class="titleproper">
					<c:out value="${eacCpf.title}"/>
				</h1>
				<h2><fmt:message key="eadcontent.scopecontent"/></h2>	
				<div class="ead-content">
				<p><em>Still waiting</em> for the XSLT from <em>Issue 836</em>...</p>
				</div>				
		
	</div>
</div>
<div id="more-line" class="hide-more-line">&nbsp;</div>
<div id="viewFullFond" class="linkButton">
	<portal:eacCpfPersistentLink var="url" repoCode="${repositoryCode}" id="${identifier}" searchFieldsSelectionId="${element}"  searchTerms="${term}"/>
	<a href="${url}" target="_blank"><fmt:message key="seconddisplay.view.eaccpf" /><span class="icon_new_window">&nbsp;</span></a>
</div>

