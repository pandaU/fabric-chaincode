@rem
@rem Copyright 2015 the original author or authors.
@rem
@rem Licensed under the Apache License, Version 2.0 (the "License");
@rem you may not use this file except in compliance with the License.
@rem You may obtain a copy of the License at
@rem
@rem      https://www.apache.org/licenses/LICENSE-2.0
@rem
@rem Unless required by applicable law or agreed to in writing, software
@rem distributed under the License is distributed on an "AS IS" BASIS,
@rem WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
@rem See the License for the specific language governing permissions and
@rem limitations under the License.
@rem

@if "%DEBUG%" == "" @echo off
@rem ##########################################################################
@rem
@rem  commonChaincode startup script for Windows
@rem
@rem ##########################################################################

@rem Set local scope for the variables with windows NT shell
if "%OS%"=="Windows_NT" setlocal

set DIRNAME=%~dp0
if "%DIRNAME%" == "" set DIRNAME=.
set APP_BASE_NAME=%~n0
set APP_HOME=%DIRNAME%..

@rem Resolve any "." and ".." in APP_HOME to make it shorter.
for %%i in ("%APP_HOME%") do set APP_HOME=%%~fi

@rem Add default JVM options here. You can also use JAVA_OPTS and COMMON_CHAINCODE_OPTS to pass JVM options to this script.
set DEFAULT_JVM_OPTS=

@rem Find java.exe
if defined JAVA_HOME goto findJavaFromJavaHome

set JAVA_EXE=java.exe
%JAVA_EXE% -version >NUL 2>&1
if "%ERRORLEVEL%" == "0" goto init

echo.
echo ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH.
echo.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation.

goto fail

:findJavaFromJavaHome
set JAVA_HOME=%JAVA_HOME:"=%
set JAVA_EXE=%JAVA_HOME%/bin/java.exe

if exist "%JAVA_EXE%" goto init

echo.
echo ERROR: JAVA_HOME is set to an invalid directory: %JAVA_HOME%
echo.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation.

goto fail

:init
@rem Get command-line arguments, handling Windows variants

if not "%OS%" == "Windows_NT" goto win9xME_args

:win9xME_args
@rem Slurp the command line arguments.
set CMD_LINE_ARGS=
set _SKIP=2

:win9xME_args_slurp
if "x%~1" == "x" goto execute

set CMD_LINE_ARGS=%*

:execute
@rem Setup the command line

set CLASSPATH=%APP_HOME%\lib\commonChaincode-1.0-SNAPSHOT.jar;%APP_HOME%\lib\genson-1.5.jar;%APP_HOME%\lib\fabric-sdk-java-2.2.0.jar;%APP_HOME%\lib\fabric-chaincode-shim-2.2.4.jar;%APP_HOME%\lib\grpc-netty-1.31.0.jar;%APP_HOME%\lib\fabric-chaincode-protos-2.2.4.jar;%APP_HOME%\lib\grpc-protobuf-1.31.1.jar;%APP_HOME%\lib\grpc-stub-1.31.1.jar;%APP_HOME%\lib\netty-tcnative-boringssl-static-2.0.30.Final.jar;%APP_HOME%\lib\netty-codec-http2-4.1.49.Final.jar;%APP_HOME%\lib\protobuf-java-util-3.12.4.jar;%APP_HOME%\lib\protobuf-java-3.12.4.jar;%APP_HOME%\lib\bcpkix-jdk15on-1.67.jar;%APP_HOME%\lib\commons-pool2-2.9.0.jar;%APP_HOME%\lib\commons-lang3-3.11.jar;%APP_HOME%\lib\bctls-jdk15on-1.67.jar;%APP_HOME%\lib\httpclient-4.5.12.jar;%APP_HOME%\lib\org.everit.json.schema-1.12.1.jar;%APP_HOME%\lib\commons-validator-1.6.jar;%APP_HOME%\lib\commons-logging-1.2.jar;%APP_HOME%\lib\commons-cli-1.4.jar;%APP_HOME%\lib\commons-compress-1.20.jar;%APP_HOME%\lib\commons-io-2.6.jar;%APP_HOME%\lib\log4j-core-2.13.3.jar;%APP_HOME%\lib\log4j-1.2-api-2.13.3.jar;%APP_HOME%\lib\futures-extra-4.2.0.jar;%APP_HOME%\lib\api-common-1.9.0.jar;%APP_HOME%\lib\javax.json-1.1.4.jar;%APP_HOME%\lib\snakeyaml-1.26.jar;%APP_HOME%\lib\milagro-crypto-java-0.4.0.jar;%APP_HOME%\lib\jaxb-api-2.3.1.jar;%APP_HOME%\lib\javax.annotation-api-1.3.2.jar;%APP_HOME%\lib\bcprov-jdk15on-1.67.jar;%APP_HOME%\lib\classgraph-4.8.47.jar;%APP_HOME%\lib\json-20190722.jar;%APP_HOME%\lib\grpc-netty-shaded-1.31.1.jar;%APP_HOME%\lib\grpc-core-1.31.1.jar;%APP_HOME%\lib\netty-handler-proxy-4.1.48.Final.jar;%APP_HOME%\lib\grpc-protobuf-lite-1.31.1.jar;%APP_HOME%\lib\grpc-api-1.31.1.jar;%APP_HOME%\lib\guava-29.0-android.jar;%APP_HOME%\lib\error_prone_annotations-2.3.4.jar;%APP_HOME%\lib\perfmark-api-0.19.0.jar;%APP_HOME%\lib\animal-sniffer-annotations-1.18.jar;%APP_HOME%\lib\jsr305-3.0.2.jar;%APP_HOME%\lib\netty-codec-http-4.1.49.Final.jar;%APP_HOME%\lib\netty-handler-4.1.49.Final.jar;%APP_HOME%\lib\netty-codec-socks-4.1.48.Final.jar;%APP_HOME%\lib\netty-codec-4.1.49.Final.jar;%APP_HOME%\lib\netty-transport-4.1.49.Final.jar;%APP_HOME%\lib\netty-buffer-4.1.49.Final.jar;%APP_HOME%\lib\netty-resolver-4.1.49.Final.jar;%APP_HOME%\lib\netty-common-4.1.49.Final.jar;%APP_HOME%\lib\gson-2.8.6.jar;%APP_HOME%\lib\log4j-api-2.13.3.jar;%APP_HOME%\lib\httpcore-4.4.13.jar;%APP_HOME%\lib\commons-codec-1.11.jar;%APP_HOME%\lib\auto-value-annotations-1.7.jar;%APP_HOME%\lib\commons-math3-3.6.1.jar;%APP_HOME%\lib\javax.activation-api-1.2.0.jar;%APP_HOME%\lib\proto-google-common-protos-1.17.0.jar;%APP_HOME%\lib\handy-uri-templates-2.1.8.jar;%APP_HOME%\lib\re2j-1.3.jar;%APP_HOME%\lib\failureaccess-1.0.1.jar;%APP_HOME%\lib\listenablefuture-9999.0-empty-to-avoid-conflict-with-guava.jar;%APP_HOME%\lib\checker-compat-qual-2.5.5.jar;%APP_HOME%\lib\j2objc-annotations-1.3.jar;%APP_HOME%\lib\grpc-context-1.31.1.jar;%APP_HOME%\lib\commons-digester-1.8.1.jar;%APP_HOME%\lib\commons-collections-3.2.2.jar;%APP_HOME%\lib\joda-time-2.10.2.jar;%APP_HOME%\lib\annotations-4.1.1.4.jar


@rem Execute commonChaincode
"%JAVA_EXE%" %DEFAULT_JVM_OPTS% %JAVA_OPTS% %COMMON_CHAINCODE_OPTS%  -classpath "%CLASSPATH%" org.hyperledger.fabric.contract.ContractRouter %CMD_LINE_ARGS%

:end
@rem End local scope for the variables with windows NT shell
if "%ERRORLEVEL%"=="0" goto mainEnd

:fail
rem Set variable COMMON_CHAINCODE_EXIT_CONSOLE if you need the _script_ return code instead of
rem the _cmd.exe /c_ return code!
if  not "" == "%COMMON_CHAINCODE_EXIT_CONSOLE%" exit 1
exit /b 1

:mainEnd
if "%OS%"=="Windows_NT" endlocal

:omega