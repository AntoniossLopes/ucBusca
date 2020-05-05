<%--
  Created by IntelliJ IDEA.
  User: antonio
  Date: 08/12/19
  Time: 10:50
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
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
<body>

</body>
</html>
