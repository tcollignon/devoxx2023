
const USER_NAME = 'USER_NAME'
const USER_NICKNAME = 'USER_NICKNAME'
const USER_BALANCE = 'USER_BALANCE'
const USER_ICON = 'USER_ICON'
const USER_DESCRIPTION = 'USER_DESCRIPTION'
const USER_INTERPOLATE_DESCRIPTION = 'USER_INTERPOLATE_DESCRIPTION'
const ROLES = 'ROLES'

export class UserData {

  isAdmin () {
    return this.getRoles() != undefined && this.getRoles().split(',').includes('admin')
  }

  isAuthenticated() {
    return this.getNickname() != undefined
  }

  //username = login = email
  getUserName () {
    return localStorage.getItem(USER_NAME)
  }

  getNickname () {
    return localStorage.getItem(USER_NICKNAME)
  }

  getRoles () {
    return localStorage.getItem(ROLES)
  }

  getUserIcon () {
    return localStorage.getItem(USER_ICON)
  }

  getDescription () {
    return localStorage.getItem(USER_DESCRIPTION)
  }

  getInterpolateDescription () {
    return localStorage.getItem(USER_INTERPOLATE_DESCRIPTION)
  }

  setUserName (userName) {
    localStorage.setItem(USER_NAME, userName)
  }

  setNickname (nickname) {
    localStorage.setItem(USER_NICKNAME, nickname)
  }

  setRoles (roles) {
    localStorage.setItem(ROLES, roles)
  }

  setUserIcon (icon) {
    localStorage.setItem(USER_ICON, icon)
  }

  setDescription (desc) {
    localStorage.setItem(USER_DESCRIPTION, desc)
  }

  setInterpolateDescription (desc) {
    localStorage.setItem(USER_INTERPOLATE_DESCRIPTION, desc)
  }

  remove () {
    localStorage.removeItem(USER_NAME)
    localStorage.removeItem(USER_NICKNAME)
    localStorage.removeItem(USER_BALANCE)
    localStorage.removeItem(USER_ICON)
    localStorage.removeItem(ROLES)
    localStorage.removeItem(USER_DESCRIPTION)
    localStorage.removeItem(USER_INTERPOLATE_DESCRIPTION)
  }
}
