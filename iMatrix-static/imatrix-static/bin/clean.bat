@echo off
rem 删除存放cas war包的temp目录
cd ..
cd expense
@echo on
@for /d /r %%c in (temp) do @if exist %%c ( rd /s /q %%c & echo     删除目录%%c)
@echo off
echo "imatrix-static中的temp目录已删除"
pause