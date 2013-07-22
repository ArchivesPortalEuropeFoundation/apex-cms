<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="portal" uri="http://portal.archivesportaleurope.eu/tags"%>
<%@ taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui"%>
<%@ taglib prefix="liferay-portlet" uri="http://liferay.com/tld/portlet"%>
<portlet:defineObjects />
<portlet:actionURL var="translationShowUrl">
    <portlet:param name="myaction" value="display" />
</portlet:actionURL>

<div id="languagePortlet">

    <table>
        <c:forEach var="layout" varStatus="stat" items="${layouts}">
            <form:form id="layout" name="layout" method="post" action="${translationShowUrl}">
                <tr>
                    <td>${layout}</td>
                    <td>
                        <input type="hidden" name="layout" size="50" id="layout" value="${layout}"/>
                        <input type="submit" id="translations_save_send" name="method:execute" value="Go" class="mainButton" />
                    </td>
                </tr>
            </form:form>
        </c:forEach>
    </table>
</div>