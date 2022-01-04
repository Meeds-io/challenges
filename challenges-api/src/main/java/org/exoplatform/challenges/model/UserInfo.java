package org.exoplatform.challenges.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInfo implements Cloneable {

  private String  id;

  private String  remoteId;

  private String  fullName;

  private String  avatarUrl;

  private boolean canEdit;

  private boolean canAnnounce;

  private boolean isMember;

  private boolean isRedactor;

  private boolean isManager;

}
