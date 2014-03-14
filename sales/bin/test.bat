@echo off
cd..
echo [INFO] 运行单元测试
call mvn clean cobertura:cobertura
pause