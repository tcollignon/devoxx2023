
const EMAIL = 'USER_LOGIN_PAGE_EMAIL'
const PASSWORD = 'USER_LOGIN_PAGE_PASSWORD'

export class UserLoginPageData {
    
  getEmail () {
    return localStorage.getItem(EMAIL)
  }

  getPassword () {
    return localStorage.getItem(PASSWORD)
  }
  
  setEmail (email) {
    localStorage.setItem(EMAIL, email)
  }

  setPassword (password) {
    localStorage.setItem(PASSWORD, password)
  }
  
  remove () {
    localStorage.removeItem(EMAIL)
    localStorage.removeItem(PASSWORD)
  }
}
