<template>
  <v-app
    class="challenges-application border-box-sizing"
    role="main"
    flat>
    <v-toolbar
      color="transparent"
      flat
      class="pa-4">
      <add-challenge v-if="canAddChallenge" />
      <v-spacer />
    </v-toolbar>
    <template>
      <welcome-message
        v-if="!displayChallenges"
        :can-add-challenge="canAddChallenge" />
    </template>
  </v-app>
</template>
<script>
export default {
  data: () => ({
    canAddChallenge: false,
    challenges: [],
  }),
  computed: {
    displayChallenges() {
      return this.challenges && this.challenges.length > 0 ;
    }
  },
  created() {
    this.$challengesServices.canAddChallenge().then(canAddChallenge => {
      this.canAddChallenge = canAddChallenge;
    });
  }
};
</script>