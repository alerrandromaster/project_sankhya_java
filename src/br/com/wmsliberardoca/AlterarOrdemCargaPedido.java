package br.com.wmsliberardoca;


import br.com.wmsliberardoca.SWServiceInvoker;

import br.com.sankhya.dwf.services.ServiceUtils;
import br.com.sankhya.extensions.actionbutton.AcaoRotinaJava;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.extensions.actionbutton.Registro;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;
import br.com.sankhya.mgecomercial.model.facades.FormacaoCargaSP;
import br.com.sankhya.mgecomercial.model.facades.FormacaoCargaSPHome;
import br.com.sankhya.modelcore.auth.AuthenticationInfo;
import br.com.sankhya.modelcore.util.MGECoreParameter;
import java.math.BigDecimal;
import br.com.sankhya.ws.ServiceContext;
import org.jdom.Element;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory; 


public class AlterarOrdemCargaPedido implements AcaoRotinaJava {
	private static Logger logger = LoggerFactory.getLogger(AlterarOrdemCargaPedido.class);
	    

	@Override
	public void doAction(ContextoAcao contexto) throws Exception {
		
		Registro[] registro = contexto.getLinhas();
		if (registro.length == 0) {
			throw new Exception("Selecione um registro");
		}
		
		JapeWrapper usuarioDAO = JapeFactory.dao("Usuario");
		
		BigDecimal codUsu = BigDecimal.ZERO;
		
		try {
			AuthenticationInfo usuarioLogado = AuthenticationInfo.getCurrent();
			codUsu = usuarioLogado.getUserID();
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		try {
			DynamicVO usuarioVO = usuarioDAO.findByPK(codUsu);
			
			String domain = (String) MGECoreParameter.getParameter("URLSANKHYAW");
			
			SWServiceInvoker si = new SWServiceInvoker(domain, usuarioVO.asString("NOMEUSU"), usuarioVO.asString("INTERNO"));
			si.setCriptedPass(true);
			
			
			Element requestBody = new Element("requestBody");
			Element notas = new Element("notas");
			Element nota = new Element("nota");
			
			ServiceContext serviceContext = new ServiceContext(null);
			serviceContext.setAutentication(AuthenticationInfo.getCurrent());  
			serviceContext.makeCurrent();	
			
			for (Registro linha : registro) {
				Element nunotaElement;
				nunotaElement = new Element("NUNOTA");
				nunotaElement.setText(linha.getCampo("NUNOTA").toString());
				nota.addContent(nunotaElement);
				
				Element ordemCargaElement;
				ordemCargaElement = new Element("ORDEMCARGA");
				ordemCargaElement.setText(contexto.getParam("P_ORDEMCARGA") != null ? contexto.getParam("P_ORDEMCARGA").toString() : "0");
				nota.addContent(ordemCargaElement);
				
				Element codParcTransp;
				codParcTransp = new Element("CODPARCTRANSP");
				codParcTransp.setText(linha.getCampo("CODPARCTRANSP").toString());
				nota.addContent(codParcTransp);
				
				Element veiculo;
				veiculo = new Element("CODVEICULO");
				veiculo.setText(linha.getCampo("CODVEICULO").toString());
				nota.addContent(veiculo);
				
				Element seqCarga;
				seqCarga = new Element("SEQCARGA");
				seqCarga.setText(linha.getCampo("SEQCARGA") != null ? linha.getCampo("SEQCARGA").toString() : "");
				nota.addContent(seqCarga);
				
				Element dtEntSai;
				dtEntSai = new Element("DTENTSAI");
				dtEntSai.setText("" + com.sankhya.util.TimeUtils.formataDDMMYYYY(linha.getCampo("DTENTSAI")) + "");
				nota.addContent(dtEntSai);
				
				Element qtdVol;
				qtdVol = new Element("QTDVOL");
				qtdVol.setText(linha.getCampo("QTDVOL").toString());
				nota.addContent(qtdVol);
				
				Element m3;
				m3 = new Element("M3");
				m3.setText(linha.getCampo("M3") != null ? linha.getCampo("M3").toString() : "0");
				nota.addContent(m3);
				
			}
			notas.addContent(nota);
			requestBody.addContent(notas);
			Element clientEventList = new Element("clientEventList");
			String[] clientEvents = { "br.com.sankhya.mgewms.expedicao.validarPedidos",
					"br.com.sankhya.mgewms.expedicao.selecaoDocas",
					"br.com.sankhya.mgewms.expedicao.cortePedidos",
					"br.com.sankhya.mgewms.expedicao.encerrarOC",
					"br.com.sankhya.actionbutton.clientconfirm"
			};
			
			for (String event : clientEvents) {
				Element clientEvent = new Element("clientEvent").setText(event);
				clientEventList.addContent(clientEvent);
			}
			requestBody.addContent(clientEventList);
			
			serviceContext.setRequestBody(requestBody);
			FormacaoCargaSP formacaoCargaSP = (FormacaoCargaSP) ServiceUtils.getStatelessFacade(FormacaoCargaSPHome.JNDI_NAME, FormacaoCargaSPHome.class);
			formacaoCargaSP.confirmaAlteracoesNotasOC(serviceContext);
			
		} catch (Exception e) {

		}	
	}
}