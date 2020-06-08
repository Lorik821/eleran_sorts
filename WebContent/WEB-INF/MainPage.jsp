<%@ page pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="utf-8" />
        <title>Test</title>
    </head>
    <body>
        <p>Grimoire</p>
        <a href="http://localhost:8080/eleran_sorts/eleran.html">Acceuil</a>
        <p><br></p>
        <table>
            <% 
            String attribut = (String) request.getAttribute("test");
            out.println( attribut );

            %>
        </table>
        
    </body>
</html>