<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ taglib prefix="portal" uri="http://portal.archivesportaleurope.eu/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<portlet:defineObjects />
<portal:eag eagUrl="${eagUrl}"/>
<div id="printEagDetails" class="linkButton">
	<a href="javascript:printEagDetails('test')"><fmt:message key="label.print" /><span
		class="icon_print">&nbsp;</span></a>
</div>