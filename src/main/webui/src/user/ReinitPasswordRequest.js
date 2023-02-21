import { html, css, LitElement } from 'lit'
import { classMap } from 'lit/directives/class-map.js'

import '@ui5/webcomponents/dist/Label.js'
import '@ui5/webcomponents/dist/Input.js'
import '@ui5/webcomponents/dist/Button.js'

export class ReinitPasswordRequest extends LitElement {
  static get properties () {
    return {
      _email: { type: String },
      _message: { type: String },
      _errorMail: { type: String }
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

      div.champ.invalid {
        border-color: var(--color-1);
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

      a.register {
        text-decoration: none;
      }
    `
    }

  /**
   * @override
   */
  connectedCallback () {
    super.connectedCallback()
    this.addEventListener('keyup', this.enterForReinit)
  }

  /**
   * @override
   */
  disconnectedCallback () {
    super.disconnectedCallback()
    this.removeEventListener('keyup', this.enterForReinit)
  }

  enterForReinit (evenement) {
    if (evenement.key === 'Enter') {
      this.requestReinitPassword()
    }
  }

  requestReinitPassword () {
      if (this.validateForm()) {
          fetch('/users/reinitPasswordRequest', {
              method: 'POST',
              headers: new Headers({
                  "Content-Type": 'text/plain',
                  "Accept": 'application/json'
              }),
              body: this._email
          }).then(async response => {
              if (response.status === 200) {
                  this._message = "Demande envoyée par mail (si votre compte existe)"
              } else {
                  this._message = "Erreur inattendue"
              }
              }
          ).catch(error => {
              console.log(error)
          })
      }
  }

  onChangeEmail (e) {
    this._email = e.target.value
      if (this._errorMail) {
          if (this._email.length == 0) {
              this._errorMail = null
          }
      }
  }

  validateForm(){
      let valid = true;
      if (this._email == undefined || this._email.length ==0){
          valid = false
          this._message = 'Des champs obligatoires sont vides'
      } 
      return valid
  }

  displayErrors(){
    if (this._message && this._errorMail) {
      return html` <span class="alerte">${this._message} / ${this._errorMail}</span>`
    }
    else if (this._message) {
      return html` <span class="alerte">${this._message}</span>`
    }
    else if (this._errorMail) {
      return html` <span class="alerte">${this._errorMail}</span>`
    }
  }

  render () {
    return html`
    <div class="container">
      <img src="../img/devoxx-france.png" alt="Logo Devoxx" />
      <span class="titre"></span>
      <span class="titre">Demande de réinitialisation de mot de passe</span>
      ${this.displayErrors()}
      <div>
        <div class="${classMap({ champ: true, invalid: this._message && !this._email })}">
          <label for="emailReinitFor">Email</label>
          <input id="emailReinitId" type="email" required aria-required="true" @input="${this.onChangeEmail}" autofocus/>
        </div>
      </div>
      <button @click="${this.requestReinitPassword}">Réinitialiser mon mot de passe</button>
    </div>
    `
  }
}

customElements.define('pts-reinitpasswordrequest', ReinitPasswordRequest)
