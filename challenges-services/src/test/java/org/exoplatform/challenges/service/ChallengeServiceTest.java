package org.exoplatform.challenges.service;

import org.exoplatform.challenges.model.Challenge;
import org.exoplatform.challenges.storage.ChallengeStorage;
import org.exoplatform.commons.exception.ObjectNotFoundException;
import org.exoplatform.social.core.space.model.Space;
import org.exoplatform.social.core.space.spi.SpaceService;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ChallengeServiceTest {

  private ChallengeStorage challengeStorage;

  private SpaceService     spaceService;

  private ChallengeService challengeService;

  @Before
  public void setUp() throws Exception { // NOSONAR
    challengeStorage = mock(ChallengeStorage.class);
    spaceService = mock(SpaceService.class);

    challengeService = new ChallengeServiceImpl(challengeStorage, spaceService);
  }

  @Test
  public void testCreateChallenge() throws IllegalAccessException {
    // Given
    Challenge challenge = new Challenge(0,
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
    Challenge challengeCreated = new Challenge(1l,
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
    Space space = new Space();
    when(spaceService.getSpaceById("1")).thenReturn(space);
    when(spaceService.isManager(space, "root")).thenReturn(true);
    when(challengeStorage.saveChallenge(challenge, "root")).thenReturn(challengeCreated);

    // When
    Challenge challenge1 = challengeService.createChallenge(challenge, "root");

    // Then
    assertNotNull(challenge1);
    assertEquals(1l, challenge1.getId());
  }

  @Test
  public void testUpdateChallenge() throws ObjectNotFoundException, IllegalAccessException {
    // Given
    Challenge challenge = new Challenge(1l,
                                        "update challenge",
                                        "challenge description",
                                        1l,
                                        new Date(System.currentTimeMillis()).toString(),
                                        new Date(System.currentTimeMillis() + 1).toString(),
                                        true,
                                        false,
                                        Collections.emptyList(),
                                        0l,
                                "challenge");
    Challenge challenge1 = new Challenge(1l,
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

    Challenge challenge2 = new Challenge(1l,
                                         "update challenge",
                                         "challenge description",
                                         1l,
                                         new Date(System.currentTimeMillis()).toString(),
                                         new Date(System.currentTimeMillis() + 1).toString(),
                                         true,
                                         false,
                                         Collections.emptyList(),
                                         0l,
                                  "challenge");
    Space space = new Space();
    when(spaceService.getSpaceById("1")).thenReturn(space);
    when(spaceService.isManager(space, "root")).thenReturn(true);
    when(challengeStorage.getChallengeById(1l)).thenReturn(challenge1);
    when(challengeStorage.saveChallenge(challenge, "root")).thenReturn(challenge2);

    // When
    Challenge challengeUpdated = challengeService.updateChallenge(challenge, "root");

    // Then
    assertNotNull(challengeUpdated);
    assertEquals(1l, challenge1.getId());
    assertEquals("update challenge", challengeUpdated.getTitle());
  }
}
