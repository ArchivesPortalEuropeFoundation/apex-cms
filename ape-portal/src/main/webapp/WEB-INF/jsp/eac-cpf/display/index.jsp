<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.liferay.portal.kernel.portlet.LiferayWindowState"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui"%>
<%@ taglib uri="http://liferay.com/tld/portlet" prefix="liferay-portlet"%>
<%@ taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme"%>
<%@ taglib uri="http://portal.archivesportaleurope.eu/tags" prefix="portal"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<script type="text/javascript" src="${recaptchaAjaxUrl}"></script>

<portlet:defineObjects />

<c:set var="element">
	<c:out value="${param['element']}" />
</c:set>
<c:set var="term">
	<c:out value="${param['term']}" />
</c:set>
<c:set var="databaseId">
	<c:out value="${param['databaseId']}" />
</c:set>

<portlet:resourceURL var="displayEacUrl" id="displayEacDetails">
  <!--   <portlet:param name="solrId" value="${solrId}" /> -->
	<portlet:param name="databaseId" value="${databaseId}" />
	<portlet:param name="element" value="${element}" />
 	<portlet:param name="term" value="${term}" />
   	<portlet:param name="langNavigator" value="${langNavigator}"/>
	<portlet:param name="translationLanguage" value="${translationLanguage}" />
</portlet:resourceURL>
 
<portlet:renderURL var="printEacDetailsUrl" windowState="<%=LiferayWindowState.POP_UP.toString()%>">
	<portlet:param name="myaction" value="printEacDetails" />
	<portlet:param name="repositoryCode" value="${repositoryCode}" />
	<portlet:param name="eaccpfIdentifier" value="${eaccpfIdentifier}" /> 
	<portlet:param name="element" value="${element}" />
	<portlet:param name="term" value="${term}" />
	<portlet:param name="type" value="${type}" />
   	<portlet:param name="langNavigator" value="${langNavigator}"/>
	<portlet:param name="translationLanguage" value="${translationLanguage}" />
</portlet:renderURL>

<portlet:renderURL var="translateEacDetailsUrl">
	<portlet:param name="myaction" value="translateEacDetails" />
	<portlet:param name="repositoryCode" value="${repositoryCode}" />
	<portlet:param name="eaccpfIdentifier" value="${eaccpfIdentifier}" /> 
	<portlet:param name="element" value="${element}" />
	<portlet:param name="term" value="${term}" />
	<portlet:param name="type" value="${type}" />
   	<portlet:param name="langNavigator" value="${langNavigator}"/>
	<portlet:param name="translationLanguage" value="${translationLanguage}" />
</portlet:renderURL>

<portal:friendlyUrl var="aiCodeUrl" type="directory-institution-code"/>
<portal:friendlyUrl var="eacUrlBase" type="eac-display"/>
<portal:friendlyUrl var="eadUrl" type="eaddisplay-persistent-archdesc"/>

<script type="text/javascript">var switchTo5x=true;</script>
<script type="text/javascript" src="https://wd-edge.sharethis.com/button/buttons.js"></script>
<script type="text/javascript">stLight.options({publisher: "e059943f-766d-434b-84ea-1e0d4a91b7d4", doNotHash: true, doNotCopy: true, hashAddressBar: true, shorten:false});</script>

<portlet:actionURL var="contactUrl">
	<portlet:param name="myaction" value="contact" />
</portlet:actionURL>

<script type="text/javascript">
	 var RecaptchaOptions = {
	    theme : 'white'
	 };
</script>
<script type='text/javascript'>
	$(document).ready(function() {
		document.title = "${documentTitle}";
		init();	
		makeRelationsCollapsible();
		enableFeedback();
	});
</script>

<div id="eacCpfDisplayPortlet">
	<!-- Path for the apeEAC-CPF. -->
	<h3 id="contextInformation">
		${localizedCountryName}
		&gt; <a href="${aiCodeUrl}/${archivalInstitution.encodedRepositorycode}">${archivalInstitution.ainame}</a>
	</h3>

	<!-- Persistent link. -->
	<portal:eacCpfPersistentLink var="url" repoCode="${archivalInstitution.repositorycode}" id="${eaccpfIdentifier}" searchFieldsSelectionId="${element}" searchTerms="${term}" />

	<!-- User's feedback section. -->
	<div id="feedbackArea">
		<div>&nbsp;</div>
		<portlet:resourceURL var="feedbackUrl" id="feedback"/>
		<div id="feedbackEacCpf" class="sendFeedback linkButton">
			<a href="javascript:showFeedback('${feedbackUrl}', '${documentTitle}','${url}','${recaptchaPubKey}')">
				<fmt:message key="label.feedback" />
			</a>	
		</div>
		<div id="feedbackContent" class="hidden"></div>
		<div>&nbsp;</div>
	</div>

	<!-- Display of the apeEAC-CPF content. -->
	<div id="eaccpfcontent">
		<!-- Div for the buttons. -->
		<div id="buttonsDiv">
			<!-- Print button. -->
			<div id="printEacDetails" class="linkButton">
				<a href="javascript:printEacDetails('${printEacDetailsUrl}')">
					<fmt:message key="label.print" />
					<span class="icon_print">&nbsp;</span>
				</a>
			</div>
			
			<div id="bookmarksArea">
				<div>&nbsp;</div>
				<portlet:resourceURL var="bookmarkUrl" id="bookmark"/>
				<div id="bookmarkEacCpf" class="doBookmark linkButton">
		 			<a href="javascript:showBookmark('${bookmarkUrl}','${documentTitle}','${url}')">Bookmark this</a>
				</div>
				<div id="bookmarkContent" class="hidden"></div>
				<div>&nbsp;</div>
			</div>
			
			<div id="bookmarksArea">
				<div>&nbsp;</div>
				<div id="bookmarkEacCpf" class="doBookmark linkButton">
		 			<a href="javascript:saveBookmark('${documentTitle}','${url}')">Bookmark this</a> <%-- <fmt:message key="label.feedback" /> --%>	
				</div>
				<div id="answerMessageSavedBookmark"></div>
				<div>&nbsp;</div>
			</div>
			<!-- Translations selector. -->
			<div id="translationsSelectorDiv">
				<form:select path="type" name="translationsSelector" id="translationsSelector" onchange="javascript:translateContent('${translateEacDetailsUrl}', $(this))" >
					<c:forEach var="langOptions" items="${languagesMap}">
						<c:choose>
							<c:when test="${translationLanguage == langOptions.key}">
								<form:option selected="true" value="${langOptions.key}">
									${langOptions.value}
								</form:option>
							</c:when>
							<c:otherwise>
								<form:option value="${langOptions.key}">
									${langOptions.value}
								</form:option>
							</c:otherwise>
						</c:choose>
					</c:forEach>
				</form:select>
			</div>
		</div>

		<portal:eac type="eaccpfdetails" eacUrl="${eac.path}" repositoryCode="${repositoryCode}" eaccpfIdentifier="${eaccpfIdentifier}" aiCodeUrl="${aiCodeUrl}" eacUrlBase="${eacUrlBase}" eadUrl="${eadUrl}" searchFieldsSelectionId="${element}" searchTerms="${term}" langNavigator="${langNavigator}" translationLanguage="${translationLanguage}" />
	</div>
</div>
	