<template>
  <div class="assigneeParent">
    <v-menu
      id="assigneeMenu"
      v-model="globalMenu"
      :content-class="menuId"
      :close-on-content-click="false"
      :nudge-left="0"
      :max-width="300"
      attach
      transition="scale-transition"
      class=""
      offset-y
      allow-overflow
      eager
      bottom>
      <template v-slot:activator="{ on }">
        <div class="d-flex align-center challengeAssignItem" v-on="on">
          <div class="assigneeName">
            <v-chip
              close
              v-for="user in challengeAssigneeObj"
              :key="user"
              class="identitySuggesterItem mx-1 mt-2"
              @click:close="removeManager(user)">
              <v-avatar left>
                <v-img :src="user.avatarUrl" />
              </v-avatar>
              <span class="text-truncate">
                {{ user.fullName }}
              </span>
            </v-chip>
          </div>
          <a
            class="challengeAssignBtn mt-1 align-end">
            <i class="uiIcon uiAddAssignIcon"></i>
            <span class="text-decoration-underline">{{ $t('challenges.label.assign') }}</span>
          </a>
        </div>
      </template>
      <v-card class="pb-4 assignChallengeMenu">
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
          :ignore-items="challengeAssigneeObj"
          :width="220"
          name="challengeSpaceAutocomplete"
          include-users
          only-manager />
      </v-card>
    </v-menu>
  </div>
</template>
<script>
export default {
  name: 'ChallengeAssignment',
  props: {
    challenge: {
      type: Object,
      default: function() {
        return {};
      },
    },
    globalMenu: {
      type: Boolean,
      default: false,
    },
  },
  data() {
    return {
      challengeAssigneeObj: [],
      invitedChallengeAssignee: [],
      currentUser: eXo.env.portal.userName,
      menu: false,
      space: {},
      menuId: `AssigneeMenu${parseInt(Math.random() * 10000).toString()}`,

    };
  },
  computed: {
    searchOptions() {
      if (this.challengeAssigneeObj && this.challengeAssigneeObj.length >0) {
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
  },
  watch: {
    invitedChallengeAssignee() {
      const found = this.challengeAssigneeObj.find(attendee => {
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
          this.challengeAssigneeObj.push(newManager);
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
    this.challengeAssigneeObj = [];
    this.invitedChallengeAssignee = [];
    document.addEventListener('audienceChanged', event => {
      if (event && event.detail) {
        this.challengeAssigneeObj = event.detail.managers;
        this.spaceId = event.detail.space;
      } else {
        this.challengeAssigneeObj = [];
        this.space = {};
      }
    });
  },
  methods: {
    assignToMe() {
      if (!this.isAssigned(this.currentUser)){
        this.$identityService.getIdentityByProviderIdAndRemoteId('organization',this.currentUser).then(user => {
          const newManager= {
            id: user.profile.id,
            remoteId: user.profile.username,
            fullName: user.profile.fullname,
            avatarUrl: user.profile.avatar,
          };
          this.challengeAssigneeObj.push(newManager);
          this.$emit('add-manager',newManager.id);
          this.globalMenu = false;
        });
      }
    },
    isAssigned( username) {
      return this.challengeAssigneeObj && this.challengeAssigneeObj.findIndex(manager => {
        return manager.remoteId === username;
      }) >= 0 ? true : false;
    },
    removeManager( user) {
      const index =  this.challengeAssigneeObj.findIndex(manager => {
        return manager.remoteId === user.remoteId;
      });
      this.challengeAssigneeObj.splice(index, 1);
      this.$emit('remove-manager', user.id);
    },
  }
};
</script>
