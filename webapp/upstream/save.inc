<div id="dialog-form-$form_action" title="$form_title">
	<form id="form-${form_action}" method="post" action="$root/$module/${form_action}-action.vm">
	<fieldset>
		<input type="hidden" name="key" id="key"/>
#if($form_action=="new")
#set($isNewAction=true)
#else
#set($isNewAction=false)
#end
		#inputbox("name","Name",$isNewAction)
		#inputbox("description","Beschreibung",true)
		<br>
		#inputbox("dist","Distribution",true)
		#inputbox("component","Komponente",true)
		<br>
		#inputbox("arch","Architektur",true)
		<br>
		#inputbox("url","URL",true)
		<br>
		<label for="type">Typ</label>
		#dropdown("type", $repo_types ,"")
		<br>
		<label for="pubkey">Repository-Schlüssel:</label>
		<textarea cols="65" rows="6" name="pubkey" id="pubkey" style="font-family:monospace">
		</textarea>
	</fieldset>
	</form>
</div>
<script type="text/javascript">
function form_${form_action}_load(o) {
#if($isNewAction)
				$("input#key").val('');
				$("input#name").val('');
				$("input#description").val('');
				$("input#dist").val('');
				$("input#component").val('');
				$("input#arch").val('');
				$("input#url").val('');
				$("textarea#pubkey").val('');
#else
				$("input#key").val(o.id);
				$("input#name").val(o.name);
				$("input#description").val(o.description);
				$("input#dist").val(o.dist);
				$("input#component").val(o.component);
				$("input#arch").val(o.arch);
				$("input#url").val(o.url);
				$("select#type").val(o.type);
				$("textarea#pubkey").val(o.pubkey);
#end
}

function form_${form_action}_validate() {
        var name = $("input#name").filter(":visible");
        var url = $("input#url").filter(":visible");
        valid = checkIdentifier(name);
	valid = checkURL(url) && valid;
        return valid;
}
</script>
