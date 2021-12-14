<template>
  <v-app
    class="challenges-application border-box-sizing"
    :class="classWelcomeMessage"
    role="main"
    flat>
    <v-toolbar
      color="transparent"
      flat
      class="pa-4">
      <div class="border-box-sizing clickable addChallengeButton" v-if="canAddChallenge">
        <v-btn class="btn btn-primary" @click="openChallengeDrawer">
          <i class="fas fa-plus pr-1"></i>
          <span class="ms-2 d-none d-lg-inline">
            {{ $t('challenges.button.addChallenge') }}
          </span>
        </v-btn>
      </div>
      <v-spacer />
    </v-toolbar>
    <template v-if="displayChallenges">
      <div class="pl-2 pt-5">
        <challenges-list :challenges="challenges" @edit-challenge="editChallenge($event)" />
      </div>
    </template>
    <template v-else>
      <welcome-message
        :can-add-challenge="canAddChallenge" />
    </template>
    <challenge-drawer
      ref="challengeDrawer"
      :challenge="selectedChallenge" />
    <v-alert
      v-model="alert"
      :type="type"
      class="walletAlert"
      dismissible>
      {{ message }}
    </v-alert>
  </v-app>
</template>
<script>
export default {
  data: () => ({
    canAddChallenge: false,
    challenges: [],
    selectedChallenge: {},
    displayChallenges: true,
    alert: false,
    type: '',
    message: '',
  }),
  computed: {
    classWelcomeMessage() {
      return !this.displayChallenges ? 'emptyChallenges': '';
    }
  },
  created() {
    this.$challengesServices.canAddChallenge().then(canAddChallenge => {
      this.canAddChallenge = canAddChallenge;
    });
    this.$challengesServices.getAllChallengesByUser().then(challenges => {
      this.challenges = challenges;
      this.displayChallenges = this.challenges && this.challenges.length > 0 ? true : false;
    });
    this.$root.$on('show-alert', message => {
      this.displayMessage(message);
    });
  },
  methods: {
    openChallengeDrawer(){
      this.$refs.challengeDrawer.open();
    },
    displayMessage(message) {
      this.message=message.message;
      this.type=message.type;
      this.alert = true;
      window.setTimeout(() => this.alert = false, 5000);
    },
    editChallenge(challenge) {
      this.selectedChallenge = challenge;
      this.$nextTick().then(() => this.openChallengeDrawer());
    },
  }
};
</script>