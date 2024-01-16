<div id="dialog-form-edit" title="Profil bearbeiten">
	<p class="validateTips">All form fields are required.</p>
	<form id="form-edit" method="post" accept-charset="ISO-8859-2" action="$root/$module/edit-action.vm">
	<fieldset>
		<input type="hidden" name="key" id="key"/>
		<label for="name">Name</label>
		<input type="text" name="name" id="name" disabled class="text ui-widget-content ui-corner-all ui-state-disabled"/>
		<br>
		<label for="description">Beschreibung</label>
		<input type="text" name="description" id="description" class="text ui-widget-content ui-corner-all"/>
		<br>
		<label for="configOpts">Extra-Konfiguration</label>
		<input type="text" name="configOpts" id="configOpts" class="text ui-widget-content ui-corner-all"/>
		<br>
		<div id="container">
		<div class="leftcol">
		<label for="members">Repositories:</label>
		<select class="selectboxleft" name="members" id="members" size="10">
			<option>dummy</option>
		</select>
		</div>
		<div class="centercol">
		<img id="addmember" class="ui-icon ui-icon-circle-arrow-w"/>
		<img id="removemember" class="ui-icon ui-icon-circle-arrow-e"/>
		</div>
		<div class="rightcol">
		<select class="selectboxright" name="nonmembers" id="nonmembers" size="10">
		<option>dummy</option>
		</select>
		</div>
		</div>
	</fieldset>
	</form>
</div>
<script type="text/javascript">
#[[
function form_update_selects(o) {
	var el = $("select#members");
	el.empty(); // remove old options
	$.each(o.repos, function(key,value) {
		el.append($("<option></option>")
		.attr("value", value).text(value));
	});

	el=$('select#nonmembers');
	el.empty();
]]#
	var l=[ #foreach($r in $sorter.sort($repos.all,"name"))"$r.name",#end ];
#[[
	$.each(l,function(key,value) {
		if($.inArray(value,o.repos)!=-1) {
			el.append($('<option disabled></option>')
			.attr("value", value).text(value));
		} else {
			el.append($('<option></option>')
			.attr("value", value).text(value));
		}
	});
}
]]#
function form_edit_load(o) {
				$('#addmember').unbind('click');
				$('#addmember').click(function() {
					var m=$('#nonmembers').val();
					o.repos.push(m);
					form_update_selects(o);
					$('#nonmembers').val([]);
					$.post('$root/$module/addlist-action.vm?key='+o.id+'&reponame='+m)
						.error(post_error_handler);	
				});
				$('#removemember').unbind('click');
				$('#removemember').click(function() {
					var m=$('#members').val();
					o.repos.splice($.inArray(m,o.repos),1);
					form_update_selects(o);
					$('#members').val([]);
					$.post('$root/$module/dellist-action.vm?key='+o.id+'&reponame='+m)
						.error(post_error_handler);	
				});
				$("input#key").val(o.id);
				$("input#name").val(o.name);
				$("input#description").val(o.description);
				$("input#configOpts").val(o.configOpts);
				form_update_selects(o);
}

function form_edit_validate() {
	var name = $("input#name").filter(":visible");
	return checkIdentifier(name);
}
</script>
