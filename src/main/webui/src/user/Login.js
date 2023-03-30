import { html, css, LitElement } from 'lit'

import '@ui5/webcomponents/dist/Label.js'
import '@ui5/webcomponents/dist/Input.js'
import '@ui5/webcomponents/dist/Button.js'
import { UserData } from './UserData.js'
import {UserLoginPageData} from "./UserLoginPageData";
import {User} from "./User";
import {CODE_EXCEPTION} from "../common/Enums";
import '../common/UploadFile.js'

export class Login extends LitElement {
  static get properties () {
    return {
      _login: { type: String },
      _password: { type: String },
      _authentificationKO: { type: Boolean },
      _message: { type: String },
      _rememberMe: { type: Boolean },
       logged: { type: Boolean },
       _imgHome: { type: String },
    }
  }

  static get styles () {
    return css`
      :host {
        display: flex;
        align-items: center;
        justify-content: center;
        width: 100vw;
        height: 100vh;
        background: radial-gradient(circle at top left, rgba(54,47,184,1) 0%, rgba(0,212,255,0.5) 100%), radial-gradient(circle at bottom right, rgba(19,142,35,1) 0%, rgba(0,255,188,1) 100%);
        font-family: var(--font);
      }

      .container {
        position: relative;
        display: flex;
        flex-direction: column;
        align-items: center;
        background-color: var(--color-3);
        gap: 2rem;
        border-radius: 0.6rem;
        padding: 4rem 2rem;
        box-shadow: rgba(0, 0, 0, 0.25) 0px 2px 8px;
      }

      img {
        position: absolute;
        top: -8rem;
        width: 8rem;
        background: rgb(255, 255, 255);
        padding: 3rem;
        clip-path: circle(7rem at 50% 70%);
      }

      div.champ {
        display: flex;
        flex-direction: column;
        width: 100%;
        height: 4rem;
        background: #cfd8dc;
        border: 1px solid #cfd8dc;
        border-radius: 0.6rem;
        overflow: hidden;
      }

      div.champ:focus-within {
        border-color: #546e7a;
      }

      div.champ > * {
        padding: 0.4rem 0.6rem;
      }

      div.champ > label {
        color: #414141;
      }

      div.champ > input {
        flex: 1;
        background: transparent;
        border: none;
        outline: none;
        font-family: var(--font);
      }

      label {
        display: flex;
        align-items: center;
        gap: 6px;
        user-select: none;
        cursor: pointer;
      }

      div.boutons {
        display: flex;
        gap: 6px;
        width: 100%;
      }

      div.boutons > :is(button, a.register),
      a.passwordForgot {
        cursor: pointer;
        font-size: 14px;
        font-family: var(--font);
        border-radius: 0.6rem;
        padding: 0.6rem 1rem;
        flex: 1;
        border: 2px solid var(--bouton-border-color, transparent);
        background: var(--bouton-background);
        color: var(--bouton-color);
        transition: background 0.15s ease, border-color 0.15s ease, color 0.15s ease;
      }

      div.boutons > button {
        --bouton-border-color: var(--color-5);
        --bouton-background: var(--color-5);
        --bouton-color: var(--color-3);
      }

      div.boutons > button:is(:focus-visible, :hover) {
        --bouton-border-color: #003dd7;
        --bouton-background: #003dd7;

        outline: none;
      }

      div.boutons > a.register {
        --bouton-border-color: var(--color-5);
        --bouton-background: var(--color-3);
        --bouton-color: var(--color-5);

        text-align: center;
        text-decoration: none;
      }

      div.boutons > a.register:is(:focus-visible, :hover) {
        --bouton-border-color: #d0ddff;
        --bouton-background: #d0ddff;

        outline: none;
      }

      a.passwordForgot {
        --bouton-border-color: var(--color-3);
        --bouton-background: var(--color-3);
        --bouton-color: var(--color-5);

        text-align: center;
        text-decoration: none;
        width: 100%;
        box-sizing: border-box;
      }

      a.passwordForgot:is(:focus-visible, :hover) {
        --bouton-border-color: #d0ddff;
        --bouton-background: #d0ddff;
      }

      .alerte {
        font-family: var(--font);
        font-weight: var(--font-weight);
        color: red
      }
    `
  }

  constructor () {
    super()

    this._message = ''

    const queryParam = new URLSearchParams(window.location.search.substring(1))
    let registrationSuccess = queryParam.get('registrationSuccess')
    if (registrationSuccess != undefined && registrationSuccess){
      this._message = "Inscription réussie !"
    }
    let reinitPasswordSuccess = queryParam.get('reinitPasswordSuccess')
    if (reinitPasswordSuccess != undefined && reinitPasswordSuccess){
      this._message = "Changement de mot de passe réussi !"
    }
    let sessionExpired = queryParam.get('sessionExpired')
    if (sessionExpired != undefined && sessionExpired) {
      this._message = "Session expirée"
    }
  
    const userLogin = new UserLoginPageData()
    this._rememberMe = userLogin.getPassword() != undefined ? true : false
    this._login = userLogin.getEmail()
    this._password = userLogin.getPassword()

    const userData = new UserData()
    this.logged = userData.getUserName() != undefined
  }

  /**
   * @override
   */
  connectedCallback () {
    super.connectedCallback()
    this.setImageHome()
    this.addEventListener('keyup', this.toucheEntreePourSeConnecter)
  }
  
  setImageHome() {
      const userData = new UserData()
      this._imgHome = userData.getUserIcon() != undefined && userData.getUserIcon() != "null" ? "./img/profil/" + userData.getUserIcon() : "./img/devoxx-france.png"
      this.requestUpdate()
  }

  /**
   * @override
   */
  disconnectedCallback () {
    super.disconnectedCallback()
    this.removeEventListener('keyup', this.toucheEntreePourSeConnecter)
  }
  
  /**
   * Permet de lancer la procédure de connexion en pressant la touche Entrée.
   * @param {KeyboardEvent} evenement - l'évènement déclenché par l'utilisateur
   */
  toucheEntreePourSeConnecter (evenement) {
    if (evenement.key === 'Enter') {
        if (this.logged) {
            this.updateInformations()
        } else{
            this.connecte()
        }
    }
  }

  connecteWithLoginPassword (login, password) {
    const userData = new UserData()
    let formData = new FormData();
    formData.append('j_username', login);
    formData.append('j_password', password);
    fetch('/j_security_check', {
        method: 'POST',
        body: formData
    }).then(response => {
        if (response.status === 200) {
            this.logged = true
            this._authentificationKO = false
            const userLogin = new UserLoginPageData()
            if (this._rememberMe) {
                userLogin.setEmail(login)
                userLogin.setPassword(password)
            } else {
                userLogin.remove()
            }
            this.getUserAndStoreInLocalStorage()
        } else {
            this._authentificationKO = true
            userData.remove()
        }
    }).catch(error => {
        this._authentificationKO = true
        userData.remove()
    })
  }

  connecte () {
    this.connecteWithLoginPassword(this._login, this._password)
  }

  getUserAndStoreInLocalStorage () {
    fetch('/users/myprofile', {
        method: 'GET'
    }).then(response => {
        if (response.status === 200) {
            return response.json()
        } else {
            //TODO
        }
    }).then(user => {
        if (user != undefined) {
            const userData = new UserData()
            userData.setRoles(user.roles)
            userData.setNickname(user.nickname)
            userData.setUserName(user.email)
            userData.setUserIcon(user.profilImage)
            userData.setDescription(user.desc)
            userData.setInterpolateDescription(user.interpolateDesc)
            this.requestUpdate()
            this.setImageHome()
        }
    }).catch(error => {
        //TODO
    })
  }
  
  deconnecte() {
      const userData = new UserData()
      userData.remove()
      this.logged = false
      document.cookie = 'quarkus-credential=; Max-Age=0'
      this.setImageHome()
      this.requestUpdate()
  }

  updateInformations () {
        const userData = new UserData()
        let userInfos = new User()
        userInfos.email = userData.getUserName() 
        userInfos.nickname =  this.shadowRoot.getElementById('champLoginLoggue').value
        userInfos.password =  this.shadowRoot.getElementById('champMotDePasseLoggue').value
        userInfos.desc =  this.shadowRoot.getElementById('champDescLoggue').value
        if (userInfos.nickname && userInfos.password && userInfos.password.length >= 6) {
            fetch('/users/myprofile', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(userInfos)
            })
            .then(response => {
                if (response.status == 403) {
                    this._message = 'Non autorisé'
                } else if (response.status == 400) {
                    response.json().then(dataError => {
                        if (dataError.codeException === CODE_EXCEPTION.EMAIL_ALREADY_EXIST) {
                            this._message = "Cette adresse email existe déjà"
                        } else if (dataError.codeException === CODE_EXCEPTION.NICKNAME_ALREADY_EXIST) {
                            this._message = "Ce pseudo existe déjà"
                        } else {
                            this._message = 'Erreur inattendue'
                        }
                        this.requestUpdate()
                    })
                } else {
                    return response.json()
                }
            })
            .then(user => {
                if (user != undefined) {
                    userData.setNickname(user.nickname)
                    userData.setDescription(user.desc)
                    userData.setInterpolateDescription(user.interpolateDesc)
                }
                this._message = ''
                this.requestUpdate()
            })
            .catch(err => console.log(err))
        } else {
            this._message = 'Pseudo et mot de passe obligatoire (6 char min)'
        }
  }

  handleUploadImage () {
    const uploadComponent = this.shadowRoot.getElementById('userImageFileId')
    if (uploadComponent.files.length > 0) {
        const imageFile = uploadComponent.files[0]
        if (imageFile.size > 500000) {
            alert("Fichier trop volumineux, max 500Ko")
        } else {
            const reader = new FileReader()

            reader.addEventListener('load', function () {
                const userData = new UserData()
                const image = reader.result

                fetch('/users/uploadImage/' + imageFile.name, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'text/plain'
                    },
                    body: image
                })
                .then(response => {
                    if (response.status == 403) {
                        this._message = "Non autorisé"
                    } else if (response.status == 400) {
                        response.json().then(dataError => {
                            this._message = dataError.messageException
                            this.requestUpdate()
                        })
                    } else {
                        return response.json()
                    }
                })
                .then(user => {
                    if (user != undefined) {
                        userData.setUserIcon(user.profilImage)
                        this.setImageHome()
                        this.requestUpdate()
                    }
                })
                .catch(err => console.log(err))
            }.bind(this), false)

            reader.readAsDataURL(imageFile)
        }
    } else {
        this.requestUpdate()
    }
  }

  onChangeLogin (e) {
    this._login = e.target.value
  }

  onChangePassword (e) {
    this._password = e.target.value
  }

  onChangeRememberMe (e) {
    this._rememberMe = e.target.checked
  }
  
  displayFormIfNotLogged() {
      if (!this.logged) {
          return html `
      <div class="champ">
        <label for="champLogin">Email</label>
        <input id="champLogin" type="text" required aria-required="true" @input="${this.onChangeLogin}" autofocus .value="${this._login}"/>
      </div>
      <div class="champ">
        <label for="champMotDePasse">Mot de passe</label>
        <input id="champMotDePasse" type="password" required aria-required="true"  @input="${this.onChangePassword}" .value="${this._password}"/>
      </div>
      <label>
        <input type="checkbox" id="rememberMeId" .checked="${this._rememberMe}" @click="${this.onChangeRememberMe}" />
        Se souvenir de mes identifiants
      </label>
      <div class="boutons">
        <button @click="${this.connecte}">Me connecter</button>
        <a class="register" href="?register=true">M'inscrire</a>
      </div>
      <a class="passwordForgot" href="?reinitPasswordRequest=true">J'ai oublié mon mot de passe</a>
          `
      }
  }
  
  displayLogged() {
      if (this.logged) {
          const userData = new UserData()
          return html`
              <span class="titre">Bienvenue à Devoxx <b>${userData.getNickname()}</b></span>
              <span class="titre">Votre description est <b>${userData.getInterpolateDescription()}</b></span>
              <div class="boutons">
                  <button @click="${this.deconnecte}">Me dé-connecter</button>
              </div>
              <span class="titre">Mettre à jour mes informations</span>
              <div class="champ">
                  <label for="champLoginLoggueFor">Pseudo</label>
                  <input id="champLoginLoggue" type="text" required aria-required="true" .value="${userData.getNickname()}"/>
              </div>
              <div class="champ">
                  <label for="champMotDePasseLoggueFor">Mot de passe</label>
                  <input id="champMotDePasseLoggue" required aria-required="true" minlength="6" type="password" .value=""/>
              </div>
              <div class="champ">
                  <label for="champDescLoggueFor">Description (Inclure la date -> \${date:dd-MM-yyyy})</label>
                  <input id="champDescLoggue" type="text" required aria-required="true" .value="${userData.getDescription()}"/>
              </div>
              <section slot="text" class="login-form">
                  <pts-upload-file id="userImageFileId" libelle="Image du profil" accept="image/*" @change="${this.handleUploadImage}"></pts-upload-file>
              </section>
              <div class="boutons">
                  <button @click="${this.updateInformations}">Valider</button>
              </div>
          `
      }
  }
  
  displayLogoIfNotLogged() {
      if (!this.logged) {
          return html `<div > <img src="./img/quarkus.png" style="position: relative;clip-path: none; padding:0px;top: 320px;left: -190px;background: none;width:50px"/></div>`
      }
  }

  render () {
    return html`
    <div class="container">
      <img src="${this._imgHome}" alt="Logo Home" />
      <span class="titre"></span>
      ${this._authentificationKO ? html`<span class="alerte">Email ou mot de passe incorrect.</span>` : html``}
      ${this._message ? html`<span class="alerte">${this._message}</span>` : html``}
      ${this.displayFormIfNotLogged()}
      ${this.displayLogged()}
    </div>
    ${this.displayLogoIfNotLogged()}
    `
  }
}

customElements.define('pts-login', Login)
