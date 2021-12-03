package org.exoplatform.challenges.utils;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.exoplatform.challenges.entity.AnnouncementEntity;
import org.exoplatform.challenges.entity.ChallengeEntity;
import org.exoplatform.challenges.model.Announcement;
import org.exoplatform.challenges.model.Challenge;
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
                         challengeEntity.getStartDate(),
                         challengeEntity.getEndDate(),
                         Utils.canEditChallenge(String.valueOf(challengeEntity.getId())),
                         Utils.canAnnounce(String.valueOf(challengeEntity.getId())),
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
      challengeEntity.setEndDate(challenge.getEndDate());
    }
    if (challenge.getStartDate() != null) {
      challengeEntity.setStartDate(challenge.getStartDate());
    }
    challengeEntity.setAudience(challenge.getAudience());
    challengeEntity.setManagers(challenge.getManagers());
    return challengeEntity;
  }

  public static Announcement fromEntity(AnnouncementEntity announcementEntity) {
    if (announcementEntity == null) {
      return null;
    }
    Challenge challenge = new Challenge();
    challenge.setId(announcementEntity.getChallenge().getId());
    challenge.setAudience(announcementEntity.getChallenge().getAudience());
    challenge.setDescription(announcementEntity.getChallenge().getDescription());
    challenge.setEndDate(announcementEntity.getChallenge().getEndDate());
    challenge.setManagers(announcementEntity.getChallenge().getManagers());
    challenge.setTitle(announcementEntity.getChallenge().getTitle());
    challenge.setStartDate(announcementEntity.getChallenge().getStartDate());
    return new Announcement(announcementEntity.getId(),
                            challenge,
                            announcementEntity.getAssignee(),
                            announcementEntity.getComment(),
                            announcementEntity.getCreator(),
                            announcementEntity.getCreatedDate(),
                            announcementEntity.getActivityId());
  }

  public static AnnouncementEntity toEntity(Announcement announcement) {
    if (announcement == null) {
      return null;
    }
    ChallengeEntity challengeEntity = new ChallengeEntity();
    challengeEntity.setId(announcement.getChallenge().getId());
    challengeEntity.setTitle(announcement.getChallenge().getTitle());
    challengeEntity.setDescription(announcement.getChallenge().getDescription());
    challengeEntity.setAudience(announcement.getChallenge().getAudience());
    challengeEntity.setManagers(announcement.getChallenge().getManagers());
    challengeEntity.setStartDate(announcement.getChallenge().getStartDate());
    challengeEntity.setEndDate(announcement.getChallenge().getEndDate());
    AnnouncementEntity announcementEntity = new AnnouncementEntity();
    if (announcement.getId() != 0) {
      announcementEntity.setId(announcement.getId());
    }
    if (announcement.getAssignee() != null) {
      announcementEntity.setAssignee(announcement.getAssignee());
    }
    if (announcement.getActivityId() != 0) {
      announcementEntity.setActivityId(announcement.getActivityId());
    }
    announcementEntity.setComment(announcement.getComment());
    announcementEntity.setCreatedDate(announcement.getCreatedDate());
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
}
