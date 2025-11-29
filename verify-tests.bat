@echo off
REM Script de verificación rápida de tests para Windows

echo ================================
echo VERIFICACION DE SUITE DE TESTS
echo ================================
echo.

REM 1. Compilar el proyecto
echo 1. Compilando proyecto...
call gradlew.bat compileJava compileTestJava

REM 2. Ejecutar tests
echo.
echo 2. Ejecutando suite completa de tests...
call gradlew.bat test

REM 3. Generar reporte de cobertura
echo.
echo 3. Generando reporte de cobertura JaCoCo...
call gradlew.bat jacocoTestReport

REM 4. Mostrar resumen
echo.
echo ================================
echo RESUMEN DE RESULTADOS
echo ================================

if exist "build\reports\tests\test\index.html" (
    echo [OK] Reporte de tests generado:
    echo    file:///%CD%\build\reports\tests\test\index.html
)

if exist "build\reports\jacoco\test\html\index.html" (
    echo [OK] Reporte de cobertura generado:
    echo    file:///%CD%\build\reports\jacoco\test\html\index.html
)

echo.
echo ================================
echo ARCHIVOS DE TEST CREADOS
echo ================================
echo [OK] src\test\java\com\example\Mutantes\service\MutantDetectorTest.java (25 tests)
echo [OK] src\test\java\com\example\Mutantes\service\MutantServiceTest.java (6 tests)
echo [OK] src\test\java\com\example\Mutantes\service\StatsServiceTest.java (9 tests)
echo [OK] src\test\java\com\example\Mutantes\controller\MutantControllerTest.java (13 tests)
echo.
echo TOTAL: 53 TESTS
echo.
echo Presiona cualquier tecla para abrir los reportes...
pause >nul

REM Abrir reportes en el navegador
if exist "build\reports\tests\test\index.html" (
    start "" "build\reports\tests\test\index.html"
)

if exist "build\reports\jacoco\test\html\index.html" (
    timeout /t 2 >nul
    start "" "build\reports\jacoco\test\html\index.html"
)

