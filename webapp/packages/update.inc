<div id="dialog-form-update" title="Aktualisierung bestätigen">
<form id="form-update" method="POST" action="update-action.vm">
<input type="hidden" id="key" name="key">
<input type="hidden" id="list" name="list" value="$params.list">
#if($params.show)
<input type="hidden" id="show" name="show" value="$params.show">
#end
#if ($viaprofile)
<input type="hidden" id="profile" name="profile" value="$!params.profile">
#end
<p><span class="ui-icon ui-icon-alert" style="float: left; margin: 0 7px 20px 0;"></span>
Das ausgewählte Paket wird auf die Version aus der Vorgänger-Paketliste aktualisiert.</p>
<p>Dieser Vorgang lässt sich nicht rückgängig machen.</p>
<p>Bitte bestätigen Sie die Aktualisierung.</p>
</form>
</div>
<script type="text/javascript">
	function form_update_load(o) {
		$("input#key").val(o.id);
	}
</script>
