:: Author: Eric Subach (2010)
:: For use with Bloomsburg University Weather Viewer
:: NOTE: This batch script only works if run on the server where the services
::       are running and the project located in C:\WeatherProject was compiled
::       in Netbeans.

echo off

::rd /s/q 
::del /s/q

:: Directories
set source=C:\WeatherProject\WeatherProjectNew\dist
set movie=C:\MovieSystem\bin
set retrieval=C:\RetrievalSystem\bin
set storage=C:\StorageSystem\bin
set watchdog=C:\ServerWatchdog\bin

:: Services
set servMovie="WeatherMovie"
set servRetrieval="WeatherRetrieval"
set servStorage="WeatherStorage"
set servWatchdog="Server Watchdog"
::set servMovie=Weather Movie Service
::set servRetrieval=Weather Retrieval Service
::set servStorage=Weather Storage Service
::set servWatchdog=Weather Server Watchdog Service

:: Show prompt
echo Copy files from directory %source% to directories:
echo %movie%
echo %retrieval%
echo %storage%
echo %watchdog%


set /p ans=[y/n]^> 

:: If no, don't run script
if not "%ans%" == "y" goto :end

:: Copy files to each service location
xcopy /s/v/x/y %source% %movie%
xcopy /s/v/x/y %source% %retrieval%
xcopy /s/v/x/y %source% %storage%
xcopy /s/v/x/y %source% %watchdog%


:: Restart each service
net stop %servWatchdog%

net stop %servStorage%
net start %servStorage%

net stop %servMovie%
net start %servMovie%

net stop %servRetrieval%
net start %servRetrieval%

net start %servWatchdog%


echo ====================================
echo Files copied and services restarted.

goto :eof



:end

echo No changes have been made.
