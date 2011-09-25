%~d0
cd %~dp0
set CLASSPATH=jars\bzip2.jar
call scalac -d build util\*.scala defender\*.scala || goto :pause
for %%f in ( defender\*.properties ) do native2ascii -encoding UTF-8 %%f build\fake\%%f
echo Tests
for %%f in ( test\*.scala ) do echo %%f && call scala -classpath build %%f || goto :pause

:pause
