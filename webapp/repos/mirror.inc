#set($form_action="mirror")
#set($form_title="Spiegeln bestätigen")
<div id="dialog-form-${form_action}" title="$form_title">
<form id="form-${form_action}" method="POST" action="${form_action}-action.vm">
<input type="hidden" id="key" name="key">
#if($params.profile)
<input type="hidden" id="profile" name="profile" value="$!params.profile">
#end
<p><span class="ui-icon ui-icon-alert" style="float: left; margin: 0 7px 20px 0;"></span>
Das ausgewählte Repository wird sofort von seinem Upstream-Repository aktualisiert.</p>
<p>Dieser Vorgang kann je nach Größe des Upstream-Repositories und der Download-Geschwindigkeit mehrere Stunden dauern.</p>
<p>Bitte bestätigen Sie die Aktion.</p>
</form>
</div>
<script type="text/javascript">
	function form_${form_action}_load(o) {
		$("input#key").val(o.id);
	}

	#js_form_submit("mirror", "repos", "id")
</script>
