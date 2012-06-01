@echo off
REM ---------------------------------------------------------------------------
REM  Script for building/running hmm.
REM
REM Environment Variable Prequisites:
REM   JAVA_HOME     Must point at your Java Development Kit installation.
REM
REM !!! Note: This has been made obsolete by Ant-based build script!
REM !!! Note: Please use the new build file, build.xml!
REM
REM ---------------------------------------------------------------------------
REM $Id: hmm.bat,v 1.2 2003/02/11 09:44:36 drwye Exp $


REM ----- Save Environment Variables That May Change --------------------------
if "%HMM_HOME%" == "" set HMM_HOME=E:\hmmsdk\hmmlib
set _HMM_HOME=%HMM_HOME%
set _XML_LIB=%_HMM_HOME%\lib\xml.jar;%_HMM_HOME%\lib\xml4j_1_1_9.jar
set _JAVAHELP_LIB=%_HMM_HOME%\lib\jhall.jar
set _JDBC_LIB=%_HMM_HOME%\lib\jdbc2_0-stdext.jar
set _CLASSPATH=%CLASSPATH%

REM ----- Verify and Set Required Environment Variables -----------------------
if "%JAVA_HOME%" == "" goto javahomeerror

REM ----- Set Up The Runtime Classpath ----------------------------------------
REM set CLASSPATH=%CLASSPATH%;%_HMM_HOME%\build\classes
set CLASSPATH=%_HMM_HOME%\build\classes
set CLASSPATH=%CLASSPATH%;%JAVA_HOME%\lib\tools.jar;%_JAVAHELP_LIB%;%_XML_LIB%
if exist %_JDBC_LIB% set CLASSPATH=%CLASSPATH%;%_JDBC_LIB%
REM echo Using CLASSPATH: %CLASSPATH%

REM ----- Prepare Appropriate Java Execution Commands -------------------------
set _JAVAC=classic
set _START_CMD=start %JAVA_HOME%\bin\java -cp "%CLASSPATH%"
set _RUN_CMD=%JAVA_HOME%\bin\java -cp "%CLASSPATH%"
set _BUILD_CMD=%JAVA_HOME%\bin\javac -d %_HMM_HOME%\build\classes -classpath "%CLASSPATH%"
set _JAR_CMD=%JAVA_HOME%\bin\jar
set _DOC_CMD=%JAVA_HOME%\bin\javadoc -classpath "%CLASSPATH%" -private -version -author -windowtitle "Hmm in Java API Specification" -doctitle "Hmm in Java"
if "%OS%" == "Windows_NT" set _CLEAN_CMD=del /S /Q
if "%OS%" == "" set _CLEAN_CMD=del

REM ----- Execute The Requested Command ---------------------------------------
if "%1" == "env" goto doEnv
if "%1" == "build" goto doBuild
if "%1" == "jar" goto doJar
if "%1" == "javadoc" goto doJavadoc
if "%1" == "clean" goto doClean
if "%1" == "run" goto doRun
if "%1" == "start" goto doStart
if "%1" == "stop" goto doStop

:doUsage
echo Usage:  hmm " env | build | jar | javadoc | clean | run | start | stop "
echo Commands:
echo   env     -  Set up environment variables that hmm would use
echo   build   -  Build all hmm classes and the jar file
echo   jar     -  Create hmm.jar
echo   javadoc -  Generate api docs.
echo   clean   -  Clean all hmm classes
echo   run     -  Start hmm in the current window
echo   start   -  Start hmm in a separate window
echo   stop    -  Stop hmm
goto cleanup

:doEnv
goto finish

:doBuild
echo Building hmm...
REM %_CLEAN_CMD% %_HMM_HOME%\src\java\com\bluecraft\hmm\*.class
%_BUILD_CMD% %_HMM_HOME%\src\java\com\bluecraft\hmm\file\*.java
%_BUILD_CMD% %_HMM_HOME%\src\java\com\bluecraft\hmm\resource\*.java
%_BUILD_CMD% %_HMM_HOME%\src\java\com\bluecraft\hmm\util\*.java
%_BUILD_CMD% %_HMM_HOME%\src\java\com\bluecraft\hmm\*.java
REM echo Done.
REM goto cleanup

:doJar
echo Creating jar file...
%_CLEAN_CMD% %_HMM_HOME%\src\java\com\bluecraft\hmm\*.*~
%_CLEAN_CMD% %_HMM_HOME%\build\hmm.jar
cd %_HMM_HOME%\src
%_JAR_CMD% cfm %_HMM_HOME%\build\hmm.jar %_HMM_HOME%\bin\manifest.mf -C %_HMM_HOME%\build\classes com\bluecraft\hmm\*.class com\bluecraft\hmm\file\*.class com\bluecraft\hmm\resource\*.class com\bluecraft\hmm\util\*.class com\bluecraft\hmm\images com\bluecraft\hmm\help
cd %_HMM_HOME%\bin
echo Done.
goto cleanup

:doJavadoc
echo Creating API docs...
%_CLEAN_CMD% %_HMM_HOME%\docs\api\*.*
%_DOC_CMD% -d %_HMM_HOME%\docs\api -sourcepath %_HMM_HOME%\src\java @%_HMM_HOME%\bin\package.list
echo Done.
goto cleanup

:doClean
echo Cleaning up...
%_CLEAN_CMD% %_HMM_HOME%\src\java\com\bluecraft\hmm\*.*~
%_CLEAN_CMD% %_HMM_HOME%\src\java\com\bluecraft\hmm\*.class
echo Done.
goto cleanup

:doRun
cd %_HMM_HOME%
%_RUN_CMD% -DJAVAC=%_JAVAC% com.bluecraft.hmm.Hmm
REM %_RUN_CMD% -DJAVAC=%_JAVAC% com.bluecraft.hmm.WorkSheet
cd %_HMM_HOME%\bin
goto cleanup

:doStart
cd %_HMM_HOME%
%_START_CMD% -DJAVAC=%_JAVAC% com.bluecraft.hmm.Hmm
REM %_START_CMD% -DJAVAC=%_JAVAC% com.bluecraft.hmm.WorkSheet
cd %_HMM_HOME%\bin
goto cleanup

:doStop
echo Stop is not implemented yet.
goto cleanup

REM ----- Errors --------------------------------------------------------------
:javahomeerror
echo "ERROR: JAVA_HOME not found in your environment."
echo "Please, set the JAVA_HOME variable in your environment to match the"
echo "location of the Java Virtual Machine you want to use."

REM ----- Restore Environment Variables ---------------------------------------
:cleanup
set CLASSPATH=%_CLASSPATH%
set _CLASSPATH=
set _JAVAC=
set _XML_LIB=
set _JAVAHELP_LIB=
set _JDBC_LIB=
set _HMM_HOME=
set _BUILD_CMD=
set _JAR_CMD=
set _DOC_CMD=
set _CLEAN_CMD=
set _RUN_CMD=
set _START_CMD=
:finish


