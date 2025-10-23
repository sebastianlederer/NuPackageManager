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
		<fieldset>
			<legend>Rollen</legend>
			<div id="${form_action}_checkboxes" class="checkboxfield">
#set($colorindex = 1)
#foreach($role in $rolenames)
				<input type="checkbox" name="$role" value="$role" id="${form_action}_r_$role"/>
				<label class="cell$colorindex" for="${form_action}_r_$role">$role</label>
#set($colorindex = $colorindex + 1)
#if($colorindex > $colorwheel.size())
#set($colorindex = 1)
#end
#end
			</div>
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
				$.post("$root/$module/roles.json.vm?key="+o.id)
				.done(function(result) {
					var checkboxes = $("#${form_action}_checkboxes input");
					checkboxes.each(function() {
							const name = $(this).val();
							$(this).prop("checked", result.includes(name));
					});
				})
				.fail(function() {
					alert("POST error");
				});

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
	var checkboxes = $("#${form_action}_checkboxes input");
	var roles = [];
	checkboxes.each(function() {
			if($(this).prop("checked")) {
				roles.push($(this).val());
			}
		}
	);

	$.post("$root/$module/${form_action}-action.vm",
		{
			"key":		$("fieldset[name=$form_action] #key").val(),
			"name": 	$("fieldset[name=$form_action] #name").val(),
			"description":	$("fieldset[name=$form_action] #description").val(),
			"ipAddress":	$("fieldset[name=$form_action] #ipAddress").val(),
			"macAddress":	$("fieldset[name=$form_action] #macAddress").val(),
			"options":	$("fieldset[name=$form_action] #option").val(),
			"profileName":	$("#dialog-form-$form_action select#profileName").val(),
			"roles":	roles
		})
	.done(function() {
		forceRefresh();
	})
	.fail(function() {
		alert("POST error");
	});
}
</script>
