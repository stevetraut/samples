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

set ITEM_SOURCE=source\com\onlinestore\item\*.java
set INVENTORY_SOURCE=source\com\onlinestore\inventory\*.java

set ITEM_CLASSES=com\onlinestore\item\*.class
set INVENTORY_CLASSES=com\onlinestore\inventory\*.class

javac -classpath %CLASS_PATH% %ITEM_SOURCE% %INVENTORY_SOURCE% -d classes

cd classes

jar cvf ..\..\jars\Inventory.jar %ITEM_CLASSES% %INVENTORY_CLASSES% META-INF\*.xml

cd..