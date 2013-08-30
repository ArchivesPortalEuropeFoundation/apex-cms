<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="com.liferay.portal.kernel.portlet.LiferayWindowState"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="ape"
	uri="http://commons.archivesportaleurope.eu/tags"%>
<%@ taglib prefix="portal"
	uri="http://portal.archivesportaleurope.eu/tags"%>
<%@ taglib prefix="liferay-portlet" uri="http://liferay.com/tld/portlet"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<portlet:defineObjects />
<portal:friendlyUrl var="aiCodeUrl" type="directory-institution-code"/>
<div id="eadDisplayPortlet">
	<h3 id="contextInformation">
		<fmt:message key="country.${fn:toLowerCase(country.cname)}" />
		&gt; <a href="${aiCodeUrl}/${archivalInstitution.repositorycode}">${archivalInstitution.ainame}</a>
	</h3>
	<div id="eaddetailsContent">
		<portal:ead type="frontpage" xml="${eadContent.xml}" />
	</div>
</div>