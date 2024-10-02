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
				<input name="pactionArgs" id="pactionArgs" size=25 placeholder="shell command"
					style="margin-left:-6em; margin-top: 0.3em;display: none">
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
	$("input[name=action]").on("change", form_paction_showhide);

	form_paction_showhide();
}

function form_${form_action}_validate() {
        if($("#pactionCustom:checked").val() !== undefined) 
	{
		return checkLength($("#pactionArgs"),"args", 1, 128);
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
		args = $("#pactionArgs").val();
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
