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
        <challenges-list :challenges="challenges" />
      </div>
    </template>
    <template v-else>
      <welcome-message
        :can-add-challenge="canAddChallenge" />
    </template>
    <challenge-drawer
      ref="challengeDrawer" />
  </v-app>
</template>
<script>
export default {
  data: () => ({
    canAddChallenge: false,
    challenges: [],
    displayChallenges: true
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
  },
  methods: {
    openChallengeDrawer(){
      this.$refs.challengeDrawer.open();
    },
  }
};
</script>