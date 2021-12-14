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
        <div
          v-if="warning"
          class="alert alert-error v-content mb-4">
          <i class="uiIconError"></i>
          {{ warning }}
        </div>
        <v-form
          ref="form"
          v-model="isValid.title"
          @submit="
            $event.preventDefault();
            $event.stopPropagation();
          ">
          <v-textarea
            v-model="challenge.title"
            :disabled="disabledTitleEdit"
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
            :disabled="disabledSuggester"
            name="challengeSpaceAutocomplete"
            include-spaces
            only-manager />

          <span class="subtitle-1"> {{ $t('challenges.label.challengeOwners') }}</span>
          <challenge-assignment
            ref="challengeAssignment"
            :challenge="challenge"
            class="my-2"
            v-model="challenge.managers"
            @remove-manager="removeManager"
            @add-manager="addManager" />

          <challenge-date-picker
            ref="challengeDatePicker"
            :challenge="challenge"
            class="challengeDates my-2"
            @startDateChanged="updateChallengeStartDate($event)"
            @endDateChanged="updateChallengeEndDate($event)" />

          <div class="challengeDescription py-4 my-2">
            <challenge-description
              ref="challengeDescription"
              :challenge="challenge"
              v-model="challenge.description"
              :value="challenge.description"
              @invalidDescription="invalidDescription($event)"
              @validDescription="validDescription($event)"
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
          v-if="this.challenge && this.challenge.id"
          :disabled="disabledUpdate"
          class="ignore-vuetify-classes btn btn-primary"
          @click="updateChallenge">
          {{ $t('challenges.button.save') }}
        </button>
        <button
          v-else
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
    disabledSave() {
      return this.challenge && this.challenge.title && this.challenge.audience && this.challenge.managers.length > 0 && this.challenge.startDate && this.challenge.endDate && this.isValid.title && this.isValid.description;
    },
  },
  data() {
    return {
      rules: {
        length: (v) => (v && v.length < 250) || this.$t('challenges.label.challengeTitleLengthExceed') ,
      },
      audience: '' ,
      isValid: {
        title: true,
        description: true },
      disabledSuggester: false,
      disabledTitleEdit: false,
      warning: null,
      disabledUpdate: false,
    };
  },
  watch: {
    audience() {
      if ( this.audience && this.audience.id && !this.audience.notToChange) {
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
            this.$set(this.challenge.managers,this.challenge.managers.length, newManager.id);
            listManagers.push(newManager);
          });
          this.$set(this.challenge,'audience', this.audience.spaceId);
          const data = {
            managers: listManagers,
            space: this.audience,
          };
          document.dispatchEvent(new CustomEvent('audienceChanged', {detail: data}));
        });
      } else if (this.audience && this.audience.id && this.audience.notToChange){
        return;
      } else {
        this.challenge.managers= [];
        document.dispatchEvent(new CustomEvent('audienceChanged'));
      }
    },
  },
  methods: {
    setUp(){
      const space= this.challenge.space ;
      const NewAudience = {
        id: `space:${ space.displayName }` ,
        profile: {
          avatarUrl: space.avatarUrl,
          fullName: space.displayName,
        },
        providerId: 'space',
        remoteId: space.displayName,
        spaceId: this.challenge.space.id,
        notToChange: true,
      };
      const status= this.getChallengeStatus();
      if (status === 'STARTED'){
        this.$refs.challengeDatePicker.startDate = new Date(this.challenge.startDate);
        this.$refs.challengeDatePicker.endDate = this.challenge.endDate;
        this.$refs.challengeDatePicker.disabledStartDate = true;
        this.$refs.challengeSpaceSuggester.emitSelectedValue(NewAudience);
        this.$refs.challengeAssignment.disabledUnAssign = true;
        this.disabledSuggester = true ;
      } else if (status === 'ENDED'){
        this.$refs.challengeDatePicker.startDate = new Date(this.challenge.startDate);
        this.$refs.challengeDatePicker.endDate = new Date(this.challenge.endDate);
        this.$refs.challengeDatePicker.disabledStartDate = true;
        this.$refs.challengeDatePicker.disabledEndDate = true;
        this.$refs.challengeSpaceSuggester.emitSelectedValue(NewAudience);
        this.$refs.challengeSpaceSuggester.disabledUnAssign = true;
        this.disabledSuggester = true ;
        this.disabledTitleEdit = true ;
        this.disabledUpdate = true ;
        this.$refs.challengeAssignment.disabledUnAssign = true;
        this.$refs.challengeDescription.disabled = true;
        this.warning = this.$t('challenges.warn.challengeEnded');
      } else {
        this.$refs.challengeDatePicker.startDate = this.challenge.startDate;
        this.$refs.challengeDatePicker.endDate = this.challenge.endDate;
        this.$refs.challengeSpaceSuggester.emitSelectedValue(NewAudience);
      }
      const data = {
        managers: this.challenge.managers,
        space: space,
      };
      document.dispatchEvent(new CustomEvent('audienceChanged', {detail: data}));
      this.$refs.challengeAssignment.challengeAssigneeObj = this.challenge.managers;
      this.challenge.audience= space.id;
    },
    cleanUp(){
      this.challenge = {};
      this.$refs.challengeDatePicker.startDate = null;
      this.$refs.challengeDatePicker.endDate = null;
      this.$refs.challengeDescription.inputVal = null;
      this.$refs.challengeAssignment.challengeAssigneeObj = null;
      this.$refs.challengeSpaceSuggester.emitSelectedValue( {});
      this.$refs.challengeDatePicker.disabledStartDate = false;
      this.$refs.challengeDatePicker.disabledEndDate = false;
      this.$refs.challengeSpaceSuggester.disabledUnAssign = false;
      this.disabledSuggester = false ;
      this.disabledTitleEdit = false ;
      this.disabledUpdate = false ;
      this.$refs.challengeAssignment.disabledUnAssign = false;
      this.$refs.challengeDescription.disabled = false;
      this.warning= null;
    },
    open(){
      this.$refs.challengeDescription.initCKEditor();
      if (this.challenge && this.challenge.id){
        this.setUp();
      }
      this.$refs.challengeDrawer.open();
    },
    close(){
      this.cleanUp();
      this.$refs.challengeDescription.deleteDescription();
      this.$refs.challengeDrawer.close();
    },
    removeManager(id) {
      const index = this.challenge.managers.findIndex(managerId => {
        return managerId === id;
      });
      if (index >= 0) {
        this.challenge.managers.splice(index, 1);
      }
    },
    addManager(id) {
      const index = this.challenge.managers.findIndex(managerId => {
        return managerId === id;
      });
      if (index < 0) {
        this.challenge.managers.push(id);
      }
    },

    updateChallengeStartDate(value) {
      if (value) {
        this.$set(this.challenge,'startDate', value);
      }
    },
    updateChallengeEndDate(value) {
      if (value) {
        this.$set(this.challenge,'endDate', value);
      }
    },
    addChallengeDescription(value) {
      if (value) {
        this.$set(this.challenge,'description', value);
      }
    },
    invalidDescription() {
      this.$set(this.isValid,'description', false);
    },
    validDescription() {
      this.$set(this.isValid,'description', true);
    },
    getChallengeStatus() {
      const status = {
        NOTSTARTED: 'NOTSTARTED',
        STARTED: 'STARTED',
        ENDED: 'ENDED'
      };
      const currentDate = new Date();
      const startDate = new Date(this.challenge && this.challenge.startDate);
      const endDate = new Date(this.challenge && this.challenge.endDate);
      if (startDate.getTime() > currentDate.getTime() && endDate.getTime() > currentDate.getTime()) {
        return status.NOTSTARTED;
      } else if (startDate.getTime()<currentDate.getTime() && endDate.getTime() > currentDate.getTime()) {
        return status.STARTED;
      } else if (endDate.getTime() < currentDate.getTime() && startDate.getTime()< currentDate.getTime()) {
        return status.ENDED;
      }
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
    updateChallenge() {
      if (this.challenge.startDate > this.challenge.endDate){
        this.$root.$emit('show-alert', {type: 'error',message: this.$t('challenges.challengeDateError')});
        return;
      }
      this.$challengesServices.updateChallenge(this.challenge).then(() =>{
        this.$root.$emit('show-alert', {type: 'success',message: this.$t('challenges.challengeUpdateSuccess')});
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