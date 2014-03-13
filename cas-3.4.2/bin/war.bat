@echo off
cd..
echo [INFO] Éú³Éwar°ü
call mvn clean package -Dmaven.test.skip=true
@pause