package org.exoplatform.challenges.storage;

import org.exoplatform.challenges.dao.AnnouncementDAO;
import org.exoplatform.challenges.entity.AnnouncementEntity;
import org.exoplatform.challenges.entity.ChallengeEntity;
import org.exoplatform.challenges.model.Announcement;
import org.exoplatform.challenges.model.Challenge;
import org.exoplatform.challenges.utils.EntityMapper;
import org.exoplatform.challenges.utils.Utils;
import org.exoplatform.social.core.identity.model.Identity;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PowerMockIgnore("javax.management.*")
public class AnnouncementStorageTest {


  private AnnouncementDAO announcementDAO;

  private ChallengeStorage challengeStorage;
  private AnnouncementStorage announcementStorage;

  @Before
  public void setUp() throws Exception { // NOSONAR
    announcementDAO = mock(AnnouncementDAO.class);
    challengeStorage = mock(ChallengeStorage.class);
    announcementStorage = new AnnouncementStorage(announcementDAO,challengeStorage);
  }

  @PrepareForTest({ Utils.class, EntityMapper.class })
  @Test
  public void testSaveAnnouncement() {
    Date startDate = new Date(System.currentTimeMillis());
    Date endDate = new Date(System.currentTimeMillis() + 1);
    Challenge challenge = new Challenge(1l,
        "new challenge",
        "challenge description",
        1l,
        startDate.toString(),
        endDate.toString(),
        true,
        false,
        Collections.emptyList());

    ChallengeEntity challengeEntity = new ChallengeEntity();
    challengeEntity.setDescription(challenge.getDescription());
    challengeEntity.setTitle(challenge.getTitle());
    challengeEntity.setStartDate(startDate);
    challengeEntity.setEndDate(endDate);
    challengeEntity.setId(challenge.getId());

    List<Long> assignee = new ArrayList<Long>();
    assignee.add(1L);
    Date createDate = new Date(System.currentTimeMillis());
    Announcement announcement = new Announcement(0,
        challenge.getId(),
        assignee,
        "announcement comment",
        1L,
        createDate.toString(),
        null);

    AnnouncementEntity announcementEntity = new AnnouncementEntity();
    announcementEntity.setAssignee(announcement.getAssignee());
    announcementEntity.setCreator(announcement.getCreator());
    announcementEntity.setChallenge(challengeEntity);
    announcementEntity.setComment(announcement.getComment());
    announcementEntity.setCreatedDate(createDate);

    AnnouncementEntity newAnnouncementEntity = new AnnouncementEntity();
    newAnnouncementEntity.setAssignee(announcementEntity.getAssignee());
    newAnnouncementEntity.setCreator(announcementEntity.getCreator());
    newAnnouncementEntity.setChallenge(challengeEntity);
    newAnnouncementEntity.setComment(announcementEntity.getComment());
    newAnnouncementEntity.setCreatedDate(createDate);
    newAnnouncementEntity.setId(1l);

    Announcement announcementFromEntity = new Announcement();
    announcementFromEntity.setAssignee(newAnnouncementEntity.getAssignee());
    announcementFromEntity.setCreator(newAnnouncementEntity.getCreator());
    announcementFromEntity.setComment(newAnnouncementEntity.getComment());
    announcementFromEntity.setCreatedDate(createDate.toString());
    announcementFromEntity.setChallengeId(newAnnouncementEntity.getChallenge().getId());
    announcementFromEntity.setId(newAnnouncementEntity.getId());


    PowerMockito.mockStatic(Utils.class);
    PowerMockito.mockStatic(EntityMapper.class);
    Identity identity = mock(Identity.class);
    when(announcementDAO.create(anyObject())).thenReturn(newAnnouncementEntity);
    when(challengeStorage.getChallengeById(anyLong())).thenReturn(challenge);
    when(Utils.getIdentityByTypeAndId(any(), any())).thenReturn(identity);
    when(EntityMapper.toEntity(challenge)).thenReturn(challengeEntity);
    when(EntityMapper.toEntity(announcement,challenge)).thenReturn(announcementEntity);
    when(EntityMapper.fromEntity(newAnnouncementEntity)).thenReturn(announcementFromEntity);

    Announcement createdAnnouncement = announcementStorage.saveAnnouncement(announcement);


    // Then
    assertNotNull(createdAnnouncement);
    assertEquals(createdAnnouncement.getId(), 1l);
    announcementFromEntity.setActivityId(createdAnnouncement.getActivityId());
    assertEquals(announcementFromEntity, createdAnnouncement);
  }

  @PrepareForTest({ EntityMapper.class })
  @Test
 public void testGetAnnouncementById(){
   Date startDate = new Date(System.currentTimeMillis());
   Date endDate = new Date(System.currentTimeMillis() + 1);
   ChallengeEntity challengeEntity = new ChallengeEntity();
   challengeEntity.setDescription("challenge description");
   challengeEntity.setTitle("new challenge");
   challengeEntity.setStartDate(startDate);
   challengeEntity.setEndDate(endDate);
   challengeEntity.setId(1l);

   List<Long> assignee = new ArrayList<Long>();
   assignee.add(1L);
   Date createDate = new Date(System.currentTimeMillis());

   AnnouncementEntity announcementEntity = new AnnouncementEntity();
   announcementEntity.setId(1l);
   announcementEntity.setAssignee(assignee);
   announcementEntity.setCreator(1L);
   announcementEntity.setChallenge(challengeEntity);
   announcementEntity.setComment("announcement comment");
   announcementEntity.setCreatedDate(createDate);

   Announcement announcementFromEntity = new Announcement();
   announcementFromEntity.setId(announcementEntity.getId());
   announcementFromEntity.setAssignee(announcementEntity.getAssignee());
   announcementFromEntity.setCreator(announcementEntity.getCreator());
   announcementFromEntity.setComment(announcementEntity.getComment());
   announcementFromEntity.setCreatedDate(createDate.toString());
   announcementFromEntity.setChallengeId(announcementEntity.getChallenge().getId());
   announcementFromEntity.setId(announcementEntity.getId());


   PowerMockito.mockStatic(EntityMapper.class);
   when(announcementDAO.find(anyLong())).thenReturn(announcementEntity);
   when(EntityMapper.fromEntity(announcementEntity)).thenReturn(announcementFromEntity);

   Announcement createdAnnouncement = announcementStorage.getAnnouncementById(1l);

   // Then
   assertNotNull(createdAnnouncement);
   assertEquals(createdAnnouncement.getId(), 1l);
   assertEquals(announcementFromEntity, createdAnnouncement);
 }
  @PrepareForTest({ EntityMapper.class })
  @Test
 public void testGetAnnouncementByChallengeId(){
   Date startDate = new Date(System.currentTimeMillis());
   Date endDate = new Date(System.currentTimeMillis() + 1);
   ChallengeEntity challengeEntity = new ChallengeEntity();
   challengeEntity.setDescription("challenge description");
   challengeEntity.setTitle("new challenge");
   challengeEntity.setStartDate(startDate);
   challengeEntity.setEndDate(endDate);
   challengeEntity.setId(1l);

   List<Long> assignee = new ArrayList<Long>();
   assignee.add(1L);
   Date createDate = new Date(System.currentTimeMillis());

   AnnouncementEntity announcementEntity1 = new AnnouncementEntity();
   announcementEntity1.setId(1l);
   announcementEntity1.setAssignee(assignee);
   announcementEntity1.setCreator(1L);
   announcementEntity1.setChallenge(challengeEntity);
   announcementEntity1.setComment("announcement comment 1");
   announcementEntity1.setCreatedDate(createDate);

   Announcement announcementFromEntity1 = new Announcement();
   announcementFromEntity1.setId(announcementEntity1.getId());
   announcementFromEntity1.setAssignee(announcementEntity1.getAssignee());
   announcementFromEntity1.setCreator(announcementEntity1.getCreator());
   announcementFromEntity1.setComment(announcementEntity1.getComment());
   announcementFromEntity1.setCreatedDate(createDate.toString());
   announcementFromEntity1.setChallengeId(announcementEntity1.getChallenge().getId());
   announcementFromEntity1.setId(announcementEntity1.getId());

   AnnouncementEntity announcementEntity2 = new AnnouncementEntity();
   announcementEntity2.setId(1l);
   announcementEntity2.setAssignee(assignee);
   announcementEntity2.setCreator(1L);
   announcementEntity2.setChallenge(challengeEntity);
   announcementEntity2.setComment("announcement comment 2");
   announcementEntity2.setCreatedDate(createDate);

   Announcement announcementFromEntity2 = new Announcement();
   announcementFromEntity2.setId(announcementEntity2.getId());
   announcementFromEntity2.setAssignee(announcementEntity2.getAssignee());
   announcementFromEntity2.setCreator(announcementEntity2.getCreator());
   announcementFromEntity2.setComment(announcementEntity2.getComment());
   announcementFromEntity2.setCreatedDate(createDate.toString());
   announcementFromEntity2.setChallengeId(announcementEntity2.getChallenge().getId());
   announcementFromEntity2.setId(announcementEntity2.getId());

   AnnouncementEntity announcementEntity3 = new AnnouncementEntity();
   announcementEntity3.setId(1l);
   announcementEntity3.setAssignee(assignee);
   announcementEntity3.setCreator(1L);
   announcementEntity3.setChallenge(challengeEntity);
   announcementEntity3.setComment("announcement comment 3");
   announcementEntity3.setCreatedDate(createDate);

   Announcement announcementFromEntity3 = new Announcement();
   announcementFromEntity3.setId(announcementEntity3.getId());
   announcementFromEntity3.setAssignee(announcementEntity3.getAssignee());
   announcementFromEntity3.setCreator(announcementEntity3.getCreator());
   announcementFromEntity3.setComment(announcementEntity3.getComment());
   announcementFromEntity3.setCreatedDate(createDate.toString());
   announcementFromEntity3.setChallengeId(announcementEntity3.getChallenge().getId());
   announcementFromEntity3.setId(announcementEntity3.getId());

   List<Announcement> announcementList = new ArrayList<>();
   announcementList.add(announcementFromEntity1);
   announcementList.add(announcementFromEntity2);
   announcementList.add(announcementFromEntity3);

   List<AnnouncementEntity> announcementEntities = new ArrayList<>();
    announcementEntities.add(announcementEntity1);
    announcementEntities.add(announcementEntity2);
    announcementEntities.add(announcementEntity3);

   PowerMockito.mockStatic(EntityMapper.class);
   when(announcementDAO.findAllAnnouncementByChallenge(anyLong(),anyInt(),anyInt())).thenReturn(announcementEntities);
   when(EntityMapper.fromAnnouncementEntities(announcementEntities)).thenReturn(announcementList);

    List<Announcement> announcementListByChallenge = announcementStorage.findAllAnnouncementByChallenge(challengeEntity.getId(),0,10);

   // Then
   assertNotNull(announcementListByChallenge);
   assertEquals(announcementListByChallenge.size(),3);
 }
}
