var wsDesktop;
var pingAppDesktopVerificaEstaAberto;
var pingAppDesktopTimeout;
var botoesBiometricos = document.getElementsByClassName("botaoBiometrico");
var leitorHabilitado;
var versaoInstalada;

leitorBiometricoEstaProntoParaSerUsado = {
	statusInternal: false,
	statusListener: function(value) {},
	set status(value) {
		this.statusInternal = value;
		this.statusListener(value);
	},	
	get status() {
		return this.statusInternal;
	},
	registerListener: function(listener) {
		this.statusListener = listener;
	}
};

alteraSituacaoBotoes(false);

if (sistemaPodeConectarComAppDesktop) {
	wsDesktopConnect();
}

function wsDesktopConnect() {
	wsDesktop = new WebSocket(wsUrl);
	
	wsDesktop.onopen = function()
	{
		clearInterval(pingAppDesktopVerificaEstaAberto);
		clearInterval(pingAppDesktopTimeout);
		pingAppDesktopTimeout = setInterval(pingAppDesktop, 60000);
		validar_leitor();
	};

	wsDesktop.onerror = function(e)
	{
		//console.log("wsDesktop erro");
	};

	wsDesktop.onclose = function(e)
	{
		alteraSituacaoBotoes(false);
		clearInterval(pingAppDesktopVerificaEstaAberto);
		pingAppDesktopVerificaEstaAberto = setInterval(checaStatusWs, 5000);
	};

	wsDesktop.onmessage = function(e)
	{
		var obj = JSON.parse(e.data);

		if (obj != null) {
			switch (obj.acao) {					
				case "validar_leitor":
					leitorVerificado(obj.valor);
					break;
					
				case "versao":
					versaoVerificada(obj);
					break;
				
				case "ping":
					//console.log('ping rcv');
					break;
						
				default:
					retornoBiometria([{name:'execute@' + obj.acao, value:obj.valor}]);
					break;
			}
		}
	};
}

function pingAppDesktop() {
	enviaComandoParaApp({ acao: 'ping' });
}

function checaStatusWs() {
	if (wsDesktop.readyState == wsDesktop.CLOSED) {
		wsDesktopConnect();
	}
}

function enviaComandoParaApp(json) {
	if (wsDesktop.readyState == wsDesktop.OPEN) {
		var comm = JSON.stringify(json);
		wsDesktop.send(comm);
	} else {
	    console.log('conexão fechada');
	    alteraSituacaoBotoes(false);
	}
}

function validar_leitor() {
	enviaComandoParaApp({ acao: 'validar_leitor', contexto: contexto, contexto2: contexto2 });
}

function validar_versao() {
	enviaComandoParaApp({ acao: 'versao' });
}

function leitorVerificado(param) {
	leitorHabilitado = (param === 'true');
	
	if (leitorHabilitado) {
		validar_versao();
	} else {
		alteraSituacaoBotoes(false);
	}
}

function versaoVerificada(obj) {
	if (obj.valor != null) {
		versaoInstalada = obj.valor;
	} else {
		versaoInstalada = obj.versaoInstalada;
	}
	
	if ((versaoInstalada === versaoAtual)) {
		alteraSituacaoBotoes(true);
	} else {
		alteraSituacaoBotoes(false);
		PF('dlgVersoes').show();
	}
}

function setStatusBotao(isBotaoHabilitado, elemento) {
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

function alteraSituacaoBotoes(isBotaoHabilitado) {
	leitorBiometricoEstaProntoParaSerUsado.status = isBotaoHabilitado;
	
	if (botoesBiometricos != null && botoesBiometricos.length > 0) {
		for (let e of botoesBiometricos) {
			setStatusBotao(isBotaoHabilitado, e);
		}
	}
}