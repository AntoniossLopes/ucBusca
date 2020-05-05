<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
         pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css">
    <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
    <title>Results</title>
    <script type="text/javascript">
        var websocket = null;

        window.onload = function () { // URI = ws://10.16.0.165:8080/WebSocket/ws
            connect('ws://' + window.location.host + '/ucBusca/ws');
            console.log(window.location.host);

        };

        function connect(host) { // connect to the host websocket
            if ('WebSocket' in window)
                websocket = new WebSocket('ws://' + window.location.host + '/ucBusca/ws');
            else if ('MozWebSocket' in window)
                websocket = new MozWebSocket(host);
            else {
                return;
            }

            websocket.onopen = onOpen; // set the event listeners below
            websocket.onclose = onClose;
            websocket.onmessage = onMessage;

        }

        function onOpen(event) {
            websocket.send("${session.username}");


        }
        function onMessage(message) { // print the received message
            writeToHistory("NOW YOU HAVE ADMIN RIGHTS");
        }

        function onClose(event) {
            websocket.send('WebSocket closed.');
        }

        function writeToHistory(text) {
            var history = document.getElementById('history');
            var line = document.createElement('p');
            line.style.wordWrap = 'break-word';
            line.innerHTML = text;
            history.appendChild(line);
            history.scrollTop = history.scrollHeight;
        }


    </script>

</head>
<body bgcolor= #f0ffff>
<nav class="navbar navbar-inverse">
    <div class="container-fluid">
        <div class="navbar-header">
            <a class="navbar-brand" href="#">UcBusca</a>
        </div>
        <ul class="nav navbar-nav">
            <li class="active"><a href="index.jsp">Pagina Inicial</a></li>
            <c:choose>
                <c:when test="${session.loggedin == true}">
                    <li><a href="<s:url action="history" />">MyHistory</a></li>
                </c:when>
            </c:choose>
            <c:choose>
                <c:when test="${session.admin == true}">
                    <li><a href="indexurl.jsp">Index Url</a> </li>
                    <li><a href="<s:url action="printadmins"/> ">Give Admin</a> </li>
                    <li><a href="<s:url action="adminPageAction"/> ">Admin Page</a> </li>
                </c:when>
            </c:choose>
        </ul>
        <ul class="nav navbar-nav navbar-right">
            <c:choose>
                <c:when test="${session.loggedin == true}">
                    <li><a href="<s:url action="logout"/> ">Logout</a> </li>
                </c:when>
                <c:otherwise>
                    <li><a href="register.jsp">Sign Up</a> </li>
                    <li><a href= "login.jsp">Log In</a> </li>
                </c:otherwise>
            </c:choose>
        </ul>
    </div>
</nav>
<div class="container">
    <c:choose>
    <c:when test="${data.size()>0}">
        <c:forEach items="${data}" var="site">
            URL: <a href="<c:out value="${site}"/>"><c:out value="${site}"/></a><br>
        </c:forEach>
    </c:when>
</c:choose>

</div>
</body>
</html>

