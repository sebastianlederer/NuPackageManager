#if($form_action=="new")
#set($isNewAction=true)
#set ($profile_names = [ "-" ])
#foreach ($l in $sorter.sort($profiles.getAll(),"name"))
#set($dummy=$profile_names.add($l.name))
#end
#else
#set($isNewAction=false)
#end
<div id="dialog-form-${form_action}" title="${form_title}">
	<p class="validateTips">All form fields are required.</p>
	<form id="form-${form_action}" method="post" accept-charset="utf-8" action="$root/$module/${form_action}-action.vm">
	<fieldset name="$form_action">
		<input type="hidden" name="key" id="key"/>
		#inputbox("name","Name",$isNewAction)
		#inputbox("description","Beschreibung",true)
		<br>
		#inputbox("ipAddress","IP-Adresse",true)
		<br>
		#inputbox("macAddress","MAC-Adresse",true)
		<br>
		<label for="profileName">Profil</label>
		#dropdown( "profileName", $profile_names, "") 
		<br>
		#inputbox("options","Optionen",true)
		<br>
		<fieldset class="checkboxline" style="layout:grid-row">
			<legend>Rollen</legend>
			<label for="role-1">SLES</label>
			<input type="checkbox" name="role-1" id="role-1">
			<label for="role-2">backup_veeam</label>
			<input type="checkbox" name="role-2" id="role-2">
			<label for="role-3">root_CA_elw</label>
			<input type="checkbox" name="role-3" id="role-3">
			<label for="role-4">docker_ce</label>
			<input type="checkbox" name="role-4" id="role-4">
			<label for="role-5">monitoring</label>
			<input type="checkbox" name="role-5" id="role-5">
		</fieldset>
	</fieldset>
	</form>
</div>
<script type="text/javascript">
#if($isNewAction)
function form_${form_action}_load(o) {
				$("fieldset[name=$form_action] #key").val('');
				$("fieldset[name=$form_action] #name").val('');
				$("fieldset[name=$form_action] #description").val('');
				$("fieldset[name=$form_action] #ipAddress").val('');
				$("fieldset[name=$form_action] #macAddress").val('');
				$("select#profileName").val("");
				$("fieldset[name=$form_action] #options").val('');
}
#else
function form_${form_action}_load(o) {
				$("input").checkboxradio();
				$("fieldset[name=$form_action] #key").val(o.id);
				$("fieldset[name=$form_action] #name").val(o.name);
				$("fieldset[name=$form_action] #description").val(o.description);
				$("fieldset[name=$form_action] #ipAddress").val(o.ipAddress);
				$("fieldset[name=$form_action] #macAddress").val(o.macAddress);
				$("fieldset[name=$form_action] #options").val(o.options);
				if(o.profileName!="") {
					$("select#profileName").val(o.profileName);
				}
}
#end

function form_${form_action}_validate() {
	var valid = true;
	var name = $("fieldset[name=$form_action] #name").filter(":visible");
	var description = $("fieldset[name=$form_action] #description").filter(":visible");
	var mac = $("fieldset[name=$form_action] #macAddress").filter(":visible");

	valid = checkIdentifier(name) && valid;
	valid = (mac.val()=="" || checkMacAddress(mac)) && valid;
	return valid;
}

function form_${form_action}_submit() {
	$.post("$root/$module/${form_action}-action.vm",
		{
			"key":		$("fieldset[name=$form_action] #key").val(),
			"name": 	$("fieldset[name=$form_action] #name").val(),
			"description":	$("fieldset[name=$form_action] #description").val(),
			"ipAddress":	$("fieldset[name=$form_action] #ipAddress").val(),
			"macAddress":	$("fieldset[name=$form_action] #macAddress").val(),
			"options":	$("fieldset[name=$form_action] #option").val(),
			"profileName":	$("#dialog-form-$form_action select#profileName").val()
		})
	.done(function() {
		forceRefresh();
	})
	.fail(function() {
		alert("POST error");
	});
}
</script>
