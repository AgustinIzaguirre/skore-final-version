<!DOCTYPE html>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<html>
<head>
    <%-- Include Bootstrap v4.1.3 and Custom CSS --%>
    <jsp:include page="css.jsp"></jsp:include>
    <link rel="icon" href="img/bullseye-solid.ico"/>
    <title>skore</title>
</head>
<body>

<%-- Include Navigation Bars --%>
<jsp:include page="navbar.jsp"></jsp:include>

<div class="container-fluid">
    <div class="row">
        <div class="container-fluid sign-in-container offset-sm-2 col-sm-8 offset-md-3 col-md-6 offset-lg-3 col-lg-6 offset-xl-4 col-xl-4"> <!-- Form container -->

            <div class="row text-center">
                <div class="col">
                    <img class="profile-pic" src="img/user-default.svg"/>
                </div>
            </div>

            <div class="row text-center">
                <div class="col">
                    <p class="profile-name"><c:out value="${user.getFullName()}"/></p>
                </div>
            </div>

            <div class="row mt-4 text-center">
                <div class="col">
                    <span class="mr-1"><spring:message code="areYouNewLabel"/></span>
                    <a class="link" href="/create"><spring:message code="signUpMessage"/></a>
                </div>
            </div>

        </div> <!-- END Form container -->
    </div>
</div>

<%-- Include JS Scripts --%>
<jsp:include page="js.jsp"></jsp:include>

</body>
</html>