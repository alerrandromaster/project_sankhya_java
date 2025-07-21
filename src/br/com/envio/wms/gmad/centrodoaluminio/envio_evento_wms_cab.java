package br.com.envio.wms.gmad.centrodoaluminio;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.Timestamp;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormatter;

import com.google.gson.JsonObject;
import com.sankhya.util.BigDecimalUtil;
import com.sankhya.util.TimeUtils;

import br.com.sankhya.dwf.services.ServiceUtils;
import br.com.sankhya.extensions.eventoprogramavel.EventoProgramavelJava;
import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.event.PersistenceEvent;
import br.com.sankhya.jape.event.TransactionContext;
import br.com.sankhya.jape.sql.NativeSql;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.mgecomercial.model.facades.helpper.carrinho.JsonUtils;
import br.com.sankhya.mgewms.model.services.ExpedicaoMercadoriaSP;
import br.com.sankhya.mgewms.model.services.ExpedicaoMercadoriaSPHome;
import br.com.sankhya.modelcore.auth.AuthenticationInfo;
import br.com.sankhya.modelcore.servicos.listeners.MgeWmsSPListener;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;
import br.com.sankhya.modelcore.util.SPBeanUtils;
import br.com.sankhya.ws.ServiceContext;
import br.com.sankhya.ws.transformer.json.Json2XMLParser;



public class envio_evento_wms_cab implements EventoProgramavelJava {

	
	public Timestamp hora;
	public String stJson = "";
	
	@Override
	public void afterDelete(PersistenceEvent arg0) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void afterInsert(PersistenceEvent arg0) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void afterUpdate(PersistenceEvent arg0) throws Exception {
		// TODO Auto-generated method stub
		DynamicVO cabNota = (DynamicVO)arg0.getVo();
		EntityFacade dwfFacade = EntityFacadeFactory.getDWFFacade();
		BigDecimal nunota = BigDecimalUtil.getValueOrZero((BigDecimal)cabNota.getProperty("NUNOTA"));
		 String AD_STATUSNOTA = cabNota.asString("STATUSNOTA");
		 String AD_TIPMOV = cabNota.asString("TIPMOV");
		
		 
		JdbcWrapper jdbc = null;
		JapeSession.SessionHandle hnd = null;
		 ServiceContext serviceContext = new ServiceContext(null);
		    serviceContext.setAutentication(AuthenticationInfo.getCurrent());
		    serviceContext.makeCurrent();
		    try {
		        SPBeanUtils.setupContext(serviceContext);
		        hnd = JapeSession.getCurrentSession().getTopMostHandle();
		        jdbc = dwfFacade.getJdbcWrapper();
		        jdbc.openSession();
		      } catch (Exception e) {
		        e.printStackTrace();
		      } 
		    MgeWmsSPListener listener = new MgeWmsSPListener();
		    try {
		    	listener.beforeExecute(serviceContext);
		    	 LocalDate currentDate = LocalDate.now();
		    	 hora = TimeUtils.getNow();
		    	 String hor = "19/12/2024";
		    	 BigDecimal OrdemCarga = new BigDecimal(5112);
		    	 BigDecimal Codemp = new BigDecimal(10);
		    	 NativeSql sql = new NativeSql(jdbc);
		    	 BigDecimal Doca = new BigDecimal(69);
		    	  sql.appendSql(" SELECT DOCA.CODDOCA FROM TGWDCA DOCA  INNER JOIN TGWEND WEN ON DOCA.CODEND = WEN.CODEND  WHERE DOCA.TIPDOCA = 'S' AND DOCA.SITUACAO = 'L' AND DOCA.ATIVO = 'S' AND DOCA.BALCAO = 'N' AND CODEMP = (SELECT CODEMP FROM TGFCAB WHERE NUNOTA = :PIDIPROC ) ORDER BY CODDOCA DESC ");
		          sql.setNamedParameter("PIDIPROC", nunota);
		          ResultSet rset = sql.executeQuery();
		  
		        	   stJson = "{\r\n  \"params\": {\r\n    \"separacaoPorOC\": true,\r\n    \"agrupar\": false,\r\n    \"notas\": {},\r\n    \"ORDEMCARGA\": " +
		        			  OrdemCarga + ",\r\n" + 
		        	            "    \"CODEMP\": " + Codemp + ",\r\n" + 
		        	            "    \"expedicaoContextProperty\": {\r\n" + 
		        	            "      \"TIPOSEPARACAO\": \"SEPARACAO_NORMAL\"\r\n" + 
		        	            "    },\r\n" + 
		        	            "    \"docas\": {\r\n" + 
		        	            "      \"doca\": {\r\n" + 
		        	            "        \"CODDOCA\": " + Doca + ",\r\n" + 
		        	            "        \"ORDEMCARGA\": \"" + OrdemCarga + "\",\r\n" + 
		        	            "        \"CODEMPOC\": \"" + Codemp + "\"\r\n" + 
		        	            "      }\r\n" + 
		        	            "    },\r\n" + 
		        	            "    \"dataExpedicao\": \"" + hor + "\"\r\n" + 
		        	            "  }\r\n" + 
		        	            "}\r\n";
		          
		          JsonObject jsonObject = JsonUtils.convertStringToJsonObject(stJson);
		          serviceContext.setJsonRequestBody(jsonObject);
		          serviceContext.setRequestBody(Json2XMLParser.jsonToElement("requestBody", serviceContext.getJsonRequestBody()));
		          ExpedicaoMercadoriaSP expedicaoMercadoriaSP = (ExpedicaoMercadoriaSP)ServiceUtils.getStatelessFacade("mge/wms/ejb/session/ExpedicaoMercadoriaSP", ExpedicaoMercadoriaSPHome.class);
		          System.out.println("Enviando Requisi EVENTO" + serviceContext.getJsonRequestBody());
		          expedicaoMercadoriaSP.gerarOndaSeparacao(serviceContext);
		          System.out.println("ApEnviando Requisi" + serviceContext.getJsonRequestBody());	 
		    }catch (Exception e) {
		    	  e.printStackTrace();
		          System.out.println("a Nota deu erro EVENTO");
			}finally {
			      if (listener != null)
			          try {
			            listener.clean(serviceContext);
			          } catch (Exception e) {
			            e.printStackTrace();
			          }  
			      } 
		 
		
	}

	@Override
	public void beforeCommit(TransactionContext arg0) throws Exception {
		
	}

	@Override
	public void beforeDelete(PersistenceEvent arg0) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void beforeInsert(PersistenceEvent arg0) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void beforeUpdate(PersistenceEvent arg0) throws Exception {
		// TODO Auto-generated method stub
		
	}


}
