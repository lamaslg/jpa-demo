<%-- 
    Document   : modificar
    Created on : 15-abr-2014, 21:41:47
    Author     : luis
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
    </head>
    <body>
        <form action="runModificar.html" method="post">
            <input type="hidden" name="id" value="${emple.idEmpleado}">
            
            Nombre:<input type="text" name="txNom" 
                          value="${emple.nombre}">
            <br>
            Salario: <input type="text" name="txSal" 
                            value="${emple.salario}">
            
            <br>
            <input type="submit" value="Enviar">
            
            
        </form>
    </body>
</html>





