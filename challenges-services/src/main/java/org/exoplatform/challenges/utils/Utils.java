package org.exoplatform.challenges.utils;

import org.apache.commons.lang3.StringUtils;
import org.exoplatform.challenges.model.UserInfo;
import org.exoplatform.commons.utils.CommonsUtils;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.security.ConversationState;
import org.exoplatform.social.core.identity.model.Identity;
import org.exoplatform.social.core.identity.provider.OrganizationIdentityProvider;
import org.exoplatform.social.core.manager.IdentityManager;
import org.exoplatform.social.core.space.model.Space;
import org.exoplatform.social.core.space.spi.SpaceService;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class Utils {

  private static final Log              LOG                = ExoLogger.getLogger(Utils.class);

  public static final DateTimeFormatter RFC_3339_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss[.SSS][XXX]")
                                                                              .withResolverStyle(ResolverStyle.LENIENT);

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

  public static String toRFC3339Date(Date dateTime) {
    if (dateTime == null) {
      return null;
    }
    ZonedDateTime zonedDateTime = ZonedDateTime.from(dateTime.toInstant().atOffset(ZoneOffset.UTC));
    return zonedDateTime.format(RFC_3339_FORMATTER);
  }

  public static Date parseRFC3339Date(String dateString) {
    if (StringUtils.isBlank(dateString)) {
      return null;
    }
    ZonedDateTime zonedDateTime = ZonedDateTime.parse(dateString, RFC_3339_FORMATTER);
    return Date.from(zonedDateTime.toInstant());
  }

  public static Space getSpaceById(String spaceId) {
    if (StringUtils.isBlank(spaceId)) {
      return null;
    }
    Space space = CommonsUtils.getService(SpaceService.class).getSpaceById(spaceId);
    if (space == null) {
      throw new IllegalArgumentException("space is not exist");
    }
    return space;
  }

  public static List<UserInfo> getUsersByIds(List<Long> ids) {
    IdentityManager identityManager = CommonsUtils.getService(IdentityManager.class);
    if (ids.isEmpty()) {
      return Collections.emptyList();
    }
    List<UserInfo> users = new ArrayList<>();
    for (Long id : ids) {
      Identity identity = identityManager.getIdentity(String.valueOf(id));
      if (identity != null && OrganizationIdentityProvider.NAME.equals(identity.getProviderId())) {
        UserInfo userInfo = new UserInfo();
        userInfo.setAvatarUrl(identity.getProfile().getAvatarUrl());
        userInfo.setFullName(identity.getProfile().getFullName());
        userInfo.setRemoteId(identity.getRemoteId());
        userInfo.setId(identity.getId());
        users.add(userInfo);
      }
    }
    return users;
  }

}
