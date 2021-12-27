package org.exoplatform.challenges.utils;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.exoplatform.challenges.entity.AnnouncementEntity;
import org.exoplatform.challenges.entity.ChallengeEntity;
import org.exoplatform.challenges.model.*;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class EntityMapper {
  private static final Log LOG = ExoLogger.getLogger(EntityMapper.class);

  private EntityMapper() {
  }

  public static Challenge fromEntity(ChallengeEntity challengeEntity) {
    if (challengeEntity == null) {
      return null;
    }
    return new Challenge(challengeEntity.getId(),
                         challengeEntity.getTitle(),
                         challengeEntity.getDescription(),
                         challengeEntity.getAudience(),
                         challengeEntity.getStartDate() == null ? null : Utils.toRFC3339Date(challengeEntity.getStartDate()),
                         challengeEntity.getEndDate() == null ? null : Utils.toRFC3339Date(challengeEntity.getEndDate()),
                         Utils.canEditChallenge(String.valueOf(challengeEntity.getAudience())),
                         Utils.canAnnounce(String.valueOf(challengeEntity.getAudience())),
                         challengeEntity.getManagers());
  }

  public static ChallengeEntity toEntity(Challenge challenge) {
    if (challenge == null) {
      return null;
    }
    ChallengeEntity challengeEntity = new ChallengeEntity();
    if (challenge.getId() != 0) {
      challengeEntity.setId(challenge.getId());
    }
    if (StringUtils.isNotBlank(challenge.getTitle())) {
      challengeEntity.setTitle(challenge.getTitle());
    }
    if (StringUtils.isNotBlank(challenge.getDescription())) {
      challengeEntity.setDescription(challenge.getDescription());
    }
    if (challenge.getEndDate() != null) {
      challengeEntity.setEndDate(Utils.parseRFC3339Date(challenge.getEndDate()));
    }
    if (challenge.getStartDate() != null) {
      challengeEntity.setStartDate(Utils.parseRFC3339Date(challenge.getStartDate()));
    }
    if (challenge.getManagers() == null || challenge.getManagers().isEmpty()) {
      challengeEntity.setManagers(Collections.emptyList());
    } else {
      challengeEntity.setManagers(challenge.getManagers());
    }
    challengeEntity.setAudience(challenge.getAudience());
    challengeEntity.setManagers(challenge.getManagers());
    return challengeEntity;
  }

  public static Announcement fromEntity(AnnouncementEntity announcementEntity) {
    if (announcementEntity == null) {
      return null;
    }
    return new Announcement(announcementEntity.getId(),
                            announcementEntity.getChallenge().getId(),
                            announcementEntity.getAssignee(),
                            announcementEntity.getComment(),
                            announcementEntity.getCreator(),
                            announcementEntity.getCreatedDate() == null ? null
                                                                        : Utils.toRFC3339Date(announcementEntity.getCreatedDate()),
                            announcementEntity.getActivityId());
  }

  public static AnnouncementEntity toEntity(Announcement announcement, Challenge challenge) {
    if (announcement == null) {
      return null;
    }
    ChallengeEntity challengeEntity = toEntity(challenge);
    AnnouncementEntity announcementEntity = new AnnouncementEntity();
    if (announcement.getId() != 0) {
      announcementEntity.setId(announcement.getId());
    }
    if (announcement.getAssignee() != null) {
      announcementEntity.setAssignee(announcement.getAssignee());
    }
    if (announcement.getActivityId() != null) {
      announcementEntity.setActivityId(announcement.getActivityId());
    }
    announcementEntity.setComment(announcement.getComment());
    announcementEntity.setCreatedDate(Utils.parseRFC3339Date(announcement.getCreatedDate()));
    announcementEntity.setChallenge(challengeEntity);
    announcementEntity.setCreator(announcement.getCreator());
    return announcementEntity;
  }

  public static List<Challenge> fromChallengeEntities(List<ChallengeEntity> challengeEntities) {
    if (CollectionUtils.isEmpty(challengeEntities)) {
      return new ArrayList<>(Collections.emptyList());
    } else {
      List<Challenge> challenges = challengeEntities.stream()
                                                    .map(challengeEntity -> fromEntity(challengeEntity))
                                                    .collect(Collectors.toList());
      return challenges;
    }
  }

  public static ChallengeRestEntity fromChallenge(Challenge challenge, List<Announcement> challengeAnnouncements) {
    if (challenge == null) {
      return null;
    }
    List<AnnouncementRestEntity> announcementRestEntities = fromAnnouncementList(challengeAnnouncements);
    return new ChallengeRestEntity(challenge.getId(),
                                   challenge.getTitle(),
                                   challenge.getDescription(),
                                   Utils.getSpaceById(String.valueOf(challenge.getAudience())),
                                   challenge.getStartDate(),
                                   challenge.getEndDate(),
                                   Utils.canEditChallenge(String.valueOf(challenge.getAudience())),
                                   Utils.canAnnounce(String.valueOf(challenge.getAudience())),
                                   Utils.getUsersByIds(challenge.getManagers()),
                                   announcementRestEntities);
  }

  public static List<Announcement> fromAnnouncementEntities(List<AnnouncementEntity> announcementEntities) {
    if (CollectionUtils.isEmpty(announcementEntities)) {
      return new ArrayList<>(Collections.emptyList());
    } else {
      List<Announcement> announcements = announcementEntities.stream()
                                                             .map(announcementEntity -> fromEntity(announcementEntity))
                                                             .collect(Collectors.toList());
      return announcements;
    }
  }

  public static AnnouncementRestEntity fromAnnouncement(Announcement announcement) {
    if (announcement == null) {
      return null;
    }
    return new AnnouncementRestEntity(announcement.getId(),
                                      announcement.getChallengeId(),
                                      Utils.getUsersByIds(announcement.getAssignee()),
                                      announcement.getComment(),
                                      Utils.getUsersByIds(new ArrayList<Long>(Collections.singleton(announcement.getCreator())))
                                           .get(0),
                                      announcement.getCreatedDate(),
                                      announcement.getActivityId());
  }

  public static List<AnnouncementRestEntity> fromAnnouncementList(List<Announcement> announcements) {
    if (CollectionUtils.isEmpty(announcements)) {
      return new ArrayList<>(Collections.emptyList());
    } else {
      List<AnnouncementRestEntity> restEntities = announcements.stream()
                                                               .map(announcement -> fromAnnouncement(announcement))
                                                               .collect(Collectors.toList());
      return restEntities;
    }
  }
}
