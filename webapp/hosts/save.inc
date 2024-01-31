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
	<fieldset>
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
	</fieldset>
	</form>
</div>
<script type="text/javascript">
#if($isNewAction)
function form_${form_action}_load(o) {
				$("input#key").val('');
				$("input#name").val('');
				$("input#description").val('');
				$("input#ipAddress").val('');
				$("input#macAddress").val('');
				$("select#profileName").val("");
				$("input#options").val('');
}
#else
function form_${form_action}_load(o) {
				$("input#key").val(o.id);
				$("input#name").val(o.name);
				$("input#description").val(o.description);
				$("input#ipAddress").val(o.ipAddress);
				$("input#macAddress").val(o.macAddress);
				$("input#options").val(o.options);
				if(o.profileName!="") {
					$("select#profileName").val(o.profileName);
				}
}
#end

function form_${form_action}_validate() {
	var valid = true;
	var name = $("input#name").filter(":visible");
	var description = $("input#description").filter(":visible");
	var mac = $("input#macAddress").filter(":visible");

	valid = checkIdentifier(name) && valid;
	valid = (mac.val()=="" || checkMacAddress(mac)) && valid;
	return valid;
}
</script>
