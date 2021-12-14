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
          v-model="isValid.title"
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
            v-model="challenge.managers"
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
        description: true }
    };
  },
  watch: {
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