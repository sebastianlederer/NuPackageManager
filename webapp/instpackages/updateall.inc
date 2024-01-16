<div id="dialog-form-updateall" title="Aktualisierung bestätigen">
<form id="form-updateall" method="POST" action="updateall-action.vm">
<input type="hidden" id="key" name="key" value="$params.list">
<input type="hidden" id="list" name="list" value="$params.list">
#if($params.show)
<input type="hidden" id="show" name="show" value="$params.show">
#end
#if ($viaprofile)
<input type="hidden" id="profile" name="profile" value="$!params.profile">
#end
<p><span class="ui-icon ui-icon-alert" style="float: left; margin: 0 7px 20px 0;"></span>
Alle Pakete werden auf die Version aus der Vorgänger-Paketliste aktualisiert.</p>
<p>Dieser Vorgang lässt sich nicht rückgängig machen.</p>
<p>Bitte bestätigen Sie die Aktualisierung.</p>
</form>
</div>
<script type="text/javascript">
	function form_updateall_load(o) {
	}
</script>
