/**
 * 
 */

var uxlib = new UXLib();

uxlib.marionetteApp.addRegions({
	headerRegion: '#spaHeader',
	mainRegion: '#spaRegion1',
	formRegion: '#spaForm1'
});

uxlib.marionetteApp.vent.on('all', function (evt, model){
	console.log('uxlib marionetteApp DEBUG: Event Caught: ' + evt);
	if (model) {
		console.dir(model);
	}
});

uxlib.marionetteApp.addInitializer(function(options) {
	var angryCatsView = new uxlib.views.AngryCatsView({
		collection: options.cats
	});
	
	var headerView = new uxlib.views.HeaderView({model: options.headerModel});
	
	var formView = new uxlib.views.GenericFormView({model: options.formModel});
	
	uxlib.marionetteApp.headerRegion.show(headerView);
	uxlib.marionetteApp.mainRegion.show(angryCatsView);
	uxlib.marionetteApp.formRegion.show(formView);
});


$(document).ready(function() {
	var cats = new uxlib.viewModels.AngryCats([ 
	                                            new uxlib.viewModels.AngryCat({ name: 'web cat' }),
	                                            new uxlib.viewModels.AngryCat({ name: 'bitey cat' }),
	                                            new uxlib.viewModels.AngryCat({ name: 'surprised cat' })
	                                            ]);
	
	var formModel = new uxlib.viewModels.GenericFormData({
		uxph : {
			uname : 'Name',
			comment : 'Comment',
			formButtonLabel : 'Submit'
		}
	});
	
	var headerModel = new uxlib.viewModels.HeaderModel();
	
	uxlib.marionetteApp.start({ cats: cats, formModel: formModel, headerModel: headerModel});
	uxlib.backbone.history.start();
});