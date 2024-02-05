<div id="dialog-form-update" title="Aktualisierung bestätigen">
<form id="form-update" method="POST" action="update-action.vm">
<input type="hidden" id="key" name="key">
#if ($viaprofile)
<input type="hidden" id="profile" name="profile" value="$!params.profile">
#end
<p><span class="ui-icon ui-icon-alert" style="float: left; margin: 0 7px 20px 0;"></span>
#if($viaprofile)
Alle Repositories des ausgewählten Profils werden aktualisiert.
#else
Das ausgewählte Repository wird von seinem <span id="changetext">...</span> aktualisiert.
#end
Dieser Vorgang lässt sich nicht rückgängig machen.</p>
<p>Bitte bestätigen Sie die Aktualisierung.</p>
</form>
</div>
<script type="text/javascript">
	function form_update_load(o) {
#if(!$viaprofile)
		$("input#key").val(o.id);
		if(o.upstream && o.upstream!="") {
			$("#changetext").html("Upstream-Repository");
		} else {
			$("#changetext").html("Vorgänger");
		}
		
#end
	}

	#js_form_submit("update", "repos", "id")
</script>
