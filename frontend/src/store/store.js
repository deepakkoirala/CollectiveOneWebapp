import Vue from 'vue'
import Vuex from 'vuex'

import user from './modules/user'
import modals from './modules/modals'
import messages from './modules/messages'
import userElements from './modules/userElements'

Vue.use(Vuex)

export const store = new Vuex.Store({
  state: {},

  modules: {
    user,
    modals,
    messages,
    userElements
  }
})
