<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page isErrorPage="true" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Page non trouvée - Erreur 404</title>
    <style>
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            line-height: 1.6;
            margin: 0;
            padding: 20px;
            background-color: #f8f9fa;
            color: #343a40;
            display: flex;
            justify-content: center;
            align-items: center;
            min-height: 100vh;
            text-align: center;
        }
        .error-container {
            max-width: 600px;
            padding: 40px;
            background: white;
            border-radius: 10px;
            box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
        }
        .error-code {
            font-size: 72px;
            font-weight: bold;
            color: #dc3545;
            margin: 0 0 20px;
        }
        .error-message {
            font-size: 24px;
            margin-bottom: 30px;
            color: #495057;
        }
        .error-details {
            color: #6c757d;
            margin-bottom: 30px;
        }
        .btn {
            display: inline-block;
            padding: 10px 25px;
            background-color: #007bff;
            color: white;
            text-decoration: none;
            border-radius: 5px;
            transition: background-color 0.3s;
            font-weight: 500;
        }
        .btn:hover {
            background-color: #0056b3;
            color: white;
            text-decoration: none;
        }
        .error-icon {
            font-size: 60px;
            color: #dc3545;
            margin-bottom: 20px;
        }
    </style>
</head>
<body>
    <div class="error-container">
        <div class="error-icon">⚠️</div>
        <div class="error-code">404</div>
        <div class="error-message">Page non trouvée</div>
        <div class="error-details">
            <p>Désolé, la page que vous recherchez est introuvable ou n'existe plus.</p>
            <p>URL demandée: <strong>${pageContext.errorData.requestURI}</strong></p>
        </div>
        <a href="${pageContext.request.contextPath}/" class="btn">Retour à l'accueil</a>
    </div>
</body>
</html>
