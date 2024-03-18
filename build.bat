@REM
@REM  Copyright Lealone Database Group.
@REM  Licensed under the Server Side Public License, v 1.
@REM  Initial Developer: zhh

@echo off
if "%OS%" == "Windows_NT" setlocal

set ARG=%1

cd embed-db-demo
call mvn eclipse:eclipse -DdownloadSources=true 
cd ..

cd fullstack-demo
call mvn eclipse:eclipse -DdownloadSources=true
cd ..

cd embed-db-demo
call mvn eclipse:eclipse -DdownloadSources=true 
cd ..

cd js-demo
call mvn eclipse:eclipse -DdownloadSources=true
cd ..

cd orm-demo
call mvn eclipse:eclipse -DdownloadSources=true 
cd ..

cd petstore
call mvn eclipse:eclipse -DdownloadSources=true
cd ..

cd embed-db-demo
call mvn eclipse:eclipse -DdownloadSources=true 
cd ..

cd polyglot-demo
call mvn eclipse:eclipse -DdownloadSources=true
cd ..

cd python-demo
call mvn eclipse:eclipse -DdownloadSources=true 
cd ..

cd rpc-demo
call mvn eclipse:eclipse -DdownloadSources=true
cd ..

cd spring-demo
call mvn eclipse:eclipse -DdownloadSources=true 
cd ..

cd storage-engine-demo
call mvn eclipse:eclipse -DdownloadSources=true
cd ..
