package src.gmad;



import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.ResultSet;

import org.cuckoo.core.ScheduledAction;
import org.cuckoo.core.ScheduledActionContext;

import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.sql.NativeSql;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.modelcore.auth.AuthenticationInfo;
import br.com.sankhya.ws.ServiceContext;



public class envio_pa implements ScheduledAction {
	
	public String cod = "";
	public String marca = "";
	public String descrprod = "";
	public String codvol = "";
	public String pesobruto = "";
	public String pesoliq = "";
	AuthenticationInfo authInfo;
	  
	  ServiceContext sctx;
	  public void onTime(ScheduledActionContext ctx) {

		    try {
		        JdbcWrapper jdbc = JapeFactory.getEntityFacade().getJdbcWrapper();
		        JapeSession.SessionHandle hnd = JapeSession.open();
		        NativeSql sql = new NativeSql(jdbc);

		        String notas = "SELECT CODPROD,MARCA,DESCRPROD,CODVOL,PESOBRUTO,PESOLIQ FROM TGFPRO WHERE AD_ISSENDINGPATHFIND = 'N'";
		        ResultSet rs = sql.executeQuery(notas);
		        while (rs.next()) {
		            try {
		              cod = rs.getString("CODPROD");
		              marca = rs.getString("MARCA");
		              descrprod = rs.getString("DESCRPROD");
		              codvol = rs.getString("CODVOL");
		              pesobruto = rs.getString("PESOBRUTO");
		              pesoliq = rs.getString("PESOLIQ");
		              System.out.println("Entrou no while = " + cod +""+descrprod);
		              enviarPathfind(cod,marca,descrprod,codvol,pesobruto,pesoliq);
		              atualizar(cod);
		            } catch (Exception c) {
		              c.printStackTrace();
		              System.out.println("Erro no Envio para o PF= " + rs.getBigDecimal("CODPROD") + " " + c.getMessage());
		            } 
		          } 
		        } catch (Exception e) {
		          e.printStackTrace();
		          System.out.println("Erro no Envio de Produto para o PathFinder =  " + e.getMessage());
		        }  

		      }
	  
	  public void atualizar (String Nota)
	  {
		  JdbcWrapper jdbc = JapeFactory.getEntityFacade().getJdbcWrapper();
	        JapeSession.SessionHandle hnd = JapeSession.open();
	        NativeSql sql2 = new NativeSql(jdbc);
	        try {
	        sql2.setNamedParameter("P_NOTA", Nota);
	        sql2.executeUpdate("UPDATE TGFPRO SET AD_ISSENDINGPATHFIND = 'S' WHERE CODPROD = :P_NOTA");
	        
	  } catch (Exception a )
	  {
		  a.printStackTrace();
		  System.out.println("Deu erro na atualização da TGFPRO no Codigo"+Nota);
	  }
	  finally {
		  JdbcWrapper.closeSession(jdbc);
			JapeSession.close(hnd);
	  }
	  } 
	  

	  
	  
	  public static void enviarPathfind(String cODPRODstr, String mARCA, String dESCRPROD, String cODVOL, String pESOBRUTOstr, String pESOLIQstr) throws Exception {
		    try {
		      URL url = new URL("https://pathfindsistema.com.br:443/pathfind_centro_do_aluminio/ProdutoService");
		      HttpURLConnection connection = (HttpURLConnection)url.openConnection();
		      connection.setRequestMethod("POST");
		      connection.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
		      connection.setRequestProperty("usuario", "luana");
		      connection.setRequestProperty("senha", "Q1w2e3r4@");
		      connection.setDoOutput(true);
		      connection.setReadTimeout(30000);
		      connection.setConnectTimeout(30000);
		      StringBuilder bodyResponse = new StringBuilder();
		      bodyResponse.append("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" ")
		        .append("xmlns:ws=\"http://ws.integracao.pathfind.lantech.com.br/\">")
		        .append("<soapenv:Header/>")
		        .append("<soapenv:Body>")
		        .append("<ws:importarProdutos>")
		        .append("<produto>")
		        .append("<codigo>").append(cODPRODstr).append("</codigo>")
		        .append("<descricao>").append(dESCRPROD).append("</descricao>")
		        .append("<tipoEmbalagem>UNIDADE</tipoEmbalagem>")
		        .append("<quantidadeUnidades>01</quantidadeUnidades>")
		        .append("<pesoCaixaCompleta>0</pesoCaixaCompleta>")
		        
		        .append("<pesoUnitario>").append(pESOLIQstr).append("</pesoUnitario>")
		        .append("<cubagemCaixa>0</cubagemCaixa>")
		        .append("<cubagemUnidade>0</cubagemUnidade>")
		        .append("<status>A</status>")
		        .append("<tempoDescarregamento>0</tempoDescarregamento>")
		        .append("</produto>")
		        .append("</ws:importarProdutos>")
		        .append("</soapenv:Body>")
		        .append("</soapenv:Envelope>");
		      DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
		      wr.writeBytes(bodyResponse.toString());
		      wr.flush();
		      wr.close();
		      String responseStatus = connection.getResponseMessage();
		      System.out.println("Response Status: " + responseStatus);
		      BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));
		      StringBuffer response = new StringBuffer();
		      String inputLine;
		      while ((inputLine = in.readLine()) != null)
		        response.append(inputLine); 
		      in.close();
		      System.out.println("Response SOAP Message:");
		      System.out.println(response.toString());
		      if (connection.getResponseCode() != 200) {
		        System.out.println("estou excessao:" + connection.getResponseCode());
		        throw new IOException("Retorno durante a conexCode Response: " + connection.getResponseCode());
		      } 
		    } catch (IOException erro) {
		      System.out.println("logerro:" + erro.toString());
		      StringWriter sw = new StringWriter();
		      PrintWriter pw = new PrintWriter(sw);
		      StringBuffer mensagem = new StringBuffer();
		      erro.printStackTrace(pw);
		      mensagem.append("Erro Exce" + erro.getMessage() + sw.toString());
		      throw new Exception(mensagem.toString());
		    } 
		  }
	  
	  


	  
	  
	  
	  
	  
	  
	  
	  
	  
	  
	  }


