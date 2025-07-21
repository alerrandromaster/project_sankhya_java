package br.com.listerners.cancel.pathfind;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import br.com.sankhya.extensions.eventoprogramavel.EventoProgramavelJava;
import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.event.PersistenceEvent;
import br.com.sankhya.jape.event.TransactionContext;
import br.com.sankhya.jape.sql.NativeSql;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;

public class CancelamentoPathFind implements EventoProgramavelJava {

	public String NOta = "";
	public Timestamp DTPREV = null;
	
	@Override
	public void afterDelete(PersistenceEvent arg0) throws Exception {
		// TODO Auto-generated method stub
		System.out.println("INICIO DO EVENTO CANCELAMENTO PATHFIND");
		DynamicVO registro = (DynamicVO) arg0.getVo();
		BigDecimal CODTIPOPER = registro.asBigDecimal("CODTIPOPER");
		
		try {
			
		
		if (CODTIPOPER.compareTo(new BigDecimal(199)) != 0)
		{
		BigDecimal NUNOTA = registro.asBigDecimal("NUNOTA");
		String AD_ENTREGA = registro.asString("AD_ENTREGA");
		String STATUSNOTA = "";
		STATUSNOTA = registro.asString("STATUSNOTA");
		String TIPMOV = "";
		TIPMOV = registro.asString("TIPMOV");
		Timestamp DTPREVENT = registro.asTimestamp("DTPREVENT");
		String STATUSNFE = "";
		STATUSNFE = registro.asString("STATUSNFE");
		String TOP = CODTIPOPER.toString();
		List<String> valoresAD_ENTREGA = Arrays.asList(new String[] { "D", "F", "R", "S", "A", "P" });
		List<String> valoresTop = Arrays.asList(new String[] { "199", "9199", "214", "211"});
		System.out.println("NUNOTA DO PF:"+NUNOTA);
		System.out.println("AD_ENTREGA DO PF:"+AD_ENTREGA);
		System.out.println("STATUSNOTA DO PF:"+STATUSNOTA);
		System.out.println("TIPMOV DO PF:"+TIPMOV);
		
		//excluir tops 199,9199,214,211
		
		
		if (valoresAD_ENTREGA.contains(AD_ENTREGA) && (TIPMOV.equals("V")) && STATUSNOTA.equals("L") && STATUSNFE.equals("A") && !valoresTop.contains(TOP))
		{
			
			System.out.println("INICIOU O PROCESSO DE CANCELAMENTO");
			int ano = getAno(DTPREVENT);
		    int mes = getMes(DTPREVENT);
		    int dia = getDia(DTPREVENT);
		    System.out.println("ANO"+ano);
		    System.out.println("MES"+mes);
		    System.out.println("DIA"+dia);
		    String NUNOTAStr = (NUNOTA != null) ? NUNOTA.toString() : null;
		    
		    enviarPathfind(NUNOTAStr,ano,mes,dia);
		    System.out.println("CANCELAMENTO EFETUADO COM SUCESSO!!!");
		}
		}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	@Override
	public void afterInsert(PersistenceEvent arg0) throws Exception {
		// TODO Auto-generated method stub
	}

	@Override
	public void afterUpdate(PersistenceEvent arg0) throws Exception {
		
		/*System.out.println("INICIO DO EVENTO CANCELAMENTO DE DEVOLUCAO PATHFIND");
		DynamicVO registro = (DynamicVO) arg0.getVo();
		BigDecimal NUNOTA = registro.asBigDecimal("NUNOTA");
		String AD_ENTREGA = registro.asString("AD_ENTREGA");
		String STATUSNOTA = registro.asString("STATUSNOTA");
		String TIPMOV = registro.asString("TIPMOV");
		BigDecimal CODTIPOPER = registro.asBigDecimal("CODTIPOPER");		
		//Timestamp DTPREVENT = registro.asTimestamp("DTPREVENT");
		List<String> valoresAD_ENTREGA = Arrays.asList(new String[] { "D", "F", "R", "S", "A", "P" });
		
		
		System.out.println("NUNOTA DO PF:"+NUNOTA);
		System.out.println("AD_ENTREGA DO PF:"+AD_ENTREGA);
		System.out.println("STATUSNOTA DO PF:"+STATUSNOTA);
		System.out.println("TIPMOV DO PF:"+TIPMOV);
		
		if (valoresAD_ENTREGA.contains(AD_ENTREGA) && (TIPMOV.equals("D")) && STATUSNOTA.equals("L")) 
		{
			System.out.println("INICIOU O PROCESSO DE CANCELAMENTO");
			Find_nota(arg0,NUNOTA);
			Find_Dt(arg0,NOta);
			int ano = getAno(DTPREV);
		    int mes = getMes(DTPREV);
		    int dia = getDia(DTPREV);
		    System.out.println("ANO"+ano);
		    System.out.println("MES"+mes);
		    System.out.println("DIA"+dia);
		    
		    String NUNOTAStr = (NOta != null) ? NOta.toString() : null;
		    enviarPathfind(NUNOTAStr,ano,mes,dia);
		    System.out.println("CANCELAMENTO EFETUADO COM SUCESSO!!!");
		}
		*/
	}

	@Override
	public void beforeCommit(TransactionContext arg0) throws Exception {
		// TODO Auto-generated method stub
		
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
	
	private static int getAno(Timestamp timestamp) {
	      Calendar calendar = Calendar.getInstance();
	      calendar.setTimeInMillis(timestamp.getTime());
	      return calendar.get(Calendar.YEAR);
	  }

	  private static int getMes(Timestamp timestamp) {
	      Calendar calendar = Calendar.getInstance();
	      calendar.setTimeInMillis(timestamp.getTime());
	      return calendar.get(Calendar.MONTH) + 1;
	  }

	  private static int getDia(Timestamp timestamp) {
	      Calendar calendar = Calendar.getInstance();
	      calendar.setTimeInMillis(timestamp.getTime());
	      return calendar.get(Calendar.DAY_OF_MONTH);
	  }
	
	  private static String montarMensagemSOAP(String nunota, int ano, int mes,int dia) {
	      StringBuilder bodyResponse = new StringBuilder();
	      bodyResponse.append("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" ")
	                  .append("xmlns:ws=\"http://ws.integracao.pathfind.lantech.com.br/\">")
	                  .append("<soapenv:Header/>")
	                  .append("<soapenv:Body>")
	                  .append("<ws:excluirPedidosV2>")
	                  .append("<pedido>")
	                  .append("<anoPedido>").append(ano).append("</anoPedido>")
	                  .append("<mesPedido>").append(mes).append("</mesPedido>")
	                  .append("<diaPedido>").append(dia).append("</diaPedido>")
	                  .append("<codigoPedido>").append(nunota).append("</codigoPedido>")
	                  .append("</pedido>")
	                  .append("</ws:excluirPedidosV2>")
	                  .append("</soapenv:Body>")
	                  .append("</soapenv:Envelope>");
	      System.out.println("bodyResponse_LOG_CANCELAMENTO:" + bodyResponse.toString());
	      return bodyResponse.toString();
	  }
	  
	  private static void enviarMensagemSOAP(String soapMessage) throws IOException {
	      URL url = new URL("https://routing.pathfindsistema.com.br:443/pathfind_centro_do_aluminio/PedidoService");
	      
	      HttpURLConnection connection = (HttpURLConnection) url.openConnection();

	      connection.setRequestMethod("POST");
	      connection.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
	      connection.setRequestProperty("usuario", "luana");
	      connection.setRequestProperty("senha", "Q1w2e3r4@");
	      connection.setDoOutput(true);
	      connection.setReadTimeout(30000);
	      connection.setConnectTimeout(30000);

	      // Envia a requisi��o
	      try (DataOutputStream wr = new DataOutputStream(connection.getOutputStream())) {
	          wr.writeBytes(soapMessage);
	          wr.flush();
	      }

	      // Captura e processa a resposta
	      String responseStatus = connection.getResponseMessage();
	      System.out.println("Response Status: " + responseStatus);
	      
	      try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"))) {
	          String inputLine;
	          StringBuilder response = new StringBuilder();
	          while ((inputLine = in.readLine()) != null) {
	              response.append(inputLine);
	          }
	          System.out.println("Response SOAP Message:");
	          System.out.println(response.toString());
	      }

	      if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
	          throw new IOException("Retorno durante a conex�o: Code Response: " + connection.getResponseCode());
	      }
	  }
	  
	  
		public static void enviarPathfind(String nunota, int ano,int mes, int dia) throws Exception {
				try {
		          String soapMessage = montarMensagemSOAP(nunota, ano,mes,dia);
		          enviarMensagemSOAP(soapMessage);
		      } catch (IOException erro) {
		          StringWriter sw = new StringWriter();
		          PrintWriter pw = new PrintWriter(sw);
		          erro.printStackTrace(pw);
		          String mensagem = "Erro Exce��o: " + erro.getMessage() + sw.toString();
		          throw new Exception(mensagem);
		      }
		  }
		
		
		private void Find_nota(PersistenceEvent arg0,BigDecimal NUNOTA) throws Exception {
			 EntityFacade dwfEntityFacade = EntityFacadeFactory.getDWFFacade();
			  JdbcWrapper jdbc = dwfEntityFacade.getJdbcWrapper();
			   NativeSql SqlNativo = new NativeSql(jdbc);
			  StringBuilder SQLNOTA = new StringBuilder();
			  
			  SQLNOTA.append(" SELECT TOP 1 V3.NUNOTA AS NOTA_REC "
			  		+ "FROM TGFVAR V1 INNER JOIN TGFVAR V2 ON V2.NUNOTA = V1.NUNOTAORIG  INNER JOIN TGFVAR V3 ON V3.NUNOTA = V2.NUNOTAORIG");
			  SQLNOTA.append(" WHERE V1.NUNOTA = "+NUNOTA);
			  ResultSet R = SqlNativo.executeQuery(SQLNOTA.toString());
			    if(R.next())
			    {
			    	NOta = R.getString("NOTA_REC");
				    System.out.println("O NUMERO DA NOTA DA DEVOLUÇÃO é ESSA"+NOta);
			    }
			    R.close();
			
		}
		
		private void Find_Dt(PersistenceEvent arg0,String NUNOTA) throws Exception {
			 EntityFacade dwfEntityFacade = EntityFacadeFactory.getDWFFacade();
			  JdbcWrapper jdbc = dwfEntityFacade.getJdbcWrapper();
			   NativeSql SqlNativo = new NativeSql(jdbc);
			  StringBuilder SQLNOTA = new StringBuilder();
			  
			  SQLNOTA.append(" SELECT DTPREVENT ");
			  SQLNOTA.append("FROM TGFCAB WHERE NUNOTA = "+NUNOTA);
			  ResultSet R = SqlNativo.executeQuery(SQLNOTA.toString());
			    if(R.next())
			    {
			    	DTPREV = R.getTimestamp("DTPREVENT");
				    System.out.println("O NUMERO DA NOTA DA DEVOLUÇÃO é ESSA"+NOta);
			    }
			    R.close();
			
		}
		
		
		
		
		
		
		
		
		
		

}
