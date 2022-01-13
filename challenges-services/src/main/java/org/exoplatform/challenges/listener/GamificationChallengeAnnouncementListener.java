package org.exoplatform.challenges.listener;

import org.exoplatform.addons.gamification.service.configuration.DomainService;
import org.exoplatform.addons.gamification.service.dto.configuration.DomainDTO;
import org.exoplatform.addons.gamification.service.dto.configuration.RuleDTO;
import org.exoplatform.addons.gamification.service.effective.GamificationService;
import org.exoplatform.challenges.model.Announcement;
import org.exoplatform.challenges.model.Challenge;
import org.exoplatform.challenges.service.ChallengeService;
import org.exoplatform.challenges.utils.Utils;
import org.exoplatform.commons.utils.CommonsUtils;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.container.PortalContainer;
import org.exoplatform.container.component.RequestLifeCycle;
import org.exoplatform.services.listener.Event;
import org.exoplatform.services.listener.Listener;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;



public class GamificationChallengeAnnouncementListener extends Listener<ChallengeService, Announcement> {

    private static final Log LOG = ExoLogger.getLogger(GamificationChallengeAnnouncementListener.class);

    private DomainService domainService;

    private PortalContainer container;

    private GamificationService gamificationService;

    private static final String   GAMIFICATION_ANNOUNCE_CHALLENGE    = "announceChallenge";

    public GamificationChallengeAnnouncementListener(GamificationService gamificationService, DomainService domainService, PortalContainer container) {
        this.domainService = domainService;
        this.gamificationService = gamificationService;
        this.container = container;
    }

    @Override
    public void onEvent(Event<ChallengeService, Announcement> event) throws Exception {
        ExoContainerContext.setCurrentContainer(container);
        RequestLifeCycle.begin(container);
        try {
            Announcement announcement = event.getData();
            ChallengeService challengeService = event.getSource();
            String currentUser = Utils.getCurrentUser();
            Challenge challenge = challengeService.getChallengeById(announcement.getChallengeId(), currentUser);
            RuleDTO rule = new RuleDTO();
            rule.setDescription(GAMIFICATION_ANNOUNCE_CHALLENGE);
            rule.setEvent(GAMIFICATION_ANNOUNCE_CHALLENGE);
            rule.setArea("challenge"); // to be changed with challenge program
            rule.setScore(20); // to be changed with challenge points
            rule.setEnabled(true);
            rule.setDeleted(false);
            DomainDTO domain = domainService.findDomainByTitle("challenge");
            rule.setDomainDTO(domain);
            for (Long id : announcement.getAssignee()) {
                String activityUrl = "/" + PortalContainer.getCurrentPortalContainerName() + "/" + CommonsUtils.getCurrentPortalOwner() + "/activity?id=" + announcement.getActivityId();
                gamificationService.createHistoryWithRule(rule, String.valueOf(id), String.valueOf(announcement.getCreator()), activityUrl);
            }
        } catch (Exception e) {
            LOG.error("Cannot broadcast gamification event");
        } finally {
            RequestLifeCycle.end();
        }
    }
}
