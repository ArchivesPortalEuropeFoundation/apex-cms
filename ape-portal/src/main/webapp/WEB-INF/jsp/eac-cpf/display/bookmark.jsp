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

<div id="bookmarkPortlet">

<form:form id="bookmarkForm" name="bookmarkForm" commandName="bookmark" method="post" action="${bookmarkUrl}">
    
    <table class="bookmarkForm" id="bookmarkFormTr">
		<tr id="bookmarkFormTr">
		</tr>
        <c:if test="${!loggedIn}">
	        <tr>
	            <td>
	            	<div id="answerMessageSavedSearch">
						<script type="text/javascript">showError("${message}");</script>
					</div>
				</td>
	        </tr> 
        </c:if>
		<c:if test="${loggedIn}">
			<c:if test="${!saved}">
		        <tr>
		            <td>
		            	<div id="answerMessageSavedSearch">
							<script type="text/javascript">hasSaved(false, "${message}");</script>
						</div>
					</td>
		        </tr> 
	        </c:if>
     		<c:if test="${saved}">
		        <tr>
		            <td>
		            	<div id="answerMessageSavedSearch">
							<script type="text/javascript">hasSaved(true, "${message}");</script>
						</div>
					</td>
		        </tr> 
	        </c:if>
		</c:if>
		</table>
</form:form>
</div>