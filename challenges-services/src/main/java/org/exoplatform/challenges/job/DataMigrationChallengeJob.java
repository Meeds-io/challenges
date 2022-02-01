package org.exoplatform.challenges.job;

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
import org.exoplatform.social.core.identity.model.Identity;
import org.exoplatform.social.core.manager.IdentityManager;
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

  private ExoContainer                                           container;

  private static final String                                    SOCIAL = "social";

  public DataMigrationChallengeJob() {
    this.container = PortalContainer.getInstance();
  }

  @Override
  public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
    ExoContainer currentContainer = ExoContainerContext.getCurrentContainer();
    ExoContainerContext.setCurrentContainer(container);
    RequestLifeCycle.begin(this.container);
    try {
      List<Challenge> challenges = getChallengeService().getAllChallenges();
      for (Challenge c : challenges) {
        org.exoplatform.addons.gamification.service.dto.configuration.Challenge challenge =
                                                                                          new org.exoplatform.addons.gamification.service.dto.configuration.Challenge(0l,
                                                                                                                                                                      c.getTitle(),
                                                                                                                                                                      c.getDescription(),
                                                                                                                                                                      c.getAudience(),
                                                                                                                                                                      c.getStartDate(),
                                                                                                                                                                      c.getEndDate(),
                                                                                                                                                                      c.isCanEdit(),
                                                                                                                                                                      c.isCanAnnounce(),
                                                                                                                                                                      new ArrayList<>(c.getManagers()),
                                                                                                                                                                      0l,
                                                                                                                                                                      SOCIAL);
        Identity identity = getIdentityById(String.valueOf(c.getManagers().get(0)));
        org.exoplatform.addons.gamification.service.dto.configuration.Challenge newChallenge =
                                                                                             getChallengeServiceGamification().createChallenge(challenge,
                                                                                                                                               identity.getRemoteId());
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
                                                                                                                                                                                     a.getActivityId(),
                                                                                                                                                                                     creator.getRemoteId(),
                                                                                                                                                                                     null,
                                                                                                                                                                                     null);
              getAnnouncementServiceChallenge().createAnnouncement(announcement, creator.getRemoteId());
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
                                                                                                                                                                                   a.getActivityId(),
                                                                                                                                                                                   creator.getRemoteId(),
                                                                                                                                                                                   null,
                                                                                                                                                                                   null);
            getAnnouncementServiceChallenge().createAnnouncement(announcement, creator.getRemoteId());
          }
          getAnnouncementService().deleteAnnouncementById(a.getId());

        }
        getChallengeService().deleteChallengeById(c.getId());
      }
    } catch (Exception e) {
      LOG.error("Error while migration data", e);
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

  private AnnouncementService getAnnouncementServiceChallenge() {
    if (announcementServiceChallenge == null) {
      announcementServiceChallenge = CommonsUtils.getService(AnnouncementService.class);
    }
    return announcementServiceChallenge;
  }

  private org.exoplatform.challenges.service.AnnouncementService getAnnouncementService() {
    if (announcementService == null) {
      announcementService = CommonsUtils.getService(org.exoplatform.challenges.service.AnnouncementService.class);
    }
    return announcementService;
  }
}
