import { css, html, LitElement } from 'lit'

export const POPUP_FLOW = {
  column: css`column`,
  row: css`row`
}

export class Popup extends LitElement {
  static get properties () {
    return {
      headerText: { type: String },
      visible: { type: Boolean, reflect: true },
      closable: { type: Boolean }
    }
  }

  constructor () {
    super()
    this.closable = true
    this.flow = css`column`
  }
  
  connectedCallback() {
    super.connectedCallback();
    window.addEventListener('keydown', this.typeKeyOnKeyboard.bind(this))
  }

  static get styles () {
    return css`
      :host {
        --popup-max-width-container: 400px;
        --popup-max-height-container: 400px;
        --popup-flow: column;

        display: none;
        position: fixed;
        top: 0;
        bottom: 0;
        left: 0;
        right: 0;
        background-color: rgba(0, 0, 0, 0.4);
        align-items: center;
        justify-content: center;
        z-index: 10000;
      }

      :host([visible]) {
        display: flex;
      }

      div.container {
        max-width: var(--popup-max-width-container);
        max-height: var(--popup-max-height-container);
        background-color: var(--color-3);
        border-radius: 0.6rem;
        border-radius: 0.6rem;
        padding: 2rem;
        display: grid;
        grid-template-columns: 100%;
        grid-template-rows: auto minmax(1px, 1fr) auto;
        gap: 1rem;
        box-shadow: rgba(0, 0, 0, 0.25) 0px 2px 8px;
        position: relative;
      }

      div.header {
        text-align: center;
        font-family: var(--font);
        font-size: 1.6rem;
        font-weight: bold;
      }

      div.header span.close {
        --size-close-popup-icon: 14px;

        position: absolute;
        right: 1rem;
        top: 1rem;
        display: inline-block;
        width: var(--size-close-popup-icon);
        height: var(--size-close-popup-icon);
        background: url('./img/close.svg');
        background-size: contain;
        cursor: pointer;
      }

      ::slotted([slot=text]) {
        font-family: var(--font);
        text-align: center;
        min-height: 1px;
        margin: 0 auto;
      }

      div.content {
        display: flex;
        flex-direction: var(--popup-flow);
        gap: 1rem;
      }

      div.buttons {
        display: flex;
        flex-direction: column;
        gap: 0.6rem;
      }
    `
  }

  render () {
    return html`
      <div class="container">
        <div class="header">
          <span>${this.headerText}</span>
          ${this.closable ? html`<span class="close" @click=${this.close}></span>` : ''}
        </div>
        <div class="content">
          <slot name="text"></slot>
        </div>
        <div class="buttons">
          <slot name="button"></slot>
        </div>
      </div>
    `
  }

  typeKeyOnKeyboard (event) {
    if (event.key === 'Escape') {
        if (this.closable) {
            this.close()
        }
    }
  }

  show () {
    this.visible = true
  }

  close () {
    this.visible = false
  }
}

customElements.define('pts-popup', Popup)
