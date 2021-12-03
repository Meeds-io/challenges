package org.exoplatform.challenges.utils;

import org.exoplatform.commons.utils.CommonsUtils;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.security.ConversationState;
import org.exoplatform.social.core.identity.model.Identity;
import org.exoplatform.social.core.identity.provider.OrganizationIdentityProvider;
import org.exoplatform.social.core.manager.IdentityManager;
import org.exoplatform.social.core.space.model.Space;
import org.exoplatform.social.core.space.spi.SpaceService;

public class Utils {

  private static final Log LOG = ExoLogger.getLogger(Utils.class);

  public Utils() {
  }

  public static Identity getIdentityByTypeAndId(String remoteId) {
    IdentityManager identityManager = CommonsUtils.getService(IdentityManager.class);
    return identityManager.getOrCreateIdentity(OrganizationIdentityProvider.NAME, remoteId, true);
  }

  public static final String getCurrentUser() {
    return ConversationState.getCurrent().getIdentity().getUserId();
  }

  public static final boolean canEditChallenge(String id) {
    Space space = CommonsUtils.getService(SpaceService.class).getSpaceById(id);
    if (space == null) {
      throw new IllegalArgumentException("space is not exist");
    }
    return CommonsUtils.getService(SpaceService.class).isManager(space, getCurrentUser());
  }

  public static final boolean canAnnounce(String id) {
    Space space = CommonsUtils.getService(SpaceService.class).getSpaceById(id);
    if (space == null) {
      throw new IllegalArgumentException("space is not exist");
    }
    return CommonsUtils.getService(SpaceService.class).isRedactor(space, getCurrentUser());
  }
}
