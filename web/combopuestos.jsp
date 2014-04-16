<%-- 
    Document   : combopuestos
    Created on : 16-abr-2014, 19:35:54
    Author     : luis
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>

<%@taglib uri="http://java.sun.com/jsp/jstl/core"
          prefix="c"%>

<select name="puesto">
    <c:forEach items="${puestos}" var="puesto">
        <option value="${puesto.idPuesto}">
            ${puesto.nombre}
        </option>
    </c:forEach>
</select>