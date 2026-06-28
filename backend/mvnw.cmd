@REM ----------------------------------------------------------------------------
@REM Maven Wrapper Startup Script for Windows
@REM ----------------------------------------------------------------------------
@echo off
setlocal enabledelayedexpansion

set "MAVEN_PROJECTBASEDIR=%CD%"
if not "%MVNW_VERBOSE%" == "" @echo on

set "MVNW_CMD=%MAVEN_PROJECTBASEDIR%\.mvn\wrapper\maven-wrapper.jar"
set "MVNW_MAVEN_OPTS=-Xmx1024m"

if not exist "%MVNW_CMD%" (
    echo ERROR: Maven wrapper jar not found at %MVNW_CMD%
    echo Please download it from Maven Central
    exit /b 1
)

set "JAVA_EXE="
for %%i in (java.exe) do set "JAVA_EXE=%%~$PATH:i"
if not defined JAVA_EXE (
    echo ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH.
    exit /b 1
)

set "MAVEN_OPTS=%MAVEN_OPTS% -Dfile.encoding=UTF-8"
set "MAVEN_CONFIG=%USERPROFILE%\.m2"

"%JAVA_EXE%" %MVNW_MAVEN_OPTS% -jar "%MVNW_CMD%" %*
exit /b %ERRORLEVEL%
