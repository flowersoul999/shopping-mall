@echo off
chcp 65001 >nul
echo ================================================
echo            牢大商城 - 一键启动脚本
echo ================================================

REM 配置路径
set TOMCAT_HOME=D:\apache-tomcat-9.0.80
set PROJECT_DIR=%~dp0
set BACKEND_DIR=%PROJECT_DIR%backend
set BUILD_DIR=%BACKEND_DIR%\build-vscode
set CLASSES_DIR=%TOMCAT_HOME%\webapps\shopping-mall\WEB-INF\classes

echo.
echo [1/3] 编译后端代码...
echo ------------------------

REM 创建编译目录
if not exist %BUILD_DIR% mkdir %BUILD_DIR%

REM 编译 Java 代码
javac -d %BUILD_DIR% -cp "%BACKEND_DIR%\src;%BACKEND_DIR%\webapp\WEB-INF\lib\*" %BACKEND_DIR%\src\com\shop\**\*.java %BACKEND_DIR%\src\dao\*.java

if %errorlevel% neq 0 (
    echo 编译失败！请检查 JDK 是否正确安装。
    pause
    exit /b 1
)

echo 编译成功！

echo.
echo [2/3] 部署到 Tomcat...
echo ------------------------

REM 创建部署目录
if not exist %CLASSES_DIR% mkdir %CLASSES_DIR%

REM 复制编译后的 class 文件
xcopy /E /Y %BUILD_DIR%\* %CLASSES_DIR%\

REM 复制 web.xml
if not exist %TOMCAT_HOME%\webapps\shopping-mall\WEB-INF mkdir %TOMCAT_HOME%\webapps\shopping-mall\WEB-INF
copy /Y %BACKEND_DIR%\webapp\WEB-INF\web.xml %TOMCAT_HOME%\webapps\shopping-mall\WEB-INF\

echo 部署成功！

echo.
echo [3/3] 启动 Tomcat...
echo ------------------------

REM 启动 Tomcat
start "" "%TOMCAT_HOME%\bin\startup.bat"

echo.
echo ================================================
echo           启动完成！
echo ================================================
echo 前端服务: http://localhost:3000
echo 后端服务: http://localhost:8080/shopping-mall
echo ================================================
pause