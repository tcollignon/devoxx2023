# Devoxx 2023
_HOL : Hacker son application JAVA pour mieux la sécuriser ensuite_

Slides : [lien vers la présentation](https://www.canva.com/design/DAFcIbKCXek/yKW3d5LBIQomt4jhfQRAnA/view?utm_content=DAFcIbKCXek&utm_campaign=designshare&utm_medium=link&utm_source=publishsharelink)

# Pré-requis
- Avoir JAVA 17 installé sur votre machine : https://jdk.java.net/archive/ ou https://adoptium.net/temurin/releases/
- Avoir Maven installé sur votre machine : https://maven.apache.org/download.cgi, puis https://maven.apache.org/install.html
- Importez le projet JAVA dans votre IDE préféré
- Lancez Quarkus : https://quarkus.io/guides/getting-started#running-the-application
- Puis mettez-vous directement en mode Hibernate "update", en décommentant la ligne 19 du fichier de configuration src/main/resources/application.properties et en commentant la ligne 18

````
#quarkus.hibernate-orm.database.generation=drop-and-create
quarkus.hibernate-orm.database.generation=update
````