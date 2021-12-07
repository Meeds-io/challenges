export function canAddChallenge() {
  return fetch(`${eXo.env.portal.context}/${eXo.env.portal.rest}/challenge/api/canAddChallenge`, {
    headers: {
      'Content-Type': 'application/json'
    },
    method: 'GET'
  }).then((resp) => {
    if (resp && resp.ok) {
      return resp.json();
    } else {
      throw new Error ('Server indicates an error while sending request');
    }
  });
}