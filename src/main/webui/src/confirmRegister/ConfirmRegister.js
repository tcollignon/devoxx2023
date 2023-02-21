import { html, css, LitElement } from 'lit'

import '@ui5/webcomponents/dist/Label.js'
import '@ui5/webcomponents/dist/Input.js'
import '@ui5/webcomponents/dist/Button.js'

export class ConfirmRegister extends LitElement {
    static get properties () {
        return {
            message: { type: String }
        }
    }

    static get styles () {
        return css`
    `
    }

    connectedCallback () {
        super.connectedCallback()
        this.confirmRegister()
    }

    confirmRegister () {
        const uri = decodeURI(window.location.pathname)
        let url = uri.split('confirmRegister')[1]
        fetch('/users/confirmRegister' + url, {
            method: 'GET',
            headers: new Headers({
                "Content-Type": 'application/json',
                "Accept": 'application/json'
            })
        }).then(response => {
                if (response.status === 200) {
                    this.message = 'REGISTRATION SUCCESS'
                    window.location.replace('/login?registrationSuccess=true')
                } else if (response.status === 401) {
                    this.message = 'UNAUTHORIZED'
                } else{
                    this.message = 'UNKNOWN ERROR'
                }
            }
        ).catch(error => {
            console.log(error)
        })
    }

    render () {
        return html`${this.message}`
    }
}

customElements.define('pts-confirmregister', ConfirmRegister)