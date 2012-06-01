@echo off

REM ----------------------------------------------------------
REM Bluecraft HmmSDK Main Build Script
REM It is tested under Windows NT 4.0 and 2K/XP.
REM 
REM This script is no longer needed.
REM This file will be removed from the repository.
REM ----------------------------------------------------------
REM $Id: build.bat,v 1.4 2003/09/18 03:26:20 drwye Exp $

setlocal

REM -- Root directory for the project
set _PROJECTDIR=%PROJECTDIR%
set PROJECTDIR=E:/hmmsdk/hmmall


REM --------------------------------------------
REM No need to edit anything past here
REM --------------------------------------------

:init
REM echo %CLASSPATH%

:testjavahome
if "%JAVA_HOME%" == "" goto javahomeerror
goto build

:testanthome
if "%ANT_HOME%" == "" goto anthomeerror
goto build


:build
ant %1
goto end


:javahomeerror
echo ERROR: JAVA_HOME not found in your environment.
echo Please, set the JAVA_HOME variable in your environment to match the
echo location of the Java Virtual Machine you want to use.

:anthomeerror
echo ERROR: ANT_HOME not found in your environment.
echo Please, set the ANT_HOME variable in your environment to match the
echo location of the ant installation.

:end
set PROJECTDIR=%_PROJECTDIR%


endlocal

