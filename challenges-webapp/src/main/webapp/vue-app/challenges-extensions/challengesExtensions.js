
export function initExtensions() {
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
    getTitle: activity => activity && activity.templateParams && activity.templateParams.announcementTitle || '',
    getThumbnail: () => '/challenges/images/challengesAppIcon.png',
    getSummary: activity => activity && activity.templateParams && activity.templateParams.announcementDescription  || '',
    getBody: activity => {
      return Vue.prototype.$utils.trim((activity.templateParams && activity.templateParams.announcementComment)
           || '');
    },
  };

  extensionRegistry.registerExtension('activity', 'type', {
    type: 'exo-announcement:activity',
    options: announcementActivityTypeExtensionOptions,
  });

}