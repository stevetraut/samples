set ITEM_PACKAGE=com\onlinestore\item
set INVENTORY_PACKAGE=com\onlinestore\inventory
set PROJECT_NAME=Inventory

set BEA_HOME=c:\bea
set WL_HOME=%BEA_HOME%\weblogic700
set JAVA_HOME=%BEA_HOME%\jdk131_03
set COMPILER_PATH=%JAVA_HOME%\bin
set PATH=%COMPILER_PATH%

set WLS_LIB=%WL_HOME%\server\lib
set CLASS_PATH=%WLS_LIB%\weblogic.jar

set CREDIT_SOURCE=source\com\accounts\creditcheck\ch10\*.java

set CREDIT_CLASSES=com\accounts\creditcheck\ch10\*.class

javac -classpath %CLASS_PATH% %CREDIT_SOURCE% -d classes

cd classes

jar cvf ..\..\jars\CreditCheck_10.jar %CREDIT_CLASSES% META-INF\*.xml

cd..