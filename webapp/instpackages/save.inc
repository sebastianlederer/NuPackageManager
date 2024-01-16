#if($form_action=="new")
#set($isNewAction=true)
#else
#set($isNewAction=false)
#end
<div id="dialog-form-${form_action}" title="${form_title}">
	<p class="validateTips">All form fields are required.</p>
	<form id="form-${form_action}" method="post" accept-charset="iso-8859-2" action="$root/$module/${form_action}-action.vm">
	<fieldset>
		<input type="hidden" name="key" id="key"/>
#if($params.profile)
		<input type="hidden" name="profile" id="profile" value="$params.profile"/>
#end
#if($params.show)
		<input type="hidden" name="show" id="show" value="$params.show"/>
#end
		<input type="hidden" name="list" id="list" value="$params.list"/>
		#inputbox("name","Name",$isNewAction)
		#inputbox("description","Beschreibung",true)
		<br>
		#inputbox("version","Version",true)
		#inputbox("arch","Architektur",true)
		<br>
		#checkbox("install","Installieren",false)
		<br>
	</fieldset>
	</form>
</div>
<script type="text/javascript">
#if($isNewAction)
function form_${form_action}_load(o) {
				$("input#name").val('');
				$("input#description").val('');
				$("input#version").val(0);
				$("input#arch").val('');
				$("input#install").prop('checked',true);
}
#else
function form_${form_action}_load(o) {
				$("input#key").val(o.id);
				$("input#name").val(o.name);
				$("input#description").val(o.description);
				$("input#version").val(o.version);
				$("input#arch").val(o.arch);
				$("input#install").prop('checked',o.install);
}
#end
function form_${form_action}_validate() {
        var name = $("input#name").filter(":visible");
        return checkPackageIdentifier(name);
}
</script>
