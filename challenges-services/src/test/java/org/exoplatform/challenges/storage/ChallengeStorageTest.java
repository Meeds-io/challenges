package org.exoplatform.challenges.storage;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.exoplatform.challenges.entity.ChallengeEntity;
import org.exoplatform.challenges.model.Challenge;
import org.exoplatform.challenges.utils.EntityMapper;
import org.exoplatform.challenges.utils.Utils;
import org.exoplatform.social.core.identity.model.Identity;
import org.junit.Before;
import org.junit.Test;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.modules.junit4.PowerMockRunner;
import org.exoplatform.challenges.dao.ChallengeDAO;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;

import java.util.Collections;
import java.util.Date;

@RunWith(PowerMockRunner.class)
@PowerMockIgnore("javax.management.*")
public class ChallengeStorageTest {
  private ChallengeDAO     challengeDAO;

  private ChallengeStorage challengeStorage;

  @Before
  public void setUp() throws Exception { // NOSONAR
    challengeDAO = mock(ChallengeDAO.class);
    challengeStorage = new ChallengeStorage(challengeDAO);
  }

  @PrepareForTest({ Utils.class, EntityMapper.class })
  @Test
  public void testSaveChallenge() {
    // Given
    Challenge challenge = new Challenge(0,
                                        "new challenge",
                                        "challenge description",
                                        1l,
                                        new Date(System.currentTimeMillis()).toString(),
                                        new Date(System.currentTimeMillis() + 1).toString(),
                                        true,
                                        false,
                                        Collections.emptyList());
    long challengeId = 0;
    ChallengeEntity challengeEntity = new ChallengeEntity();
    challengeEntity.setDescription("challenge description");
    challengeEntity.setTitle("new challenge");
    challengeEntity.setStartDate(new Date(System.currentTimeMillis()));
    challengeEntity.setEndDate(new Date(System.currentTimeMillis() + 1));
    challengeEntity.setId(challengeId);
    when(challengeDAO.create(anyObject())).thenAnswer(new Answer<ChallengeEntity>() {
      @Override
      public ChallengeEntity answer(InvocationOnMock invocation) throws Throwable {
        ChallengeEntity challengeEntity = invocation.getArgument(0, ChallengeEntity.class);
        challengeEntity.setId(1l);
        challengeEntity.setStartDate(new Date(System.currentTimeMillis()));
        challengeEntity.setEndDate(new Date(System.currentTimeMillis() + 1));
        return challengeEntity;
      }
    });
    PowerMockito.mockStatic(Utils.class);
    PowerMockito.mockStatic(EntityMapper.class);
    Identity identity = mock(Identity.class);
    when(Utils.getIdentityByTypeAndId(any())).thenReturn(identity);
    when(EntityMapper.toEntity(challenge)).thenReturn(challengeEntity);
    Challenge challengeFromEntity = new Challenge(1l,
                                                  "new challenge",
                                                  "challenge description",
                                                  1l,
                                                  new Date(System.currentTimeMillis()).toString(),
                                                  new Date(System.currentTimeMillis() + 1).toString(),
                                                  true,
                                                  false,
                                                  Collections.emptyList());
    when(EntityMapper.fromEntity(challengeEntity)).thenReturn(challengeFromEntity);
    // When
    Challenge createdChallenge = challengeStorage.saveChallenge(challenge, "root");

    // Then
    assertNotNull(createdChallenge);
    assertEquals(createdChallenge.getId(), 1l);
    challenge.setId(createdChallenge.getId());
    assertEquals(challenge, createdChallenge);
  }

  @PrepareForTest({ EntityMapper.class })
  @Test
  public void testGetChallengeById() {
    ChallengeEntity challengeEntity = new ChallengeEntity();
    challengeEntity.setTitle("Challenge");
    challengeEntity.setDescription("description");
    challengeEntity.setStartDate(new Date(System.currentTimeMillis()));
    challengeEntity.setEndDate(new Date(System.currentTimeMillis() + 1));
    challengeEntity.setId(1l);
    challengeEntity.setAudience(1l);
    challengeEntity.setManagers(Collections.emptyList());
    PowerMockito.mockStatic(EntityMapper.class);

    Challenge challenge = new Challenge(1l,
                                        "Challenge",
                                        "description",
                                        1l,
                                        new Date(System.currentTimeMillis()).toString(),
                                        new Date(System.currentTimeMillis() + 1).toString(),
                                        true,
                                        false,
                                        Collections.emptyList());

    when(EntityMapper.fromEntity(challengeEntity)).thenReturn(challenge);
    when(challengeDAO.find(eq(1l))).thenReturn(challengeEntity);

    Challenge notExistingChallenge = challengeStorage.getChallengeById(2l);
    assertNull(notExistingChallenge);
    verify(challengeDAO, times(1)).find(anyLong());

    Challenge result = challengeStorage.getChallengeById(1l);
    assertNotNull(result);
    verify(challengeDAO, times(2)).find(anyLong());

  }
}
