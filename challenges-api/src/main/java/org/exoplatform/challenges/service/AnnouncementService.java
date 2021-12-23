package org.exoplatform.challenges.service;

import org.exoplatform.challenges.model.Announcement;
import org.exoplatform.commons.exception.ObjectNotFoundException;

import java.util.List;

public interface AnnouncementService {

  /**
   * Creates a new announcement
   *
   * @param announcement {@link Announcement} object to create
   * @param username     User name accessing announcement
   * @return created {@link Announcement} with generated technical identifier
   * @throws IllegalAccessException when user is not authorized to create a
   *                                announcement for the designated owner defined in object
   */
  Announcement createAnnouncement(Announcement announcement, String username) throws IllegalAccessException, ObjectNotFoundException;

  /**
   * Retrieves all Announcements by challenge.
   *
   * @param offset Offset
   * @param limit  Limit
   * @return A {@link List<Announcement>} object
   * @throws IllegalAccessException when user is not authorized to access
   *                                announcement
   */
  List<Announcement> findAllAnnouncementByChallenge(long challengeId, int offset, int limit) throws IllegalAccessException, ObjectNotFoundException;

}
