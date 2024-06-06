javac -d bin src\jello\*.java src\jello\solute\*.java src\jello\gui\*.java
jar cfvm Jello.jar src\manifest.txt -C bin .