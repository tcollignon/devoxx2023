import { html, css, LitElement } from 'lit'

import '../user/Login.js'
import '../user/Register.js'
import './Application.js'
import '../user/ReinitPasswordRequest.js'
import '../reinitPassword/ReinitPassword.js'
import { UserData } from '../user/UserData.js'

export class Application extends LitElement {
  static get properties () {
    return {
    }
  }

  static get styles () {
    return css`
      div.beta {
        padding: 0.4rem;
        text-align: center;
        position: absolute;
        width: 10rem;
        z-index: 10;
        background-color: #f44336;
        color: white;
        text-transform: uppercase;
        font-family: var(--font);
        transform: rotate(-45deg);
        top: 30px;
        left: -40px;
        box-shadow: rgba(0, 0, 0, 0.25) 0px 2px 8px;
      }
    `
  }
  render () {
    const userData = new UserData()
    const queryParam = new URLSearchParams(window.location.search.substring(1))
    if (userData.isAuthenticated()) {
      return html `<pts-login logged="true"></pts-login>`
    } else if (queryParam.get('register')) {
      return html`
        <pts-register></pts-register>`
    } else if (queryParam.get('reinitPasswordRequest')) {
        return html`
        <pts-reinitpasswordrequest></pts-reinitpasswordrequest>`
    } else if (queryParam.get('reinitPassword')) {
        return html`
         <pts-reinitpassword></pts-reinitpassword>`
    }
      return html`<pts-login></pts-login>`
  }
}

customElements.define('pts-application', Application)
