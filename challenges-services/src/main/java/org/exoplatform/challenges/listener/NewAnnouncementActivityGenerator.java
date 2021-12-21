package org.exoplatform.challenges.listener;

import org.apache.commons.lang.StringEscapeUtils;
import org.exoplatform.challenges.model.Announcement;
import org.exoplatform.challenges.model.UserInfo;
import org.exoplatform.challenges.storage.AnnouncementStorage;
import org.exoplatform.challenges.utils.Utils;
import org.exoplatform.commons.exception.ObjectNotFoundException;
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

public class NewAnnouncementActivityGenerator extends Listener<AnnouncementStorage, Announcement> {
  private static final Log LOG = ExoLogger.getLogger(NewAnnouncementActivityGenerator.class);

  public static final String ANNOUNCEMENT_ACTIVITY_TYPE = "exo-announcement:activity";


  private ActivityStorage activityStorage;

  public NewAnnouncementActivityGenerator(ActivityStorage activityStorage) {
    this.activityStorage = activityStorage;
  }

  @Override
  public void onEvent(Event<AnnouncementStorage, Announcement> event) throws ObjectNotFoundException {

    Announcement announcement = event.getData();
    AnnouncementStorage announcementStorage = event.getSource();

    Space space = Utils.getSpaceById(String.valueOf(announcement.getChallenge().getAudience()));
    if (space == null) {
      throw new ObjectNotFoundException("space does not exist");
    }
    Identity owner = Utils.getIdentityByTypeAndId("space", space.getPrettyName());
    ExoSocialActivity activity = createActivity(announcement);
    activity = activityStorage.saveActivity(owner, activity);
    announcement.setActivityId(Long.parseLong(activity.getId()));
    announcementStorage.saveAnnouncement(announcement);
  }


  private ExoSocialActivity createActivity(Announcement announcement) {
    ExoSocialActivityImpl activity = new ExoSocialActivityImpl();
    activity.setType(ANNOUNCEMENT_ACTIVITY_TYPE);
    activity.setTitle(this.getAssigneeFullNames(announcement.getAssignee()) + " succeeded this challenge");
    activity.setUserId(String.valueOf(announcement.getCreator()));
    Map<String, String> params = new HashMap<>();
    params.put("announcementTitle", this.getAssigneeFullNames(announcement.getAssignee()) + " succeeded this challenge");
    params.put("announcementComment", announcement.getComment());
    params.put("announcementDescription", announcement.getChallenge().getDescription());
    activity.setTemplateParams(params);
    return activity;

  }

  private String getAssigneeFullNames(List<Long> assignee) {
    if (assignee.isEmpty()) {
      throw new IllegalArgumentException("announcement assignee must have at least one winner");
    }
    String AssigneeFullNames = "";
    List<UserInfo> AssigneeIdentityList = Utils.getUsersByIds(assignee);
    for (UserInfo user : AssigneeIdentityList) {
      String userLink = "<a href='/portal/dw/profile/" + user.getRemoteId() + "'>" + user.getFullName() + "</a>";
      userLink = StringEscapeUtils.unescapeHtml(userLink);
      AssigneeFullNames = AssigneeFullNames + " " + userLink;
    }
    return AssigneeFullNames;
  }

}
