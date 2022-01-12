package org.exoplatform.challenges.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AnnouncementRestEntity implements Cloneable {

  private long           id;

  private Long           challengeId;

  private List<UserInfo> assignee;

  private String         comment;

  private UserInfo       creator;

  private String         createdDate;

  private Long           activityId;

  private Long           points;

  public AnnouncementRestEntity clone() { // NOSONAR
    return new AnnouncementRestEntity(id, challengeId, assignee, comment, creator, createdDate, activityId, points);
  }

}
