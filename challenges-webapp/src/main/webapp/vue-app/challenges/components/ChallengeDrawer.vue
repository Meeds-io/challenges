<template>
  <exo-drawer
    ref="challengeDrawer"
    class="challengeDrawer"
    :right="!$vuetify.rtl"
    @closed="close"
    eager>
    <template slot="title">
      <span class="pb-2"> {{ drawerTitle }} </span>
    </template>
    <template slot="content">
      <v-card-text>
        <v-form
          ref="form"
          v-model="valid"
          @submit="
            $event.preventDefault();
            $event.stopPropagation();
          ">
          <v-textarea
            v-model="challenge.title"
            :disabled="loading"
            :placeholder="$t('challenges.label.enterChallengeTitle') "
            :rules="[rules.length]"
            name="challengeTitle"
            class="pl-0 pt-0 challenge-title"
            auto-grow
            rows="1"
            row-height="13"
            required
            autofocus />
          <v-divider class="my-2" />
          <span class="subtitle-1"> {{ $t('challenges.label.audienceSpace') }}</span>
          <exo-identity-suggester
            ref="challengeSpaceSuggester"
            v-model="audience"
            :labels="spaceSuggesterLabels"
            :include-users="false"
            :width="220"
            name="challengeSpaceAutocomplete"
            include-spaces
            only-manager />

          <span class="subtitle-1"> {{ $t('challenges.label.challengeOwners') }}</span>
          <challenge-assignment
            :challenge="challenge"
            class="my-2"
            @remove-manager="removeManager"
            @add-manager="addManager" />
          <challenge-date-picker
            ref="challengeDatePicker"
            :challenge="challenge"
            class="challengeDates my-1"
            @startDateChanged="updateChallengeStartDate($event)"
            @endDateChanged="updateChallengeEndDate($event)" />

          <div class="challengeDescription py-4 my-1">
            <challenge-description
              ref="challengeDescription"
              :challenge="challenge"
              v-model="challenge.description"
              :value="challenge.description"
              @disableCreateButton="disableCreateButton($event)"
              @enableCreateButton="enableCreateButton($event)"
              @addChallengeDescription="addChallengeDescription($event)" />
          </div>
        </v-form>
      </v-card-text>
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
          @click="SaveChallenge">
          {{ $t('challenges.button.create') }}
        </button>
      </div>
    </template>
  </exo-drawer>
</template>

<script>
export default {
  name: 'ChallengeDrawer',
  props: {
    challenge: {
      type: Object,
      default: function() {
        return {};
      },
    },
  },
  computed: {
    drawerTitle(){
      return this.challenge && this.challenge.id ? this.$t('challenges.button.editChallenge') : this.$t('challenges.button.addChallenge') ;
    },
    spaceSuggesterLabels() {
      return {
        searchPlaceholder: this.$t('challenges.spaces.noDataLabel'),
        placeholder: this.$t('challenges.spaces.placeholder'),
      };
    },
  },
  data() {
    return {
      rules: {
        length: (v) => (v && v.length < 250) || this.$t('challenges.label.challengeTitleLengthExceed') ,
      },
      audience: '' ,
      disabledSave: false,
      validDescription: true,
      valid: true,
    };
  },
  watch: {
    valid(){
      this.checkEnableSaveChallenge();
    },
    audience() {
      if (this.audience) {
        this.$spaceService.getSpaceMembers(null, 0, 0, null,'manager', this.audience.spaceId).then(managers => {
          this.challenge.managers = [];
          const listManagers = [];
          managers.users.forEach(manager => {
            const newManager= {
              id: manager.id,
              remoteId: manager.username,
              fullName: manager.fullname,
              avatarUrl: manager.avatar,
            };
            this.challenge.managers.push(newManager.id);
            listManagers.push(newManager);
          });
          this.challenge.audience = this.audience.spaceId;
          const data = {
            managers: listManagers,
            space: this.audience,
          };
          document.dispatchEvent(new CustomEvent('audienceChanged', {detail: data}));
          this.checkEnableSaveChallenge();
        });
      } else {
        this.challenge.managers= [];
        document.dispatchEvent(new CustomEvent('audienceChanged'));
      }
    },
  },
  methods: {
    init(){
      this.challenge = {};
      this.$refs.challengeDatePicker.startDate = null;
      this.$refs.challengeDatePicker.endDate = null;
      this.$refs.challengeDescription.inputVal = null;
      this.audience = '';
    },
    open(){
      this.init();
      this.$refs.challengeDescription.initCKEditor();
      this.$refs.challengeDrawer.open();
    },
    close(){
      this.$refs.challengeDescription.deleteDescription();
      this.$refs.challengeDrawer.close();
    },
    removeManager(id) {
      const index = this.challenge.managers.findIndex(managerId => {
        return managerId === id;
      });
      if (index >= 0) {
        this.challenge.managers.splice(index, 1);
        this.checkEnableSaveChallenge();
      }
    },
    addManager(id) {
      const index = this.challenge.managers.findIndex(managerId => {
        return managerId === id;
      });
      if (index < 0) {
        this.challenge.managers.push(id);
        this.checkEnableSaveChallenge();
      }
    },

    updateChallengeStartDate(value) {
      if (value) {
        this.challenge.startDate = value;
        this.checkEnableSaveChallenge();
      }
    },
    updateChallengeEndDate(value) {
      if (value) {
        this.challenge.endDate = value;
        this.checkEnableSaveChallenge();
      }
    },
    addChallengeDescription(value) {
      if (value) {
        this.challenge.description = value;
        this.checkEnableSaveChallenge();
      }
    },
    checkEnableSaveChallenge() {
      this.disabledSave = this.valid && this.validDescription && this.challenge && this.challenge.title && this.challenge.audience && this.challenge.managers.length > 0 && this.challenge.startDate && this.challenge.endDate ;
    },
    disableCreateButton() {
      this.validDescription = false ;
      this.disabledSave = true ;
      this.checkEnableSaveChallenge();
    },
    enableCreateButton() {
      this.validDescription = true ;
      this.disabledSave = false ;
      this.checkEnableSaveChallenge();
    },
    SaveChallenge() {
      if (this.challenge.startDate > this.challenge.endDate){
        this.$root.$emit('show-alert', {type: 'error',message: this.$t('challenges.challengeDateError')});
        return;
      }
      this.$challengesServices.saveChallenge(this.challenge).then(() =>{
        this.$root.$emit('show-alert', {type: 'success',message: this.$t('challenges.challengeCreateSuccess')});
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