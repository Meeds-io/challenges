package org.exoplatform.challenges.service;

import org.exoplatform.challenges.model.Challenge;

public interface ChallengeService {

  /**
   * Creates a new challenge
   *
   * @param challenge {@link Challenge} object to create
   * @param username User name creating calendar
   * @return created {@link Challenge} with generated technical identifier
   * @throws IllegalAccessException when user is not authorized to create a
   *           challenge for the designated owner defined in object
   */
  Challenge createChallenge(Challenge challenge, String username) throws IllegalAccessException;

    /**
     * Retrieves a challenge identified by its technical identifier.
     *
     * @param challengeId technical identifier of a challenge
     * @param username User name accessing calendar
     * @return A {@link Challenge} object
     * @throws IllegalAccessException when user is not authorized to access
     *           challenge
     */
    Challenge getChallengeById(long challengeId, String username) throws IllegalAccessException;
}
