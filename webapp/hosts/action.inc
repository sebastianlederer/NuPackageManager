#set($form_action="action")
#set($form_title="Aktion für Host ausführen")

<div id="dialog-form-${form_action}" title="${form_title}">
	<form id="form-${form_action}" method="post" accept-charset="iso-8859-2" action="$root/$module/${form_action}-action.vm">
		<fieldset>
		<input type="hidden" name="key" id="key"/>
		#inputbox("name","Host", false)
		<br>
			<legend>Aktion auswählen:</legend>
			<br>
			<span class="radiogroup">
				<input type="radio" name="action" id="actionReport" value="report">
				<label for="actionReport">Report</label>
				<br>
				<input type="radio" name="action" id="actionConfigure" value="config">
				<label for="actionConfigure">Konfiguration</label>
				<br>
				<input type="radio" name="action" id="actionUpdate" value="update">
				<label for="actionUpdate">Update</label>
				<br>
				<input type="radio" name="action" id="actionReboot" value="reboot">
				<label for="actionReboot">Reboot</label>
			</span>
		</fieldset>
	</form>
</div>
<script type="text/javascript">
function form_${form_action}_load(o) {
				// $("input[type=radio]").checkboxradio({ icon:false });
				$("input#key").val(o.id);
				$("input#name").val(o.name);
				$("input#actionReport").prop('checked', true);
}

function form_${form_action}_validate() {
	var valid = true;
	return valid;
}
</script>