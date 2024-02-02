#set($form_action = "delete")

<div id="dialog-form-delete" title="Host löschen ?">
	<form id="form-delete" method="post" accept-charset="utf-8" action="$root/$module/delete-action.vm">
	<fieldset>
		<input type="hidden" name="key" id="key"/>
		<label for="delhosts">Ausgewählte Hosts:</label>
		<br><label> </label>
		<select id="delhosts" disabled style="width:40em" size="6">
		</select>
		<br>
	</fieldset>
	</form>
</div>
<script type="text/javascript">
function form_delete_load(o) {
	var rows = grid.getSelectedRows();
	const hosts = $("#delhosts");
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
}

function form_delete_validate() {
	return true;
}

function form_${form_action}_submit() {
	var rows = grid.getSelectedRows();
	var hosts = [];

	for(const r of rows) {
		hosts.push(dataView.getItem(r).id);
	}
	console.log(hosts);

	$.post("$root/$module/${form_action}-action.vm",
		{
			"key[]": hosts,
		})
	.done(function() {
		grid.setSelectedRows([]);
		#js_load_data("$module", "name")
	})
	.fail(function() {
		alert("POST error");
	});
}
</script>
