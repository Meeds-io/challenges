package org.exoplatform.challenges.dao;

import org.exoplatform.challenges.entity.ChallengeEntity;
import org.exoplatform.commons.persistence.impl.GenericDAOJPAImpl;
import javax.persistence.TypedQuery;
import java.util.Collections;
import java.util.List;

public class ChallengeDAO extends GenericDAOJPAImpl<ChallengeEntity, Long> {

  public List<ChallengeEntity> findAllChallengesByUser(int offset, int limit, List<Long> ids) {
    TypedQuery<ChallengeEntity> query = getEntityManager().createNamedQuery("Challenge.findAllChallengesByUser", ChallengeEntity.class);
    query.setParameter("ids", ids);
    query.setFirstResult(offset);
    query.setMaxResults(limit);
    List<ChallengeEntity> resultList = query.getResultList();
    return resultList == null ? Collections.emptyList() : resultList;
  }

}
