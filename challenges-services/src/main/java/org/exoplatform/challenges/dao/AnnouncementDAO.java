package org.exoplatform.challenges.dao;

import org.exoplatform.challenges.entity.AnnouncementEntity;
import org.exoplatform.commons.persistence.impl.GenericDAOJPAImpl;

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

  public AnnouncementEntity finAnnouncementByChallengeIdAndAssignedId(Long challengeId, Long assignedId) {
    TypedQuery<AnnouncementEntity> query =
                                         getEntityManager().createNamedQuery("Announcement.findAnnouncementByChallengeIdAndAssignedId",
                                                                             AnnouncementEntity.class);
    query.setParameter("challengeId", challengeId);
    query.setParameter("assignedId", assignedId);
    AnnouncementEntity announcementEntity = query.getSingleResult();
    return announcementEntity;
  }

}
