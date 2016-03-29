<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet_2_0"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="portal" uri="http://portal.archivesportaleurope.eu/tags"%>
<%@ taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui"%>
<%@ taglib prefix="liferay-portlet" uri="http://liferay.com/tld/portlet"%>
<portlet:defineObjects />
<portlet:actionURL var="actionUrl">
    <portlet:param name="myaction" value="getApiKey" />
</portlet:actionURL>
<portlet:actionURL var="changeApiKey">
    <portlet:param name="myaction" value="changeApiKey" />
</portlet:actionURL>
<portlet:renderURL var="editUrl">
    <portlet:param name="myaction" value="editApiKey" />
</portlet:renderURL>
<portal:friendlyUrl var="apiKeyUrl" type="get-api-key"/>


<script type="text/javascript">
    $(document).ready(function () {
        $('#getApiKey').on('keypress keyup submit', function (e) {
            return e.which !== 13;
        });

        $("#getApiKey").submit(function () {
            var expression = /https?:\/\/(www\.)?[-a-zA-Z0-9@:%._\+~#=]{2,256}\.[a-z]{2,6}\b([-a-zA-Z0-9@:%_\+.~#?&//=]*)/;
//            var expressionWithOutHttp = /(www\.)?[-a-zA-Z0-9@:%._\+~#=]{2,256}\.[a-z]{2,6}\b([-a-zA-Z0-9@:%_\+.~#?&//=]*)/;
            var urlText = $("#domain").val();
            var terms = $('#agree').is(":checked");
            var valid = true;
            if (!urlText.match("^http[s]*://")) {
                urlText = "http://" + urlText;
            }
            var validUrl = expression.test(urlText);

            if (validUrl) {
                $("#urlError").hide();
                valid = valid && true;
            } else {
                $("#urlError").show();
                valid = valid && false;
            }
            if (terms) {
                $("#termError").hide();
                valid = valid && true;

            } else {
                valid = valid && false;
                $("#termError").show();

            }
            if (valid) {
                $("#domain").val(urlText);
            } else {
                return false;
            }
        });
    });
</script>

<div id="languagePortlet">
    <c:choose>
        <c:when test="${not empty apiKey.key}">
            <h1><fmt:message key="label.api.key.alreadyhave"/></h1>
            <h2><fmt:message key="label.api.key"/> : ${apiKey.key}</h2>
        </c:when>
        <c:otherwise>
            <h1><fmt:message key="label.api.key.get"/></h1>
        </c:otherwise>
    </c:choose>

    <form:form id="getApiKey" name="getApiKey" commandName="apiKey" method="post"
               action="${actionUrl}">
        <form:hidden path="firstName" value="${apiKey.firstName}"/>
        <form:hidden path="lastName" value="${apiKey.lastName}"/>
        <form:hidden path="email" value="${apiKey.email}"/>
        <form:hidden path="key" value="${apiKey.key}"/>
        <form:hidden path="liferayUserId" value="${apiKey.liferayUserId}"/>
        <form:hidden path="status" value="${apiKey.status}"/>
        <table class="defaultlayout">
            <tr>
                <th><fmt:message key="advancedsearch.eaccpf.typeofname.firstname"/>:</th>
            <td><c:out value="${apiKey.firstName}"/></td>
            </tr>
            <tr>
                <th><fmt:message key="advancedsearch.eaccpf.typeofname.lastname"/>:</th>
            <td><c:out value="${apiKey.lastName}"/></td>
            </tr>
            <tr>
                <th><fmt:message key="label.email.contact"/>:</th>
            <td><c:out value="${apiKey.email}"/></td>
            </tr>
            <c:choose>
                <c:when test="${not empty apiKey.key}">
                    <tr>
                        <th><fmt:message key="feedback.url"/>:</th>
                    <td><c:out value="${apiKey.domain}"/></td>
                    </tr>
                    <tr>
                        <th><fmt:message key="label.api.key"/>:</th>
                    <td><c:out value="${apiKey.key}"/></td>
                    </tr>
                    <a href="${changeApiKey}"><fmt:message key="label.api.key.change"/></a>

                </c:when>
                <c:otherwise>
                    <tr>
                        <th class="description"><fmt:message key="feedback.url"/>:</th>
                        <td>
                            <form:input path="domain"  cssClass="longInput" maxlength="100" value="${apiKey.domain}"/>
                            <label class="error" id="urlError" for="domain" hidden><fmt:message key="error.invalid.url"/></label>
                        </td>
<!--                        <td>
                            <label class="error" id="urlError" for="domain" hidden><fmt:message key="error.invalid.url"/></label>
                        </td>-->
                    </tr>
                    <tr>
                        <th class="description"><fmt:message key="label.api.key.agreement"/>:</th>
                        <td>
                            <input type="checkbox" id="agree"/>
                            <label class="error" id="termError" for="domain" hidden><fmt:message key="error.invalid.term"/></label>
                        </td>
<!--                        <td>
                            <label class="error" id="termError" for="domain" hidden><fmt:message key="error.invalid.term"/></label>
                        </td>-->
                    </tr>
                    <tr>
                        <td>&nbsp;</td>
                        <td>
                            <input class="aui-button-input aui-button-input-submit" type="submit" value="Submit" id="apiSubmit"/>
                        </td>
                    </tr>
                </c:otherwise>
            </c:choose>
        </table>
    </form:form>
</div>