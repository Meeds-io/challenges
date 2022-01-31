package org.exoplatform.challenges.service;

import org.exoplatform.challenges.model.Announcement;
import org.exoplatform.challenges.model.Challenge;
import org.exoplatform.commons.exception.ObjectNotFoundException;

import java.util.List;

public interface AnnouncementService {

  /**
   * Creates a new announcement
   *
   * @param announcement {@link Announcement} object to create
   * @param username User name accessing announcement
   * @return created {@link Announcement} with generated technical identifier
   * @throws IllegalAccessException when user is not authorized to create a
   *           announcement for the designated owner defined in object
   */
  Announcement createAnnouncement(Announcement announcement, String username) throws IllegalAccessException,
                                                                              ObjectNotFoundException;

  /**
   * Update announcement
   *
   * @param announcement {@link Announcement} object to Update
   * @return a {@link Announcement} Object
   * @throws ObjectNotFoundException when the announcement identified by its
   *           technical identifier is not found
   */

  Announcement updateAnnouncement(Announcement announcement) throws ObjectNotFoundException;

  /**
   * Retrieves a announcement identified by its technical identifier.
   *
   * @param announcementId technical identifier of a challenge
   * @return A {@link Announcement} object
   * @throws ObjectNotFoundException when the announcement identified by its
   *           technical identifier is not found
   */

  Announcement getAnnouncementById(Long announcementId) throws ObjectNotFoundException;

  /**
   * Retrieves all Announcements by challengeId.
   *
   * @param offset Offset
   * @param limit Limit
   * @return A {@link List<Announcement>} object
   * @throws IllegalAccessException when user is not authorized to access
   *           announcement
   * @throws ObjectNotFoundException when the challenge identified by its
   *           technical identifier is not found
   */
  List<Announcement> findAllAnnouncementByChallenge(long challengeId, int offset, int limit) throws IllegalAccessException,
                                                                                             ObjectNotFoundException;

  /**
   * Retrieves number of all Announcements by challengeId.
   *
   * @return A {@link Long} number of announcements
   * @throws ObjectNotFoundException when the challenge identified by its
   *           technical identifier is not found
   */
  Long countAllAnnouncementsByChallenge(long challengeId) throws ObjectNotFoundException;

  /**
   * delete announcement by id.
   * @param id the id of challenge
   */
  void deleteAnnouncementById(Long id) throws Exception;

  /**
   * Retrieves all announcements.
   * @return A {@link List<Announcement>} object
   */
  List<Announcement> getAllAnnouncements() throws Exception;

}
