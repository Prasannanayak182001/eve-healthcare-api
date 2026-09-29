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
@rem SPDX-License-Identifier: Apache-2.0
@rem

@if "%DEBUG%"=="" @echo off
@rem ##########################################################################
@rem
@rem  api startup script for Windows
@rem
@rem ##########################################################################

@rem Set local scope for the variables, and ensure extensions are enabled
setlocal EnableExtensions

set DIRNAME=%~dp0
if "%DIRNAME%"=="" set DIRNAME=.
@rem This is normally unused
set APP_BASE_NAME=%~n0
set APP_HOME=%DIRNAME%..

@rem Resolve any "." and ".." in APP_HOME to make it shorter.
for %%i in ("%APP_HOME%") do set APP_HOME=%%~fi

@rem Add default JVM options here. You can also use JAVA_OPTS and API_OPTS to pass JVM options to this script.
set DEFAULT_JVM_OPTS=

@rem Find java.exe
if defined JAVA_HOME goto findJavaFromJavaHome

set JAVA_EXE=java.exe
%JAVA_EXE% -version >NUL 2>&1
if %ERRORLEVEL% equ 0 goto execute

echo. 1>&2
echo ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH. 1>&2
echo. 1>&2
echo Please set the JAVA_HOME variable in your environment to match the 1>&2
echo location of your Java installation. 1>&2

"%COMSPEC%" /c exit 1

:findJavaFromJavaHome
set JAVA_HOME=%JAVA_HOME:"=%
set JAVA_EXE=%JAVA_HOME%/bin/java.exe

if exist "%JAVA_EXE%" goto execute

echo. 1>&2
echo ERROR: JAVA_HOME is set to an invalid directory: %JAVA_HOME% 1>&2
echo. 1>&2
echo Please set the JAVA_HOME variable in your environment to match the 1>&2
echo location of your Java installation. 1>&2

"%COMSPEC%" /c exit 1

:execute
@rem Setup the command line

set CLASSPATH=%APP_HOME%\lib\api-1.0.0-SNAPSHOT.jar;%APP_HOME%\lib\exposed-dao-0.51.1.jar;%APP_HOME%\lib\exposed-jdbc-0.51.1.jar;%APP_HOME%\lib\exposed-java-time-0.51.1.jar;%APP_HOME%\lib\exposed-core-0.51.1.jar;%APP_HOME%\lib\ktor-serialization-kotlinx-json-jvm-3.6.0.jar;%APP_HOME%\lib\ktor-server-auth-jwt-jvm-3.6.0.jar;%APP_HOME%\lib\ktor-server-content-negotiation-jvm-3.6.0.jar;%APP_HOME%\lib\ktor-server-openapi-jvm-3.6.0.jar;%APP_HOME%\lib\ktor-server-routing-openapi-jvm-3.6.0.jar;%APP_HOME%\lib\ktor-server-auth-api-key-jvm-3.6.0.jar;%APP_HOME%\lib\ktor-server-auth-jvm-3.6.0.jar;%APP_HOME%\lib\ktor-server-netty-jvm-3.6.0.jar;%APP_HOME%\lib\ktor-server-resources-jvm-3.6.0.jar;%APP_HOME%\lib\ktor-server-status-pages-jvm-3.6.0.jar;%APP_HOME%\lib\ktor-server-csrf-jvm-3.6.0.jar;%APP_HOME%\lib\ktor-server-sessions-jvm-3.6.0.jar;%APP_HOME%\lib\ktor-server-html-builder-jvm-3.6.0.jar;%APP_HOME%\lib\ktor-server-core-jvm-3.6.0.jar;%APP_HOME%\lib\kotlin-reflect-2.3.21.jar;%APP_HOME%\lib\ktor-serialization-kotlinx-jvm-3.6.0.jar;%APP_HOME%\lib\ktor-client-core-jvm-3.6.0.jar;%APP_HOME%\lib\ktor-http-cio-jvm-3.6.0.jar;%APP_HOME%\lib\ktor-websocket-serialization-jvm-3.6.0.jar;%APP_HOME%\lib\ktor-serialization-jvm-3.6.0.jar;%APP_HOME%\lib\ktor-websockets-jvm-3.6.0.jar;%APP_HOME%\lib\ktor-resources-jvm-3.6.0.jar;%APP_HOME%\lib\ktor-openapi-schema-jvm-3.6.0.jar;%APP_HOME%\lib\ktor-http-jvm-3.6.0.jar;%APP_HOME%\lib\ktor-events-jvm-3.6.0.jar;%APP_HOME%\lib\ktor-sse-jvm-3.6.0.jar;%APP_HOME%\lib\ktor-network-jvm-3.6.0.jar;%APP_HOME%\lib\ktor-utils-jvm-3.6.0.jar;%APP_HOME%\lib\kotlinx-coroutines-slf4j-1.11.0.jar;%APP_HOME%\lib\ktor-io-jvm-3.6.0.jar;%APP_HOME%\lib\kotlinx-coroutines-core-jvm-1.11.0.jar;%APP_HOME%\lib\kaml-jvm-0.79.0.jar;%APP_HOME%\lib\kotlinx-serialization-core-jvm-1.11.0.jar;%APP_HOME%\lib\kotlinx-serialization-json-io-jvm-1.11.0.jar;%APP_HOME%\lib\kotlinx-serialization-json-jvm-1.11.0.jar;%APP_HOME%\lib\kotlinx-html-jvm-0.12.0.jar;%APP_HOME%\lib\snakeyaml-engine-kmp-jvm-3.1.1.jar;%APP_HOME%\lib\okio-jvm-3.12.0.jar;%APP_HOME%\lib\kotlinx-datetime-jvm-0.8.0-0.6.x-compat.jar;%APP_HOME%\lib\kotlinx-io-core-jvm-0.9.1.jar;%APP_HOME%\lib\urlencoder-lib-jvm-1.6.0.jar;%APP_HOME%\lib\kotlinx-io-bytestring-jvm-0.9.1.jar;%APP_HOME%\lib\kotlin-stdlib-2.4.0.jar;%APP_HOME%\lib\swagger-codegen-generators-1.0.62.jar;%APP_HOME%\lib\swagger-codegen-3.0.82.jar;%APP_HOME%\lib\logback-classic-1.5.37.jar;%APP_HOME%\lib\postgresql-42.7.3.jar;%APP_HOME%\lib\HikariCP-5.1.0.jar;%APP_HOME%\lib\jbcrypt-0.4.jar;%APP_HOME%\lib\annotations-23.0.0.jar;%APP_HOME%\lib\logback-core-1.5.37.jar;%APP_HOME%\lib\swagger-codegen-2.4.46.jar;%APP_HOME%\lib\handlebars-4.3.1.jar;%APP_HOME%\lib\slf4j-ext-2.0.17.jar;%APP_HOME%\lib\swagger-parser-2.1.48.jar;%APP_HOME%\lib\swagger-parser-v2-converter-2.1.48.jar;%APP_HOME%\lib\swagger-compat-spec-parser-1.0.76.jar;%APP_HOME%\lib\swagger-parser-1.0.76.jar;%APP_HOME%\lib\swagger-core-1.6.16.jar;%APP_HOME%\lib\swagger-parser-v3-2.1.48.jar;%APP_HOME%\lib\swagger-core-2.2.53.jar;%APP_HOME%\lib\swagger-models-1.6.16.jar;%APP_HOME%\lib\slf4j-api-2.0.19.jar;%APP_HOME%\lib\checker-qual-3.42.0.jar;%APP_HOME%\lib\java-jwt-4.6.0.jar;%APP_HOME%\lib\jwks-rsa-0.24.1.jar;%APP_HOME%\lib\config-1.4.9.jar;%APP_HOME%\lib\netty-codec-4.2.17.Final.jar;%APP_HOME%\lib\netty-codec-http2-4.2.17.Final.jar;%APP_HOME%\lib\netty-codec-http3-4.2.17.Final.jar;%APP_HOME%\lib\alpn-api-1.1.3.v20160715.jar;%APP_HOME%\lib\netty-transport-native-kqueue-4.2.17.Final.jar;%APP_HOME%\lib\netty-transport-native-epoll-4.2.17.Final.jar;%APP_HOME%\lib\jackson-datatype-jsr310-2.22.1.jar;%APP_HOME%\lib\jackson-dataformat-yaml-2.22.1.jar;%APP_HOME%\lib\json-patch-1.13.jar;%APP_HOME%\lib\json-schema-validator-2.2.14.jar;%APP_HOME%\lib\json-schema-core-1.2.14.jar;%APP_HOME%\lib\jackson-coreutils-equivalence-1.0.jar;%APP_HOME%\lib\jackson-coreutils-2.0.jar;%APP_HOME%\lib\jackson-databind-2.22.1.jar;%APP_HOME%\lib\jackson-core-2.22.1.jar;%APP_HOME%\lib\uri-template-0.10.jar;%APP_HOME%\lib\guava-33.6.0-jre.jar;%APP_HOME%\lib\netty-codec-http-4.2.17.Final.jar;%APP_HOME%\lib\netty-codec-compression-4.2.17.Final.jar;%APP_HOME%\lib\netty-codec-protobuf-4.2.17.Final.jar;%APP_HOME%\lib\netty-codec-marshalling-4.2.17.Final.jar;%APP_HOME%\lib\netty-codec-native-quic-4.2.17.Final-linux-x86_64.jar;%APP_HOME%\lib\netty-codec-native-quic-4.2.17.Final-linux-aarch_64.jar;%APP_HOME%\lib\netty-codec-native-quic-4.2.17.Final-osx-x86_64.jar;%APP_HOME%\lib\netty-codec-native-quic-4.2.17.Final-osx-aarch_64.jar;%APP_HOME%\lib\netty-codec-native-quic-4.2.17.Final-windows-x86_64.jar;%APP_HOME%\lib\netty-codec-classes-quic-4.2.17.Final.jar;%APP_HOME%\lib\netty-handler-4.2.17.Final.jar;%APP_HOME%\lib\netty-codec-base-4.2.17.Final.jar;%APP_HOME%\lib\netty-transport-classes-kqueue-4.2.17.Final.jar;%APP_HOME%\lib\netty-transport-classes-epoll-4.2.17.Final.jar;%APP_HOME%\lib\netty-transport-native-unix-common-4.2.17.Final.jar;%APP_HOME%\lib\netty-transport-4.2.17.Final.jar;%APP_HOME%\lib\netty-buffer-4.2.17.Final.jar;%APP_HOME%\lib\netty-resolver-4.2.17.Final.jar;%APP_HOME%\lib\netty-common-4.2.17.Final.jar;%APP_HOME%\lib\jmustache-1.16.jar;%APP_HOME%\lib\swagger-parser-safe-url-resolver-1.0.76.jar;%APP_HOME%\lib\swagger-parser-safe-url-resolver-2.1.48.jar;%APP_HOME%\lib\commons-io-2.22.0.jar;%APP_HOME%\lib\commons-lang3-3.20.0.jar;%APP_HOME%\lib\commons-cli-1.9.0.jar;%APP_HOME%\lib\snakeyaml-2.6.jar;%APP_HOME%\lib\swagger-parser-core-2.1.48.jar;%APP_HOME%\lib\swagger-models-2.2.53.jar;%APP_HOME%\lib\jackson-annotations-2.22.jar;%APP_HOME%\lib\failureaccess-1.0.3.jar;%APP_HOME%\lib\listenablefuture-9999.0-empty-to-avoid-conflict-with-guava.jar;%APP_HOME%\lib\jspecify-1.0.0.jar;%APP_HOME%\lib\error_prone_annotations-2.47.0.jar;%APP_HOME%\lib\j2objc-annotations-3.1.jar;%APP_HOME%\lib\json-20231013.jar;%APP_HOME%\lib\commonmark-0.17.0.jar;%APP_HOME%\lib\jakarta.xml.bind-api-2.3.3.jar;%APP_HOME%\lib\validation-api-1.1.0.Final.jar;%APP_HOME%\lib\rhino-1.7.15.1.jar;%APP_HOME%\lib\httpclient-4.5.14.jar;%APP_HOME%\lib\swagger-annotations-2.2.53.jar;%APP_HOME%\lib\jakarta.validation-api-2.0.2.jar;%APP_HOME%\lib\jakarta.activation-api-1.2.2.jar;%APP_HOME%\lib\swagger-annotations-1.6.16.jar;%APP_HOME%\lib\mailapi-1.6.2.jar;%APP_HOME%\lib\joda-time-2.10.5.jar;%APP_HOME%\lib\libphonenumber-8.11.1.jar;%APP_HOME%\lib\msg-simple-1.2.jar;%APP_HOME%\lib\btf-1.3.jar;%APP_HOME%\lib\jsr305-3.0.2.jar;%APP_HOME%\lib\jopt-simple-5.0.4.jar;%APP_HOME%\lib\httpcore-4.4.16.jar;%APP_HOME%\lib\commons-logging-1.2.jar;%APP_HOME%\lib\commons-codec-1.11.jar


@rem Execute api
@rem endlocal doesn't take effect until after the line is parsed and variables are expanded
@rem which allows us to clear the local environment before executing the java command
endlocal & "%JAVA_EXE%" %DEFAULT_JVM_OPTS% %JAVA_OPTS% %API_OPTS%  -classpath "%CLASSPATH%" io.ktor.server.netty.EngineMain %* & call :exitWithErrorLevel

:exitWithErrorLevel
@rem Use "%COMSPEC%" /c exit to allow operators to work properly in scripts
"%COMSPEC%" /c exit %ERRORLEVEL%
