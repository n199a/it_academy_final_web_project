<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<fmt:setLocale value="${sessionScope.locale}" scope="session"/>
<fmt:setBundle basename="locale"/>

<html lang="en">
    <head>
        <meta charset="UTF-8">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/font.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/sign.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/icon.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/font-awesome.min.css">
        <title><fmt:message key="form.signIn.title"/></title>
    </head>
    <body>
        <div id="container">
            <div id="main">
                <div id="window-title" class="row">
                    <fmt:message key="form.signIn.message"/>
                </div>

                <form action="controller" method="post">
                    <div class="row">
                        <label class="description">
                            <fmt:message key="form.signIn.warning"/>
                        </label>
                    </div>

                    <div class="row">
                        <div class="block">
                            <div class="data">
                                <div class="icon-block">
                                    <span class="icon icon-user-circle-o"></span>
                                </div>

                                <div class="input-block">
                                    <input type="text" id="account_login" name="account_login" value="${param.account_login}" placeholder="<fmt:message key="placeholder.userLogin"/>" required>
                                </div>
                            </div>
                        </div>
                    </div>

                    <div class="row">
                        <div class="block">
                            <div class="data">
                                <div class="icon-block">
                                    <span class="icon icon-lock"></span>
                                </div>

                                <div class="input-block">
                                    <input type="password" id="account_password" name="account_password" placeholder="<fmt:message key="placeholder.userPassword"/>">
                                </div>
                            </div>
                        </div>
                    </div>

                    <div class="error-message row">
                        <c:if test="${message_query_error == true}">
                            <fmt:message key="message.query.error"/>
                        </c:if>

                        <c:if test="${incorrect_sign_in_data == true}">
                            <fmt:message key="message.incorrect.signInData"/>
                        </c:if>

                        <c:if test="${account_banned == true}">
                            <fmt:message key="message.account.banned"/>
                        </c:if>

                        <c:if test="${account_not_activated == true}">
                            <fmt:message key="message.account.notActivated"/>
                        </c:if>
                    </div>

                    <div class="row">
                        <button type="submit" class="btn-confirm" name="command" value="go_to_main_page"><span class="icon icon-chevron-left">&nbsp;</span><fmt:message key="button.label.mainPage"/></button>
                        <button type="submit" class="btn-confirm" name="command" value="sign_in"><span class="icon icon-check">&nbsp;</span><fmt:message key="button.label.confirm"/></button>
                    </div>
                </form>
            </div>
        </div>

        <script type="text/javascript" src="${pageContext.request.contextPath}/js/showPassword.js"></script>
    </body>
</html>