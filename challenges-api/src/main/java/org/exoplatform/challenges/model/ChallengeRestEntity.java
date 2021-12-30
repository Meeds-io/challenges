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

  private long                         id;

  private String                       title;

  private String                       description;

  private Space                        space;

  private String                       startDate;

  private String                       endDate;

  private UserInfo                     userInfo;

  private List<UserInfo>               managers;

  private List<AnnouncementRestEntity> announcements;

  public ChallengeRestEntity clone() { // NOSONAR
    return new ChallengeRestEntity(id,
                                   title,
                                   description,
                                   space,
                                   startDate,
                                   endDate,
                                   userInfo,
                                   managers,
                                   announcements);
  }
}
