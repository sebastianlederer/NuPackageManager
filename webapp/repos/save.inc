#set ($upstream_names = ["-"])
#foreach ($l in $sorter.sort($upstream.getAll(),"name"))
#set($dummy=$upstream_names.add($l.name))
#set ($repo_names = ["-"])
#end
#foreach ($l in $sorter.sort($repos.getAll(),"name"))
#set($dummy=$repo_names.add($l.name))
#end
#if($form_action=="new")
#set($isNewAction=true)
#else
#set($isNewAction=false)
#end
<div id="dialog-form-${form_action}" title="${form_title}">
	<p class="validateTips">All form fields are required.</p>
	<form id="form-${form_action}" method="post" accept-charset="utf-8" action="$root/$module/${form_action}-action.vm">
	<fieldset>
		<input type="hidden" name="key" id="key"/>
#if($params.profile)
		<input type="hidden" name="profile" id="profile" value="$params.profile"/>
#end
		#inputbox("name","Name",$isNewAction)
		#inputbox("description","Beschreibung",true)
		<br>
		#checkbox("approvalRequired","Freigabe des Vorgängers zum Aktualisieren erforderlich",false)
		<br>
		<label for="pred">Vorgänger</label>
		#dropdown( "pred", $repo_names,"") 

		<label for="upstream">Upstream</label>
		#dropdown( "upstream", $upstream_names,"") 
		<br>
		<label for="origin">Ursprung</label>
		#dropdown( "origin", $repo_names, "")
		<label for="schedule">Zeitplan</label>
		#dropdown( "schedule", $schedules,"") 
		<br>
		#if($ownerlist)
		<label for="owner">Gruppe</label>
		#dropdown( "owner", $sorter.sort($ownerlist.all), "")
		#end
		<br>
		#checkbox("approved","Freigegeben",false)
		<br>
	</fieldset>
	</form>
</div>
<script type="text/javascript">
#if($isNewAction)
function form_${form_action}_load(o) {
				$("input#name").val('');
				$("input#description").val('');
				$("input#autoUpdate").prop('checked',false);
				$("input#owner").val('');
				$("input#approvalRequired").prop('checked',false);
				$("input#approved").prop('checked',false);
				$("select#pred").val("-");
				$("select#origin").val("-");
				$("select#upstream").val("-");
				$("select#schedule").val("-");
}
#else
#[[
function form_update_selects(o) {
}
]]#
function form_${form_action}_load(o) {
				$("input#key").val(o.id);
				$("input#name").val(o.name);
				$("input#description").val(o.description);
				$("input#owner").val(o.owner);
				$("input#approvalRequired").prop('checked',o.approvalRequired);
				$("input#approved").prop('checked',o.approved);
				if(o.pred!="") {
					$("select#pred").val(o.pred);
				}
				else {
					$("select#pred").val("-");
				}
				if(o.origin!="") {
					$("select#origin").val(o.origin);
				}
				else {
					$("select#origin").val("-");
				}
				if(o.upstream!="") {
					$("select#upstream").val(o.upstream);
				}
				else {
					$("select#upstream").val("-");
				}
				if(o.schedule!="") {
					$("select#schedule").val(o.schedule);
				}
				else {
					$("select#schedule").val("-");
				}
				form_update_selects(o);
}
#end
function form_${form_action}_validate() {
	var valid;
        var name = $("input#name").filter(":visible");

	valid = checkIdentifier(name);
        return valid;
}

function form_${form_action}_submit() {
	var fields = $("#form-${form_action} input, #form-${form_action} select");
	var values = {};

	for(var i = 0; i < fields.length; i++) {
		const name = fields[i].name;

		if(fields[i].type === 'checkbox') {
			if(fields[i].checked) values[name] = true;
		}
		else {
			values[name] = fields[i].value;
		}
	}
	console.log("submit ${form_action}" + JSON.stringify(values));

	$.post("$root/$module/${form_action}-action.vm", values)
	.done(function() {
		#js_load_data("$module", "id")
	})
	.fail(function() {
		alert("POST error");
	});
}
</script>
