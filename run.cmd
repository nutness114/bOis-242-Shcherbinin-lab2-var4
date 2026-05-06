@echo off
setlocal

set "JAVA_HOME=D:\GigaIde\jbr"
set "MAVEN_CMD=D:\GigaIde\plugins\maven\lib\maven3\bin\mvn.cmd"

if not exist "%JAVA_HOME%\bin\java.exe" (
  echo GIGA IDE JBR not found: %JAVA_HOME%
  exit /b 1
)

if not exist "%MAVEN_CMD%" (
  echo Maven not found: %MAVEN_CMD%
  exit /b 1
)

call "%MAVEN_CMD%" -q -o -DskipTests compile
if errorlevel 1 exit /b 1

"%JAVA_HOME%\bin\java.exe" -cp target\classes catchball.GameApplication
