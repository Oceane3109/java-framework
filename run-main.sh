#!/bin/bash

echo "===================================="
echo "  BUILD + RUN Main (annotation demo)"
echo "===================================="
echo

# Aller à la racine du dépôt
PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$PROJECT_ROOT" || exit 1

# 1) Build framework JAR
echo "[1/4] Building framework JAR..."
mvn -q -f "$PROJECT_ROOT/frameworkJAVA/pom.xml" clean package
if [ $? -ne 0 ]; then
    echo "ERREUR: Échec de la construction du framework"
    exit 1
fi

FRAMEWORK_JAR="$PROJECT_ROOT/frameworkJAVA/target/framework-java-1.0.0.jar"
if [ ! -f "$FRAMEWORK_JAR" ]; then
    echo "ERREUR: JAR introuvable: $FRAMEWORK_JAR"
    exit 1
fi
echo "✓ Framework JAR prêt: $FRAMEWORK_JAR"

# 2) S'assurer que le répertoire lib contient le JAR
echo "[2/4] Copie du JAR du framework dans le répertoire de test..."
mkdir -p "$PROJECT_ROOT/teste/src/main/webapp/WEB-INF/lib"
cp "$FRAMEWORK_JAR" "$PROJECT_ROOT/teste/src/main/webapp/WEB-INF/lib/framework-java-1.0.0.jar"
echo "✓ JAR copié dans teste/src/main/webapp/WEB-INF/lib"

# 3) Compiler les sources de test avec le JAR
echo "[3/4] Compilation des sources de test (Main/Teste) avec le JAR du framework..."
OUT_DIR="teste/target/classes"
mkdir -p "$OUT_DIR"

SRC_MAIN1="teste/src/main/java/mg/teste/Main.java"
SRC_MAIN2="teste/src/main/java/mg/teste/Teste.java"

javac -cp "$FRAMEWORK_JAR" -d "$OUT_DIR" "$SRC_MAIN1" "$SRC_MAIN2"
if [ $? -ne 0 ]; then
    echo "ERREUR: Échec de la compilation des sources du module de test"
    exit 1
fi
echo "✓ Compilation OK: $OUT_DIR"

# 4) Exécuter Main pour afficher les valeurs des annotations
echo "[4/4] Exécution de mg.teste.Main ..."
echo "----------------------------------------"
java -cp "$OUT_DIR:$FRAMEWORK_JAR" mg.teste.Main

echo "----------------------------------------"
echo "Terminé."
echo "Appuyez sur Entrée pour quitter..."
read -r
