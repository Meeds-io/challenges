package org.exoplatform.challenges.service;

import org.exoplatform.challenges.model.Announcement;
import org.exoplatform.challenges.model.Challenge;
import org.exoplatform.challenges.storage.AnnouncementStorage;
import org.exoplatform.challenges.storage.ChallengeStorage;
import org.exoplatform.challenges.utils.EntityMapper;
import org.exoplatform.challenges.utils.Utils;
import org.exoplatform.commons.exception.ObjectNotFoundException;
import org.exoplatform.commons.testing.BaseExoTestCase;
import org.exoplatform.services.listener.ListenerService;
import org.exoplatform.services.security.ConversationState;
import org.exoplatform.social.core.identity.model.Identity;
import org.exoplatform.social.core.manager.IdentityManager;
import org.exoplatform.social.core.space.model.Space;
import org.exoplatform.social.core.space.spi.SpaceService;
import org.junit.After;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PowerMockIgnore("javax.management.*")
public class AnnouncementServiceTest extends BaseExoTestCase {
  private AnnouncementStorage announcementStorage;

  private ChallengeStorage    challengeStorage;

  private AnnouncementService announcementService;

  private ListenerService     listenerService;

  private SpaceService        spaceService;

  private IdentityManager     identityManager;

  @Before
  public void setUp() { // NOSONAR
    announcementStorage = mock(AnnouncementStorage.class);
    challengeStorage = mock(ChallengeStorage.class);
    spaceService = mock(SpaceService.class);
    identityManager = mock(IdentityManager.class);
    listenerService = mock(ListenerService.class);
    announcementService = new AnnouncementServiceImpl(announcementStorage, challengeStorage, listenerService);

  }

  @PrepareForTest({ Utils.class, EntityMapper.class })
  @Test
  public void testCreateAnnouncement() throws IllegalAccessException, ObjectNotFoundException {
    Challenge challenge = new Challenge(1,
                                        "new challenge",
                                        "challenge description",
                                        1l,
                                        new Date(System.currentTimeMillis()).toString(),
                                        new Date(System.currentTimeMillis() + 1).toString(),
                                        true,
                                        false,
                                        Collections.emptyList(),
                                        0l,
                                "challenge");
    List<Long> assignee = new ArrayList<Long>();
    assignee.add(1L);
    Announcement announcement = new Announcement(0,
                                                 challenge.getId(),
                                                 assignee,
                                                 "announcement comment",
                                                 1L,
                                                 new Date(System.currentTimeMillis()).toString(),
                                                 null,
                                                 0l);
    Announcement createdAnnouncement = new Announcement(1,
                                                        challenge.getId(),
                                                        new ArrayList<>(1),
                                                        "announcement comment",
                                                        1L,
                                                        new Date(System.currentTimeMillis()).toString(),
                                                        null,
                                                        0l);
    Identity spaceIdentity = new Identity();
    spaceIdentity.setId("1");
    spaceIdentity.setProviderId("space");
    spaceIdentity.setRemoteId("test_space");
    Identity rootIdentity = new Identity();
    rootIdentity.setId("1");
    rootIdentity.setProviderId("organization");
    rootIdentity.setRemoteId("root");

    when(challengeStorage.getChallengeById(challenge.getId())).thenReturn(challenge);
    String[] spaceMembers = { "root" };
    Space space = new Space();
    space.setId("1");
    space.setPrettyName("test_space");
    space.setDisplayName("test space");
    space.setGroupId("/spaces/test_space");
    space.setManagers(spaceMembers);
    space.setMembers(spaceMembers);
    space.setRedactors(new String[0]);
    when(spaceService.getSpaceById("1")).thenReturn(space);
    when(spaceService.isRedactor(space, "root")).thenReturn(true);
    when(identityManager.getOrCreateIdentity("space", "root")).thenReturn(spaceIdentity);
    when(identityManager.getOrCreateIdentity("organization", "root")).thenReturn(rootIdentity);
    when(announcementStorage.saveAnnouncement(announcement)).thenReturn(createdAnnouncement);
    PowerMockito.mockStatic(Utils.class);
    when(Utils.canAnnounce(any())).thenReturn(true);
    when(Utils.canEditChallenge(any())).thenReturn(true);
    Identity identity = mock(Identity.class);
    when(Utils.getIdentityByTypeAndId(any(), any())).thenReturn(identity);
    when(identity.getId()).thenReturn("1");
    Announcement newAnnouncement = announcementService.createAnnouncement(announcement, "root");

    assertNotNull(newAnnouncement);
    assertEquals(1l, newAnnouncement.getId());
  }

  @PrepareForTest({ Utils.class, EntityMapper.class })
  @Test
  public void testUpdateAnnouncement() throws IllegalAccessException, ObjectNotFoundException {
    Challenge challenge = new Challenge(1,
                                        "new challenge",
                                        "challenge description",
                                        1l,
                                        new Date(System.currentTimeMillis()).toString(),
                                        new Date(System.currentTimeMillis() + 1).toString(),
                                        true,
                                        false,
                                        Collections.emptyList(),
                                        0l,
                                        "challenge");
    List<Long> assignee = new ArrayList<Long>();
    assignee.add(1L);
    Announcement announcement = new Announcement(0,
                                                 challenge.getId(),
                                                 assignee,
                                                 "announcement comment",
                                                 1L,
                                                 new Date(System.currentTimeMillis()).toString(),
                                                 null,
                                                 0l);
    Announcement createdAnnouncement = new Announcement(1,
                                                        challenge.getId(),
                                                        new ArrayList<>(1),
                                                        "announcement comment",
                                                        1L,
                                                        new Date(System.currentTimeMillis()).toString(),
                                                        null,
                                                        0l);
    Announcement editedAnnouncement = new Announcement(1,
                                                       challenge.getId(),
                                                       new ArrayList<>(1),
                                                       "announcement comment",
                                                       1L,
                                                       new Date(System.currentTimeMillis()).toString(),
                                                       1L,
                                                       0l);
    Identity spaceIdentity = new Identity();
    spaceIdentity.setId("1");
    spaceIdentity.setProviderId("space");
    spaceIdentity.setRemoteId("test_space");
    Identity rootIdentity = new Identity();
    rootIdentity.setId("1");
    rootIdentity.setProviderId("organization");
    rootIdentity.setRemoteId("root");

    when(challengeStorage.getChallengeById(challenge.getId())).thenReturn(challenge);
    String[] spaceMembers = { "root" };
    Space space = new Space();
    space.setId("1");
    space.setPrettyName("test_space");
    space.setDisplayName("test space");
    space.setGroupId("/spaces/test_space");
    space.setManagers(spaceMembers);
    space.setMembers(spaceMembers);
    space.setRedactors(new String[0]);
    when(spaceService.getSpaceById("1")).thenReturn(space);
    when(spaceService.isRedactor(space, "root")).thenReturn(true);
    when(identityManager.getOrCreateIdentity("space", "root")).thenReturn(spaceIdentity);
    when(identityManager.getOrCreateIdentity("organization", "root")).thenReturn(rootIdentity);
    when(announcementStorage.saveAnnouncement(announcement)).thenReturn(createdAnnouncement);
    when(announcementStorage.saveAnnouncement(createdAnnouncement)).thenReturn(editedAnnouncement);
    when(announcementStorage.getAnnouncementById(1L)).thenReturn(createdAnnouncement);
    PowerMockito.mockStatic(Utils.class);
    when(Utils.canAnnounce(any())).thenReturn(true);
    when(Utils.canEditChallenge(any())).thenReturn(true);
    Identity identity = mock(Identity.class);
    when(Utils.getIdentityByTypeAndId(any(), any())).thenReturn(identity);
    when(identity.getId()).thenReturn("1");

    Announcement newAnnouncement = announcementService.createAnnouncement(announcement, "root");

    assertNotNull(newAnnouncement);

    newAnnouncement.setActivityId(1L);
    Announcement updatedAnnouncement = announcementService.updateAnnouncement(newAnnouncement);
    assertEquals(updatedAnnouncement, newAnnouncement);
  }

  @Test
  public void testGetAnnouncementByChallenge() throws IllegalAccessException, ObjectNotFoundException {
    Challenge challenge = new Challenge(1,
                                        "new challenge",
                                        "challenge description",
                                        1l,
                                        new Date(System.currentTimeMillis()).toString(),
                                        new Date(System.currentTimeMillis() + 1).toString(),
                                        true,
                                        false,
                                        Collections.emptyList(),
                                        0l,
                                "challenge");
    List<Long> assignee = new ArrayList<Long>();
    assignee.add(1L);
    Announcement announcement1 = new Announcement(0,
                                                  challenge.getId(),
                                                  assignee,
                                                  "announcement comment",
                                                  1L,
                                                  new Date(System.currentTimeMillis()).toString(),
                                                  null,
                                                  0l);
    Announcement announcement2 = new Announcement(1,
                                                  challenge.getId(),
                                                  new ArrayList<>(1),
                                                  "announcement comment",
                                                  1L,
                                                  new Date(System.currentTimeMillis()).toString(),
                                                  null,
                                                  0l);
    Announcement announcement3 = new Announcement(1,
                                                  challenge.getId(),
                                                  new ArrayList<>(1),
                                                  "announcement comment",
                                                  1L,
                                                  new Date(System.currentTimeMillis()).toString(),
                                                  1L,
                                                  0l);
    List<Announcement> announcementList = new ArrayList<>();
    announcementList.add(announcement1);
    announcementList.add(announcement2);
    announcementList.add(announcement3);

    when(challengeStorage.getChallengeById(challenge.getId())).thenReturn(challenge);
    when(announcementStorage.findAllAnnouncementByChallenge(challenge.getId(), 0, 10)).thenReturn(announcementList);

    List<Announcement> newAnnouncementList = announcementService.findAllAnnouncementByChallenge(challenge.getId(), 0, 10);

    assertNotNull(newAnnouncementList);
    assertEquals(announcementList, newAnnouncementList);
  }

}
