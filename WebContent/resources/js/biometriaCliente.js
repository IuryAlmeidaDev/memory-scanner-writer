function pesquisar(isPesquisaAntesDoCadastro) {
	usuarioEstaCadastrando(isPesquisaAntesDoCadastro);
	enviaComandoParaApp({ acao: 'pesquisar' });
}

function cadastrar(biometriaEhObrigatoriaNoCadastroDaUnidade) {
	if (wsDesktop.readyState == wsDesktop.OPEN && leitorHabilitado && biometriaEhObrigatoriaNoCadastroDaUnidade) {
		usuarioEstaCadastrando(true);
		pesquisar(true);
	} else {
		executarCadastro();
	}
}

function usuarioEstaCadastrando(value) {
	var c = document.getElementById("usuarioEstaCadastrando");
	c.value = value;
}

leitorBiometricoEstaProntoParaSerUsado.registerListener(function(value) {
  setUsuarioPossuiLeitorBiometricoAtivo([{name: 'status', value: value}]);
});

function registrar(idPessoa) {
	enviaComandoParaApp({acao: 'registrar', idPessoa: idPessoa });
}

function capturar() {
	enviaComandoParaApp({ acao: 'capturar' });
}