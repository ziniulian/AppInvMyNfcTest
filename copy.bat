@echo off
xcopy L:\Doc\SVN\Work\MyNfc\trunk\src\MyNfc\app\src\main L:\Doc\Git\AppInvMyNfcTest\app\src\main\ /S
xcopy L:\Doc\SVN\Work\MyNfc\trunk\src\MyNfc\app\libs L:\Doc\Git\AppInvMyNfcTest\app\libs\ /S
copy L:\Doc\SVN\Work\MyNfc\trunk\src\MyNfc\app\build.gradle L:\Doc\Git\AppInvMyNfcTest\app
pause
