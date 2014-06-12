<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="portal" uri="http://portal.archivesportaleurope.eu/tags"%>
<portal:friendlyUrl var="directoryDisplayUrl" type="directory-institution-code"/>
<div id="content">
<img src="http://maps.googleapis.com/maps/api/staticmap?zoom=8&size=400x150&maptype=roadmap
&markers=color:red%7C${coordinates.lat},${coordinates.lon}" height="150" width="400"/>
<div id="viewFullFond" class="linkButton">
<a href="https://maps.google.com/maps?daddr=${coordinates.lat},${coordinates.lon}" target="_blank"><fmt:message key="advancedsearch.eag.preview.directions"/><span class="icon_new_window">&nbsp;</span></a>
</div>
<portal:eag eagUrl="${eagUrl}"/>
</div>
<div id="more-line" class="hide-more-line">&nbsp;</div>
<div id="viewFullFond" class="linkButton">
<c:set var="url" value="${directoryDisplayUrl}/${ai.encodedRepositorycode}"/>
<a href="${url}" target="_blank"><fmt:message key="advancedsearch.eag.preview.details"/><span class="icon_new_window">&nbsp;</span></a>
</div>