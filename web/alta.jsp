<%-- 
    Document   : alta.jsp
    Created on : 14-abr-2014, 21:15:19
    Author     : luis
--%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
    </head>
    <body>
       
        <form action="alta.html" method="post">
            Nombre:<input type="text" name="txNom">
            <br>
            Salario: <input type="text" name="txSal">
            <br>
            <c:import url="/ServletComboPuestos"></c:import>
            <br>
            <input type="submit" value="enviar">
            
        </form>
        
    </body>
</html>
