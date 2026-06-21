# Programowanie ekstremalne

Pracujemy na repo na githubie: [repo link](https://github.com/ExtremeProgrammingAGH2026/-XP-2026-gr2)

Wszelkie zmiany wprowadzone na Gitlab zostaną **NADPISANE**, gdyż jest skonfiguorwany force sync z githuba na gitlaba.
Gitlab jest utrzymywany tylko na potrzeby zajęć.

Aby stworzyć plik wykonywalny `.exe`, należy:
```powershell
  cd TaskPlanner
  mvn clean package
  jpackage --type app-image --name TaskPlanner --input target --main-jar TaskPlanner-1.0-SNAPSHOT.jar --main-class org.example.App --win-console  
```
