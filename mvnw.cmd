@REM ----------------------------------------------------------------------------
@REM Maven Wrapper batch script
@REM ----------------------------------------------------------------------------

@echo off
setlocal

set WRAPPER_VERSION=3.2.0
set MAVEN_VERSION=3.9.6

set DOWNLOAD_URL="https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/%MAVEN_VERSION%/apache-maven-%MAVEN_VERSION%-bin.zip"

set "MAVEN_HOME=%USERPROFILE%\.m2\wrapper\dists\apache-maven-%MAVEN_VERSION%"
set "MAVEN_CMD=%MAVEN_HOME%\bin\mvn.cmd"

if exist "%MAVEN_CMD%" goto runMaven

echo Descargando Maven %MAVEN_VERSION%...
if not exist "%MAVEN_HOME%" mkdir "%MAVEN_HOME%"

set "MAVEN_ZIP=%MAVEN_HOME%\apache-maven-%MAVEN_VERSION%-bin.zip"

powershell -Command "& {Invoke-WebRequest -Uri '%DOWNLOAD_URL%' -OutFile '%MAVEN_ZIP%'}"
powershell -Command "& {Expand-Archive -Path '%MAVEN_ZIP%' -DestinationPath '%MAVEN_HOME%' -Force}"

@REM Move files from nested directory
for /d %%i in ("%MAVEN_HOME%\apache-maven-*") do (
    xcopy /s /e /y "%%i\*" "%MAVEN_HOME%\" >nul
    rmdir /s /q "%%i"
)

del "%MAVEN_ZIP%" 2>nul

:runMaven
"%MAVEN_CMD%" %*
