<template>
  <exo-drawer
    ref="announcementDrawer"
    id="announcementDrawer"
    class="challengeDrawer"
    :right="!$vuetify.rtl"
    @closed="close"
    eager>
    <template slot="title">
      <span class="pb-2"> {{ $t('challenges.label.announce') }} </span>
    </template>
    <template slot="content">
      <div class="pl-4 pr-4 mt-7 descriptionLabel">
        {{ $t('challenges.label.title') }}
      </div>
      <div class="pr-4 pl-4 pt-4 titleChallenge">
        {{ challenge && challenge.title }}
      </div>
      <hr class="ml-4 mr-4 separator">
      <div class="pl-4 pr-4 mt-7 descriptionLabel">
        {{ $t('challenges.label.assignedAchievement') }}
      </div>
      <div class="pl-4">
        <challenge-assignment
          ref="challengeAssignment"
          class="my-2"
          v-model="announcement.assigned" />
      </div>
      <div class="pl-4 pr-4 pt-9 py-4 my-2">
        <challenge-description
          ref="challengeDescription"
          v-model="announcement.description"
          :value="announcement.description" />
      </div>
      <div class="pl-4 pr-4 pt-4">
        {{ $t('challenges.label.audience') }}
      </div>
      <div v-if="space" class="pl-4 pr-4">
        <v-chip
          :title="space && space.displayName"
          color="primary"
          class="identitySuggesterItem mt-2">
          <v-avatar left>
            <v-img :src="space && space.avatarUrl" />
          </v-avatar>
          <span class="text-truncate">
            {{ space && space.displayName }}
          </span>
        </v-chip>
      </div>
    </template>
    <template slot="footer">
      <div class="d-flex mr-2">
        <v-spacer />
        <button
          class="ignore-vuetify-classes btn mx-1"
          @click="close">
          {{ $t('challenges.button.cancel') }}
        </button>
        <button
          class="ignore-vuetify-classes btn btn-primary">
          {{ $t('challenges.button.create') }}
        </button>
      </div>
    </template>
  </exo-drawer>
</template>

<script>
export default {
  props: {
    challenge: {
      type: Object,
      default: null
    }
  },
  data() {
    return {
      announcement: {}
    };
  },
  computed: {
    space() {
      return this.challenge && this.challenge.space;
    },
  },
  methods: {
    open() {
      this.$refs.challengeDescription.initCKEditor();
      this.$refs.announcementDrawer.open();
    },
  }
};
</script>
