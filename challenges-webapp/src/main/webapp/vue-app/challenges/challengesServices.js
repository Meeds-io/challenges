export function canAddChallenge() {
  return fetch(`${eXo.env.portal.context}/${eXo.env.portal.rest}/challenge/api/canAddChallenge`, {
    headers: {
      'Content-Type': 'text/plain'
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

export function saveChallenge(challenge) {
  return fetch(`${eXo.env.portal.context}/${eXo.env.portal.rest}/challenge/api/addChallenge`, {
    method: 'POST',
    credentials: 'include',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify(challenge),
  }).then((resp) => {
    if (resp && resp.ok) {
      return resp.json();
    } else {
      throw new Error('Error saving challenge');
    }
  });
}

export function getAllChallengesByUser(offset, limit) {
  return fetch(`${eXo.env.portal.context}/${eXo.env.portal.rest}/challenge/api/allChallenge?offset=${offset || 0}&limit=${limit|| 10}`, {
    method: 'GET',
    credentials: 'include',
  }).then((resp) => {
    if (resp && resp.ok) {
      return resp.json();
    } else {
      throw new Error('Error when getting challenges');
    }
  });
}
