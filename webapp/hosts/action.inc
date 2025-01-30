#set($form_action="action")
#set($form_title="Aktion für Host(s) ausführen")
#parse("$module/action-presets.inc")

<div id="dialog-form-${form_action}" title="${form_title}">
	<form id="form-${form_action}" method="post" accept-charset="utf-8" action="$root/$module/${form_action}-action.vm">
		<fieldset>
		<input type="hidden" name="key" id="key"/>
		<br>
			<legend>Aktion auswählen:</legend>

			<label for="hosts">Ausgewählte Hosts:</label>
			<br><label> </label>
			<select id="hosts" disabled style="width:40em" size="6">
			</select>
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
				<select name="actionSelection" id="actionSelection" size=1"
					style="margin-left:-6em; margin-top: 0.3em" disabled>
					<option>- Benutzerdefiniert -</option>
#foreach($a in $actionPresets)
					<option>$esc.java($a)</option>
#end
				</select>
				<br>
				<label for="actionArgs">
				<input type="text" name="actionArgs" id="actionArgs" size=23
					style="margin-left:20em; margin-top:-1em;width:18em"
					placeholder="shell command" disabled>
				<div style="height:1.8em"></div>
			</span>
		</fieldset>
	</form>
</div>
<script type="text/javascript">
function form_action_enable_fields() {
	let argsField = $("#actionArgs");
	let actionField = $("#actionSelection");
	let radio = $("#actionCustom:checked");

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
	var rows = grid.getSelectedRows();
	const hosts = $("#hosts");
	var hostnames = [];

	for(const r of rows) {
		hostnames.push(dataView.getItem(r).name);
	}
	hostnames = hostnames.sort();

	hosts.html("");
	for(const h of hostnames) {
		hosts.append(new Option(h));
	}

	$("input#key").val(o.id);
	$("input#actionReport").prop('checked', true);
	$("input#actionArgs").val('');

	$("input[name=action]").off("change");
	$("input[name=action]").on("change", form_action_enable_fields);
	$("input[name=actionSelection]").off("change");
	$("select[name=actionSelection]").on("change", form_action_enable_fields);

	form_action_enable_fields();
}

function form_${form_action}_validate() {
        if($("#actionCustom:checked").val() !== undefined) 
	{
		var rangeStart = 1;

		let actionSel = $("#actionSelection").val();
		if(actionSel[0] !== "-")
			rangeStart = 0;

		return checkLength($("#actionArgs"),"args", rangeStart, 128);
	}
	else {
		return true;
	}
}

function form_${form_action}_submit() {
	var rows = grid.getSelectedRows();
	var hosts = [];
	var action = "";
        var actionSel = "";
	var args = "";

	if($("#actionReport:checked").val() !== undefined) action = "report";
	if($("#actionConfigure:checked").val() !== undefined) action = "config";
	if($("#actionUpdate:checked").val() !== undefined) action = "update";
	if($("#actionReboot:checked").val() !== undefined) action = "reboot";
	if($("#actionCustom:checked").val() !== undefined) {
		action = "custom";
		actionSel = $("#actionSelection").val();

		if(actionSel[0] === "-")
			args = $("#actionArgs").val();
		else
			args = actionSel;
	}

	for(const r of rows) {
		hosts.push(dataView.getItem(r).id);
	}
	console.log(hosts);

	$.post("$root/$module/${form_action}-action.vm",
		{
			"action": action,
			"key[]": hosts,
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
