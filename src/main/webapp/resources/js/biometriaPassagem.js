
function pesquisar() {
	enviaComandoParaApp({ acao: 'pesquisar' });
}

function capturar() {
	enviaComandoParaApp({ acao: 'capturar' });
}

leitorBiometricoEstaProntoParaSerUsado.registerListener(function(value) {
  setUsuarioPossuiLeitorBiometricoAtivo([{name: 'status', value: value}]);
});

function validaBotaoBiometrico() {
	botoesBiometricos = document.getElementsByClassName("botaoBiometrico");
	validar_leitor();
	
	var temBiometria = document.getElementById('formPassagem:visitanteTemBiometria').value === 'true';
	var habilitaBotaoAutorizacao = null;
	
	if (leitorBiometricoEstaProntoParaSerUsado.status && temBiometria) {
		habilitaBotaoAutorizacao = false;
	} else if (leitorBiometricoEstaProntoParaSerUsado.status && !temBiometria) {
		habilitaBotaoAutorizacao = true;
	} else if (!leitorBiometricoEstaProntoParaSerUsado.status) {
		habilitaBotaoAutorizacao = true;
	}
	
	setStatusBotaoAutorizacao(habilitaBotaoAutorizacao, "formPassagem:btnAutorizacao");
}

function setStatusBotaoAutorizacao(isBotaoHabilitado, id) {
	var elemento = document.getElementById(id);
	
	if (elemento == null) {
		return;
	}
	
	var classe = elemento.getAttribute("class");
	
	if (isBotaoHabilitado) {
		elemento.removeAttribute("disabled");
		elemento.removeAttribute("aria-disabled");
		
		if (classe.includes("ui-state-disabled")) {
			elemento.setAttribute("class", classe.replace("ui-state-disabled", ""));
		}
	} else {
		elemento.setAttribute("disabled", "disabled");
		elemento.setAttribute("aria-disabled", "true");
		
		if (!classe.includes("ui-state-disabled")) {
			elemento.setAttribute("class", classe.concat(" ui-state-disabled"));
		}
	}
}
