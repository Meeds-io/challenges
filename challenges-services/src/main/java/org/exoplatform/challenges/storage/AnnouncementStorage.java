package org.exoplatform.challenges.storage;

import org.exoplatform.challenges.dao.AnnouncementDAO;
import org.exoplatform.challenges.entity.AnnouncementEntity;
import org.exoplatform.challenges.model.Announcement;
import org.exoplatform.challenges.utils.EntityMapper;

import java.util.List;

public class AnnouncementStorage {

  private AnnouncementDAO announcementDAO;

  public AnnouncementStorage(AnnouncementDAO announcementDAO) {
    this.announcementDAO = announcementDAO;
  }

  public Announcement saveAnnouncement(AnnouncementEntity announcementEntity) {
    if (announcementEntity == null) {
      throw new IllegalArgumentException("Announcement argument is null");
    }

    if (announcementEntity.getId() == null) {
      announcementEntity = announcementDAO.create(announcementEntity);
    } else {
      announcementEntity = announcementDAO.update(announcementEntity);
    }

    return EntityMapper.fromEntity(announcementEntity);
  }

  public List<AnnouncementEntity> findAllAnnouncementByChallenge(Long challengeId, int offset, int limit) {
    return announcementDAO.findAllAnnouncementByChallenge(challengeId, offset, limit);
  }

}
