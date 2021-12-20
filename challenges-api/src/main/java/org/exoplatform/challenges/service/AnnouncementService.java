package org.exoplatform.challenges.service;

import org.exoplatform.challenges.model.Announcement;
import org.exoplatform.commons.exception.ObjectNotFoundException;

import java.util.List;

public interface AnnouncementService {

  /**
   * Creates a new announcement
   *
   * @param announcement {@link Announcement} object to create
   * @return created {@link Announcement} with generated technical identifier
   * @throws IllegalAccessException when user is not authorized to create a
   *           announcement for the designated owner defined in object
   */
  Announcement createAnnouncement(Announcement announcement) throws IllegalAccessException, ObjectNotFoundException;

}
