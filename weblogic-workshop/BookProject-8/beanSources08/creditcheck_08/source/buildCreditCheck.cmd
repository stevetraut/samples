set CC_PACKAGE=com\accounts\creditcheck
set PROJECT_NAME=CreditCheck
set BEA_HOME=c:\bea
set WL_HOME=%BEA_HOME%\weblogic700
set JAVA_HOME=%BEA_HOME%\jdk131_03
set COMPILER_PATH=%JAVA_HOME%\bin
set PATH=%COMPILER_PATH%;%PATH%

set WLS_LIB=%WL_HOME%\server\lib
set CLASS_PATH=%WLS_LIB%\knex.jar;%WLS_LIB%\weblogic.jar

javac -classpath %CLASS_PATH% %CC_PACKAGE%\*.java
