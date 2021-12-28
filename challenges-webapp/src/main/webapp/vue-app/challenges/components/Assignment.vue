<template>
  <div class="challengeAssignItem">
    <v-menu
      id="assigneeMenu"
      v-model="globalMenu"
      :content-class="menuId"
      :close-on-content-click="false"
      :nudge-left="0"
      :max-width="300"
      attach
      transition="scale-transition"
      offset-y
      allow-overflow
      eager
      bottom
      :disabled="disabledUnAssign">
      <template v-slot:activator="{ on }">
        <div class="d-flex align-" v-on="on">
          <a
            :disabled="disabledUnAssign"
            :class="assignButtonClass"
            class="challengeAssignBtn align-end">
            <i class="uiIcon uiAddAssignIcon"></i>
            <span class="text-decoration-underline">{{ $t('challenges.label.assign') }}</span>
          </a>
        </div>
      </template>
      <v-card class="pb-4 assignChallengeMenu pa-4">
        <v-card-text class="pb-0 d-flex justify-space-between">
          <span>{{ $t('challenges.label.assignTo') }} :</span>
          <a class="ml-4" @click="assignToMe()">
            <i class="uiIcon uiAssignToMeIcon"></i>
            <span>{{ $t('challenges.label.addMe') }}</span>
          </a>
        </v-card-text>

        <exo-identity-suggester
          ref="challengeSpaceSuggester"
          v-model="invitedChallengeAssignee"
          :labels="spaceSuggesterLabels"
          :search-options="searchOptions"
          :ignore-items="assigneeObj"
          :width="220"
          name="challengeSpaceAutocomplete"
          include-users
          only-manager />
      </v-card>
    </v-menu>
    <div class="assigneeName">
      <v-chip
        :close="!disabledUnAssign"
        v-for="user in assigneeObj"
        :key="user"
        class="identitySuggesterItem mx-1 mb-2"
        @click:close="removeUser(user)">
        <v-avatar left>
          <v-img :src="user.avatarUrl" />
        </v-avatar>
        <span class="text-truncate">
          {{ user.fullName }}
        </span>
      </v-chip>
    </div>
  </div>
</template>
<script>
export default {
  props: {
    globalMenu: {
      type: Boolean,
      default: false,
    },
  },
  data() {
    return {
      assigneeObj: [],
      invitedChallengeAssignee: [],
      currentUser: eXo.env.portal.userName,
      menu: false,
      space: {},
      menuId: `AssigneeMenu${parseInt(Math.random() * 10000)}`,
      disabledUnAssign: false,
    };
  },
  computed: {
    searchOptions() {
      if (this.assigneeObj && this.assigneeObj.length >0) {
        return {
          currentUser: '',
          spaceURL: this.space && this.space.remoteId
        };
      } else {
        return  {
          currentUser: '',
        };
      }
    },
    assignButtonClass(){
      return this.assigneeObj &&  this.assigneeObj.length && 'mt-2';
    }
  },
  watch: {
    invitedChallengeAssignee() {
      const found = this.assigneeObj.find(attendee => {
        return attendee.username === this.invitedChallengeAssignee.remoteId;
      });
      if (!found && this.invitedChallengeAssignee.remoteId) {
        this.$identityService.getIdentityByProviderIdAndRemoteId('organization',this.invitedChallengeAssignee.remoteId).then(user => {
          const newManager= {
            id: user.profile.id,
            remoteId: user.profile.username,
            fullName: user.profile.fullname,
            avatarUrl: user.profile.avatar,
          };
          this.assigneeObj.push(newManager);
          this.$emit('add-manager',newManager.id);
          this.invitedChallengeAssignee = null;
          this.globalMenu = false;
        });
      }
    },
  },
  mounted() {
    $(document).on('click', (e) => {
      if (e.target && !$(e.target).parents(`.${this.menuId}`).length && !$(e.target).parents('.v-autocomplete__content').length) {
        this.globalMenu = false;
      }
    });
  },
  created() {
    this.assigneeObj = [];
    this.invitedChallengeAssignee = [];
    document.addEventListener('audienceChanged', event => {
      if (event && event.detail) {
        this.assigneeObj = event.detail.managers;
        this.space = event.detail.space;
      } else {
        this.assigneeObj = [];
        this.space = {};
      }
    });
  },
  methods: {
    initAssignment(list) {
      this.assigneeObj = list;
    },
    assignToMe() {
      if (!this.isAssigned(this.currentUser)){
        this.$identityService.getIdentityByProviderIdAndRemoteId('organization',this.currentUser).then(user => {
          const newManager= {
            id: user.profile.id,
            remoteId: user.profile.username,
            fullName: user.profile.fullname,
            avatarUrl: user.profile.avatar,
          };
          this.assigneeObj.push(newManager);
          this.$emit('add-user',newManager.id);
          this.globalMenu = false;
        });
      }
    },
    isAssigned( username) {
      return this.assigneeObj && this.assigneeObj.findIndex(manager => {
        return manager.remoteId === username;
      }) >= 0 ? true : false;
    },
    removeUser(user) {
      const index =  this.assigneeObj.findIndex(manager => {
        return manager.remoteId === user.remoteId;
      });
      this.assigneeObj.splice(index, 1);
      this.$emit('remove-user', user.id);
    },
  }
};
</script>
