var wsDesktop;
var intervalo;
var intervaloPing;
var btnBio = document.getElementById("btnBio");
var leitorHabilitado;
var versaoInstalada;

if (btnBio == null) {
	btnBio = document.getElementById("tabView:btnBio");
}

desabilitaBotao();

if (podeHabilitarEConectar) {
	wsDesktopConnect();
}

function wsDesktopConnect() {
	wsDesktop = new WebSocket(wsUrl);
	
	wsDesktop.onopen = function()
	{
		//console.log("wsDesktop abriu");
		clearInterval(intervalo);
		clearInterval(intervaloPing);
		intervaloPing = setInterval(pingWs, 60000);
		validar_leitor();
	};

	wsDesktop.onerror = function(e)
	{
		//console.log("wsDesktop erro");
	};

	wsDesktop.onclose = function(e)
	{
		//console.log("wsDesktop conexão fechada");
		desabilitaBotao();
		clearInterval(intervalo);
		intervalo = setInterval(checaStatusWs, 5000);
	};

	wsDesktop.onmessage = function(e)
	{
		//console.log("wsDesktop mensagem");
		var obj = JSON.parse(e.data);

		if (obj != null) {
			switch (obj.acao) {
				case "capturar_template":
					preencherTemplate(obj.valor);
					break;
					
				case "pesquisar":
					enviarParametroParaServidor(obj.valor);
					break;
					
				case "registrar":
					enviarParametroParaServidor(obj.valor);
					break;
					
				case "validar_leitor":
					leitorVerificado(obj.valor);
					break;
					
				case "versao":
					versaoVerificada(obj);
					break;
				
				case "ping":
					console.log('ping rcv');
					break;
						
				case "objeto":
					//console.log(obj.valor);
					break;
				default:
					break;
			}
		}
	};
}

function pingWs() {
	if (wsDesktop.readyState == wsDesktop.OPEN) {
		enviaComandoWs('ping');
	}
}

function checaStatusWs() {
	if (wsDesktop.readyState == wsDesktop.CLOSED) {
		wsDesktopConnect();
	}
}

function preencherTemplate(template) {
	var h = document.getElementById("paramTemplate")
	h.value = template;
	capturarTemplate();
}

function enviarParametroParaServidor(obj) {
    var isValidar = document.getElementById("paramIsValidar");
	var h = document.getElementById("paramBiometria");
	var hBioRec = document.getElementById("paramBiometriaRecuperando");
	var hTemplate = document.getElementById("paramTemplate");
	
	if(hBioRec) 
		hBioRec.value = obj;
	

	if (h == null)
		h = document.getElementById("tabView:paramBiometria");

    if (h) {
        h.value = obj;
        if(isValidar && isValidar.value === "true")
        	remoteValidarBiometriaRecuperandoVisitante();
        else
        	executarBiometria();
    }
    
}

function enviaComandoWs(comando) {
	var obj;
	
	if (comando == 'registrar') {
		var idPessoa = document.getElementById("tabView:idPessoa");
		obj = { acao: comando, idPessoa: idPessoa.value };
	} else if (comando == 'validar_leitor') {
		obj = { acao: comando, contexto: contexto, contexto2: contexto2 };
	} else if (comando == 'capturar_template') {
		obj = { acao: 'pesquisar' };
	} else {
		obj = { acao: comando };
	}

	wsDesktop.send(JSON.stringify(obj));	
}

function setIsCadastro(value) {
	var c = document.getElementById("paramIsCadastro");
	c.value = value;
}

function pesquisar() {
	setIsCadastro(false);
	enviaComandoWs('pesquisar');
}

function cadastrar(paramBiometriaObrigatoria) {
	if (wsDesktop.readyState == wsDesktop.OPEN && leitorHabilitado && paramBiometriaObrigatoria) {
		setIsCadastro(true);
		enviaComandoWs('pesquisar');
	} else {
		executarCadastro();
	}
}

function validar_leitor() {
	enviaComandoWs('validar_leitor');
}

function validar_versao() {
	enviaComandoWs('versao');
}

function leitorVerificado(param) {
	leitorHabilitado = (param === 'true');
	
	if (leitorHabilitado && biometriaObrigatoria) {
		validar_versao();
	} else {
		desabilitaBotao();
	}
}

function versaoVerificada(obj) {
	if (obj.valor != null) {
		versaoInstalada = obj.valor;
	} else {
		versaoInstalada = obj.versaoInstalada;
	}
	
	if ((versaoInstalada === versaoAtual)) {
		habilitaBotao();
	} else {
		desabilitaBotao();
		PF('dlgVersoes').show();
	}
}

function desabilitaBotao() {
	if (btnBio != null && btnBio.getAttribute("disabled") == null) {
		validaBiometriaAtiva(false);
		 
		btnBio.setAttribute("disabled", "disabled");
		btnBio.setAttribute("aria-disabled", "true");
		btnBio.setAttribute("class", btnBio.getAttribute("class").concat(" ui-state-disabled"));
	}
 }

function habilitaBotao() {
	if (btnBio != null && btnBio.getAttribute("disabled") != null) {
		validaBiometriaAtiva(true);
		
		btnBio.removeAttribute("disabled");
		btnBio.removeAttribute("aria-disabled");
		btnBio.setAttribute("class", btnBio.getAttribute("class").replace("ui-state-disabled", ""));
	}
}

function validaBiometriaAtiva(param) {
	validaBioTelaPesquisa(param);
	var h = document.getElementById("tabView:temBiometria");

	if (h != null) {
		h.value = param;
		PrimeFaces.ajax.Request.handle({
			formId:'tabView:formGamb'
			,source:'tabView:formGamb'
			,process:'tabView:formGamb'
			});
	}
}

function validaBioTelaPesquisa(param) {
	var h = document.getElementById("temBio");
	if (h != null) {
		h.value = param;

		PrimeFaces.ajax.Request.handle({
			formId:'frmInit'
			,source:'frmInit'
			,process:'frmInit'
		});
	}
}


function clickValidarBiometriaRecuperandoVisitante(){
	var c = document.getElementById("paramIsValidar");
	c.value = true;
	enviaComandoWs('pesquisar');
}
