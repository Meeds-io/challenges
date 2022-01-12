package org.exoplatform.challenges.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Challenge implements Cloneable {
  private long       id;

  private String     title;

  private String     description;

  private long       audience;

  private String     startDate;

  private String     endDate;

  private boolean    canEdit;

  private boolean    canAnnounce;

  private List<Long> managers;

  private Long       points;

  @Override
  public Challenge clone() { // NOSONAR
    return new Challenge(id, title, description, audience, startDate, endDate, canEdit, canAnnounce, managers, points);
  }
}
