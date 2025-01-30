#set($form_action="paction")
#set($form_title="Aktion für alle Hosts eines Profils ausführen")
#parse("$module/action-presets.inc")
#set ($profile_names = [ "-" ])
#foreach ($l in $sorter.sort($profiles.getAll(),"name"))
#set($dummy=$profile_names.add($l.name))
#end

<div id="dialog-form-${form_action}" title="${form_title}">
	<form id="form-${form_action}" method="post" accept-charset="utf-8" action="$root/$module/${form_action}-action.vm">
	<fieldset>
		<input type="hidden" name="key" id="key"/>
		<label for="pactionProfile">Profil</label>
		#dropdown("pactionProfile", $profile_names, "")
		<br>
			<legend>Aktion auswählen:</legend>
			<br>
			<span class="radiogroup">
				<input type="radio" name="action" id="pactionReport" value="report">
				<label for="pactionReport">Report</label>
				<br>
				<input type="radio" name="action" id="pactionConfigure" value="config">
				<label for="pactionConfigure">Konfiguration</label>
				<br>
				<input type="radio" name="action" id="pactionUpdate" value="update">
				<label for="pactionUpdate">Update</label>
				<br>
				<input type="radio" name="action" id="pactionReboot" value="reboot">
				<label for="pactionReboot">Reboot</label>
				<br>
				<input type="radio" name="action" id="pactionCustom" value="custom"
					style="margin-bottom: 1.5em">
				<label for="pactionCustom">Custom</label>
				<select name="pactionSelection" id="pactionSelection" size=1
					style="margin-left:-6em; margin-top: 0.3em" disabled>
					<option>- Benutzerdefiniert -</option>
#foreach($a in $actionPresets)
					<option>$esc.java($a)</option>
#end
				</select>
				<br>
				<label for="pactionArgs">
				<input type="text" name="pactionArgs" id="pactionArgs" size=23
					style="margin-left:20em; margin-top:-1em;width:18em"
					placeholder="shell command" disabled>
				<div style="height:1.8em"></div>
			</span>
		</fieldset>
	</form>
</div>
<script type="text/javascript">
function form_paction_showhide() {
	let argsField = $("#pactionArgs");
	let radio = $("#pactionCustom:checked");

	if(radio.val() == "custom") {
		argsField.show();
		argsField.focus();
	}
	else {
		argsField.hide();
	}
}

function form_paction_enable_fields() {
	let argsField = $("#pactionArgs");
	let actionField = $("#pactionSelection");
	let radio = $("#pactionCustom:checked");

	if(radio.val() == "custom") {
		actionField.prop("disabled", false);

		let s = actionField.val();

		if(s[0] === "-") {
			argsField.prop("disabled", false);
			argsField.focus();
		}
		else {
			argsField.prop("disabled", true);
			clearError(argsField);
		}
	}
	else {
		actionField.prop("disabled", true);
		argsField.prop("disabled", true);
	}

}

function form_${form_action}_load(o) {
	// $("input[type=radio]").checkboxradio({ icon:false });
	if(o !== undefined)  {
		console.log("setting default profile " + o.profileName);
		$("#pactionProfile").val(o.profileName);
	}
	else
		$("#pactionProfile").val("");

	$("input#pactionReport").prop('checked', true);
	$("input#pactionArgs").val('');

	$("input[name=action]").off("change");
	$("input[name=action]").on("change", form_paction_enable_fields);

	$("input[name=pactionSelection]").off("change");
	$("select[name=pactionSelection]").on("change", form_paction_enable_fields);

	form_action_enable_fields();
}

function form_${form_action}_validate() {
        if($("#pactionCustom:checked").val() !== undefined) 
	{
		var rangeStart = 1;

		let actionSel = $("#pactionSelection").val();
		if(actionSel[0] !== "-")
			rangeStart = 0;

		return checkLength($("#pactionArgs"),"args", rangeStart, 128);
	}
	else {
		return true;
	}
}

function form_${form_action}_submit() {
	var action = "";
	var args = "";

	if($("#pactionReport:checked").val() !== undefined) action = "report";
	if($("#pactionConfigure:checked").val() !== undefined) action = "config";
	if($("#pactionUpdate:checked").val() !== undefined) action = "update";
	if($("#pactionReboot:checked").val() !== undefined) action = "reboot";
	if($("#pactionCustom:checked").val() !== undefined) {
		action = "custom";
		actionSel = $("#pactionSelection").val();

		if(actionSel[0] === "-")
			args = $("#pactionArgs").val();
		else
			args = actionSel;
	}

	$.post("$root/$module/${form_action}-action.vm",
		{
			"action": action,
			"actionProfile": $("#pactionProfile").val(),
			"action_args": args,
		})
	.done(function() {
		#js_load_data("$module", "name")
	})
	.fail(function() {
		alert("POST error");
	});
}
</script>
