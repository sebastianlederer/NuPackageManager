<div id="dialog-form-rename" title="System umbenennen">
	<form id="form-rename" method="post" accept-charset="utf-8" action="$root/$module/rename-action.vm">
	<fieldset>
		<input type="hidden" name="key" id="key"/>
		<label for="name">Neuer Name</label>
		<input type="text" name="name" id="name" class="text ui-widget-content ui-corner-all"/>
		<br>
	</fieldset>
	</form>
</div>
<script type="text/javascript">
function form_rename_load(o) {
				$("input#key").val(o.id);
				$("input#name").val(o.name);
}

function form_rename_validate() {
        var name = $("input#name").filter(":visible");
        return checkIdentifier(name);
}
#js_rename_submit("hosts", "name")
</script>
