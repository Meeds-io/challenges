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
          v-model="announcement.assignee"
          @remove-user="removeAssignee"
          @add-user="addAssignee" />
      </div>
      <div class="pl-4 pr-4 pt-9 py-4 my-2">
        <challenge-description
          ref="challengeDescription"
          v-model="announcement.comment"
          :value="announcement.comment"
          @invalidDescription="invalidDescription($event)"
          @validDescription="validDescription($event)"
          @addDescription="addDescription($event)" />
      </div>
      <div
        class="
          pl-4
          pr-4
          pt-4">
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
          :disabled="!disabledSave"
          class="ignore-vuetify-classes btn btn-primary"
          @click="createAnnouncement">
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
    },
    listAssignee: {
      type: Array,
      default: () =>[],
    }
  },
  data() {
    return {
      announcement: { assignee: []},
      isValidDescription: {
        description: true },
    };
  },
  computed: {
    space() {
      return this.challenge && this.challenge.space;
    },
    disabledSave() {
      return this.announcement.assignee && this.announcement.assignee.length > 0 && this.isValidDescription.description ;
    },
  },
  methods: {
    open() {
      this.$refs.challengeDescription.initCKEditor();
      this.$refs.challengeAssignment.initAssignment(this.listAssignee);
      this.$refs.announcementDrawer.open();
    },
    close() {
      this.$refs.announcementDrawer.close();
    },
    removeAssignee(id) {
      const index = this.announcement.assignee.findIndex(assignee => {
        return assignee === id;
      });
      if (index >= 0) {
        this.$delete(this.announcement.assignee,index, 1);
      }
    },
    addAssignee(id) {
      const index = this.announcement.assignee.findIndex(assignee => {
        return assignee === id;
      });
      if (index < 0) {
        this.$set(this.announcement.assignee,this.announcement.assignee.length, id);
      }
    },
    addDescription(value) {
      if (value) {
        this.$set(this.announcement,'comment', value);
      }
    },
    invalidDescription() {
      this.$set(this.isValidDescription,'description', false);
    },
    validDescription() {
      this.$set(this.isValidDescription,'description', true);
    },
    createAnnouncement() {
      this.announcement.challengeId =  this.challenge.id;
      this.announcement.createdDate = new Date();
      this.$challengesServices.saveAnnouncement(this.announcement).then(() =>{
        this.$root.$emit('show-alert', {type: 'success',message: this.$t('challenges.announcementCreateSuccess')});
        this.close();
        this.challenge = {};
      })
        .catch(e => {
          this.$root.$emit('show-alert', {type: 'error',message: String(e)});
        });
    },
  }
};
</script>
