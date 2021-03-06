<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="ct" uri="/WEB-INF/tld/custom.tld" %>

<fmt:setLocale value="${sessionScope.locale}" scope="session"/>
<fmt:setBundle basename="locale"/>
<jsp:useBean id="offer" scope="request" type="by.tarasiuk.ct.model.entity.impl.Offer"/>

<html>
    <head>
        <meta charset="UTF-8">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/font.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/header.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/offer.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/icon.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/table.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/font-awesome.min.css">
        <title><fmt:message key="offer.viewer"/></title>
    </head>

    <body>
        <div id="container">
            <jsp:include page="/WEB-INF/jsp/common/header.jsp"/>

            <div id="middle">
                <div id="m-left">
                    <jsp:include page="/WEB-INF/jsp/navigation/left_navigation.jsp"/>
                </div>

                <div id="m-right">
                    <div id="mr-up">
                        <div id="window-title">
                            <fmt:message key="offer.viewer"/>
                        </div>

                        <c:if test="${offer.status == 'OPEN'}">
                            <div id="ow-buttons">
                                <form action="controller" method="post">
                                    <input type="hidden" name="command" value="show_offer_editor">

                                    <button type="submit" class="btn-simple btn-blue" name="offer_id" value="${offer.id}">
                                        <span class="icon icon-pencil">&nbsp;</span>
                                        <fmt:message key="offer.edit"/>
                                    </button>
                                </form>

                                <form action="controller" method="post">
                                    <input type="hidden" name="command" value="deactivate_offer">

                                    <button type="submit" class="btn-simple btn-red" name="offer_id" value="${offer.id}">
                                        <span class="icon icon-close">&nbsp;</span>
                                        <fmt:message key="offer.deactivate"/>
                                    </button>
                                </form>
                            </div>
                        </c:if>

                        <a class="btn-simple btn-blue" href="${pageContext.request.contextPath}/controller?command=show_account_offers">
                            <span class="icon icon-chevron-left">&nbsp;</span>
                            <fmt:message key="offers.my"/>
                        </a>
                    </div>

                    <div id="mr-down">
                        <div class="sub-title">
                            <span class="icon icon-circle" style="color: dodgerblue">&nbsp;</span>
                            <fmt:message key="offer.applyFreight"/>
                        </div>

                        <ct:account_offer_viewer/>

                        <div class="title">
                            <fmt:message key="tradings.actions"/>
                        </div>

                        <div class="sub-title">
                            <span class="icon icon-circle" style="color: dodgerblue">&nbsp;</span>
                            <fmt:message key="tradings.info"/>
                        </div>

                        <!-- Trading list for select -->
                        <ct:trading_action/>
                    </div>
                </div>
            </div>

            <jsp:include page="/WEB-INF/jsp/common/footer.jsp"/>
        </div>
    </body>
</html>