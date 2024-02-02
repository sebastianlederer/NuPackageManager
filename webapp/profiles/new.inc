#set ($profile_names = ["-"])
#foreach ($p in $profiles.fetchAll())
#set($dummy=$profile_names.add($p.name))
#end
<div id="dialog-form-new" title="Profil anlegen">
	<p class="validateTips">All form fields are required.</p>
	<form id="form-new" method="post" accept-charset="ISO-8859-2" action="$root/profiles/new-action.vm">
	<fieldset>
		<label for="name">Name</label>
		<input type="text" name="name" id="name" class="text ui-widget-content ui-corner-all"/>
		<br>
		<label for="description">Beschreibung</label>
		<input type="text" name="description" id="description" class="text ui-widget-content ui-corner-all"/>
		<br>
		<label for="configExtras">Extra-Konfiguration</label>
		<input type="text" name="configExtras" id="configExtras" class="text ui-widget-content ui-corner-all"/>
		<br>
	</fieldset>
	</form>
</div>
<script type="text/javascript">
function form_new_load(o) {
	$("input#name").val('');
	$("input#description").val('');
	$("input#configExtras").val('');
}

function form_new_validate() {
	var name = $("input#name").filter(":visible");
	return checkIdentifier(name);
}

#js_form_submit("new" "profiles" "id")
</script>
