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
	</fieldset>
	</form>
</div>
<script type="text/javascript">
function form_delete_load(o) {
				$("input#key").val(o.id);
				$("input#name").val(o.name);
}

function form_delete_validate() {
	return true;
}

#js_delete_submit("repos", "id")
</script>
