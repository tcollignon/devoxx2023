# Devoxx 2023
_HOL : Hacker son application JAVA pour mieux la sécuriser ensuite_

# Pré-requis
- Avoir JAVA 17 installé sur votre machine : https://jdk.java.net/archive/ ou https://adoptium.net/temurin/releases/
- Avoir Maven installé sur votre machine : https://maven.apache.org/download.cgi, puis https://maven.apache.org/install.html
- Importer le projet JAVA dans votre IDE préféré
- Lancer Quarkus : https://quarkus.io/guides/getting-started#running-the-application

# Cas 01 : Réinitialisation de mot de passe par envoi d'un lien

- Rendez-vous sur la page principale de cette application Java (sous Firefox, car Chrome n'aime pas http non sécurisé) : http://localhost:8081
- Il existe un compte administrateur créé automatiquement, vous ne le connaissez pas, mais vous allez devoir tenter de le découvrir.
- Vous allez maintenant créer un compte, via le bouton associé, le compte est automatiquement créé (on n'envoie pas de mail de confirmation ici pour simplification, mais simplement un mail de bienvenue que vous trouverez dans la log console de l'application)

## Description du cas fonctionnel

- Vous allez utiliser la fonction "J'ai oublié mon mot de passe", qui va vous demander votre email, puis va vous envoyer un email avec un lien spécifique.
- Ici, nous utilisons un mock pour le mail, donc le contenu du mail va apparaitre dans votre console (vous pouvez le copier/coller dans un fichier html puis l'ouvrir avec votre navigateur)
- Lancer la réinitialisation du mot de passe par un nouveau
- Vérifiez que le nouveau mot de passe fonctionne bien (et que l'ancien ne fonctionne plus)

## Phase d'attaque

- Vous souhaitez prendre le contrôle de l'administrateur de l'application
- Vous devez deja trouver son email
- Puis vous allez utiliser la fonction d'oubli de mot de passe pour réinitialiser le mot de passe de l'administrateur avec un mot de passe de votre choix
    - Attention vous devez vous placer dans la peau d'un attaquant donc considérer que vous n'avez pas accès à la log console du serveur
- Vous allez enfin vous connecter en tant qu'administrateur

### Solution

- Vous créez un compte d'attaque avec un mail qui existe (car il faut pouvoir recevoir le mail avec le lien)
- Vous constatez que le mail de bienvenu provient de info@devoxx.com. Vous cherchez donc un autre compte qui pourrait être admin
    - Quand vous vous inscrivez, vous constatez que si vous vous inscrivez 2 fois, il vous dit que le mail est deja pris, cela va vous permettre de tester les comptes pour trouver l'admin => admin@devoxx.com
- Vous utilisez la fonction d'oubli de mot pour votre compte d'attaque => vous récupérez le lien correspondant
- Vous modifiez le lien en mettant l'adresse email de l'administrateur puis vous allez sur le lien
    - Attention vous devez effectuer la manipulation de modification du lien rapidement, car les demandes de changements de mots de passes expirent toutes les 5 minutes (Cf UserScheduleService#deleteReinitPasswordRequest)
- Vous remplissez le mot de passe que vous voulez, puis vous lancez la réinitialisation
- Vous pouvez ensuite vous connecter en tant qu'administrateur

## Phase de défense

- Maintenant que vous avez trouvé une faille dans cette application, il est temps de la corriger ! C'est tout de même vous qui maintenez cette application !
- _OPTIONNEL_ : Mettez en évidence le problème par un nouveau ou une modification de test unitaire/intégration
- _OPTIONNEL_ : Ajouter un logger de sécurité qui met en évidence :
    - Une action sensible (légitime) => [SECURITY OK]
    - Une action malveillante => [SECURITY WARN]
- Corrigez le problème
