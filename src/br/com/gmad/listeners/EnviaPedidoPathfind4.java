package br.com.gmad.listeners;

import br.com.sankhya.extensions.eventoprogramavel.EventoProgramavelJava;
import br.com.sankhya.jape.event.PersistenceEvent;
import br.com.sankhya.jape.event.TransactionContext;
import br.com.sankhya.jape.vo.DynamicVO;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


public class EnviaPedidoPathfind4 implements EventoProgramavelJava {
	public String Telefone = "";
	private static final Logger LOGGER = Logger.getLogger(EnviaPedidoPathfind4.class.getName());
  public void afterDelete(PersistenceEvent arg0) throws Exception {}
  
  public void afterInsert(PersistenceEvent ctx) throws Exception {
		System.out.println("INICIO SOAP PATHFIND");
		DynamicVO registro = (DynamicVO) ctx.getVo();
		BigDecimal NUNOTA = registro.asBigDecimal("NUNOTA");
		BigDecimal CODPARC = registro.asBigDecimal("CODPARC");
		String AD_ENTREGA = registro.asString("AD_ENTREGA");
		String STATUSNFE = registro.asString("STATUSNFE");
		String TIPMOV = registro.asString("TIPMOV");
		String STATUSNOTA = registro.asString("STATUSNOTA");
		BigDecimal CODTIPOPER = registro.asBigDecimal("CODTIPOPER");
		Timestamp DTPREVENT = registro.asTimestamp("DTPREVENT");
		
		// Validar condi��es
      if (validarCondicoes(AD_ENTREGA, STATUSNFE, TIPMOV, STATUSNOTA, CODTIPOPER, DTPREVENT)) {
      	
      	 // Extraindo ano, m�s e dia
          int ano = getAno(DTPREVENT);
          int mes = getMes(DTPREVENT);
          int dia = getDia(DTPREVENT);
      	
          // Converte NUNOTA e CODPARC para String
	        String NUNOTAStr = (NUNOTA != null) ? NUNOTA.toString() : null;
	        String CODPARCStr = (CODPARC != null) ? CODPARC.toString() : null;
	        
	        PedidoInfo info = getPedidoInfo(NUNOTA);
	        
	        //System.out.println("info.getCep()LOG: " + info.getCep());
	        //System.out.println("info.getNomeVendedorLOG: " + info.getNomeVendedor());
	        
	        List<PedidoItem> items = getItensPedido(NUNOTA);
	        
	        enviarPathfind(CODPARCStr, NUNOTAStr, items, info, ano,mes,dia);
        
          
      } else {
      	System.out.println("N�O VALIDOU STATUSNFE:"+ STATUSNFE);
      	System.out.println("N�O VALIDOU TIPMOV:"+ TIPMOV);
      	System.out.println("N�O VALIDOU STATUSNOTA:"+ STATUSNOTA);
      	System.out.println("N�O VALIDOU DTPREVENT:"+ DTPREVENT);
  
      	System.out.println("N�O VALIDOU:"+ AD_ENTREGA);
      }

	}

	@Override
	public void afterUpdate(PersistenceEvent ctx) throws Exception {
		System.out.println("INICIO SOAP PATHFIND afterUpdate");
		DynamicVO registro = (DynamicVO) ctx.getVo();
		BigDecimal NUNOTA = registro.asBigDecimal("NUNOTA");
		BigDecimal CODPARC = registro.asBigDecimal("CODPARC");
		String AD_ENTREGA = registro.asString("AD_ENTREGA");
		String STATUSNFE = registro.asString("STATUSNFE");
		String TIPMOV = registro.asString("TIPMOV");
		String STATUSNOTA = registro.asString("STATUSNOTA");
		BigDecimal CODTIPOPER = registro.asBigDecimal("CODTIPOPER");
		Timestamp DTPREVENT = registro.asTimestamp("DTPREVENT");
		
		// Validar condi��es
      if (validarCondicoes(AD_ENTREGA, STATUSNFE, TIPMOV, STATUSNOTA, CODTIPOPER, DTPREVENT)) {
      	
      	 // Extraindo ano, m�s e dia
          int ano = getAno(DTPREVENT);
          int mes = getMes(DTPREVENT);
          int dia = getDia(DTPREVENT);
         
          
          // Converte NUNOTA e CODPARC para String
	        String NUNOTAStr = (NUNOTA != null) ? NUNOTA.toString() : null;
	        String CODPARCStr = (CODPARC != null) ? CODPARC.toString() : null;
	        
	        PedidoInfo info = getPedidoInfo(NUNOTA);
	        
	        //System.out.println("info.getCep()LOG: " + info.getCep());
	        //System.out.println("info.getNomeVendedorLOG: " + info.getNomeVendedor());
	        
	        List<PedidoItem> items = getItensPedido(NUNOTA);
	        
	        enviarPathfind(CODPARCStr, NUNOTAStr, items, info, ano,mes,dia);
        
          
      } else {
      	/*System.out.println("N�O VALIDOU STATUSNFE:"+ STATUSNFE);
      	System.out.println("N�O VALIDOU TIPMOV:"+ TIPMOV);
      	System.out.println("N�O VALIDOU STATUSNOTA:"+ STATUSNOTA);
      	System.out.println("N�O VALIDOU DTPREVENT:"+ DTPREVENT);*/
      	
      }
		

	}
	
	public static void enviarPathfind(String codigoCliente, String codigoPedido, List<PedidoItem> items, PedidoInfo info, int ano,int mes, int dia) throws Exception {
      System.out.println("ITENS:" + items.toString());
		try {
          String soapMessage = montarMensagemSOAP(codigoCliente, codigoPedido, items, info, ano,mes,dia);
          //System.out.println("info.getNomeVendedor(): " + info.getCodVendedor());
          enviarMensagemSOAP(soapMessage);
      } catch (IOException erro) {
          StringWriter sw = new StringWriter();
          PrintWriter pw = new PrintWriter(sw);
          erro.printStackTrace(pw);
          String mensagem = "Erro Exce��o: " + erro.getMessage() + sw.toString();
          throw new Exception(mensagem);
      }
  }
	
	private static String montarMensagemSOAP(String codigoCliente, String codigoPedido, List<PedidoItem> items, PedidoInfo info, int ano, int mes,int dia) {
      StringBuilder bodyResponse = new StringBuilder();
      bodyResponse.append("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" ")
                  .append("xmlns:ws=\"http://ws.integracao.pathfind.lantech.com.br/\">")
                  .append("<soapenv:Header/>")
                  .append("<soapenv:Body>")
                  .append("<ws:importarPedidos>")
                  .append("<pedido>")
                  .append("<codigoCliente>").append(codigoCliente).append("</codigoCliente>")
                  .append("<tipoEntrega>O</tipoEntrega>")
                  //.append("<anoPedido>2024</anoPedido>")
                  .append("<anoPedido>").append(ano).append("</anoPedido>")
                  .append("<mesPedido>").append(mes).append("</mesPedido>")
                  .append("<diaPedido>").append(dia).append("</diaPedido>")
                  //.append("<mesPedido>07</mesPedido>")
                  //.append("<diaPedido>19</diaPedido>")
                  .append("<codigoPedido>").append(codigoPedido).append("</codigoPedido>")
                  //.append("<codigoVendedor></codigoVendedor>")
                  .append("<codigoVendedor>").append(info.getCodVendedor()).append("</codigoVendedor>")
                  //.append("<nomeVendedor></nomeVendedor>")
                  .append("<nomeVendedor>").append(info.getNomeVendedor()).append("</nomeVendedor>")
                  .append("<dadosEndereco>")
                  //.append("<endereco>Avenida Rua Ana Nogueira, 200</endereco>")
                  .append("<endereco>").append(info.getEndereco()).append("</endereco>")
                  //.append("<municipio>Manaus</municipio>")
                  .append("<municipio>").append(info.getMunicipio()).append("</municipio>")
                  //.append("<bairro>Educandos</bairro>")
                  .append("<bairro>").append(info.getBairro()).append("</bairro>")
                  //.append("<estado>AM</estado>")
                  .append("<estado>").append(info.getEstado()).append("</estado>")
                  .append("<cep>").append(info.getCep()).append("</cep>")
                  //.append("<cep>69070230</cep>")
                  //.append("<latitude>-3.1421533929996768</latitude>")
                  .append("<latitude>").append(info.getLatitude()).append("</latitude>")
                  .append("<longitude>").append(info.getlongitude()).append("</longitude>")
                  //.append("<longitude>-60.01224111336213</longitude>")
                  .append("</dadosEndereco>");
      for (PedidoItem item : items) {
                      bodyResponse.append("<itens>")
                      			.append("<codigoProduto>").append(item.getCodigoProduto().toPlainString()).append("</codigoProduto>")
                                  .append("<pesoProduto>").append(item.getPesoProduto() != null ? item.getPesoProduto() : 0.0).append("</pesoProduto>")
                                  .append("<volumeProduto>").append(item.getVolumeProduto() != null ? item.getVolumeProduto() : 0.0).append("</volumeProduto>")
                                  .append("<cubagemProduto>").append(item.getCubagemProduto() != null ? item.getCubagemProduto() : 0.0).append("</cubagemProduto>")
                                  .append("<quantidadeProduto>").append(item.getQuantidadeProduto()).append("</quantidadeProduto>")
                                  .append("<filial></filial>")
                                  .append("</itens>");
      }
      bodyResponse.append("<origem>SANKHYA</origem>")
                  //.append("<observacao></observacao>")
                  .append("<observacao>").append(info.getObservacao()).append("</observacao>")
                  .append("<valor>").append(info.getValor()).append("</valor>")
                  .append("<codigoBox></codigoBox>")
                  //.append("<filial></filial>")
                  .append("<filial>").append(info.getFilial()).append("</filial>")
                  .append("</pedido>")
                  .append("</ws:importarPedidos>")
                  .append("</soapenv:Body>")
                  .append("</soapenv:Envelope>");
      
      //System.out.println("bodyResponse_LOG:" + bodyResponse.toString());

      return bodyResponse.toString();
  }

  private static void enviarMensagemSOAP(String soapMessage) throws IOException {
      //URL url = new URL("https://pathfindsistema.com.br:443/pathfind_centro_do_aluminio_sup/PedidoService?wsdl");
      URL url = new URL("https://pathfindsistema.com.br:443/pathfind_centro_do_aluminio/PedidoService");
      
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();

      // Configura��es da conex�o
      connection.setRequestMethod("POST");
      connection.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
      //connection.setRequestProperty("usuario", "integracao_centro");
      //connection.setRequestProperty("senha", "Q1w2e3r4$");
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

  public static class PedidoItem {
      private BigDecimal codigoProduto;
      private Double pesoProduto;
      private Double volumeProduto;
      private Double cubagemProduto;
      private Double quantidadeProduto;

      public PedidoItem(BigDecimal codigoProduto, Double pesoProduto, Double volumeProduto, Double cubagemProduto, Double quantidadeProduto) {
          this.codigoProduto = codigoProduto;
          this.pesoProduto = pesoProduto;
          this.volumeProduto = volumeProduto;
          this.cubagemProduto = cubagemProduto;
          this.quantidadeProduto = quantidadeProduto;
      }

      public BigDecimal getCodigoProduto() {
          return codigoProduto;
      }

      public Double getPesoProduto() {
          return pesoProduto;
      }

      public Double getVolumeProduto() {
          return volumeProduto;
      }

      public Double getCubagemProduto() {
          return cubagemProduto;
      }

      public Double getQuantidadeProduto() {
          return quantidadeProduto;
      }
  }
  
  private PedidoInfo getPedidoInfo(BigDecimal NUNOTA) throws SQLException {

      String sql = "SELECT CI.NOMECID as municipio, B.NOMEBAI as bairro, UU.UF as estado, CAB.AD_CEP as cep, " +
              "ISNULL(CAB.AD_LATITUDEENTREGA,'') AS LATITUDE, " +
              "ISNULL(CAB.AD_LONGITUDENETREGA,'') AS LONGITUDE, " +
              "RTRIM(LTRIM(V.APELIDO)) AS APELIDO, " +
              "CONVERT(VARCHAR,V.CODVEND) AS CODVEND, " +
              "ISNULL(CAB.OBSERVACAO,'') AS OBS, " +
              "CONVERT(VARCHAR,CASE WHEN CAB.CODEMP=7 THEN 'C' " +
              "                     WHEN CAB.CODEMP=9 THEN 'I' " +
              "                     WHEN CAB.CODEMP=10 THEN 'C' " +
              "                     WHEN CAB.CODEMP=11 THEN 'C' " +
              "                     ELSE 'M' END) AS CODEMP, " +
              "VLRNOTA as valor, RTRIM(LTRIM(E.TIPO)) + ' ' + RTRIM(LTRIM(E.NOMEEND)) + ' N:' + CAB.AD_NUMEROEND AS endereco " +
              "FROM TGFCAB CAB " +
              "INNER JOIN TGFPAR P ON P.CODPARC = CAB.CODPARC " +
              "LEFT JOIN TGFCPL C ON C.CODPARC = P.CODPARC " +
              "LEFT JOIN TSIEND E ON E.CODEND = CAB.AD_ENDERECO " +
              "LEFT JOIN TSIBAI B ON B.CODBAI = CAB.AD_CBAIRRO " +
              "LEFT JOIN TSICID CI ON CI.CODCID = CAB.AD_CID " +
              "LEFT JOIN TSIUFS UU ON UU.CODUF = CI.UF " +
              "LEFT JOIN TGFVEN V ON V.CODVEND = CAB.CODVEND " +
              "WHERE NUNOTA = ?";

      String jdbcUrl = "jdbc:sqlserver://10.1.100.5:1433;databaseName=sankhya_wms;user=sankhya;password=tecsis;";

      LOGGER.info("Iniciando a busca por informa��es do pedido para NUNOTA: " + NUNOTA);

      try (Connection conn = DriverManager.getConnection(jdbcUrl);
           PreparedStatement stmt = conn.prepareStatement(sql)) {

          LOGGER.info("Conex�o com o banco de dados estabelecida com sucesso.");

          stmt.setBigDecimal(1, NUNOTA);
          LOGGER.info("Consulta preparada com SQL: " + sql);

          try (ResultSet rs = stmt.executeQuery()) {
              LOGGER.info("Executando a consulta para NUNOTA: " + NUNOTA);

              if (rs.next()) {
                  LOGGER.info("Pedido encontrado para NUNOTA: " + NUNOTA);

                  String municipio = rs.getString("municipio");
                  String bairro = rs.getString("bairro");
                  String estado = rs.getString("estado");
                  String cep = rs.getString("cep");
                  BigDecimal valor = rs.getBigDecimal("valor");
                  String endereco = rs.getString("endereco");
                  BigDecimal codVendedor = rs.getBigDecimal("CODVEND");
                  String apelido = rs.getString("APELIDO");
                  String filial = rs.getString("CODEMP");
                  String obs = rs.getString("OBS");
                  String latitude = rs.getString("LATITUDE");
                  String longitude = rs.getString("LONGITUDE");

                  LOGGER.info(String.format("Informa��es do pedido:\nMunicipio: %s, Bairro: %s, Estado: %s, CEP: %s, Valor: %s, Endere�o: %s, CodVendedor: %s, Apelido: %s, Filial: %s, Obs: %s, Latitude: %s, Longitude: %s",
                          municipio, bairro, estado, cep, valor, endereco, codVendedor, apelido, filial, obs, latitude, longitude));

                  return new PedidoInfo(municipio, bairro, estado, cep, valor, endereco, codVendedor.toString(), apelido, filial, obs, latitude, longitude);
              } else {
                  LOGGER.warning("Nenhum pedido encontrado com NUNOTA: " + NUNOTA);
                  throw new SQLException("Nenhum pedido encontrado com NUNOTA: " + NUNOTA);
              }
          }
      } catch (SQLException e) {
          LOGGER.log(Level.SEVERE, "Erro ao buscar informa��es do pedido para NUNOTA: " + NUNOTA, e);
          throw e;
      }
  }
  
  public static List<PedidoItem> getItensPedido(BigDecimal nUNOTA) throws Exception {
      List<PedidoItem> itens = new ArrayList<>();

      String jdbcUrl = "jdbc:sqlserver://10.1.100.5:1433;databaseName=sankhya_wms;user=sankhya;password=tecsis;";

      String sql = "SELECT CODPROD, QTDNEG, C.PESO, C.VOLUME FROM TGFCAB C INNER JOIN TGFITE I ON C.NUNOTA = I.NUNOTA WHERE C.NUNOTA = ?";

      LOGGER.info("Iniciando busca por itens do pedido para NUNOTA: " + nUNOTA);

      try (Connection connection = DriverManager.getConnection(jdbcUrl);
           PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

          LOGGER.info("Conex�o com o banco de dados estabelecida com sucesso.");

          preparedStatement.setBigDecimal(1, nUNOTA);
          LOGGER.info("Consulta preparada com SQL: " + sql);

          try (ResultSet resultSet = preparedStatement.executeQuery()) {
              LOGGER.info("Executando a consulta para itens do pedido com NUNOTA: " + nUNOTA);

              while (resultSet.next()) {
                  LOGGER.fine("Processando item do pedido.");

                  String codigoProdutoStr = resultSet.getString("CODPROD");
                  String quantidadeProdutoStr = resultSet.getString("QTDNEG");
                  String pesoStr = resultSet.getString("PESO");
                  String volumeStr = resultSet.getString("VOLUME");

                  // Default values if null
                  codigoProdutoStr = (codigoProdutoStr != null) ? codigoProdutoStr.trim() : "0";
                  quantidadeProdutoStr = (quantidadeProdutoStr != null) ? quantidadeProdutoStr.trim() : "0";
                  pesoStr = (pesoStr != null) ? pesoStr.trim() : "0";
                  volumeStr = (volumeStr != null) ? volumeStr.trim() : "0";

                  BigDecimal codigoProduto;
                  BigDecimal quantidadeProduto;
                  BigDecimal peso;
                  BigDecimal volume;

                  try {
                      codigoProduto = new BigDecimal(codigoProdutoStr);
                      quantidadeProduto = new BigDecimal(quantidadeProdutoStr);
                      peso = new BigDecimal(pesoStr);
                      volume = new BigDecimal(volumeStr);
                  } catch (NumberFormatException e) {
                      LOGGER.log(Level.SEVERE, "Erro ao converter valores para BigDecimal", e);
                      throw new IllegalArgumentException("Erro ao converter valores para BigDecimal.", e);
                  }

                  if (codigoProduto.compareTo(BigDecimal.ZERO) <= 0) {
                      LOGGER.warning("C�digo do produto inv�lido: " + codigoProduto);
                      throw new IllegalArgumentException("C�digo do produto deve ser maior que zero.");
                  }

                  if (quantidadeProduto.compareTo(BigDecimal.ZERO) <= 0) {
                      LOGGER.warning("Quantidade do produto inv�lida: " + quantidadeProduto);
                      throw new IllegalArgumentException("Quantidade do produto deve ser maior que zero.");
                  }

                  PedidoItem item = new PedidoItem(
                          codigoProduto,
                          peso.doubleValue(),
                          volume.doubleValue(),
                          1.0, // Cubagem produto fixo
                          quantidadeProduto.doubleValue()
                  );

                  LOGGER.fine(String.format("Item processado: C�digoProduto=%s, Quantidade=%s, Peso=%s, Volume=%s",
                          codigoProduto, quantidadeProduto, peso, volume));

                  itens.add(item);
              }

              LOGGER.info("Total de itens processados: " + itens.size());
          }
      } catch (SQLException e) {
          LOGGER.log(Level.SEVERE, "Erro ao buscar itens do pedido para NUNOTA: " + nUNOTA, e);
          throw e;
      }

      return itens;
  }

  
  private PedidoInfo getPedidoInfo_backup(BigDecimal NUNOTA) throws SQLException {
      
      String sql = "SELECT CI.NOMECID as municipio, B.NOMEBAI as bairro, UU.UF as estado, CAB.AD_CEP as cep, " +
              "ISNULL(CAB.AD_LATITUDEENTREGA,'') AS LATITUDE, " +
              "ISNULL(CAB.AD_LONGITUDENETREGA,'') AS LONGITUDE, " +
              "RTRIM(LTRIM(V.APELIDO)) AS APELIDO, " +
              "CONVERT(VARCHAR,V.CODVEND) AS CODVEND, " +
              "ISNULL(CAB.OBSERVACAO,'') AS OBS, " +
              "CONVERT(VARCHAR,CASE WHEN CAB.CODEMP=7 THEN 'C' " +
              "                     WHEN CAB.CODEMP=9 THEN 'I' " +
              "                     WHEN CAB.CODEMP=10 THEN 'C' " +
              "                     WHEN CAB.CODEMP=11 THEN 'C' " +
              "                     ELSE 'M' END) AS CODEMP, " +
              "VLRNOTA as valor, RTRIM(LTRIM(E.TIPO)) + ' ' + RTRIM(LTRIM(E.NOMEEND)) + ' N:' + CAB.AD_NUMEROEND AS endereco " +
              "FROM TGFCAB CAB " +
              "INNER JOIN TGFPAR P ON P.CODPARC = CAB.CODPARC " +
              "LEFT JOIN TGFCPL C ON C.CODPARC = P.CODPARC " +
              "LEFT JOIN TSIEND E ON E.CODEND = CAB.AD_ENDERECO " +
              "LEFT JOIN TSIBAI B ON B.CODBAI = CAB.AD_CBAIRRO " +
              "LEFT JOIN TSICID CI ON CI.CODCID = CAB.AD_CID " +
              "LEFT JOIN TSIUFS UU ON UU.CODUF = CI.UF " +
              "LEFT JOIN TGFVEN V ON V.CODVEND = CAB.CODVEND " +
              "WHERE NUNOTA = ?";

      //String jdbcUrl = "jdbc:sqlserver://10.1.100.5:1433;databaseName=sankhya_prod;user=sankhya;password=tecsis;";
      String jdbcUrl = "jdbc:sqlserver://10.1.100.5:1433;databaseName=sankhya_wms;user=sankhya;password=tecsis;";
      //String jdbcUrl = "jdbc:sqlserver://10.1.100.5:8280;databaseName=sankhya_prod;user=sankhya;password=tecsis;";
      try (Connection conn = DriverManager.getConnection(jdbcUrl);
           PreparedStatement stmt = conn.prepareStatement(sql)) {

          stmt.setBigDecimal(1, NUNOTA);
          try (ResultSet rs = stmt.executeQuery()) {
              if (rs.next()) {
                  String municipio = rs.getString("municipio");
                  String bairro = rs.getString("bairro");
                  String estado = rs.getString("estado");
                  String cep = rs.getString("cep");
                  BigDecimal valor = rs.getBigDecimal("valor");
                  String endereco = rs.getString("endereco");
                  
                  BigDecimal codVendedor = rs.getBigDecimal("CODVEND");
                  String apelido = rs.getString("APELIDO");
                  String filial = rs.getString("CODEMP");
                  String obs = rs.getString("OBS");
                  String latitude = rs.getString("LATITUDE");
                  String longitude = rs.getString("LONGITUDE");
                  
                  System.out.println("getPedidoInfo_endereco:" + endereco);

                  return new PedidoInfo(municipio, bairro, estado, cep, valor, endereco, codVendedor.toString(), apelido, filial.toString(), obs, latitude, longitude );
              } else {
                  throw new SQLException("Nenhum pedido encontrado com NUNOTA: " + NUNOTA);
              }
          }
      }
  }
  
  public static List<PedidoItem> getItensPedido_backup(BigDecimal nUNOTA) throws Exception {
      List<PedidoItem> itens = new ArrayList<>();
      Connection connection = null;
      PreparedStatement preparedStatement = null;
      ResultSet resultSet = null;

      try {
          //String jdbcUrl = "jdbc:sqlserver://10.1.100.9:1433;databaseName=sankhya_prod;user=sankhya;password=tecsis;";
          String jdbcUrl = "jdbc:sqlserver://10.1.100.5:1433;databaseName=sankhya_wms;user=sankhya;password=tecsis;";
      	//String jdbcUrl = "jdbc:sqlserver://10.1.100.5:8280;databaseName=sankhya_prod;user=sankhya;password=tecsis;";
          
          connection = DriverManager.getConnection(jdbcUrl);
          connection.setAutoCommit(false); // Start transaction

          String sql = "SELECT CODPROD, QTDNEG, C.PESO, C.VOLUME FROM TGFCAB C INNER JOIN TGFITE I ON C.NUNOTA = I.NUNOTA WHERE C.NUNOTA = ?";
          preparedStatement = connection.prepareStatement(sql);
          preparedStatement.setBigDecimal(1, nUNOTA);
          resultSet = preparedStatement.executeQuery();

          while (resultSet.next()) {
              String codigoProdutoStr = resultSet.getString("CODPROD");
              String quantidadeProdutoStr = resultSet.getString("QTDNEG");
              String pesoStr = resultSet.getString("PESO");
              String volumeStr = resultSet.getString("VOLUME");

              // Default values if null
              codigoProdutoStr = (codigoProdutoStr != null) ? codigoProdutoStr.trim() : "0";
              quantidadeProdutoStr = (quantidadeProdutoStr != null) ? quantidadeProdutoStr.trim() : "0";
              pesoStr = (pesoStr != null) ? pesoStr.trim() : "0";
              volumeStr = (volumeStr != null) ? volumeStr.trim() : "0";

              BigDecimal codigoProduto;
              BigDecimal quantidadeProduto;
              BigDecimal peso;
              BigDecimal volume;
              
              try {
                  codigoProduto = new BigDecimal(codigoProdutoStr);
                  quantidadeProduto = new BigDecimal(quantidadeProdutoStr);
                  peso = new BigDecimal(pesoStr);
                  volume = new BigDecimal(volumeStr);
              } catch (NumberFormatException e) {
                  throw new IllegalArgumentException("Erro ao converter valores para BigDecimal.", e);
              }

              if (codigoProduto.compareTo(BigDecimal.ZERO) <= 0) {
                  throw new IllegalArgumentException("C�digo do produto deve ser maior que zero.");
              }
              
              if (quantidadeProduto.compareTo(BigDecimal.ZERO) <= 0) {
                  throw new IllegalArgumentException("Quantidade do produto deve ser maior que zero.");
              }

              PedidoItem item = new PedidoItem(
                  codigoProduto,
                  peso.doubleValue(), // Peso produto fixo
                  volume.doubleValue(),  // Volume produto fixo
                  1.0,  // Cubagem produto fixo
                  quantidadeProduto.doubleValue()
              );
              itens.add(item);
          }
          connection.commit(); // Commit transaction
      } catch (Exception e) {
          if (connection != null) {
              connection.rollback(); // Rollback transaction on error
          }
          throw e;
      } finally {
          if (resultSet != null) resultSet.close();
          if (preparedStatement != null) preparedStatement.close();
          if (connection != null) connection.close();
      }
      return itens;
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

  private boolean validarCondicoes(String AD_ENTREGA, String STATUSNFE, String TIPMOV, String STATUSNOTA, BigDecimal CODTIPOPER, Timestamp DTPREVENT) {
      // Lista de valores permitidos para AD_ENTREGA
      List<String> valoresPermitidosAD_ENTREGA = Arrays.asList("D", "F", "R", "S", "A", "P");
      
      // Lista de valores n�o permitidos para CODTIPOPER
      List<BigDecimal> valoresNaoPermitidosCODTIPOPER = Arrays.asList(BigDecimal.valueOf(27), BigDecimal.valueOf(199));
      
      // Verificar se AD_ENTREGA est� na lista de valores permitidos
      if (!valoresPermitidosAD_ENTREGA.contains(AD_ENTREGA)) {
      	System.out.println("lOGAD_ENTREGA: " + AD_ENTREGA);
          return false;
      }
      
      // Verificar se CODTIPOPER est� na lista de valores n�o permitidos
      if (valoresNaoPermitidosCODTIPOPER.contains(CODTIPOPER)) {
      	System.out.println("lOGCODTIPOPER: " + CODTIPOPER);
          return false;
      }
      
      // Verificar STATUSNOTA, STATUSNFE, TIPMOV e DTPREVENT
      if (!"L".equals(STATUSNOTA) /*|| !"A".equals(STATUSNFE)*/ || !"V".equals(TIPMOV) || DTPREVENT == null) {
          /*System.out.println("Verificar STATUSNOTA, : "+ STATUSNOTA);
          System.out.println("Verificar STATUSNFE: "+ STATUSNFE);
          System.out.println("Verificar TIPMOV: "+ TIPMOV);*/
      	return false;
      }
      System.out.println("VARIFICARLOGAD_ENTREGA:" + AD_ENTREGA);
      System.out.println("VARIFICARLOGSTATUSNFE:" + STATUSNFE);
      System.out.println("VARIFICARLOGTIPMOV:" + TIPMOV);
      System.out.println("VARIFICARLOGCODTIPOPER:" + CODTIPOPER);
      System.out.println("VARIFICARLOGAD_ENTREGA:" + AD_ENTREGA);
      System.out.println("VARIFICARLOGDTPREVENT:" + DTPREVENT);
      return true;
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
	 
}

class PedidoInfo {
  private String municipio;
  private String bairro;
  private String estado;
  private String cep;
  private BigDecimal valor;
  private String endereco;
	private String codVendedor;
	private String apelido;
	private String filial;
	private String obs;
	private String latitude;
	private String longitude;
  

 
  public PedidoInfo(String municipio, String bairro, String estado, String cep, BigDecimal valor, String endereco, String string, String apelido, String filial, String obs, String latitude, String longitude) {
  	this.municipio = municipio;
      this.bairro = bairro;
      this.estado = estado;
      this.cep = cep;
      this.valor = valor;
      this.endereco = endereco;
      this.codVendedor = string; 
      this.apelido = apelido;
      this.filial = filial;
      this.obs = obs;
      this.latitude = latitude;
      this.longitude = longitude;
	}

	public String getObservacao() {
		// TODO Auto-generated method stub
		return obs;
	}

	public Object getlongitude() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getNomeVendedor() {
		// TODO Auto-generated method stub
		return apelido;
	}

	public String getCodVendedor() {
		// TODO Auto-generated method stub
		return codVendedor;
	}

	public String getFilial() {
		
		return filial;
	}
	
	public String getLatitude() {
		
		return latitude;
	}
	
	public String getLongitude() {
		
		return longitude;
	}

	public String getMunicipio() {
      return municipio;
  }

  public void setMunicipio(String municipio) {
      this.municipio = municipio;
  }

  public String getBairro() {
      return bairro;
  }

  public void setBairro(String bairro) {
      this.bairro = bairro;
  }

  public String getEstado() {
      return estado;
  }

  public void setEstado(String estado) {
      this.estado = estado;
  }

  public String getCep() {
      return cep;
  }

  public void setCep(String cep) {
      this.cep = cep;
  }

  public BigDecimal getValor() {
      return valor;
  }

  public void setValor(BigDecimal valor) {
      this.valor = valor;
  }

  public String getEndereco() {
      return endereco;
  }

  public void setEndereco(String endereco) {
      this.endereco = endereco;
  }

}

