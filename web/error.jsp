<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Hey!</title>
</head>
<body>
	<h4>You got an exception. Please <i>throw</i> it to someone who can handle it.</h4>
	<p><s:property value="exceptionStack" /></p>
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

</body>
</html>