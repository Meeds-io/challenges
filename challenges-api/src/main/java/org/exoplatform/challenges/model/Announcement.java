package org.exoplatform.challenges.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Announcement implements Cloneable {
  private long id;
  private Challenge challenge;
  private List<Long> assignee;
  private String comment;
  private Long creator;
  private Date createdDate;
  private Long activityId;

  @Override
  public Announcement clone() { // NOSONAR
    return new Announcement(id,
                            challenge,
                            assignee,
                            comment,
                            creator,
                            createdDate,
                            activityId);
  }
}
