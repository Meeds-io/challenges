package org.exoplatform.challenges.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.exoplatform.social.core.space.model.Space;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RestEntity implements Cloneable {

  private long          id;

  private String        title;

  private String        description;

  private Space         space;

  private String        startDate;

  private String        endDate;

  private boolean       canEdit;

  private boolean       canAnnounce;

  private List<UserInfo> managers;

  public RestEntity clone() { // NOSONAR
    return new RestEntity(id, title, description, space, startDate, endDate, canEdit, canAnnounce, managers);
  }
}
