/**
 * 
 */

var uxlib = new UXLib();

uxlib.marionetteApp.addRegions({
	tabRegion: '#spaTabRegion'
});

uxlib.marionetteApp.vent.on('all', function (evt, model){
	console.log('uxlib marionetteApp DEBUG: Event Caught: ' + evt);
	if (model) {
		console.dir(model);
	}
});

uxlib.marionetteApp.addInitializer(function(options) {
	var settingView = new uxlib.views.SettingView({model: options.mozuSettingsModel});

	uxlib.marionetteApp.tabRegion.show(settingView);
});


$(document).ready(function() {
	
	var settingsModel = new uxlib.viewModels.MozuSettings({
		uxph : {
			entry1 : 'SomeSetting',
			saveButtonLabel : 'Save',
			cancelButtonLabel : 'Cancel'
		}
	});
	
	uxlib.marionetteApp.start({ mozuSettingsModel: settingsModel});
	uxlib.backbone.history.start();
});