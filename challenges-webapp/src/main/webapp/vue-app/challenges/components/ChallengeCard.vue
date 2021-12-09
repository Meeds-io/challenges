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
              <i class="uiIconStatus iconStatus" :class="classStatus"></i> {{ getStatus() }}
            </div>
            <div class="edit">
              <v-menu
                v-if="challenge && challenge.canEdit"
                v-model="showMenu"
                offset-y
                attach
                @blur="closeMenu()"
                left>
                <template v-slot:activator="{ on }">
                  <v-btn
                    icon
                    class="ml-2"
                    v-on="on">
                    <v-icon>mdi-dots-vertical</v-icon>
                  </v-btn>
                </template>
                <v-list>
                  <v-list-item @mousedown="$event.preventDefault()">
                    <v-list-item-title>{{ $t('challenges.edit') }}</v-list-item-title>
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
    showMenu: false,
    label: '',
    status: ''
  }),
  computed: {
    classStatus() {
      if (this.status === 'Starts') {
        return 'startsColor';
      } else if (this.status === 'Ended') {
        return 'endedColor';
      } else {
        return 'endsColor';
      }
    }
  },
  methods: {
    closeMenu() {
      this.showMenu= false;
    },
    getStatus() {
      const currentDate = new Date();
      const startDate = new Date(this.challenge && this.challenge.startDate);
      const endDate = new Date(this.challenge && this.challenge.endDate);
      if (startDate.getTime() > currentDate.getTime() && endDate.getTime() > currentDate.getTime()) {
        this.status = 'Starts';
        this.label=this.$t('challenges.status.starts');
        return `${this.label } ${ this.challenge.startDate.split('T')[0]}`;
      } else if (startDate.getTime()<currentDate.getTime() && endDate.getTime() > currentDate.getTime()) {
        this.status = 'Ends';
        this.label=this.$t('challenges.status.ends');
        return `${this.label } ${ this.challenge.endDate.split('T')[0]}`;
      } else if (endDate.getTime() < currentDate.getTime() && startDate.getTime()< currentDate.getTime()) {
        this.status = 'Ended';
        this.label=this.$t('challenges.status.ended');
        return this.label;
      }
    }
  }
};
</script>
