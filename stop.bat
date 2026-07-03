@echo off
chcp 65001 >nul
echo ================================================
echo            牢大商城 - 一键停止脚本
echo ================================================

REM 配置路径
set TOMCAT_HOME=D:\apache-tomcat-9.0.80

echo 停止 Tomcat...
"%TOMCAT_HOME%\bin\shutdown.bat"

echo.
echo ================================================
echo           已停止！
echo ================================================
pause