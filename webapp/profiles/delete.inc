<div id="dialog-form-delete" title="Profil löschen ?">
	<form id="form-delete" method="post" accept-charset="iso-8859-2" action="$root/$module/delete-action.vm">
	<fieldset>
		<input type="hidden" name="key" id="key"/>
		<label for="name">Name</label>
		<input type="text" name="name" id="name" disabled class="text ui-widget-content ui-corner-all ui-state-disabled"/>
		<br>
		<label for "hostcount">Anzahl Hosts:</label>
		<input type="text" name="hostcount" id="hostcount" disabled class="text ui-widget-content ui-corner-all ui-state-disabled"/>
		<br>
	</fieldset>
	</form>
</div>
<script type="text/javascript">
function form_delete_load(o) {
				$("input#key").val(o.id);
				$("input#name").val(o.name);
				$("input#hostcount").val(o.hostcount);
}

function form_delete_validate() {
	return o.hostcount===0;
}

#js_delete_submit("profiles", "id")
</script>
