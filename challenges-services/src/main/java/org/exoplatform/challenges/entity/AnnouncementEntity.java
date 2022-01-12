package org.exoplatform.challenges.entity;

import org.exoplatform.commons.api.persistence.ExoEntity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Entity(name = "Announcement")
@ExoEntity
@Table(name = "ANNOUNCEMENT")
@NamedQueries({
    @NamedQuery(name = "Announcement.findAllAnnouncementByChallenge", query = "SELECT DISTINCT a FROM Announcement a where a.challenge.id = :challengeId order by a.createdDate desc"),
    @NamedQuery(name = "Announcement.countAnnouncementsByChallenge", query = "SELECT COUNT(us) FROM Announcement a LEFT JOIN a.assignee us where a.challenge.id = :challengeId"), })

public class AnnouncementEntity implements Serializable {

  @Id
  @SequenceGenerator(name = "SEQ_ANNOUNCEMENT_ID", sequenceName = "SEQ_ANNOUNCEMENT_ID")
  @GeneratedValue(strategy = GenerationType.AUTO, generator = "SEQ_ANNOUNCEMENT_ID")
  @Column(name = "ANNOUNCEMENT_ID", nullable = false)
  private Long            id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "CHALLENGE_ID", referencedColumnName = "CHALLENGE_ID")
  private ChallengeEntity challenge;

  @ElementCollection
  @CollectionTable(name = "ANNOUNCEMENT_ASSIGNEE", joinColumns = @JoinColumn(name = "ANNOUNCEMENT_ID"))
  @Column(name = "ASSIGNEE_ID")
  private List<Long>      assignee;

  @Column(name = "COMMENT")
  private String          comment;

  @Column(name = "CREATOR_ID")
  private Long            creator;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "CREATED_DATE", nullable = false)
  private Date            createdDate;

  @Column(name = "ACTIVITY_ID")
  private Long            activityId;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public ChallengeEntity getChallenge() {
    return challenge;
  }

  public void setChallenge(ChallengeEntity challenge) {
    this.challenge = challenge;
  }

  public List<Long> getAssignee() {
    return assignee;
  }

  public void setAssignee(List<Long> assignee) {
    this.assignee = assignee;
  }

  public String getComment() {
    return comment;
  }

  public void setComment(String comment) {
    this.comment = comment;
  }

  public Long getCreator() {
    return creator;
  }

  public void setCreator(Long creator) {
    this.creator = creator;
  }

  public Date getCreatedDate() {
    return createdDate;
  }

  public void setCreatedDate(Date createdDate) {
    this.createdDate = createdDate;
  }

  public Long getActivityId() {
    return activityId;
  }

  public void setActivityId(Long activityId) {
    this.activityId = activityId;
  }
}
