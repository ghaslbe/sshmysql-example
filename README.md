
# ssh mysql smtp java example
mysql via ssh

Dieses Java Programm baut eine Verbindung zu einer MySQLDB auf die hinter einem SSH Server erreichbar ist.
Das Ergebnis der SQL Abfrage wird per E-Mail versendet.


1) Config erstellen
config.sample.properties umbenennen in config.properties und die entsprechenden Werte einfüllen

2)
Maven-Projekt bauen

Öffne ein Terminal oder eine Kommandozeile in deinem Projektverzeichnis (my-project) und führe den folgenden Befehl aus:

bash
Copy code
mvn clean package install
Dadurch wird das Projekt gebaut, alle Abhängigkeiten heruntergeladen und eine ausführbare .jar-Datei im target-Verzeichnis erstellt.

3)
Programm starten

Nachdem das Projekt erfolgreich gebaut wurde, kannst du das Programm mit dem folgenden Befehl starten:

bash
Copy code
java -cp target/ssh-mysql-connector-1.0-SNAPSHOT.jar com.myapp.SSHMySQLConnect
Dieser Befehl sagt Java, die .jar-Datei zu verwenden und die main-Methode der SSHMySQLConnect-Klasse zu starten.

Wenn du eine integrierte Entwicklungsumgebung (IDE) wie Eclipse, IntelliJ IDEA oder NetBeans verwendest, kannst du das Programm auch direkt von dort aus starten, nachdem du das Maven-Projekt in deine IDE importiert hast. In den meisten IDEs reicht es aus, mit der rechten Maustaste auf die SSHMySQLConnect-Klasse zu klicken und "Run" oder "Execute" auszuwählen.

https://www.guentherhaslbeck.de/mysql-hinter-ssh-aufrufen/
