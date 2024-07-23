#set($form_action="delete")

<div id="dialog-form-delete" title="Repository löschen ?">
	<form id="form-delete" method="post" accept-charset="iso-8859-2" action="$root/repos/delete-action.vm">
	<fieldset>
		<input type="hidden" name="key" id="key"/>
#if($params.profile)
		<input type="hidden" name="profile" id="profile" value="$params.profile"/>
#end
		<label for="name">Name</label>
		<input type="text" name="name" id="name" disabled class="text ui-widget-content ui-corner-all ui-state-disabled"/>
		<br>
		<br>
		<label for="deleteerror"></label>
		<div id="deleteerror"></div>
		<br>
	</fieldset>
	</form>
</div>
<script type="text/javascript">
var delete_allowed = false;

function form_delete_load(o) {
	$("input#key").val(o.id);
	$("input#name").val(o.name);
	var res = [];
	for(i of dataView.getItems()) {
		if (i.pred === o.name || i.origin===o.name) res.push(i.name)
	}

	if(res.length > 0)
		$("#deleteerror").text("Cannot delete: Referenced by repo " + res)
	else
		$("#deleteerror").text("");

	delete_allowed = res.length === 0;

	$.getJSON("$root/profiles/json-by-repo-id.vm?key=" + o.id, function (result) {
			for(r of result) {
				$("#deleteerror").append(
					"<br><label/>Cannot delete: Referenced by profile " + r.profileName);
				delete_allowed = false;
			}
		});
}

function form_delete_validate() {
	return delete_allowed;
}

#js_delete_submit("repos", "id")
</script>
