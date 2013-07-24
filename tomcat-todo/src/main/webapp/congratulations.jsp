<html>
<head>
<title>And The Winner Is...</title>
<link rel="stylesheet" type="text/css" href="stylesheet.css">
<% String winner = (String) request.getAttribute("winner");
String country = (String) request.getAttribute("country"); %>

<font size = "18">
<span style="color:white">
Congratulations <%=winner%> of <%=country %>! You are the Champion of the Stars!
</span>
</font>

<form = "index.html">
<input type ="Submit" value ="Play again?">
</form>