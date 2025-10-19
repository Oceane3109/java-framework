#!/bin/bash

echo "===================================="
echo "  DEPLOIEMENT AUTOMATIQUE DU FRAMEWORK"
echo "===================================="
echo

# Configuration des chemins
FRAMEWORK_DIR="$(pwd)/frameworkJAVA"
TEST_DIR="$(pwd)/teste"

# Configuration du port (par défaut: 8080)
TOMCAT_PORT="8080"

# Chemin spécifique de Tomcat
TOMCAT_HOME="/Users/oceanechristodoulou/Desktop/S3/apache-tomcat-10.1.28"

# Vérification de l'existence du répertoire Tomcat
if [ ! -d "$TOMCAT_HOME" ]; then
    echo "ERREUR: Le répertoire Tomcat n'existe pas: $TOMCAT_HOME"
    echo "Veuillez vérifier le chemin et mettre à jour la variable TOMCAT_HOME si nécessaire."
    exit 1
fi

TOMCAT_DIR="$TOMCAT_HOME/webapps"
FRAMEWORK_JAR="framework-java-1.0.0.jar"
TEST_WAR="teste-framework-1.0.0.war"
APP_NAME="teste-framework-1.0.0"

# Arrêter Tomcat s'il est en cours d'exécution
echo "Arrêt de Tomcat s'il est en cours d'exécution..."
if [ -f "$TOMCAT_HOME/bin/shutdown.sh" ]; then
    "$TOMCAT_HOME/bin/shutdown.sh" 2>/dev/null || true
    sleep 3  # Donner le temps à Tomcat de s'arrêter
fi

echo "TOMCAT_HOME détecté: $TOMCAT_HOME"
echo "Utilisation du port Tomcat par défaut: $TOMCAT_PORT"

# Fonction pour gérer les erreurs
error_exit() {
    echo "ERREUR: $1"
    echo "Appuyez sur Entrée pour quitter..."
    read -r
    exit 1
}

echo "[1/4] Installation du framework dans le référentiel Maven local..."
cd "$FRAMEWORK_DIR" || error_exit "Impossible d'accéder au répertoire du framework: $FRAMEWORK_DIR"
if ! mvn -f "$FRAMEWORK_DIR/pom.xml" clean install; then
    error_exit "Échec de l'installation du framework dans le référentiel Maven local"
fi
echo "✓ Framework installé avec succès dans le référentiel Maven local"

# Vérifier que le JAR a bien été installé dans le .m2 local
LOCAL_MAVEN_REPO="$HOME/.m2/repository/mg/framework/framework-java/1.0.0/framework-java-1.0.0.jar"
if [ ! -f "$LOCAL_MAVEN_REPO" ]; then
    error_exit "Le JAR du framework n'a pas été trouvé dans le référentiel Maven local: $LOCAL_MAVEN_REPO"
fi
echo "✓ JAR du framework trouvé dans le référentiel Maven local"

echo -e "\n[2/4] Nettoyage du projet de test..."
mvn -f "$TEST_DIR/pom.xml" clean

echo -e "\n[3/4] Compilation du projet de test..."
if ! mvn -f "$TEST_DIR/pom.xml" package; then
    echo "ERREUR: Échec de la compilation du projet de test"
    echo "Tentative avec compilation verbeuse..."
    mvn -f "$TEST_DIR/pom.xml" package -X
    error_exit "Échec de la compilation du projet de test"
fi
echo "✓ Projet de test compilé avec succès"

cd ..

echo "[4/4] Déploiement sur Tomcat..."

# Suppression de l'ancien déploiement
if [ -f "$TOMCAT_DIR/$TEST_WAR" ]; then
    rm -f "$TOMCAT_DIR/$TEST_WAR"
    echo "✓ Ancien WAR supprimé"
fi

if [ -d "$TOMCAT_DIR/$APP_NAME" ]; then
    rm -rf "$TOMCAT_DIR/$APP_NAME"
    echo "✓ Ancien dossier déployé supprimé"
fi

# Attendre un peu
sleep 2

# Vérifier que le WAR existe avant de le copier
if [ ! -f "$TEST_DIR/target/$TEST_WAR" ]; then
    error_exit "Le fichier WAR n'existe pas: $TEST_DIR/target/$TEST_WAR"
fi

# Copie du nouveau WAR
cp "$TEST_DIR/target/$TEST_WAR" "$TOMCAT_DIR/"
if [ $? -ne 0 ]; then
    echo "ERREUR: Échec de la copie du WAR vers Tomcat"
    echo "Source: $TEST_DIR/target/$TEST_WAR"
    echo "Destination: $TOMCAT_DIR/$TEST_WAR"
    echo "Vérifiez:"
    echo "- Que le dossier Tomcat existe: $TOMCAT_DIR"
    echo "- Que vous avez les droits d'écriture"
    error_exit ""
fi

# Démarrer Tomcat
echo "Démarrage de Tomcat sur le port $TOMCAT_PORT..."
"$TOMCAT_HOME/bin/startup.sh"

# Vérification finale
if [ -f "$TOMCAT_DIR/$TEST_WAR" ]; then
    echo "✓ WAR copié avec succès dans Tomcat webapps"
    echo "✓ Fichier: $TOMCAT_DIR/$TEST_WAR"
    echo "✓ Tomcat démarré sur le port: $TOMCAT_PORT"
    echo "✓ Déploiement terminé avec succès!"
    echo ""
    echo "Accédez à l'application via: http://localhost:$TOMCAT_PORT/$APP_NAME/"
    echo "Ou testez une URL inexistante pour voir la page personnalisée: http://localhost:$TOMCAT_PORT/$APP_NAME/url-qui-nexiste-pas"
else
    error_exit "Le WAR n'a pas été copié correctement"
fi

echo "Appuyez sur Entrée pour quitter..."
read -r
