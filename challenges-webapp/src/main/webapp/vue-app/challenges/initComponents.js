import Challenges from './components/Challenges.vue';

const components = {
  'challenges': Challenges,
};

for (const key in components) {
  Vue.component(key, components[key]);
}
