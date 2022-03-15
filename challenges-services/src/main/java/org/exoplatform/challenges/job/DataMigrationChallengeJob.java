package org.exoplatform.challenges.job;

import org.apache.commons.lang3.StringUtils;
import org.exoplatform.addons.gamification.service.AnnouncementService;
import org.exoplatform.addons.gamification.service.ChallengeService;
import org.exoplatform.challenges.model.Announcement;
import org.exoplatform.challenges.model.Challenge;
import org.exoplatform.commons.utils.CommonsUtils;
import org.exoplatform.container.ExoContainer;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.container.PortalContainer;
import org.exoplatform.container.component.RequestLifeCycle;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.social.core.activity.model.ExoSocialActivity;
import org.exoplatform.social.core.identity.model.Identity;
import org.exoplatform.social.core.manager.IdentityManager;
import org.exoplatform.social.core.space.model.Space;
import org.exoplatform.social.core.space.spi.SpaceService;
import org.exoplatform.social.core.storage.api.ActivityStorage;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.ArrayList;
import java.util.List;

@DisallowConcurrentExecution
public class DataMigrationChallengeJob implements Job {
  private static final Log                                       LOG    = ExoLogger.getLogger(DataMigrationChallengeJob.class);

  private ChallengeService                                       challengeServiceGamification;

  private org.exoplatform.challenges.service.ChallengeService    challengeService;

  private AnnouncementService                                    announcementServiceChallenge;

  private org.exoplatform.challenges.service.AnnouncementService announcementService;

  private SpaceService                                           spaceService;

  private ExoContainer                                           container;

  private ActivityStorage                                        activityStorage;

  private static final String                                    SOCIAL = "Social";

  public DataMigrationChallengeJob() {
    this.container = PortalContainer.getInstance();
  }

  @Override
  public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
    LOG.info("CHALLENGE DATA MIGRATION HAS STARTED");
    ExoContainer currentContainer = ExoContainerContext.getCurrentContainer();
    ExoContainerContext.setCurrentContainer(container);
    RequestLifeCycle.begin(this.container);
    try {
      List<Challenge> challenges = getChallengeService().getAllChallenges();
      if(challenges.isEmpty()) {
        LOG.info("NO CHALLENGES TO MIGRATE");
        return;
      }
      LOG.info(challenges.size() + " CHALLENGES ENTITIES WILL BE MIGRATED TO GAMIFICATION RULES");
      for (Challenge c : challenges) {
        org.exoplatform.addons.gamification.service.dto.configuration.Challenge challenge =
                new org.exoplatform.addons.gamification.service.dto.configuration.Challenge(0l,
                        c.getTitle(),
                        c.getDescription() != null ? c.getDescription() : c.getTitle(),
                        c.getAudience(),
                        c.getStartDate(),
                        c.getEndDate(),
                        new ArrayList<>(c.getManagers()),
                        0l,
                        SOCIAL);
        LOG.info("START MIGRATING CHALLENGE with TITLE = {}", challenge.getTitle());
        String remoteId = "";
        Identity identity = null;
        for (int k = 0; k < c.getManagers().size(); k++) {
          identity = getIdentityById(String.valueOf(c.getManagers().get(k)));
          if (identity != null) {
            Space space = getSpaceService().getSpaceById(String.valueOf(c.getAudience()));
            if (space != null) {
              remoteId = identity.getRemoteId();
              break;
            }
          }
        }
        LOG.debug("THE REMOTE ID OF CREATOR CHALLENGE IS {}", remoteId);
        if (StringUtils.isNotBlank(remoteId)) {
          try {

            org.exoplatform.addons.gamification.service.dto.configuration.Challenge newChallenge =
                    getChallengeServiceGamification().createChallenge(challenge);
            LOG.info("CHALLENGE : {} WITH OLD ID {} IS MIGRATED WITH NEW ID {}",
                    newChallenge.getTitle(),
                    c.getId(),
                    newChallenge.getId());
            List<Announcement> announcements = getAnnouncementService().findAllAnnouncementByChallenge(c.getId(), 0, 100);
            for (Announcement a : announcements) {
              Identity creator = getIdentityById(String.valueOf(a.getCreator()));
              if (a.getAssignee().size() > 1) {
                for (int i = 0; i < a.getAssignee().size(); i++) {
                  org.exoplatform.addons.gamification.service.dto.configuration.Announcement announcement =
                          new org.exoplatform.addons.gamification.service.dto.configuration.Announcement(0l,
                                  newChallenge.getId(),
                                  a.getAssignee()
                                          .get(i),
                                  a.getComment(),
                                  a.getCreator(),
                                  a.getCreatedDate(),
                                  a.getActivityId());
                  org.exoplatform.addons.gamification.service.dto.configuration.Announcement newAnnouncement =
                          getAnnouncementServiceChallenge().createAnnouncement(announcement,
                                  creator.getRemoteId(),
                                  true);
                  LOG.info("THE ANNOUNCEMENT OF USER {} WITH THE OLD ID {} FOR CHALLENGE {} MIGRATED WITH NEW ID {}",
                          a.getAssignee().get(i),
                          a.getId(),
                          newChallenge.getTitle(),
                          newAnnouncement.getId());
                }
              } else {
                org.exoplatform.addons.gamification.service.dto.configuration.Announcement announcement =
                        new org.exoplatform.addons.gamification.service.dto.configuration.Announcement(0l,
                                newChallenge.getId(),
                                a.getAssignee()
                                        .get(0),
                                a.getComment(),
                                a.getCreator(),
                                a.getCreatedDate(),
                                a.getActivityId());
                org.exoplatform.addons.gamification.service.dto.configuration.Announcement newAnnouncement =
                        getAnnouncementServiceChallenge().createAnnouncement(announcement,
                                creator.getRemoteId(),
                                true);
                LOG.info("THE ANNOUNCEMENT OF USER {} WITH THE OLD ID {} FOR CHALLENGE {} MIGRATED WITH NEW ID {} ",
                        a.getAssignee().get(0),
                        a.getId(),
                        newChallenge.getTitle(),
                        newAnnouncement.getId());
                ExoSocialActivity activity = getActivityStorageService().getActivity(String.valueOf(a.getActivityId()));
                if (activity != null) {
                  activity.getTemplateParams().put("announcementId", String.valueOf(newAnnouncement.getId()));
                } else {
                  LOG.warn("ACTIVITY WITH ID {} IS NOT FOUNT FOR ANNOUNCEMENT WITH ID {}", a.getActivityId(), a.getId());
                }
              }
              getAnnouncementService().deleteAnnouncementById(a.getId());
              LOG.info("THE ANNOUNCEMENT WITH ID {} FOR CHALLENGE {} IS DELETED", a.getId(), newChallenge.getTitle());
            }
            getChallengeService().deleteChallengeById(c.getId());
            LOG.info("CHALLENGE WITH ID {} IS DELETED ", c.getId());
          } catch (Exception e) {
            LOG.error("Error while migration Challenge '{}'", c.getTitle(), e);
          }
        }
      }
      LOG.info("CHALLENGE DATA MIGRATION COMPLETED");
    } catch (Exception e) {
      LOG.error("CHALLENGE DATA MIGRATION FAILED", e);
    } finally {
      RequestLifeCycle.end();
      ExoContainerContext.setCurrentContainer(currentContainer);
    }
  }

  private org.exoplatform.challenges.service.ChallengeService getChallengeService() {
    if (challengeService == null) {
      challengeService = CommonsUtils.getService(org.exoplatform.challenges.service.ChallengeService.class);
    }
    return challengeService;
  }

  private ChallengeService getChallengeServiceGamification() {
    if (challengeServiceGamification == null) {
      challengeServiceGamification = CommonsUtils.getService(ChallengeService.class);
    }
    return challengeServiceGamification;
  }

  public static Identity getIdentityById(String identityId) {
    IdentityManager identityManager = CommonsUtils.getService(IdentityManager.class);
    return identityManager.getIdentity(identityId);
  }

  private SpaceService getSpaceService() {
    if (spaceService == null) {
      spaceService = CommonsUtils.getService(SpaceService.class);
    }
    return spaceService;
  }

  private AnnouncementService getAnnouncementServiceChallenge() {
    if (announcementServiceChallenge == null) {
      announcementServiceChallenge = CommonsUtils.getService(AnnouncementService.class);
    }
    return announcementServiceChallenge;
  }

  private ActivityStorage getActivityStorageService() {
    if (activityStorage == null) {
      activityStorage = CommonsUtils.getService(ActivityStorage.class);
    }
    return activityStorage;
  }

  private org.exoplatform.challenges.service.AnnouncementService getAnnouncementService() {
    if (announcementService == null) {
      announcementService = CommonsUtils.getService(org.exoplatform.challenges.service.AnnouncementService.class);
    }
    return announcementService;
  }
}
