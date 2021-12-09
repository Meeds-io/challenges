import Challenges from './components/Challenges.vue';
import AddChallenge from './components/AddChallenge.vue';
import WelcomeMessage from './components/WelcomeMessage.vue';
import ChallengeCard from './components/ChallengeCard.vue';
import ChallengesList from './components/ChallengesList.vue';
const components = {
  'challenges': Challenges,
  'add-challenge': AddChallenge,
  'welcome-message': WelcomeMessage,
  'challenge-card': ChallengeCard,
  'challenges-list': ChallengesList
};

for (const key in components) {
  Vue.component(key, components[key]);
}
import * as  challengesServices from './challengesServices';

if (!Vue.prototype.$challengesServices) {
  window.Object.defineProperty(Vue.prototype, '$challengesServices', {
    value: challengesServices,
  });
}
