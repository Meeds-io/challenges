package org.exoplatform.challenges.listener;

import org.apache.commons.lang.StringEscapeUtils;
import org.exoplatform.challenges.model.Announcement;
import org.exoplatform.challenges.model.UserInfo;
import org.exoplatform.challenges.storage.AnnouncementStorage;
import org.exoplatform.challenges.utils.Utils;
import org.exoplatform.commons.exception.ObjectNotFoundException;
import org.exoplatform.container.ExoContainer;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.container.component.RequestLifeCycle;
import org.exoplatform.services.listener.Event;
import org.exoplatform.services.listener.Listener;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.social.core.activity.model.ExoSocialActivity;
import org.exoplatform.social.core.activity.model.ExoSocialActivityImpl;
import org.exoplatform.social.core.identity.model.Identity;
import org.exoplatform.social.core.space.model.Space;
import org.exoplatform.social.core.storage.api.ActivityStorage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnnouncementActivityGenerator extends Listener<AnnouncementStorage, Announcement> {
  private static final Log LOG = ExoLogger.getLogger(AnnouncementActivityGenerator.class);

  public static final String ANNOUNCEMENT_ACTIVITY_TYPE = "challenges-announcement";

  private ExoContainer container;

  private ActivityStorage activityStorage;

  public AnnouncementActivityGenerator(ActivityStorage activityStorage, ExoContainer container) {
    this.activityStorage = activityStorage;
    this.container = container;
  }

  @Override
  public void onEvent(Event<AnnouncementStorage, Announcement> event) throws ObjectNotFoundException {
    ExoContainerContext.setCurrentContainer(container);
    RequestLifeCycle.begin(container);
    try {
      Announcement announcement = event.getData();
      AnnouncementStorage announcementStorage = event.getSource();
      ExoSocialActivity activity = createActivity(announcement);
      announcement.setActivityId(Long.parseLong(activity.getId()));
      announcementStorage.saveAnnouncement(announcement);
    } finally {
      RequestLifeCycle.end();
    }
  }


  private ExoSocialActivity createActivity(Announcement announcement) throws ObjectNotFoundException {
    ExoSocialActivityImpl activity = new ExoSocialActivityImpl();
    activity.setType(ANNOUNCEMENT_ACTIVITY_TYPE);
    activity.setTitle(this.getAssigneeUserNames(announcement.getAssignee()));
    activity.setUserId(String.valueOf(announcement.getCreator()));
    Map<String, String> params = new HashMap<>();
    params.put("announcementAssigneeUsername", this.getAssigneeUserNames(announcement.getAssignee()));
    params.put("announcementAssigneeFullName", this.getAssigneeFullNames(announcement.getAssignee()));
    params.put("announcementComment", announcement.getComment());
    params.put("announcementDescription", announcement.getChallenge().getTitle());
    activity.setTemplateParams(params);
    Space space = Utils.getSpaceById(String.valueOf(announcement.getChallenge().getAudience()));
    if (space == null) {
      throw new ObjectNotFoundException("space does not exist");
    }
    Identity owner = Utils.getIdentityByTypeAndId("space", space.getPrettyName());
    return activityStorage.saveActivity(owner, activity);
  }

  private String getAssigneeFullNames(List<Long> assignee) {
    if (assignee.isEmpty()) {
      throw new IllegalArgumentException("announcement assignee must have at least one winner");
    }
    String AssigneeFullNames = "";
    List<UserInfo> AssigneeIdentityList = Utils.getUsersByIds(assignee);
    for (UserInfo user : AssigneeIdentityList) {
      AssigneeFullNames = AssigneeFullNames  + user.getFullName()  + "#";
    }
    return AssigneeFullNames;
  }

  private String getAssigneeUserNames(List<Long> assignee) {
    if (assignee.isEmpty()) {
      throw new IllegalArgumentException("announcement assignee must have at least one winner");
    }
    String AssigneeUserNames = "";
    List<UserInfo> AssigneeIdentityList = Utils.getUsersByIds(assignee);
    for (UserInfo user : AssigneeIdentityList) {
      AssigneeUserNames = AssigneeUserNames + user.getRemoteId()  + "#";
    }
    return AssigneeUserNames;
  }

}
