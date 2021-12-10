<template>
  <div class="challengeDates">
    <div class="challengePlanDateCalender d-flex align-center">
      <i class="uiIconStartDate uiIconBlue"></i>
      <date-picker
        ref="challengeStartDate"
        v-model="startDate"
        :default-value="false"
        :placeholder="$t('challenges.label.startDate')"
        :max-value="maximumStartDate"
        :min-value="minimumStartDate"
        class="flex-grow-1 my-auto"
        @input="emitStartDate(startDate)" />
    </div>
    <div class="challengeDueDateCalender d-flex align-center">
      <i class="uiIconDueDate uiIconBlue"></i>
      <date-picker
        ref="challengeDueDate"
        v-model="dueDate"
        :default-value="false"
        :placeholder="$t('challenges.label.dueDate')"
        :min-value="minimumEndDate"
        class="flex-grow-1 my-auto"
        @input="emitDueDate(dueDate)" />
    </div>
  </div>
</template>


<script>

export default {
  name: 'ChallengeDatePicker',
  data () {
    return {
      startDate: null,
      dueDate: null,
      minimumEndDate: new Date( new Date(new Date()).setDate(new Date().getDate()+1)),
      minimumStartDate: new Date(),
      maximumStartDate: null,
    };
  },
  watch: {
    startDate() {
      this.minimumEndDate = this.startDate ?  new Date(new Date(this.startDate).setDate(new Date(this.startDate).getDate()+1)) : new Date( new Date(new Date()).setDate(new Date().getDate()+1)) ;
      this.minimumStartDate = this.startDate ? new Date( this.startDate.setDate(this.startDate.getDate()+1)) :  new Date();
    },
    dueDate() {
      this.maximumStartDate = this.dueDate ?new Date(new Date(this.dueDate).setDate(new Date(this.dueDate).getDate()-1)): new Date();
    }

  },
  mounted() {
    $('.challengeDate').off('click').on('click', () => {
      this.$refs.challengeStartDate.menu = false;
      this.$refs.challengeDueDate.menu = false;
    });
  },
  methods: {
    emitStartDate(date) {
      this.$emit('startDateChanged',new Date(date));
    },
    emitDueDate(date) {
      this.$emit('dueDateChanged',new Date(date));
    },
  }
};
</script>
