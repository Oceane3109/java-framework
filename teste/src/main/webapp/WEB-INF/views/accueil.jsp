<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Accueil - Framework Java</title>
    <style>
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            line-height: 1.6;
            margin: 0;
            padding: 20px;
            background-color: #f5f5f5;
            color: #333;
        }
        .container {
            max-width: 800px;
            margin: 0 auto;
            background: white;
            padding: 30px;
            border-radius: 8px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
        }
        h1 {
            color: #2c3e50;
            border-bottom: 2px solid #3498db;
            padding-bottom: 10px;
        }
        .menu {
            margin: 20px 0;
            padding: 0;
            list-style: none;
        }
        .menu li {
            display: inline-block;
            margin-right: 15px;
        }
        .menu a {
            display: inline-block;
            padding: 8px 15px;
            background-color: #3498db;
            color: white;
            text-decoration: none;
            border-radius: 4px;
            transition: background-color 0.3s;
        }
        .menu a:hover {
            background-color: #2980b9;
        }
        .message {
            background-color: #e8f4f8;
            border-left: 4px solid #3498db;
            padding: 15px;
            margin: 20px 0;
            border-radius: 4px;
        }
    </style>
</head>
<body>
    <div class="container">
        <h1>Bienvenue sur notre application</h1>
        
        <ul class="menu">
            <li><a href="${pageContext.request.contextPath}/accueil">Accueil</a></li>
            <li><a href="${pageContext.request.contextPath}/profil">Profil</a></li>
            <li><a href="${pageContext.request.contextPath}/bonjour">API Bonjour</a></li>
            <li><a href="${pageContext.request.contextPath}/api/info">API Info</a></li>
        </ul>
        
        <div class="message">
            <h2>${message}</h2>
            <p>Ceci est une page générée par le contrôleur et affichée via JSP.</p>
        </div>
        
        <div>
            <h3>Fonctionnalités démontrées :</h3>
            <ul>
                <li>Routage basé sur les annotations</li>
                <li>Gestion des vues JSP</li>
                <li>Gestion des réponses API (JSON/texte)</li>
                <li>Passage de données aux vues</li>
            </ul>
        </div>
    </div>
</body>
</html>
