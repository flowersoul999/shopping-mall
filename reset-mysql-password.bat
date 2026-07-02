@echo off
chcp 65001 >nul
echo ================================
echo  MySQL root 密码重置工具
echo ================================
echo.
echo 正在停止 MySQL 服务...
net stop MySQL97
if %errorlevel% neq 0 (
    echo 停止失败，请用管理员身份运行此脚本！
    pause
    exit /b 1
)
echo.
echo 正在以跳过权限模式启动 MySQL...
start /B "" "D:\MySQL\MySQL Server 9.7\bin\mysqld.exe" --skip-grant-tables --datadir="D:\MySQL\MySQL Server 9.7\Data"
echo 等待 MySQL 启动...
timeout /t 5 /nobreak >nul
echo.
echo 正在重置密码为 abcfff 并创建 flowersoul 数据库...
"D:\MySQL\MySQL Server 9.7\bin\mysql.exe" -u root -e "FLUSH PRIVILEGES; ALTER USER 'root'@'localhost' IDENTIFIED BY 'abcfff'; CREATE DATABASE IF NOT EXISTS flowersoul DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"
if %errorlevel% equ 0 (
    echo 密码重置成功！新密码: abcfff
) else (
    echo 密码可能有兼容性问题，尝试另一种方式...
    "D:\MySQL\MySQL Server 9.7\bin\mysql.exe" -u root -e "FLUSH PRIVILEGES; ALTER USER 'root'@'localhost' IDENTIFIED WITH mysql_native_password BY 'abcfff'; CREATE DATABASE IF NOT EXISTS flowersoul DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"
)
echo.
echo 正在关闭跳过权限模式的 MySQL...
taskkill /f /im mysqld.exe >nul 2>&1
timeout /t 3 /nobreak >nul
echo.
echo 正在重新启动 MySQL 服务...
net start MySQL97
echo.
echo ================================
echo  完成！新密码: abcfff
echo  数据库: flowersoul
echo  现在你可以刷新前端页面试试了
echo ================================
pause
