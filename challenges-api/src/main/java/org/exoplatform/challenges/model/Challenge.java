package org.exoplatform.challenges.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Challenge implements Cloneable {
  private long id;
  private String title;
  private String description;
  private long audience;
  private Date startDate;
  private Date endDate;
  private List<Long> managers;

  @Override
  public Challenge clone() { // NOSONAR
    return new Challenge(id,
                         title,
                         description,
                         audience,
                         startDate,
                         endDate,
                         managers);
  }
}
