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
    };
  },
  computed: {
    minimumEndDate() {
      if (!this.startDate) {
        return null;
      }
      return new Date(this.startDate);
    },
    maximumStartDate() {
      if (!this.dueDate) {
        return null;
      }
      return new Date(this.dueDate);
    },
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
