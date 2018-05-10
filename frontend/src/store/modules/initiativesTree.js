import Vue from 'vue'

const state = {
  myInitiativesTree: [],
  loadingMyInitiatives: false,
  currentInitiativeTree: []
}

const findCoordinate = function (initiatives, id, coord) {
  /** warning coord is a reference! */
  for (var ix in initiatives) {
    var thisInitiative = initiatives[ix]
    var thisCoord = JSON.parse(JSON.stringify(coord))

    thisCoord.push(ix)

    if (thisInitiative.id === id) {
      /* update input reference */
      coord.length = 0
      thisCoord.forEach((e) => {
        coord.push(e)
      })
      return true
    }

    if (thisInitiative.subInitiatives.length > 0) {
      var found = findCoordinate(thisInitiative.subInitiatives, id, thisCoord)
      if (found) {
        /* update input reference */
        coord.length = 0
        thisCoord.forEach((e) => {
          coord.push(e)
        })
        return true
      }
    }
  }
  return false
}

const findInitiative = function (initiatives, id) {
  for (var ix in initiatives) {
    var thisInitiative = initiatives[ix]
    if (thisInitiative.id === id) {
      return thisInitiative
    }

    if (thisInitiative.subInitiatives.length > 0) {
      var foundInitiative = findInitiative(thisInitiative.subInitiatives, id)
      if (foundInitiative !== null) {
        return foundInitiative
      }
    }
  }
  return null
}

const getters = {
  initiativesTree: (state, getters, rootState) => (id) => {
    return rootState.user.authenticated ? state.myInitiativesTree : state.currentInitiativeTree
  },
  initiativesTreeTop: (state, getters, rootState) => (id) => {
    if (!rootState.user.authenticated) {
      return []
    } else {
      var topInitiatives = []
      for (var ix in state.myInitiativesTree) {
        topInitiatives.push(state.myInitiativesTree[ix])
      }
      return topInitiatives
    }
  },
  initiativeCoordinate: (state, getters) => (id) => {
    var coord = []
    findCoordinate(getters.initiativesTree(), id, coord)
    return coord
  },
  colorOfInitiative: (state, getters) => (id) => {
    var initiative = findInitiative(getters.initiativesTree(), id)
    if (initiative) {
      return initiative.meta.color
    } else {
      return '#637484'
    }
  }
}

const mutations = {
  setLoadingMyInitiatives: (state, payload) => {
    state.loadingMyInitiatives = payload
  },
  setContentAnimationType: (state, payload) => {
    state.contentAnimationType = payload
  },
  setMyInitiativesTree: (state, payload) => {
    state.myInitiativesTree = payload
  },
  setCurrentInitiativeTree: (state, payload) => {
    state.currentInitiativeTree = payload
  }
}

const actions = {
  updateMyInitiatives: (context) => {
    context.commit('setLoadingMyInitiatives', true)
    Vue.axios.get('/1/initiatives/mines').then((response) => {
      context.commit('setMyInitiativesTree', response.data.data)
      context.commit('setLoadingMyInitiatives', false)
    })
  },
  updateCurrentInitiativeTree: (context, initiativeId) => {
    Vue.axios.get('/1/initiative/' + initiativeId, {
      params: {
        addSubinitiatives: true
      }
    }).then((response) => {
      context.commit('setCurrentInitiativeTree', [response.data.data])
    })
  }
}

export default {
  state,
  getters,
  mutations,
  actions
}
