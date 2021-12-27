package org.exoplatform.challenges.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Announcement implements Cloneable {
  private long       id;

  private Long       challengeId;

  private List<Long> assignee;

  private String     comment;

  private Long       creator;

  private String     createdDate;

  private Long       activityId;

  @Override
  public Announcement clone() { // NOSONAR
    return new Announcement(id, challengeId, assignee, comment, creator, createdDate, activityId);
  }
}
