<div id="dialog-form-delete" title="Paket löschen ?">
	<form id="form-delete" method="post" accept-charset="iso-8859-2" action="$root/$module/delete-action.vm">
	<fieldset>
		<input type="hidden" name="key" id="key"/>
		<input type="hidden" name="list" id="list" value="$params.list"/>
#if($params.show)
		<input type="hidden" name="show" id="show" value="$params.show"/>
#end
		#inputbox("name","Name",false)
		<br>
	</fieldset>
	</form>
</div>
<script type="text/javascript">
function form_delete_load(o) {
				$("input#key").val(o.id);
				$("input#name").val(o.name);
}

function form_delete_validate() {
	return true;
}
</script>
