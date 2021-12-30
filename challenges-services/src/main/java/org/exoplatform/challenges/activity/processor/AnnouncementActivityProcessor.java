package org.exoplatform.challenges.activity.processor;

import org.apache.commons.lang3.StringUtils;
import org.exoplatform.challenges.model.Announcement;
import org.exoplatform.challenges.model.UserInfo;
import org.exoplatform.challenges.service.AnnouncementService;
import org.exoplatform.challenges.utils.Utils;
import org.exoplatform.commons.exception.ObjectNotFoundException;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.social.core.BaseActivityProcessorPlugin;
import org.exoplatform.social.core.activity.model.ExoSocialActivity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnnouncementActivityProcessor extends BaseActivityProcessorPlugin {

  private static final Log LOG = ExoLogger.getLogger(AnnouncementActivityProcessor.class);

  AnnouncementService      announcementService;

  public AnnouncementActivityProcessor(InitParams params, AnnouncementService announcementService) {
    super(params);
    this.announcementService = announcementService;
  }

  @Override
  public void processActivity(ExoSocialActivity activity) {
    if (activity.isComment() || activity.getType() == null) {
      return;
    }
    String announcementId = activity.getTemplateParams().get("announcementId");
    if (StringUtils.isBlank(announcementId)){
      LOG.error("announcement id must not null");
      return;
    }
    try {
      Announcement announcement = announcementService.getAnnouncementById(Long.parseLong(announcementId));

      Map<String, String> params = new HashMap<>();
      params.put("announcementAssigneeUsername", this.getAssigneeUserNames(announcement.getAssignee() , announcement.getChallengeId()));
      params.put("announcementAssigneeFullName", this.getAssigneeFullNames(announcement.getAssignee(), announcement.getChallengeId()));
      activity.getTemplateParams().putAll(params);
    } catch (ObjectNotFoundException e) {
      LOG.error("Unexpected error", e);
    }
  }

  private String getAssigneeFullNames(List<Long> assignee, Long challengeId) {
    if (assignee.isEmpty()) {
      throw new IllegalArgumentException("announcement assignee must have at least one winner");
    }
    String AssigneeFullNames = "";
    List<UserInfo> AssigneeIdentityList = Utils.getUsersByIds(assignee, challengeId);
    for (UserInfo user : AssigneeIdentityList) {
      AssigneeFullNames = AssigneeFullNames + user.getFullName() + "#";
    }
    return AssigneeFullNames;
  }

  private String getAssigneeUserNames(List<Long> assignee, Long challengeId) {
    if (assignee.isEmpty()) {
      throw new IllegalArgumentException("announcement assignee must have at least one winner");
    }
    String AssigneeUserNames = "";
    List<UserInfo> AssigneeIdentityList = Utils.getUsersByIds(assignee, challengeId);
    for (UserInfo user : AssigneeIdentityList) {
      AssigneeUserNames = AssigneeUserNames + user.getRemoteId() + "#";
    }
    return AssigneeUserNames;
  }

}
