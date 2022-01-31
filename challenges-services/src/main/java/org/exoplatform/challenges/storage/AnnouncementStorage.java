package org.exoplatform.challenges.storage;

import org.exoplatform.challenges.dao.AnnouncementDAO;
import org.exoplatform.challenges.entity.AnnouncementEntity;
import org.exoplatform.challenges.model.Announcement;
import org.exoplatform.challenges.model.Challenge;
import org.exoplatform.challenges.utils.EntityMapper;

import java.util.Date;
import java.util.List;

public class AnnouncementStorage {

  private AnnouncementDAO announcementDAO;

  private ChallengeStorage challengeStorage;

  public static final long  MILLIS_IN_A_DAY = 1000 * 60 * 60 * 24;

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
    Date nextToEndDate =   new Date(announcementEntity.getChallenge().getEndDate().getTime() + MILLIS_IN_A_DAY);

    if (!announcementEntity.getCreatedDate().before(nextToEndDate)) {
      throw new IllegalArgumentException("announcement is not allowed when challenge is ended ");
    }
    if (!announcementEntity.getCreatedDate().after(announcementEntity.getChallenge().getStartDate())) {
      throw new IllegalArgumentException("announcement is not allowed when challenge is not started ");
    }
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

  public Long countAnnouncementsByChallenge(Long challengeId) {
    Long countAnnounce = announcementDAO.countAnnouncementsByChallenge(challengeId);
    return countAnnounce;
  }

<<<<<<< Updated upstream
=======
  public List<Announcement> getAllAnnouncement() {
    return EntityMapper.fromAnnouncementEntities(announcementDAO.findAll());
  }

  public void deleteAnnouncementById(Long id) {
    if (id == null) {
      throw new IllegalArgumentException("id must not be null");
    }
    Announcement announcement = getAnnouncementById(id);
    if (announcement != null) {
      Challenge challenge = challengeStorage.getChallengeById(announcement.getChallengeId());
      announcementDAO.delete(EntityMapper.toEntity(announcement, challenge));
    }
  }

  public List<Announcement> getAllAnnouncements() {
    return EntityMapper.fromAnnouncementEntities(announcementDAO.findAll());
  }
>>>>>>> Stashed changes
}
