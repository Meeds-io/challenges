<template>
  <v-app id="ChallengeCard">
    <v-card
      class="mx-auto card cardOfChallenge"
      height="200"
      max-height="200"
      outlined>
      <v-list-item three-line>
        <v-list-item-content class="title">
          <div class="d-flex">
            <div class="status">
              <i class="uiIconStatus iconStatus"></i> End {{ getFormatDate(challenge.startDate) }}
            </div>
            <div class="edit">
              <v-menu
                v-if="challenge && challenge.canEdit"
                v-model="showMenu"
                offset-y>
                <template v-slot:activator="{ on }">
                  <v-btn
                    icon
                    class="ml-2"
                    v-on="on"
                    @blur="closeMenu()">
                    <v-icon>mdi-dots-vertical</v-icon>
                  </v-btn>
                </template>
                <v-list>
                  <v-list-item @mousedown="$event.preventDefault()">
                    <v-list-item-title>Edit challenge</v-list-item-title>
                  </v-list-item>
                </v-list>
              </v-menu>
            </div>
          </div>
          <v-list-item-subtitle class="pl-5 pr-5 mb-4 mt-1 subtitleChallenge">
            {{ challenge && challenge.title }}
          </v-list-item-subtitle>
          <v-list-item-subtitle class="pl-9 pr-9 descriptionChallenge">
            {{ challenge && challenge.description }}
          </v-list-item-subtitle>
        </v-list-item-content>
      </v-list-item>

      <v-card-actions />
    </v-card>
  </v-app>
</template>

<script>

export default {
  props: {
    challenge: {
      type: Object,
      default: null
    }
  },
  data: () => ({
    showMenu: false
  }),
  methods: {
    closeMenu() {
      this.showMenu= false;
    },
    getFormatDate(date){
      return date && date.split('T')[0];
    }
  }
};
</script>
