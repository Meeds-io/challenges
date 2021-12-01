package org.exoplatform.challenges.storage;

import org.exoplatform.challenges.dao.ChallengeDAO;
import org.exoplatform.challenges.entity.ChallengeEntity;
import org.exoplatform.challenges.model.Challenge;
import org.exoplatform.challenges.utils.EntityMapper;
import org.exoplatform.challenges.utils.Utils;
import org.exoplatform.social.core.identity.model.Identity;

public class ChallengeStorage {

  private ChallengeDAO challengeDAO;

  public ChallengeStorage(ChallengeDAO challengeDAO) {
    this.challengeDAO = challengeDAO;
  }

  public Challenge saveChallenge(Challenge entity, String username) throws IllegalArgumentException {
    if (entity == null) {
      throw new IllegalArgumentException("challenge argument is null");
    }
    Identity identity = Utils.getIdentityByTypeAndId(username);
    if (identity == null) {
      throw new IllegalArgumentException("identity is not exist");
    }
    ChallengeEntity challengeEntity = EntityMapper.toEntity(entity);
    if (entity.getId() == 0) {
      challengeEntity.setId(null);
      challengeEntity = challengeDAO.create(challengeEntity);
    } else {
      challengeEntity = challengeDAO.update(challengeEntity);
    }

    return EntityMapper.fromEntity(challengeEntity);
  }

  public Challenge getChallengeById(long challengeId) {
    ChallengeEntity challengeEntity = this.challengeDAO.find(challengeId);
    return EntityMapper.fromEntity(challengeEntity);
  }
}
