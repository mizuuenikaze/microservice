var App = {};

App.Config = {};

App.Ui = {
	init : function() {
		$('#datagrid button').button();
		$('#datagrid a.button').button();
		$('#errorform button').button();

		var now = new Date();
		$('#errorform input#date').val(
				now.getFullYear() + ('0' + (now.getMonth() + 1)).slice(-2)
						+ ('0' + (now.getDate() + 1)).slice(-2)
						+ 'T08:00:00-05:00');

		$('#offerBackLink, #offerNextLink, #offerLink').click(function(event) {
			event.preventDefault();

			var segmentFilter = $('#segmentFilter').val();
			var productFilter = $('#productFilter').val();
			var extra = '';

			if (segmentFilter && segmentFilter != '') {
				extra = 'segmentFilter=' + segmentFilter;
			}

			if (productFilter && productFilter != '') {
				if (extra != '') {
					extra += "&";
				}
					extra += 'productFilter=' + productFilter;
			}

			var qs = '';

			if (extra != '') {
				qs = '?';
				
				if ($(this).attr('href').indexOf('?') > -1) {
					qs = '&';
				}
			}

			document.location.href = $(this).attr('href') + qs + extra;
		});
	}
};

App.Utils = {
	transferData : function(source, target, attrList) {
		$.each(attrList, function(index, value) {
			target.data(value, source.data(value));
		});
	},

	basicAuth : function(jqXHR, settings) {
		jqXHR.setRequestHeader("Authorization", App.Config.ajaxBasicAuth);
	},

	buildConfig : function() {
		$('#dynaScriptConfig span').each(function(index, element) {
			App.Config[$(this).attr("id")] = $(this).text();
		});
	}
};

$(function() {
	App.Ui.init();

	App.Utils.buildConfig();
});