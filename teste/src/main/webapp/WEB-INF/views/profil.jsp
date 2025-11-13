<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Profil Utilisateur - Framework Java</title>
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
        .profile-card {
            background-color: #f8f9fa;
            border-radius: 8px;
            padding: 20px;
            margin: 20px 0;
            border-left: 4px solid #3498db;
        }
        .profile-info {
            margin: 15px 0;
        }
        .profile-info label {
            font-weight: bold;
            display: inline-block;
            width: 100px;
            color: #555;
        }
        .back-link {
            display: inline-block;
            margin-top: 20px;
            padding: 8px 15px;
            background-color: #6c757d;
            color: white;
            text-decoration: none;
            border-radius: 4px;
            transition: background-color 0.3s;
        }
        .back-link:hover {
            background-color: #5a6268;
        }
    </style>
</head>
<body>
    <div class="container">
        <h1>Profil Utilisateur</h1>
        
        <div class="profile-card">
            <div class="profile-info">
                <label>Nom :</label>
                <span>${utilisateur}</span>
            </div>
            <div class="profile-info">
                <label>Email :</label>
                <span>${email}</span>
            </div>
            <div class="profile-info">
                <label>Rôle :</label>
                <span>Utilisateur standard</span>
            </div>
        </div>
        
        <a href="${pageContext.request.contextPath}/accueil" class="back-link">← Retour à l'accueil</a>
    </div>
</body>
</html>
