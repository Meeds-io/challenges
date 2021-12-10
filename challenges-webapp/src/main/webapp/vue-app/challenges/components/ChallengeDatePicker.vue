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
    <div class="challengeEndDateCalender d-flex align-center">
      <i class="uiIconEndDate uiIconBlue"></i>
      <date-picker
        ref="challengeEndDate"
        v-model="endDate"
        :default-value="false"
        :placeholder="$t('challenges.label.endDate')"
        :min-value="minimumEndDate"
        class="flex-grow-1 my-auto"
        @input="emitEndDate(endDate)" />
    </div>
  </div>
</template>


<script>

export default {
  name: 'ChallengeDatePicker',
  data () {
    return {
      startDate: null,
      endDate: null,
    };
  },
  computed: {
    minimumStartDate() {
      return new Date();
    },
    maximumStartDate() {
      if (this.endDate){
        let date = new Date();
        date = new Date(this.endDate);
        date.setDate(date.getDate()- 1) ;
        return date;
      } else {
        return null;
      }
    },
    minimumEndDate() {
      let date = new Date();
      if (this.startDate){
        date = new Date(this.startDate);
      }
      date.setDate(date.getDate() + 1) ;
      return date;
    }

  },
  mounted() {
    $('.challengeDate').off('click').on('click', () => {
      this.$refs.challengeStartDate.menu = false;
      this.$refs.challengeEndDate.menu = false;
    });
  },
  methods: {
    emitStartDate(date) {
      this.$emit('startDateChanged',new Date(date));
    },
    emitEndDate(date) {
      this.$emit('endDateChanged',new Date(date));
    },
  }
};
</script>
