function updateTips(n) {
}

function checkLength(o, n, min, max) {
	if ( o.val().length > max || o.val().length < min ) {
		o.addClass( "ui-state-error" );
		updateTips( "Length of " + n + " must be between " +
		min + " and " + max + "." );
		o.addClass( "ui-state-error" );
		return false;
	} else {
		o.removeClass("ui-state-error")
		return true;
	}
}

function checkURLold(o) {
	var regexp = new RegExp("^((([A-Za-z]{3,9}:(?:\/\/)?)(?:[\-;:&=\+\$,\w]+@)?[A-Za-z0-9\.\-]+|(?:www\.|[\-;:&=\+\$,\w]+@)[A-Za-z0-9\.\-]+)((?:\/[\+~%\/\.\w\-_]*)?\??(?:[\-\+=&;%@\.\w_]*)#?(?:[\.\!\/\\\w]*))?)$");
	return checkRegexp(o,regexp, "ung&uuml;ltige URL");
}

function checkURL(o) {
	if ( !(isValidUrl(o.val()))) {
		o.addClass("ui-state-error");
		updateTips("ung&uuml;ltige URL");
		return false;
	} else {
		o.removeClass("ui-state-error")
		return true;
	}
}

function isValidUrl(string) {
  let url;

  try {
    url = new URL(string);
  } catch (_) {
    return false;  
  }

  return url.protocol === "http:" || url.protocol === "https:"
	|| url.protocol === "ftp:";
}

function checkInteger(o) {
	var regexp=/^[0-9]+$/;
	return checkRegexp(o,regexp, "positive ganze Zahl erwartet");
}

function checkRegexp(o, regexp, n) {
	if ( !( regexp.test( o.val() ) ) ) {
		o.addClass( "ui-state-error" );
		updateTips( n );
		return false;
	} else {
		o.removeClass("ui-state-error")
		return true;
	}
}


function isHexDigit(c) {
	c=c.toUpperCase();
	return (c>='0' && c<='9') || (c>='A'&& c<='F');
}

function checkMacAddress(o, n) {
	var mac=o.val();
	var newmac="";
	var digitCount=0;
	var separatorExpected=false;

	for(i=0;i<mac.length;i++) {
		if(digitCount>=12) continue;

		c=mac[i].toUpperCase();
		separatorExpected=(newmac.length%3==2);

		if(separatorExpected) {
			if(c==':' || isHexDigit(c)) {
				newmac=newmac+':';
			} 
		}
		if(isHexDigit(c)) {
			newmac=newmac+c;
			digitCount++;
		}
	}
	o.val(newmac);
	if(digitCount==12) {
		o.removeClass("ui-state-error")
		return true;
	}
	else  {
		o.addClass("ui-state-error");
		return false;
	}
}

function checkEMail(o) {
	var regexp=/^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$/;
	return checkRegexp(o,regexp, "ung&uuml;ltige E-Mail-Adresse");
}

function checkIdentifier(o) {
	var regexp=/^[A-Za-z][-._A-Za-z0-9]*$/;
	return checkRegexp(o,regexp, "ung&uuml;ltiger Name");
}

function checkPackageIdentifier(o) {
	var regexp=/^[A-Za-z][-+._A-Za-z0-9]*$/;
	return checkRegexp(o,regexp, "ung&uuml;ltiger Name");
}

function checkIpAddress(o) {
	var regexp=/^(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$/;
	return checkRegexp(o,regexp, "ung&uuml;ltige IP-Adresse");
}
