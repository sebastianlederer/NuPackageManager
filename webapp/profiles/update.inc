<div id="dialog-form-update" title="Aktualisierung bestätigen">
<form id="form-update" method="POST" action="update-action.vm">
<input type="hidden" id="key" name="key">
<p><span class="ui-icon ui-icon-alert" style="float: left; margin: 0 7px 20px 0;"></span>
Die ausgewählte Paketliste wird von ihrem Vorgänger aktualisiert. Dieser Vorgang lässt sich nicht rückgängig machen.</p>
</p>Bitte bestätigen Sie die Aktualisierung.</p>
</form>
</div>
<script type="text/javascript">
	function form_update_load(o) {
		$("input#key").val(o.name);
	}
</script>
