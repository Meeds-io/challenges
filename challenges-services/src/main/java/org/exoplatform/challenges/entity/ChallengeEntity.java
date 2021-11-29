package org.exoplatform.challenges.entity;

import org.exoplatform.commons.api.persistence.ExoEntity;
import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Entity(name = "Challenge")
@ExoEntity
@Table(name = "CHALLENGE")
public class ChallengeEntity implements Serializable {

  @Id
  @SequenceGenerator(name = "SEQ_CHALLENGE_ID", sequenceName = "SEQ_CHALLENGE_ID")
  @GeneratedValue(strategy = GenerationType.AUTO, generator = "SEQ_CHALLENGE_ID")
  @Column(name = "CHALLENGE_ID", nullable = false)
  private Long              id;

  @Column(name = "TITLE", nullable = false)
  private String            title;

  @Column(name = "DESCRIPTION")
  private String            description;

  @Column(name = "AUDIENCE_ID")
  private Long              audience;

  @Column(name = "START_DATE", nullable = false)
  private Date              startDate;

  @Column(name = "END_DATE", nullable = false)
  private Date              endDate;

  @ElementCollection
  @CollectionTable(name = "CHALLENGE_MANAGER", joinColumns = @JoinColumn(name = "CHALLENGE_ID"))
  @Column(name = "MANAGER_ID")
  private List<Long>        managers;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Long getAudience() {
    return audience;
  }

  public void setAudience(Long audience) {
    this.audience = audience;
  }

  public Date getStartDate() {
    return startDate;
  }

  public void setStartDate(Date startDate) {
    this.startDate = startDate;
  }

  public Date getEndDate() {
    return endDate;
  }

  public void setEndDate(Date endDate) {
    this.endDate = endDate;
  }

  public List<Long> getManagers() {
    return managers;
  }

  public void setManagers(List<Long> managers) {
    this.managers = managers;
  }
}
