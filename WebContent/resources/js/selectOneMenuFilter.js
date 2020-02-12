function removeAcento(itemLabel, filterValue) {
	return (replaceSpecialChars(itemLabel)).indexOf(replaceSpecialChars(filterValue))>-1;
}

function replaceSpecialChars(str)
{
	str = str.replace(/[ÀÁÂÃÄÅ]/,"A");
	str = str.replace(/[àáâãäå]/,"a");
	str = str.replace(/[ÈÉÊË]/,"E");
	str = str.replace(/[èéêë]/,"e");
	str = str.replace(/[ÌÍÎÏ]/,"i");
	str = str.replace(/[ìíîï]/,"i");
	str = str.replace(/[ÒÓÔÕÖ]/,"O");
	str = str.replace(/[òóôõö]/,"o");
	str = str.replace(/[ÙÚÛÜ]/,"U");
	str = str.replace(/[ùúûü]/,"u");
	str = str.replace(/[Ç]/,"C");
	str = str.replace(/[ç]/,"c");

	return  str.replace(/[^a-z0-9]/gi,'');
}