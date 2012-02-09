<%@page contentType="text/html" session="false" pageEncoding="UTF-8" %>
<%@page import="fr.jmmc.mcs.astro.Catalog"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>SearchCal catalog list</title>
    </head>
    <body>
        <h1>SearchCal catalog list:</h1>
        <%
        out.println(Catalog.toHtmlTable());
        %>
    </body>
</html>
