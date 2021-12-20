package org.exoplatform.challenges.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.exoplatform.social.core.space.model.Space;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChallengeRestEntity implements Cloneable {

  private long          id;

  private String        title;

  private String        description;

  private Space         space;

  private String        startDate;

  private String        endDate;

  private boolean       canEdit;

  private boolean       canAnnounce;

  private List<UserInfo> managers;

  private List<UserInfo> winners;

  public ChallengeRestEntity clone() { // NOSONAR
    return new ChallengeRestEntity(id, title, description, space, startDate, endDate, canEdit, canAnnounce, managers, winners);
  }
}
