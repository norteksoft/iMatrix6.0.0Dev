@echo off
set ROOT_DIR=%cd%

rem cas mysql版本
cd ..
cd expense
echo [INFO] 生成imatrix-static war包
jar -cvf imatrix-static.war css/ images/ js/ META-INF/  WEB-INF/ widgets/ templateJs/
pause

echo [INFO]解压生成的war包并删除.svn文件
if not exist temp goto createDir
:createDir
mkdir temp

move imatrix-static.war temp
cd ..
cd expense/temp
jar -xvf imatrix-static.war
pause
echo [INFO]请将del-svn.bat放入imatrix-static/expense/temp文件夹中
pause
call del-svn.bat
pause
cd ..
cd expense/temp
rd imatrix-static.war

cd ..
cd expense/temp
echo [INFO] 生成imatrix-static war包
jar -cvf imatrix-static.war css/ images/ js/ META-INF/  WEB-INF/ widgets/ templateJs/
pause




