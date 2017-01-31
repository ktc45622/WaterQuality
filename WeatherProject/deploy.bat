@echo off
net use z: \\weatherproject\wpdist
xcopy dist\*.* z:\ /s /e /v
net use z: /delete
rem test