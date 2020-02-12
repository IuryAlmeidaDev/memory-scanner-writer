var wsDesktop;

wsDesktopConnect();

function wsDesktopConnect() {
	wsDesktop = new WebSocket("wss://wso.sejudh.mt.gov.br:8443/sigepen/biometria");
	
	wsDesktop.onopen = function()
	{
		var obj = { acao: 'validar_leitor', contexto: contexto, contexto2: contexto2 };
		wsDesktop.send(JSON.stringify(obj));
	};

	wsDesktop.onclose = function(e)
	{
		setStatus('Aplicativo biométrico fechado ou não instalado.');
	};

	wsDesktop.onmessage = function(e)
	{
		var obj = JSON.parse(e.data);

		if (obj.valor != null) {
			switch (obj.acao) {
				case "validar_leitor":
					validarLeitor(obj.valor);
					break;
						
				case "versao":
					validarVersao(obj.valor);
					break;
					
				default:
					break;
			}
		}
	};
}

function setStatus(mensagem) {
	var h = document.getElementById("msgStatus");
	
	if (h != null) {
		h.innerHTML = mensagem;
	}
}

function validarVersao(param) {
	if ((param === 'true')) {
		setStatus('Nenhum problema foi encontrado.');
	} else {
		setStatus('Versão do aplicativo desatualizada, por favor atualize.');
	}
}

function validarLeitor(param) {
	if ((param === 'true')) {
		var obj = { acao: 'versao', contexto: contexto, contexto2: contexto2 };
		wsDesktop.send(JSON.stringify(obj));
	} else {
		setStatus('Leitor biométrico não conectado ou driver não está instalado.');
	}
}