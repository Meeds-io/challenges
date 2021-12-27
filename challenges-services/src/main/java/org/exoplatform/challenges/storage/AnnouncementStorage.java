package org.exoplatform.challenges.storage;

import org.exoplatform.challenges.dao.AnnouncementDAO;
import org.exoplatform.challenges.entity.AnnouncementEntity;
import org.exoplatform.challenges.entity.ChallengeEntity;
import org.exoplatform.challenges.model.Announcement;
import org.exoplatform.challenges.model.Challenge;
import org.exoplatform.challenges.utils.EntityMapper;

import java.util.List;

public class AnnouncementStorage {

  private AnnouncementDAO announcementDAO;

  private ChallengeStorage challengeStorage;

  public AnnouncementStorage(AnnouncementDAO announcementDAO, ChallengeStorage challengeStorage) {
    this.announcementDAO = announcementDAO;
    this.challengeStorage = challengeStorage;
  }

  public Announcement saveAnnouncement(Announcement announcement) {
    if (announcement == null) {
      throw new IllegalArgumentException("Announcement argument is null");
    }
    Challenge challenge = challengeStorage.getChallengeById(announcement.getChallengeId());
    AnnouncementEntity announcementEntity = EntityMapper.toEntity(announcement, challenge);

    if (announcementEntity.getId() == null) {
      announcementEntity = announcementDAO.create(announcementEntity);
    } else {
      announcementEntity = announcementDAO.update(announcementEntity);
    }

    return EntityMapper.fromEntity(announcementEntity);
  }

  public Announcement getAnnouncementById(long announcementId) {
    AnnouncementEntity announcementEntity = this.announcementDAO.find(announcementId);
    return EntityMapper.fromEntity(announcementEntity);
  }

  public List<Announcement> findAllAnnouncementByChallenge(Long challengeId, int offset, int limit) {
    List<AnnouncementEntity> announcementEntities= announcementDAO.findAllAnnouncementByChallenge(challengeId, offset, limit);
    return EntityMapper.fromAnnouncementEntities(announcementEntities);
  }

}
