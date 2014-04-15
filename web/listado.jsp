<%-- 
    Document   : listado
    Created on : 11-abr-2014, 21:36:29
    Author     : luis
--%>

<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
    </head>
    <body>
        
        <table>
            <tr>
                <th>
                    Nombre
                </th>
                <th>
                    Salario
                </th>
                <th>
                    Acciones
                </th>
            </tr>
        <c:forEach items="${emp}" var="emple" >
            <tr>
                
                <td>
                   ${emple.nombre} 
                </td>
                <td>
                    <fmt:formatNumber value="${emple.salario}" type="currency" />
                </td>
                <td>
                    
                    <a href="borrar.html?id=${emple.idEmpleado}">Borrar</a>
                    
                    <a href="modificar.html?id=${emple.idEmpleado}">Modificar</a>
                </td>
                
            </tr>
            
            
         
        </c:forEach>    
        </table>
        
        
          
            
    </body>
</html>
