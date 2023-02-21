import { css, html, LitElement } from 'lit'

export class UploadFile extends LitElement {
  static get properties () {
    return {
      id: { type: String },
      accept: { type: String },
      multiple: { type: Boolean },
      libelle: { type: String }
    }
  }

  constructor () {
    super()

    this.accept = 'text/*'
    this.multiple = false
  }

  static get styles () {
    return css`
      :host {
        --upload-couleur-bordure: #939b9f;
        --upload-couleur-fond: #cfd8dc;
        --upload-couleur-police: #414141;

        box-sizing: border-box;
        position: relative;
      }

      :host(.dragover) {
        --upload-couleur-bordure: #1171ce;
        --upload-couleur-fond: #cfe7ff;
        --upload-couleur-police: #004385;
      }

      input {
        display: none;
      }

      div {
        display: flex;
        flex-direction: column;
        gap: 10px;
        padding: 0.6rem 1rem;
        border: 2px dashed var(--upload-couleur-bordure);
        border-radius: 0.6rem;
        background-color: var(--upload-couleur-fond);
        user-select: none;
        cursor: pointer;
      }

      div span {
        color: var(--upload-couleur-police);
        font-size: 14px;
      }

      div span.libelle {
        font-weight: bold;
        font-size: 18px;
      }

      div span.fichier {
        background-color: rgb(255, 255, 255, 0.7);
        border-radius: 0.6rem;
        padding: 0.4rem 0.2rem;
      }
    `
  }

  /**
   * Setter de value
   *
   * @param {string} value - la valeur à définir
   */
  set value (value) {
    this.shadowRoot.querySelector('input').value = value
    this.requestUpdate()
  }

  /**
   * @returns {string}
   */
  get value () {
    return this.shadowRoot.querySelector('input').value
  }

  /**
   * @returns {FileList}
   */
  get files () {
    return this.shadowRoot.querySelector('input').files
  }

  /** @override */
  connectedCallback () {
    super.connectedCallback()

    this.addEventListener('click', this.repliquerEvenementClick)
    this.addEventListener('dragover', this.evenementDragOver)
    this.addEventListener('dragleave', this.evenementDragLeave)
    this.addEventListener('drop', this.evenenementOnDrop)
  }

  /** @override */
  disconnectedCallback () {
    this.removeEventListener('click', this.repliquerEvenementClick)
    this.removeEventListener('dragover', this.evenementDragOver)
    this.removeEventListener('dragleave', this.evenementDragLeave)
    this.removeEventListener('drop', this.evenenementOnDrop)

    super.disconnectedCallback()
  }

  /** @override */
  render () {
    return html`
      <input type="file" accept="${this.accept}" ?multiple=${this.multiple} @change=${this.declencherOnChange}/>
      <div>
        ${this.libelle ? html`<span class="libelle">${this.libelle}</span>` : ''}
        <span>Glissez ou cliquez sur la zone pour charger un fichier</span>
        ${this.recupererNomsFichiers().map((nom) => html`<span class="fichier">${nom}</span>`)}
      </div>
    `
  }

  /**
   * Retourne le nom des fichiers choisi.
   * @returns {string[]}
   */
  recupererNomsFichiers () {
    const fichiers = this.shadowRoot.querySelector('input')?.files

    return fichiers ? [...fichiers].map(({ name }) => name) : []
  }

  /**
   * Déclenche un change à partir du composant
   */
  declencherOnChange () {
    this.requestUpdate()
    this.dispatchEvent(new Event('change', { bubbles: true }))
  }

  /**
   * Permet de déclencher l'événement "click" sur l'input.
   * @param {MouseEvent} evenement - l'événement émis par l'utilisateur
   */
  repliquerEvenementClick (evenement) {
    this.shadowRoot.querySelector('input').click(evenement)
  }

  /**
   * Actions réalisées lors d'un événement "dragover".
   * @param {DragEvent} evenement - l'événement émis quand un élément entre dans la zone
   */
  evenementDragOver (evenement) {
    evenement.preventDefault()

    this.classList.add('dragover')
  }

  /**
   * Actions réalisées lors d'un événement "dragleave".
   * @param {DragEvent} evenement - l'événement émis quand un élément quitte la zone
   */
  evenementDragLeave (evenement) {
    evenement.preventDefault()

    this.classList.remove('dragover')
  }

  /**
   * Actions réalisées lors d'un événement "drop".
   * @param {DragEvent} evenement - l'événement émis quand un élément est laché dans la zone
   */
  evenenementOnDrop (evenement) {
    evenement.preventDefault()

    this.classList.remove('dragover')

    if (evenement.dataTransfer.files) {
      this.shadowRoot.querySelector('input').files = evenement.dataTransfer.files
      this.declencherOnChange()
      this.requestUpdate()
    }
  }
}

customElements.define('pts-upload-file', UploadFile)
