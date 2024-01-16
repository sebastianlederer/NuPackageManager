#set($form_action="approve")
#set($form_title="Freigabe bestätigen")
<div id="dialog-form-${form_action}" title="$form_title">
<form id="form-${form_action}" method="POST" action="${form_action}-action.vm">
<input type="hidden" id="key" name="key">
#if($params.profile)
<input type="hidden" id="profile" name="profile" value="$!params.profile">
#end
<p><span class="ui-icon ui-icon-alert" style="float: left; margin: 0 7px 20px 0;"></span>
Das ausgewählte Repository wird freigegeben. Danach können andere Repositories von diesem Repository aktualisiert werden.</p>
<p>Bitte bestätigen Sie die Freigabe.</p>
</form>
</div>
<script type="text/javascript">
	function form_${form_action}_load(o) {
		$("input#key").val(o.id);
	}
</script>
