# Devoxx 2023
_HOL : Hacker son application JAVA pour mieux la sécuriser ensuite_

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

# Cas 01 : Réinitialisation de mot de passe par envoi d'un lien

- Rendez-vous sur la page principale de cette application Java (sous Firefox, car Chrome n'aime pas http non sécurisé) : http://localhost:8081
- Il existe un compte administrateur créé automatiquement, vous ne le connaissez pas, mais vous allez devoir tenter de le découvrir.
- Vous allez maintenant créer un compte, via le bouton associé, le compte est automatiquement créé (on n'envoie pas de mail de confirmation ici pour simplification, mais simplement un mail de bienvenue que vous trouverez dans la log console de l'application)

## Description du cas fonctionnel

- Vous allez utiliser la fonction "J'ai oublié mon mot de passe", qui va vous demander votre email, puis va vous envoyer un email avec un lien spécifique.
- Ici, nous utilisons un mock pour le mail, donc le contenu du mail va apparaitre dans la console de votre IDE (vous pouvez le copier/coller dans un fichier html puis l'ouvrir avec votre navigateur)
- Lancer la réinitialisation du mot de passe et modifiez le
- Vérifiez que le nouveau mot de passe fonctionne bien (et que l'ancien ne fonctionne plus)

## Phase d'attaque

- Vous souhaitez prendre le contrôle de l'administrateur de l'application
- Vous devez deja trouver son email
- Puis vous allez utiliser la fonction d'oubli de mot de passe pour réinitialiser le mot de passe de l'administrateur avec un mot de passe de votre choix
    - Attention vous devez vous placer dans la peau d'un attaquant donc considérer que vous n'avez pas accès à la log console du serveur
- Vous allez enfin vous connecter en tant qu'administrateur