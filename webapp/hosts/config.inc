<div id="dialog-form-config" title="Systemkonfiguration herunterladen">
	<form id="form-config" method="post" accept-charset="iso-8859-2" action="$root/$module/config-action.vm">
	<fieldset>
		<input type="hidden" name="key" id="key"/>
		<label for="name">Name</label>
		<input type="text" name="name" id="name" disabled class="text ui-widget-content ui-corner-all ui-state-disabled"/>
		<label for="password">Passwort</label>
		<input type="password" name="password" class="text ui-widget-content ui-corner-all"/>
		<br>
	</fieldset>
	</form>
</div>
<script type="text/javascript">
function form_config_load(o) {
				$("input#key").val(o.name);
				$("input#name").val(o.name);
}
function form_config_validate() {
	return true;
}
</script>
