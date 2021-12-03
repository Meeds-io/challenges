package org.exoplatform.challenges.service;

import org.apache.commons.lang3.StringUtils;
import org.exoplatform.challenges.model.Challenge;
import org.exoplatform.challenges.storage.ChallengeStorage;
import org.exoplatform.commons.exception.ObjectNotFoundException;
import org.exoplatform.social.core.space.model.Space;
import org.exoplatform.social.core.space.spi.SpaceService;

public class ChallengeServiceImpl implements ChallengeService {

  private ChallengeStorage challengeStorage;

  private SpaceService     spaceService;

  public ChallengeServiceImpl(ChallengeStorage challengeStorage, SpaceService spaceService) {
    this.challengeStorage = challengeStorage;
    this.spaceService = spaceService;
  }

  @Override
  public Challenge createChallenge(Challenge challenge, String username) throws IllegalAccessException {
    if (challenge == null) {
      throw new IllegalArgumentException("Challenge is mandatory");
    }
    String idSpace = String.valueOf(challenge.getAudience());
    if (StringUtils.isBlank(idSpace)) {
      throw new IllegalArgumentException("space id must not be null or empty");
    }
    Space space = spaceService.getSpaceById(idSpace);
    if (!spaceService.isManager(space, username)) {
      throw new IllegalAccessException("user is not allowed to create challenge");
    }
    return challengeStorage.saveChallenge(challenge, username);
  }

  @Override
  public Challenge updateChallenge(Challenge challenge, String username) throws IllegalAccessException, ObjectNotFoundException, IllegalAccessException {
    if (challenge == null) {
      throw new IllegalArgumentException("Challenge is mandatory");
    }
    String idSpace = String.valueOf(challenge.getAudience());
    if (StringUtils.isBlank(idSpace)) {
      throw new IllegalArgumentException("space id must not be null or empty");
    }
    Space space = spaceService.getSpaceById(idSpace);
    if (!spaceService.isManager(space, username)) {
      throw new IllegalAccessException("user is not allowed to modify challenge");
    }
    Challenge oldChallenge = challengeStorage.getChallengeById(challenge.getId());
    if (oldChallenge == null) {
      throw new ObjectNotFoundException("challenge is not exist");
    }
    if(oldChallenge.equals(challenge)) {
      throw new IllegalArgumentException("there are no changes to save");
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
    String idSpace = String.valueOf(challenge.getAudience());
    if (StringUtils.isBlank(idSpace)) {
      throw new IllegalArgumentException("space id must not be null or empty");
    }
    Space space = spaceService.getSpaceById(idSpace);
    if (!spaceService.isManager(space, username) && !spaceService.isMember(space, username)) {
      throw new IllegalAccessException("user is not allowed to modify challenge");
    }
    return challenge;
  }
}
