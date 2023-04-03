import { html, css, LitElement } from 'lit'

import '@ui5/webcomponents/dist/Label.js'
import '@ui5/webcomponents/dist/Input.js'
import '@ui5/webcomponents/dist/Button.js'
import {unsafeHTML} from 'lit/directives/unsafe-html.js';
import '../common/UploadFile.js'

export class Users extends LitElement {
  static get properties () {
    return {
        _message: { type: String },
        _users: {type: Object}
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
    this.getUsers()
  }

  async getUsers () {
    fetch('/users', {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json'
        },
    })
    .then(response => {
        if (response.status == 401 || response.status == 403) {
            this._message = 'Non autorisé'
        } else if (response.status == 400) {
            response.json().then(dataError => {
                    this._message = 'Erreur inattendue'
                this.requestUpdate()
            })
        } else {
            return response.json()
        }
    })
        .then(users => {
            if (users != undefined) {
                this._users = users
            }
        })    
    .catch(err => console.log(err))
  }
  
  displayUsers() {
      if (this._users != undefined) {
          return html `${this._users.map(user => {
              return html `${this.displayUserImage(user.profilImage)}<span><b>${user.nickname} (${user.email})</b> : </span>${unsafeHTML(user.interpolateDesc)}`
          })}`
      }
  }
  
  displayUserImage(image) {
      if (image != undefined && image != 'null') {
          return html `<img src="./img/profil/${image}" height="100"/>`
      }
      return html ``
  }
  
  accueil() {
      window.location.replace('/')
  }

  render () {
    return html`
    <div class="container">
        ${this._message ? html`<span class="alerte">${this._message}</span>` : html``}
        <h2>Liste des utilisateurs de l'application Devoxx</h2>
        ${this.displayUsers()}
        <div class="boutons">
            <button @click="${this.accueil}">Accueil</button>
        </div>
    </div>
    `
  }
}

customElements.define('pts-users', Users)
