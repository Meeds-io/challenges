package org.exoplatform.challenges.storage;

import org.exoplatform.challenges.dao.ChallengeDAO;
import org.exoplatform.challenges.entity.ChallengeEntity;
import org.exoplatform.challenges.model.Challenge;
import org.exoplatform.challenges.utils.EntityMapper;
import org.exoplatform.challenges.utils.Utils;
import org.exoplatform.social.core.identity.model.Identity;
import org.exoplatform.social.core.identity.provider.OrganizationIdentityProvider;

import java.util.List;

public class ChallengeStorage {

  private ChallengeDAO challengeDAO;

  public ChallengeStorage(ChallengeDAO challengeDAO) {
    this.challengeDAO = challengeDAO;
  }

  public Challenge saveChallenge(Challenge challenge, String username) throws IllegalArgumentException {
    if (challenge == null) {
      throw new IllegalArgumentException("challenge argument is null");
    }
    Identity identity = Utils.getIdentityByTypeAndId(OrganizationIdentityProvider.NAME, username);
    if (identity == null) {
      throw new IllegalArgumentException("identity is not exist");
    }
    ChallengeEntity challengeEntity = EntityMapper.toEntity(challenge);
    if (challengeEntity.getEndDate().compareTo(challengeEntity.getStartDate()) < 0
        || challengeEntity.getEndDate().equals(challengeEntity.getStartDate())) {
      throw new IllegalArgumentException("endDate must be greater than startDate");
    }
    if (challenge.getId() == 0) {
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

  public List<ChallengeEntity> findAllChallengesByUser(int offset, int limit, List<Long> ids) {
    return challengeDAO.findAllChallengesByUser(offset, limit, ids);
  }
}
