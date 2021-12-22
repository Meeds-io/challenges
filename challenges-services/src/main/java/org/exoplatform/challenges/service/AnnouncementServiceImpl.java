package org.exoplatform.challenges.service;


import org.exoplatform.challenges.entity.AnnouncementEntity;
import org.exoplatform.challenges.model.Announcement;
import org.exoplatform.challenges.model.Challenge;
import org.exoplatform.challenges.storage.AnnouncementStorage;
import org.exoplatform.challenges.storage.ChallengeStorage;
import org.exoplatform.challenges.utils.EntityMapper;
import org.exoplatform.challenges.utils.Utils;
import org.exoplatform.commons.exception.ObjectNotFoundException;
import org.exoplatform.services.listener.ListenerService;
import org.exoplatform.social.core.identity.model.Identity;
import org.exoplatform.social.core.identity.provider.OrganizationIdentityProvider;

import javax.ws.rs.core.Response;
import java.util.List;

import static org.exoplatform.challenges.utils.Utils.ANNOUNCEMENT_ACTIVITY_EVENT;

public class AnnouncementServiceImpl implements AnnouncementService {


  private AnnouncementStorage announcementStorage;

  private ChallengeStorage challengeStorage;

  private ListenerService listenerService;


  public AnnouncementServiceImpl(AnnouncementStorage announcementStorage, ChallengeStorage challengeStorage, ListenerService listenerService) {
    this.announcementStorage = announcementStorage;
    this.challengeStorage = challengeStorage;
    this.listenerService = listenerService;
  }

  @Override
  public Announcement createAnnouncement(Announcement announcement, String currentUser) throws IllegalArgumentException, ObjectNotFoundException, Exception {
    if (announcement == null) {
      throw new IllegalArgumentException("announcement is mandatory");
    }
    if (announcement.getId() != 0) {
      throw new IllegalArgumentException("announcement id must be equal to 0");
    }
    if (announcement.getChallenge() == null) {
      throw new IllegalArgumentException("challenge must not be null or empty");
    }
    if (announcement.getAssignee() == null) {
      throw new IllegalArgumentException("announcement assignee must have at least one winner");
    }
    if (Utils.canAnnounce(String.valueOf(announcement.getChallenge().getAudience()))) {
      throw new IllegalAccessException("user is not allowed to announce challenge");
    }

    Identity identity = Utils.getIdentityByTypeAndId(OrganizationIdentityProvider.NAME, currentUser);
    announcement.setCreator(Long.parseLong(identity.getId()));

    listenerService.broadcast(ANNOUNCEMENT_ACTIVITY_EVENT, announcementStorage, announcement);

    return announcementStorage.saveAnnouncement(announcement);
  }

  @Override
  public List<Announcement> findAllAnnouncementByChallenge(long challengeId, int offset, int limit) throws IllegalAccessException, ObjectNotFoundException {
    if (challengeId <= 0) {
      throw new IllegalArgumentException("Challenge id has to be positive integer");
    }
    Challenge challenge = challengeStorage.getChallengeById(challengeId);
    if (challenge == null) {
      throw new ObjectNotFoundException("challenge does not exist");
    }
    List<AnnouncementEntity> announcementEntities = announcementStorage.findAllAnnouncementByChallenge(challengeId, offset, limit);
    return EntityMapper.fromAnnouncementEntities(announcementEntities);
  }

}
