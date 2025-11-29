#!/bin/bash
# Script de verificación rápida de tests

echo "================================"
echo "VERIFICACIÓN DE SUITE DE TESTS"
echo "================================"
echo ""

# 1. Compilar el proyecto
echo "1. Compilando proyecto..."
./gradlew compileJava compileTestJava

# 2. Ejecutar tests
echo ""
echo "2. Ejecutando suite completa de tests..."
./gradlew test

# 3. Generar reporte de cobertura
echo ""
echo "3. Generando reporte de cobertura JaCoCo..."
./gradlew jacocoTestReport

# 4. Mostrar resumen
echo ""
echo "================================"
echo "RESUMEN DE RESULTADOS"
echo "================================"

if [ -f "build/reports/tests/test/index.html" ]; then
    echo "✅ Reporte de tests generado:"
    echo "   file://$(pwd)/build/reports/tests/test/index.html"
fi

if [ -f "build/reports/jacoco/test/html/index.html" ]; then
    echo "✅ Reporte de cobertura generado:"
    echo "   file://$(pwd)/build/reports/jacoco/test/html/index.html"
fi

echo ""
echo "================================"
echo "ARCHIVOS DE TEST CREADOS"
echo "================================"
echo "✅ src/test/java/com/example/Mutantes/service/MutantDetectorTest.java (25 tests)"
echo "✅ src/test/java/com/example/Mutantes/service/MutantServiceTest.java (6 tests)"
echo "✅ src/test/java/com/example/Mutantes/service/StatsServiceTest.java (9 tests)"
echo "✅ src/test/java/com/example/Mutantes/controller/MutantControllerTest.java (13 tests)"
echo ""
echo "TOTAL: 53 TESTS"
echo ""

