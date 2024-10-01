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
				<br>
				<input type="radio" name="action" id="actionCustom" value="custom"
					style="margin-bottom: 1.5em">
				<label for="actionCustom">Custom</label>
				<input name="actionArgs" id="pactionArgs" size=25 placeholder="shell command"
					style="margin-left:-6em; margin-top: 0.3em;display: none">
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
	$("input#pactionArgs").val('');

	$("input[name=action]").off("change");
	$("input[name=action]").on("change", function() {
		if($(this).val() == "custom") {
			$("#pactionArgs").show();
			$("#pactionArgs").focus();
		}
		else {
			$("#pactionArgs").hide();
		}
	});
}

function form_${form_action}_validate() {
        if($("#actionCustom:checked").val() !== undefined) 
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

	if($("#actionReport:checked").val() !== undefined) action = "report";
	if($("#actionConfigure:checked").val() !== undefined) action = "config";
	if($("#actionUpdate:checked").val() !== undefined) action = "update";
	if($("#actionReboot:checked").val() !== undefined) action = "reboot";
	if($("#actionCustom:checked").val() !== undefined) {
		action = "custom";
		args = $("#pactionArgs").val();
	}

	$.post("$root/$module/${form_action}-action.vm",
		{
			"action": action,
			"actionProfile": $("#actionProfile").val(),
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
