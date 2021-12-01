package org.exoplatform.challenges.service;

import org.exoplatform.challenges.model.Challenge;
import org.exoplatform.challenges.storage.ChallengeStorage;

public class ChallengeServiceImpl implements ChallengeService {

  private ChallengeStorage challengeStorage;

  public ChallengeServiceImpl(ChallengeStorage challengeStorage) {
    this.challengeStorage = challengeStorage;
  }

  @Override
  public Challenge createChallenge(Challenge challenge, String username) throws IllegalAccessException {
    if (challenge == null) {
      throw new IllegalArgumentException("Challenge is mandatory");
    }
    if (challenge.getId() != 0) {
      throw new IllegalArgumentException("challenge id must be equal to 0");
    }

    return challengeStorage.saveChallenge(challenge, username);
  }

  @Override
  public Challenge getChallengeById(long challengeId, String username) throws IllegalAccessException {
    if (challengeId <= 0) {
      throw new IllegalArgumentException("Challenge id has to be positive integer");
    }
    Challenge challenge = challengeStorage.getChallengeById(challengeId);
    if (challenge == null) {
      return null;
    }
    return challenge;
  }
}
