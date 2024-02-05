#set($form_action="paction")
#set($form_title="Aktion für alle Hosts eines Profils ausführen")
#set ($profile_names = [ "-" ])
#foreach ($l in $sorter.sort($profiles.getAll(),"name"))
#set($dummy=$profile_names.add($l.name))
#end

<div id="dialog-form-${form_action}" title="${form_title}">
	<form id="form-${form_action}" method="post" accept-charset="utf-8" action="$root/$module/${form_action}-action.vm">
	<fieldset>
		<input type="hidden" name="key" id="key"/>
		<label for="actionProfile">Profil</label>
		#dropdown("actionProfile", $profile_names, "")
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
				if(o !== undefined)  {
					console.log("setting default profile " + o.profileName);
					$("#actionProfile").val(o.profileName);
				}
				else
					$("#actionProfile").val("");

				$("input#actionReport").prop('checked', true);
}

function form_${form_action}_validate() {
	var valid = true;
	return valid;
}

function form_${form_action}_submit() {
	var action = "";

	if($("#actionReport:checked").val() !== undefined) action = "report";
	if($("#actionConfigure:checked").val() !== undefined) action = "config";
	if($("#actionUpdate:checked").val() !== undefined) action = "update";
	if($("#actionReboot:checked").val() !== undefined) action = "reboot";

	$.post("$root/$module/${form_action}-action.vm",
		{
			"action": action,
			"actionProfile": $("#actionProfile").val(),
		})
	.done(function() {
		#js_load_data("$module", "name")
	})
	.fail(function() {
		alert("POST error");
	});
}
</script>
