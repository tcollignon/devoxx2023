import { html, css, LitElement } from 'lit'
import { classMap } from 'lit/directives/class-map.js'

import '@ui5/webcomponents/dist/Label.js'
import '@ui5/webcomponents/dist/Input.js'
import '@ui5/webcomponents/dist/Button.js'
import {User} from "./User";
import {CODE_EXCEPTION} from "../common/Enums";

export class Register extends LitElement {
  static get properties () {
    return {
      _login: { type: String },
      _password: { type: String },
      _email: { type: String },
      _name: { type: String },
      _firstname: { type: String },
      _acceptNewsletter: { type: Boolean },
      _error: { type: String },
      _errorMail: { type: String },
      _errorPassword: { type: String }
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

      .container > div {
        display: grid;
        grid-template-columns: 1fr 1fr;
        gap: 16px 30px;
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
        width: 30vw;
        max-width: 300px;
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
        order: 1;
      }

      div.champ > input {
        flex: 1;
        order: 2;
        background: transparent;
        border: none;
        outline: none;
        font-family: var(--font);
      }

      div.champ > input:required + label::after {
        content: " *";
        color: var(--color-1);
      }

      div.champ.invalid {
        border-color: var(--color-1);
      }

      div.champ:nth-of-type(1) {
        order: 1;
      }

      div.champ:nth-of-type(2) {
        order: 3;
      }

      div.champ:nth-of-type(3) {
        order: 5;
      }

      div.champ:nth-of-type(4) {
        order: 2;
      }

      div.champ:nth-of-type(5) {
        order: 4;
      }

      button {
        --bouton-border-color: var(--color-5);
        --bouton-background: var(--color-5);
        --bouton-color: var(--color-3);

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

      div.boutons > button:is(:focus-visible, :hover) {
        --bouton-border-color: #003dd7;
        --bouton-background: #003dd7;

        outline: none;
      }

      .alerte {
        font-family: var(--font);
        font-weight: var(--font-weight);
        color: red
      }
    `
  }

  constructor() {
    super();
    this._acceptNewsletter = true
  }

  /**
   * @override
   */
  connectedCallback () {
    super.connectedCallback()
    this.addEventListener('keyup', this.enterForRegister)
  }

  /**
   * @override
   */
  disconnectedCallback () {
    super.disconnectedCallback()
    this.removeEventListener('keyup', this.enterForRegister)
  }

  /**
   * Permet de lancer la procédure d'inscription en pressant la touche Entrée.
   * @param {KeyboardEvent} evenement - l'évènement déclenché par l'utilisateur
   */
  enterForRegister (evenement) {
    if (evenement.key === 'Enter') {
      this.register()
    }
  }

  register () {
      if (this.validateForm()) {
          fetch('/users/register', {
              method: 'POST',
              headers: new Headers({
                  "Content-Type": 'application/json',
                  "Accept": 'application/json'
              }),
              body: JSON.stringify(this.getUserToRegister())
          }).then(async response => {
                  if (response.status === 201) {
                      window.location.replace('/')
                  } else if (response.status === 400) {
                      response.json().then(dataError => {
                          if (dataError.codeException === CODE_EXCEPTION.EMAIL_ALREADY_EXIST) {
                              this._error = "Cette adresse email existe déjà"
                          } else if (dataError.codeException === CODE_EXCEPTION.NICKNAME_ALREADY_EXIST) {
                              this._error = "Ce pseudo existe déjà"
                          } else {
                              this._error = dataError.messageException
                          }
                      })
                  } else{
                      this._error = "Erreur inattendue"
                  }
              }
          ).catch(error => {
              console.log(error)
          })
    }
  }

  getUserToRegister() {
      let userToRegister = new User()
      userToRegister.email = this._email
      userToRegister.name = this._name
      userToRegister.firstName = this._firstname
      userToRegister.nickname = this._login
      userToRegister.password = this._password
      userToRegister.acceptNewsletter = this._acceptNewsletter
      return userToRegister
  }

  onChangeLogin (e) {
    this._login = e.target.value
  }

  onChangePassword (e) {
    this._password = e.target.value
  }

  onChangeEmail (e) {
    this._email = e.target.value
      if (this._errorMail) {
          if (this._email.length == 0) {
              this._errorMail = null
          }
      }
  }

  onChangeName (e) {
    this._name = e.target.value
  }

  onChangeFirstname (e) {
    this._firstname = e.target.value
  }

  onClickAcceptNewsletter (e) {
    this._acceptNewsletter = e.srcElement.checked
  }

  validateForm(){
      let valid = true;
      if (this._email == undefined || this._email.length ==0){
          valid = false
          this._error = 'Des champs obligatoires sont vides'
      } 
      if (this._login == undefined || this._login.length ==0){
          valid = false
          this._error = 'Des champs obligatoires sont vides'
      }
      if (this._password == undefined || this._password.length ==0){
          valid = false
          this._error = 'Des champs obligatoires sont vides'
      } else{
          if (this._password.length < 6) {
              valid = false
              this._error = 'Le password doit contenir 6 caractères minimum'
          }
      }
      return valid
  }

  displayErrors(){
    if (this._error && this._errorMail) {
      return html` <span class="alerte">${this._error} / ${this._errorMail}</span>`
    }
    else if (this._error) {
      return html` <span class="alerte">${this._error}</span>`
    }
    else if (this._errorMail) {
      return html` <span class="alerte">${this._errorMail}</span>`
    }
  }

  render () {
    return html`
    <div class="container">
      <img src="./img/devoxx-france.png" alt="Logo Devoxx" />
      <span class="titre">Inscrivez-vous à Devoxx !</span>
      ${this.displayErrors()}
      <div>
        <div class="${classMap({ champ: true, invalid: this._error && !this._email })}">
          <input id="emailRegisterId" type="email" required aria-required="true" @input="${this.onChangeEmail}" autofocus/>
          <label for="emailRegisterFor">Email</label>
        </div>
        <div class="${classMap({ champ: true, invalid: this._error && !this._login })}">
          <input id="loginRegisterId" type="text" required aria-required="true" @input="${this.onChangeLogin}" maxlength="16"/>
          <label for="loginRegisterFor">Pseudo</label>
        </div>
        <div class="${classMap({ champ: true, invalid: this._error && !this._password })}">
          <input id="passwordRegisterId" type="password" required aria-required="true"  @input="${this.onChangePassword}"/>
          <label for="passwordRegisterFor">Mot de passe</label>
        </div>
        <div class="champ">
          <input id="nameRegisterId" type="text" @input="${this.onChangeName}"/>
          <label for="nameRegisterFor">Nom</label>
        </div>
        <div class="champ">
          <input id="firstnameRegisterId" type="text" @input="${this.onChangeFirstname}"/>
          <label for="firstnameRegisterFor">Prénom</label>
        </div>
      </div>
      <label for="acceptNewsletterFor">
        <input id="acceptNewsletter" type="checkbox" @click=${e => this.onClickAcceptNewsletter(e)} checked="true" />
        Accepte de recevoir des emails de Devoxx
      </label>
      <button @click="${this.register}">M'inscrire</button>
    </div>
    `
  }
}

customElements.define('pts-register', Register)
