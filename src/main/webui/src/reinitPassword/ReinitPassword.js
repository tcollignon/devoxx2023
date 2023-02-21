import { html, css, LitElement } from 'lit'
import { classMap } from 'lit/directives/class-map.js'

import '@ui5/webcomponents/dist/Label.js'
import '@ui5/webcomponents/dist/Input.js'
import '@ui5/webcomponents/dist/Button.js'

export class ReinitPassword extends LitElement {
    static get properties () {
        return {
            _email: { type: String },
            _id: { type: String },
            _newPassword: { type: String },
            _error: { type: String },
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
      
      div.champ.readonly {
        background: #e5e5e5;
        border-color: #e5e5e5;
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

      button,
      a.register {
        border: none;
        cursor: pointer;
        background: radial-gradient(circle at top left, rgba(54,47,184,1) 0%, rgba(0,212,255,0.5) 100%), radial-gradient(circle at bottom right, rgba(19,142,35,1) 0%, rgba(0,255,188,1) 100%);
        border-radius: 0.6rem;
        padding: 0.6rem 1rem;
        color: var(--color-3);
        font-size: 14px;
        font-family: var(--font);
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

    connectedCallback () {
        super.connectedCallback()
        this.init()
        this.addEventListener('keyup', this.enterForReinit)
    }
    
    disconnectedCallback () {
        super.disconnectedCallback()
        this.removeEventListener('keyup', this.enterForReinit)
    }

    enterForReinit (evenement) {
        if (evenement.key === 'Enter') {
            this.reinitPassword()
        }
    }

    init () {
        const queryParam = new URLSearchParams(window.location.search.substring(1))
        this._id = queryParam.get('id')
        this._email = queryParam.get('mail')
        this._newPassword = ""
    }

    reinitPassword () {
        if (this.validateForm()) {
            fetch('/users/reinitPassword/' + this._email + '/' + this._id, {
                method: 'POST',
                headers: new Headers({
                    "Content-Type": 'text/plain',
                    "Accept": 'application/json'
                }),
                body: this._newPassword
            }).then(response => {
                    if (response.status === 200) {
                        this._error = 'Changement effectué'
                        window.location.replace('?reinitPasswordSuccess=true')
                    } else if (response.status === 401) {
                        this._error = 'Le changement est impossible'
                    } else {
                        this._error = 'Erreur inattendue, veuillez contacter le support'
                    }
                }
            ).catch(error => {
                console.log(error)
            })
        }
    }

    validateForm(){
        let valid = true;
        if (this._newPassword == undefined || this._newPassword.length ==0){
            valid = false
            this._error = 'Des champs obligatoires sont vides'
        }
        return valid
    }

    displayErrors(){
        if (this._error) {
            return html` <span class="alerte">${this._error}</span>`
        }
    }

    onChangePassword (e) {
        this._newPassword = e.target.value
    }

    render () {
        return html`
    <div class="container">
      <img src="../img/devoxx-france.png" alt="Logo Devoxx" />
      <span class="titre"></span>
      <span class="titre">Changement de mot de passe</span>
      ${this.displayErrors()}
      <div class="${classMap({ champ: true, readonly: true, error: false})}">
        <label for="emailReinitFieldFor">Email</label>
        <input id="emailReinitFieldId" type="text" required aria-required="true" .value="${this._email}" readonly/>
      </div>
      <div class="${classMap({ champ: true, invalid: this._error && !this._newPassword })}">
        <label for="passwordReinitFieldIdFor">Nouveau mot de passe</label>
        <input id="passwordReinitFieldId" type="password" required aria-required="true" @input="${this.onChangePassword}"  .value="${this._newPassword}" autofocus/>
      </div>
      <button @click="${this.reinitPassword}">Réinitialiser mon mot de passe</button>
    </div>
    `
    }
}

customElements.define('pts-reinitpassword', ReinitPassword)
