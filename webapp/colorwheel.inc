<style>
#set($colorindex=1)
#foreach($c in $colorwheel)
input[type="checkbox"] + label.cell$colorindex { color: black; background: white; border-color: $c; }
input[type="checkbox"]:checked + label.cell$colorindex { color: white; background-image:none; border-color: $c; background-color: $c; }
.checkboxfield input[type="checkbox"] + label.earmarked$colorindex { background-image: linear-gradient(135deg, $c, white 15%); }
.checkboxfield input[type="checkbox"]:checked + label.earmarked$colorindex { background-image: none; }
#set($colorindex=$colorindex+1)
#end

.checkboxfield {
	display:grid;
	grid-template-columns: 1fr 1fr 1fr 1fr 1fr 1fr;
	grid-gap: 0.5rem;
}

.checkboxfield input[type="checkbox"] {
	display: none;
}
	
.checkboxfield input[type="checkbox"] + label  {
	transition: all 300ms ease;
	cursor: pointer;
	border-radius: 3px;
	border-style: solid;
	border-width: 1px;
	padding: 0.5rem;
	display: inline-block;
	user-select: none;
	text-align: center;
	margin: 0;
}

</style>
