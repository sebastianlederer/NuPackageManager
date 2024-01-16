<div id="dialog-form-certificate" title="Zertifikatsverwaltung">
	<form id="form-certificate" method="post" accept-charset="iso-8859-2"
		action="$root/$module/certificate-action.vm" autocomplete="off">
	<fieldset>
		<input type="hidden" name="key" id="key"/>
		<label for="name">Name</label>
		<input type="text" name="name" id="name" disabled class="text ui-widget-content ui-corner-all ui-state-disabled"/>
		<br>
		<label>Aktuelles Zertifikat:</label><br>
		<pre id="certinfo" class="certinfo">





		</pre>
		<label for="password">CA-Passwort</label>
		<input type="password" id="password" name="password" class="text ui-widget-content ui-corner-all"/>
		<br>
	</fieldset>
	</form>
</div>
<script type="text/javascript">
function form_certificate_load(o) {
				$("input#key").val(o.name);
				$("input#name").val(o.name);
				$("input#password").val('');
				$.get("certinfo.vm?key="+o.name, function(data) {
					$('#certinfo').html(data);			
				});
}
function form_certificate_validate() {
	return true;
}

function form_certificate_submit() {
	var capassword = $("input#password").val();
	var name = $("input#key").val();
	$.post("certificate-action.vm?key="+name+"&password="+capassword,
		function(data) {
			if(data.indexOf("error")>=0) {
				alert(data);
			}
			else {
				alert("Zertifikat wurde angelegt.");
			}
		});
}
</script>
