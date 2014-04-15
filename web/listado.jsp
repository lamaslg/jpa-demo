<%-- 
    Document   : listado
    Created on : 11-abr-2014, 21:36:29
    Author     : luis
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
    </head>
    <body>
        <c:forEach items="${emp}" var="emple" >
            
            Nombre: ${emple.nombre}
        </c:forEach>  
            
    </body>
</html>
