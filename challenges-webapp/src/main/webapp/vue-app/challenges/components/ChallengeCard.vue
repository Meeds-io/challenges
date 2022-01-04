<template>
  <v-app id="ChallengeCard">
    <v-card
      class="mx-auto card cardOfChallenge"
      height="230"
      max-height="230"
      outlined>
      <div class="contentCard">
        <v-list-item class="pa-0" three-line>
          <v-list-item-content class="title pl-4 pr-4 pt-3">
            <div class="d-flex">
              <div class="status">
                <i class="uiIconStatus iconStatus" :class="classStatus"></i> <span class="date">{{ getStatus() }}</span>
              </div>
              <div class="edit">
                <v-menu
                  v-if="enableEdit"
                  v-model="showMenu"
                  offset-y
                  attach
                  :left="!$vuetify.rtl"
                  :right="$vuetify.rtl">
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
                    <v-list-item class="editList" @mousedown="$event.preventDefault()">
                      <v-list-item-title class="editLabel" @click="$emit('edit', challenge)">{{ $t('challenges.edit') }}</v-list-item-title>
                    </v-list-item>
                  </v-list>
                </v-menu>
              </div>
            </div>
            <div class="contentChallenge" @click="showDetails">
              <v-list-item-subtitle class="pl-5 pr-5 mb-4 mt-1 subtitleChallenge">
                {{ challenge && challenge.title }}
              </v-list-item-subtitle>
              <v-list-item-subtitle class="pl-9 pr-9 descriptionChallenge" v-html="challenge && challenge.description" />
            </div>
          </v-list-item-content>
        </v-list-item>
      </div>

      <div class="footer d-flex">
        <div class="winners">
        </div>
        <div class="addAnnounce">
          <button
            class="btnAdd ignore-vuetify-classes btn mx-1"
            :disabled="!enableAnnounce"
            @click="createAnnounce">
            {{ $t('challenges.button.announce') }}
          </button>
        </div>
      </div>
    </v-card>
    <challenge-details-drawer :challenge="challenge" ref="challenge" />
    <announce-drawer :challenge="challenge" ref="announceRef" />
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
    },
    enableAnnounce(){
      return this.challenge && this.challenge.userInfo.canAnnounce && this.status !== 'Ended' && this.status !== 'Starts';
    },
    enableEdit(){
      return this.challenge && this.challenge.userInfo.canEdit && this.status !== 'Ended';
    },
  },
  methods: {
    createAnnounce() {
      this.$refs.announceRef.open();
    },
    closeMenu() {
      this.showMenu= false;
    },
    showDetails() {
      if (this.$refs.challenge){
        this.$refs.challenge.open();
      }
    },
    getStatus() {
      const currentDate = new Date();
      const startDate = new Date(this.challenge && this.challenge.startDate);
      const endDate = new Date(this.challenge && this.challenge.endDate);
      if (startDate.getTime() > currentDate.getTime() && endDate.getTime() > currentDate.getTime()) {
        this.status = 'Starts';
        this.label=this.$t('challenges.status.starts');
        return `${this.label } ${ this.formattedDate(new Date(this.challenge.startDate)) }`;
      } else if (startDate.getTime()<currentDate.getTime() && endDate.getTime() > currentDate.getTime()) {
        this.status = 'Ends';
        this.label=this.$t('challenges.status.ends');
        return `${this.label } ${ this.formattedDate(new Date(this.challenge.endDate))}`;
      } else if (endDate.getTime() < currentDate.getTime() && startDate.getTime()< currentDate.getTime()) {
        this.status = 'Ended';
        this.label=this.$t('challenges.status.ended');
        return this.label;
      }
    }, formattedDate(d) {
      let month = String(d.getMonth() + 1);
      let day = String(d.getDate());
      const year = String(d.getFullYear());

      if (month.length < 2) {month = `0${  month}`;}
      if (day.length < 2) {day = `0${  day}`;}

      return `${day}/${month}/${year}`;
    }
  }
};
</script>
