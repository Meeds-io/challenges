
export  function initExtensions() {
  const announcementActivityTypeExtensionOptions = {
    canEdit: () => false,
    supportsThumbnail: true,
    useSameViewForMobile: false,
    canShare: () => true,
    thumbnailProperties: {
      height: '90px',
      width: '90px',
      noBorder: true,
    },
    getSourceLink: () => '#',
    getTitle: async activity => {
      const announcementAssigneeUsername = activity && activity.templateParams && activity.templateParams.announcementAssigneeUsername && activity.templateParams.announcementAssigneeUsername.split('#') || '';
      const title = await buildTitle( announcementAssigneeUsername );
      return {
        key: 'challenges.succeededChallenge',
        params: { 0: `${ title }` }
      };
    },
    getThumbnail: () => '/challenges/images/challengesAppIcon.png',
    getSummary: activity => activity && activity.templateParams && activity.templateParams.announcementDescription  || '',
    getBody: activity => {
      return Vue.prototype.$utils.trim((activity.templateParams && activity.templateParams.announcementComment)
           || '');
    },
  };

  function buildTitle( announcementAssigneeUsername ){
    let title = '';
    for (let i=0 ; i < announcementAssigneeUsername.length-1; i++){
      const fullName = getFullName( announcementAssigneeUsername[i] );
      title = `${ title } <a href="${eXo.env.portal.context}/${eXo.env.portal.portalName}/profile/${ announcementAssigneeUsername[i] }">  ${ fullName }</a>`;
    }
    return title;
  }

  function getFullName( username ){
    Vue.prototype.$userService.getUser(username).then(user => {
      return user.fullname;
    }) ;
  }
  extensionRegistry.registerExtension('activity', 'type', {
    type: 'challenges-announcement',
    options: announcementActivityTypeExtensionOptions,
  });

}