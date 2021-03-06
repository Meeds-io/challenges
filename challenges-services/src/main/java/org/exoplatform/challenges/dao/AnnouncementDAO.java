package org.exoplatform.challenges.dao;

import org.exoplatform.challenges.entity.AnnouncementEntity;
import org.exoplatform.commons.persistence.impl.GenericDAOJPAImpl;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.util.Collections;
import java.util.List;

public class AnnouncementDAO extends GenericDAOJPAImpl<AnnouncementEntity, Long> {

  public List<AnnouncementEntity> findAllAnnouncementByChallenge(Long challengeId, int offset, int limit) {
    TypedQuery<AnnouncementEntity> query = getEntityManager().createNamedQuery("Announcement.findAllAnnouncementByChallenge",
                                                                               AnnouncementEntity.class);
    query.setParameter("challengeId", challengeId);
    query.setFirstResult(offset);
    query.setMaxResults(limit);
    List<AnnouncementEntity> resultList = query.getResultList();
    return resultList == null ? Collections.emptyList() : resultList;
  }

  public Long countAnnouncementsByChallenge(Long challengeId) {
    TypedQuery<Long> query = getEntityManager().createNamedQuery("Announcement.countAnnouncementsByChallenge", Long.class);
    query.setParameter("challengeId", challengeId);
    try {
      Long count = query.getSingleResult();
      return count == null ? 0l : count.longValue();
    } catch (NoResultException e) {
      return 0l;
    }
  }

}
