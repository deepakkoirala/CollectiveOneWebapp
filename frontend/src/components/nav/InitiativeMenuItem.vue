<template lang="html">
  <div class="white-text">
    <div class="w3-row this-element" :class="{'selected': isSelected, 'dark-gray-selected': isSelected }">
      <div v-if="this.level > 0" class="space-col" :class="leftSpaceClass">
        x
      </div>
      <div class="w3-col s2" :class="bulletClass"
        @click="showSubinitiatives = !showSubinitiatives">

        <div v-if="hasSubinitiatives">
          <i v-if="!showSubinitiatives"class="fa fa-chevron-circle-right" aria-hidden="true" :style="{'color': color}"></i>
          <i v-else class="fa fa-chevron-circle-down" aria-hidden="true" :style="{'color': color}"></i>
        </div>
        <div v-else>
          <i class="fa fa-circle" aria-hidden="true" :style="{'color': color}"></i>
        </div>
      </div>
      <div class=""  @click="$emit('initiative-selected')">
        <router-link :to="{ name: 'Initiative', params: {'initiativeId': initiative.id } }"
          class="w3-col name-col" :class="nameSpaceClass" :style="nameColFontSize">
          <div class="w3-left name-link noselect">
            {{ initiative.meta.name }}
          </div>
        </router-link>
      </div>

      <div class="notification-div">
        <app-notifications-list
          :element="initiative"
          :isMainNav="true"
          contextType="INITIATIVE">
        </app-notifications-list>
      </div>

    </div>

    <div :class="{'slider-container': animatingTab}">
      <transition name="slideDownUp"
          @before-enter="animatingTab = true"
          @after-enter="animatingTab = false"
          @before-leave="animatingTab = true"
          @after-leave="animatingTab = false">

        <div class="sub-initiatives-container" v-if="showSubinitiatives" >
          <div class="w3-row" v-for="(subinitiative, ix) in initiative.subInitiatives">
            <app-initiative-menu-item
              class="sub-initiative-element" :initiative="subinitiative" :key="subinitiative.id"
              :coord="coord.concat([ix])"
              @initiative-selected="$emit('initiative-selected')">
            </app-initiative-menu-item>
          </div>
        </div>
      </transition>
    </div>
  </div>
</template>

<script>
import NotificationsList from '@/components/notifications/NotificationsList.vue'

export default {
  name: 'app-initiative-menu-item',

  components: {
    'app-notifications-list': NotificationsList
  },

  props: {
    initiative: {
      type: Object,
      default: () => {
        return {
          name: '',
          driver: '',
          subInitiatives: []
        }
      }
    },
    coord: {
      type: Array,
      default: () => { return [] }
    }
  },

  data () {
    return {
      showSubinitiatives: false,
      animatingTab: false
    }
  },

  computed: {
    color () {
      return this.initiative.meta.color
    },
    level () {
      return this.coord.length - 1
    },
    isSelected () {
      return this.initiative.id === this.$route.params.initiativeId
    },
    bulletClass () {
      if (this.hasSubinitiatives) {
        return {
          'bullet-class': true,
          'w3-button': true
        }
      } else {
        return {
          'bullet-class': true,
          'w3-padding': true,
          'l3-color': true,
          'line-element': true
        }
      }
    },
    leftSpaceClass () {
      if (this.level > 0) {
        var name = 's' + (this.level < 5 ? this.level : 5)
        var classObject = {}
        classObject[name] = true
        classObject['w3-col'] = true
        return classObject
      } else {
        return {}
      }
    },
    nameSpaceClass () {
      var leftSpace = this.level < 5 ? this.level : 5
      var name = 's' + (8 - leftSpace)
      var classObject = {}
      classObject[name] = true
      return classObject
    },
    nameColFontSize () {
      var fontsize = this.level < 5 ? 16 - this.level : 11
      return {'font-size': fontsize + 'px'}
    },
    hasSubinitiatives () {
      return this.initiative.subInitiatives.length > 0
    }
  },

  methods: {
    newSubInitiative () {
      this.$store.commit('showNewSubInitiativeModal', {
        show: true,
        parentId: this.initiative.id })
    },
    isLoggedAnAdmin () {
      return this.initiative.loggedMembership[0].role === 'ADMIN'
    }
  },

  mounted () {
    var thisCoord = this.$store.getters.initiativeCoordinate(this.initiative.id)
    var pageCoord = this.$store.getters.initiativeCoordinate(this.$route.params.initiativeId)

    if (pageCoord.length > 0 && thisCoord.length > 0) {
      if (pageCoord[this.level] === thisCoord[this.level]) {
        this.showSubinitiatives = true
      }
    }
  }
}
</script>

<style scoped>

.space-col {
  visibility: hidden;
}

.bullet-class {
  height: 100%;
}

.name-col {
  padding-left: 5px;
  padding-right: 5px;
  padding-top: 8px;
  padding-bottom: 5px;
}

.name-link {
  width: 100%;
  height: 100%;
  cursor: pointer;
}

.line-element {
  text-align: center;
}

.tooltip .tooltiptext {
  top: 80%;
  right: 70%;
}

.selected {
  font-weight: bold;
}

.notification-div {
  min-height: 1px;
  width: 30px;
  float: left;
  position: relative;
  top: 8px
}


</style>
