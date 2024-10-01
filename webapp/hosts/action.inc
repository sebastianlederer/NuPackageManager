#set($form_action="action")
#set($form_title="Aktion für Host(s) ausführen")

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
				<input name="actionArgs" id="actionArgs" size=25 placeholder="shell command"
					style="margin-left:-6em; margin-top: 0.3em;display: none">
			</span>
		</fieldset>
	</form>
</div>
<script type="text/javascript">
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
	$("input[name=action]").on("change", function() {
		if($(this).val() == "custom") {
			$("#actionArgs").show();
			$("#actionArgs").focus();
		}
		else {
			$("#actionArgs").hide();
		}
	});
}

function form_${form_action}_validate() {
        if($("#actionCustom:checked").val() !== undefined) 
	{
		return checkLength($("#actionArgs"),"args", 1, 128);
	}
	else {
		return true;
	}
}

function form_${form_action}_submit() {
	var rows = grid.getSelectedRows();
	var hosts = [];
	var action = "";
	var args = "";

	if($("#actionReport:checked").val() !== undefined) action = "report";
	if($("#actionConfigure:checked").val() !== undefined) action = "config";
	if($("#actionUpdate:checked").val() !== undefined) action = "update";
	if($("#actionReboot:checked").val() !== undefined) action = "reboot";
	if($("#actionCustom:checked").val() !== undefined) {
		action = "custom";
		args = $("#actionArgs").val();
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
