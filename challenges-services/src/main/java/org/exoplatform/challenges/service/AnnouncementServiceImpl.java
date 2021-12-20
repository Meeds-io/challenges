package org.exoplatform.challenges.service;


import org.apache.commons.lang.StringEscapeUtils;
import org.exoplatform.challenges.entity.AnnouncementEntity;
import org.exoplatform.challenges.model.Announcement;
import org.exoplatform.challenges.model.Challenge;
import org.exoplatform.challenges.model.UserInfo;
import org.exoplatform.challenges.storage.AnnouncementStorage;
import org.exoplatform.challenges.storage.ChallengeStorage;
import org.exoplatform.challenges.utils.EntityMapper;
import org.exoplatform.challenges.utils.Utils;
import org.exoplatform.commons.exception.ObjectNotFoundException;
import org.exoplatform.commons.utils.CommonsUtils;
import org.exoplatform.social.core.activity.model.ExoSocialActivity;
import org.exoplatform.social.core.activity.model.ExoSocialActivityImpl;
import org.exoplatform.social.core.identity.model.Identity;
import org.exoplatform.social.core.manager.ActivityManager;
import org.exoplatform.social.core.manager.IdentityManager;
import org.exoplatform.social.core.space.model.Space;
import org.exoplatform.social.core.storage.api.ActivityStorage;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AnnouncementServiceImpl implements AnnouncementService{

  public static final String                 ANNOUNCEMENT_ACTIVITY_TYPE     = "exo-announcement:activity";

  public static final String   ACTIVITY_DEFAULT_IMAGE        = "/challenges/images/challengesAppIcon.png";

  private AnnouncementStorage announcementStorage ;

  private ChallengeStorage challengeStorage;

  private ActivityManager activityManager;

  private ActivityStorage activityStorage;

  public AnnouncementServiceImpl(AnnouncementStorage announcementStorage,ChallengeStorage challengeStorage, ActivityManager activityManager, ActivityStorage activityStorage) {
    this.announcementStorage = announcementStorage;
    this.challengeStorage = challengeStorage;
    this.activityManager = activityManager;
    this.activityStorage = activityStorage;
  }

  @Override
  public Announcement createAnnouncement(Announcement announcement) throws IllegalAccessException, ObjectNotFoundException {
    if (announcement == null) {
      throw new IllegalArgumentException("announcement is mandatory");
    }
    if (announcement.getId() != 0) {
      throw new IllegalArgumentException("announcement id must be equal to 0");
    }
    if ( announcement.getChallenge() == null) {
      throw new IllegalArgumentException("challenge must not be null or empty");
    }
    if ( announcement.getAssignee() == null) {
      throw new IllegalArgumentException("announcement assignee must have at least one winner");
    }
    Challenge challenge = challengeStorage.getChallengeById(announcement.getChallenge().getId());
    if (challenge == null) {
      throw new ObjectNotFoundException("challenge does not exist");
    }
    ExoSocialActivity activity = this.createActivity(announcement);
    IdentityManager identityManager = CommonsUtils.getService(IdentityManager.class);
    Space space = Utils.getSpaceById(String.valueOf(challenge.getAudience()));
    if (space == null) {
      throw new ObjectNotFoundException("space does not exist");
    }

    Identity owner = identityManager.getOrCreateIdentity("space",space.getPrettyName());

    activity = activityStorage.saveActivity(owner, activity);
    announcement.setActivityId(Long.parseLong(activity.getId()));
    announcement = announcementStorage.saveAnnouncement(announcement);

    return announcement;
  }

  private ExoSocialActivity createActivity(Announcement announcement){
    ExoSocialActivityImpl activity = new ExoSocialActivityImpl();
    activity.setType(ANNOUNCEMENT_ACTIVITY_TYPE);
    activity.setTitle( this.getAssigneeFullNames(announcement.getAssignee()) +" succeeded this challenge");
    activity.setUserId(String.valueOf(announcement.getCreator()));
    Map<String, String>  params = new LinkedHashMap();
    ((Map)params).put("announcementTitle",this.getAssigneeFullNames(announcement.getAssignee()) +" succeeded this challenge");
    ((Map)params).put("announcementComment", announcement.getComment());
    ((Map)params).put("announcementDescription", announcement.getChallenge().getDescription());
    activity.setTemplateParams(params);
    return activity;

  }
  private String getAssigneeFullNames(List<Long> assignee){
    if ( assignee == null) {
      throw new IllegalArgumentException("announcement assignee must have at least one winner");
    }
    String AssigneeFullNames = "";
    List<UserInfo> AssigneeIdentityList =  Utils.getUsersByIds(assignee);
    for ( UserInfo user: AssigneeIdentityList ) {
      String userLink = "<a href='/portal/dw/profile/" + user.getRemoteId() + "'>" + user.getFullName() + "</a>";
      userLink = StringEscapeUtils.unescapeHtml(userLink);
      AssigneeFullNames = AssigneeFullNames + " " + userLink;
    }
    return AssigneeFullNames;
  }

}
