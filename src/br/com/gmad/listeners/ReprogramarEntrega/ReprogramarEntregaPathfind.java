package br.com.gmad.listeners.ReprogramarEntrega;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;


import br.com.sankhya.extensions.actionbutton.AcaoRotinaJava;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.extensions.actionbutton.Registro;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;
import br.com.sankhya.modelcore.util.DynamicEntityNames;

public class ReprogramarEntregaPathfind implements AcaoRotinaJava {
	
	private static final String BASE_URL = "https://app.polichat.com.br/api/v1/customers/12242/whatsapp/send_text/channels/63614/uid/";
    private static final String USERS_PART = "/users/19451";
    private static final String TOKEN = "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiIsImp0aSI6IjYzODQzNWI2ZDczMjVkYTRiYjU4OGY4YTlhMWJiNjZmZTg3YjdjZDM4ZGI2ZTg5YmU1ZjUxMDkyZWY5YWE0ZmEwZDRlMWFlMjQ0ZmZhMzcxIn0.eyJhdWQiOiIxMTAiLCJqdGkiOiI2Mzg0MzViNmQ3MzI1ZGE0YmI1ODhmOGE5YTFiYjY2ZmU4N2I3Y2QzOGRiNmU4OWJlNWY1MTA5MmVmOWFhNGZhMGQ0ZTFhZTI0NGZmYTM3MSIsImlhdCI6MTcyNjU4MTUwMCwibmJmIjoxNzI2NTgxNTAwLCJleHAiOjE3NTgxMTc1MDAsInN1YiI6IjE5NDUxIiwic2NvcGVzIjpbXX0.y2yhbCT80XD3xvpwrD55_GTUto5jjjbxSz5EkXT8VNUpAWLGVaNyF52PdsdS-8oHuAAlTyu0eIEmQO8C9qYxOkwb3lzGG964FQcOgcWbXQJc2IErVAGfeBha2XkSg1z6BLuRnnS7w0K2G5BjJHdM_WX3Wx7AP6HjoGVykLbkGzB6vSsiwiI3S4bH1aLRgfzMvKjOxja6A0yvkMT1WarlL3HSvzzhRQVtxSJbDbaU_R1HhShkPWXVybll-ogQYvjYLFv4jN3zEw3bhv9SI7FzZHwdxNb-siNWurXcyIoa3GdIsuVw7W_hCEQIkqJtPC378_rmzjH9_Yora7s6HsgIWQnEJHydub0elqFI6ATVSpdVTH-uZznunWhNLXlKPA4h2ocAR8Y24DJ9j_taSUrBKQJfXPgsuxzPP4nMxAbqA3ZhOFyy_qU6kxrPtm_OhbpnjskG3hfgz3agQ-jFesPQ_Oxl8VG1pKskyNSVsfk2Mahr4xIEzNlsnHTmaSBcwpd6rIiQ91SgiTBuEXMfngrf2W5x7ya5wftKSxIjHENiZF7U_HfCJ4Jt_P_KekbWQA7NihnrVEjxOoR2_H6y2WJxfkVe9hO5yNOz7nWmfzi8pMsL9vtVJlFG0AphWlvo4ts_e74uTJ8gppXQQC5Fzi0X7GJl2rTaOdKUEubCJS2H9zM"; // Substitua pelo token fixo


	
	private String msgErro = "";
	String mensagem = "";

	@Override
	public void doAction(ContextoAcao ctx) throws Exception {
		
			System.out.println("INICIO REPROGRAMACAO PATHFIND");
			
			Registro[] line = ctx.getLinhas();
			
			enviar(line, ctx);
			
			ctx.setMensagemRetorno("REPROGRAMADO COM SUCESSO !!! ");
			
	}

	private void enviar(Registro[] line, ContextoAcao ctx) throws Exception {
	    
		// Obtém a data de entrega
		Timestamp dtEntrega = (Timestamp) ctx.getParam("DTENTREGA");
		System.out.println("dtEntrega: " + dtEntrega);

		// Obtém a data atual sem a parte de horas/minutos/segundos
		LocalDate dataAtual = LocalDate.now();
		LocalDate dataEntrega = dtEntrega.toLocalDateTime().toLocalDate();

		// Verifica se a data de entrega é anterior à data atual
		if (dataEntrega.isBefore(dataAtual)) {
		    throw new IllegalArgumentException("A data de entrega não pode ser anterior à data atual.");
		}

	    // Converte o parâmetro "nunota" para BigDecimal de forma segura
	    Object nunotaObj = ctx.getParam("NUNOTA");
	    BigDecimal nunota;

	    if (nunotaObj instanceof Integer) {
	        nunota = new BigDecimal((Integer) nunotaObj);
	    } else if (nunotaObj instanceof BigDecimal) {
	        nunota = (BigDecimal) nunotaObj;
	    } else {
	        throw new IllegalArgumentException("Tipo de dado inesperado para 'nunota'");
	    }
	    
	    String motivo = (String) ctx.getParam("MOTIVO");
	    System.out.println("motivo:" + motivo);

	 // Valida 'nunota' com mensagem detalhada em caso de falha
	    if (!validaNunota(nunota, ctx)) {
	        return; // Mensagem de erro já exibida em `validaNunota`
	    }

	    // Extraindo ano, mês e dia
	    int ano = getAno(dtEntrega);
	    int mes = getMes(dtEntrega);
	    int dia = getDia(dtEntrega);

	    PedidoInfo info = getPedidoInfo(nunota);
	    if (info == null) {
	        ctx.setMensagemRetorno("Informações do pedido não encontradas.");
	        return;
	    }

	    List<PedidoItem> items = getItensPedido(nunota);
	    if (items == null || items.isEmpty()) {
	        ctx.setMensagemRetorno("Itens do pedido não encontrados.");
	        return;
	    }

	        // Converte NUNOTA e CODPARC para String
	        String NUNOTAStr = (nunota != null) ? nunota.toString() : null;
	        String CODPARCStr = info.getCodParc();

	        enviarPathfind(CODPARCStr, NUNOTAStr, items, info, ano, mes, dia);

	        System.out.println("Nova data da entrega: " + dtEntrega);
	        
	        BigDecimal nunotaorig = getNunotaOrig(nunota);

	        atualizarReprogramacaoNotaVenda(nunota, dtEntrega, motivo);
	        
	        inserirRegistroNotaVenda(nunota,dtEntrega);
	        
	        atualizarReprogramacaoNotaPedido(nunotaorig, dtEntrega, motivo);
	        
	        inserirRegistroNotaPedido(nunotaorig,dtEntrega);
	    
	    
		}

	
		private BigDecimal getNunotaOrig(BigDecimal NUNOTA) throws SQLException {
        
        String sql = "SELECT NUNOTAORIG FROM TGFVAR WHERE NUNOTA = ?";

        //String jdbcUrl = "jdbc:sqlserver://10.1.100.5:1433;databaseName=sankhya_prod;user=sankhya;password=tecsis;";
        //String jdbcUrl = "jdbc:sqlserver://10.1.100.15:1433;databaseName=sankhya_prod;user=sankhya;password=tecsis;";
        String jdbcUrl = "jdbc:sqlserver://10.1.100.15:1433;databaseName=sankhya_wms;user=sankhya;password=tecsis;";
        try (Connection conn = DriverManager.getConnection(jdbcUrl);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setBigDecimal(1, NUNOTA);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    
                    
                    BigDecimal nunotaorig = rs.getBigDecimal("NUNOTAORIG");
                   
 
                    return nunotaorig;
                } else {
                	
                    throw new SQLException("Nenhum pedido encontrado com NUNOTA: " + NUNOTA);
                }
            } catch (Exception e) {
            	System.out.println("ReprogramarEntrega :" +e.fillInStackTrace());
    			e.printStackTrace();
			}
        }
		return null;
    }

	public static void inserirRegistroNotaVenda(BigDecimal nunota, Timestamp dtEntrega) {
	    //String jdbcUrl = "jdbc:sqlserver://10.1.100.15:1433;databaseName=sankhya_prod;user=sankhya;password=tecsis;";
	    String jdbcUrl = "jdbc:sqlserver://10.1.100.15:1433;databaseName=sankhya_wms;user=sankhya;password=tecsis;";
	    String sql = "INSERT INTO CA_SEND_PF (NUNOTA, DTENV, MSG, MSGRET, TIPO) VALUES (?, ?, ?, ?, ?)";

	    try (Connection conn = DriverManager.getConnection(jdbcUrl);
	         PreparedStatement stmt = conn.prepareStatement(sql)) {

	        stmt.setBigDecimal(1, nunota);
	        stmt.setTimestamp(2, dtEntrega);
	        stmt.setString(3, "N");
	        stmt.setString(4, " ");
	        stmt.setString(5, "imp");

	        int linhasAfetadas = stmt.executeUpdate();
	        System.out.println(linhasAfetadas > 0 ? "Registro inserido com sucesso." : "Falha ao inserir o registro.");

	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}
	
	public static void inserirRegistroNotaPedido(BigDecimal nunota, Timestamp dtEntrega) {
	    //String jdbcUrl = "jdbc:sqlserver://10.1.100.15:1433;databaseName=sankhya_prod;user=sankhya;password=tecsis;";
	    String jdbcUrl = "jdbc:sqlserver://10.1.100.15:1433;databaseName=sankhya_wms;user=sankhya;password=tecsis;";
	    String sql = "INSERT INTO CA_SEND_PF (NUNOTA, DTENV, MSG, MSGRET, TIPO) VALUES (?, ?, ?, ?, ?)";

	    try (Connection conn = DriverManager.getConnection(jdbcUrl);
	         PreparedStatement stmt = conn.prepareStatement(sql)) {

	        stmt.setBigDecimal(1, nunota);
	        stmt.setTimestamp(2, dtEntrega);
	        stmt.setString(3, "N");
	        stmt.setString(4, " ");
	        stmt.setString(5, "imp");

	        int linhasAfetadas = stmt.executeUpdate();
	        System.out.println(linhasAfetadas > 0 ? "Registro inserido com sucesso." : "Falha ao inserir o registro.");

	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}


	public static void sendMessage(PedidoInfo pedido, String numnota) throws Exception {
		// Formatar a data para exibir apenas o valor de data sem o tempo
	    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
	    String dataFormatada = dateFormat.format(pedido.getDtEntrega());
	    String mensagemCompra = String.format(
	    	    "Olá *%s*, a data da entrega do seu pedido *%s* no valor de *%s* precisou ser alterada. A nova previsão de entrega é *%s* 🚚\n\n" +
	    	    "Qualquer dúvida, é só nos chamar aqui no WhatsApp que estaremos prontos para te atender! 👍", 
	    	     pedido.getnomeParc().trim(), numnota, pedido.getValor() , dataFormatada 
	    	);


		System.out.println("LogIntegracaopolichatReenvioSendMessage:" + pedido.getFone());
        // Criação da URL com o ID do contato
        String apiUrl = BASE_URL + pedido.getFone() + USERS_PART;
        //System.out.println("LogIntegracaopolichat:" + apiUrl);
        URL url = new URL(apiUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        // Configuração da requisição HTTP POST
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        connection.setRequestProperty("Accept", "application/json");
        connection.setRequestProperty("Authorization", "Bearer " + TOKEN);
        connection.setDoOutput(true);

        // Parâmetros do corpo da requisição (mensagem)
        String urlParameters = "usermsg=" + mensagemCompra;
        byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8);

        // Envio da requisição
        try (OutputStream outputStream = connection.getOutputStream()) {
            outputStream.write(postData);
        }

        // Verificação da resposta
        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
        	
            System.out.println("Mensagem enviada com sucesso!");
            //JOptionPane.showMessageDialog(null, "Mensagem enviada com sucesso!");
        } else {
        	
            System.out.println("Erro ao enviar mensagem. Código de resposta: " + responseCode);
         // Exibir mensagem de erro
            //JOptionPane.showMessageDialog(null, "Erro ao enviar mensagem. Código de resposta: " + responseCode, "Erro", JOptionPane.ERROR_MESSAGE);
        }

        // Fechar a conexão
        connection.disconnect();
    }
	
	public static void enviarPathfind(String codigoCliente, String codigoPedido, List<PedidoItem> items, PedidoInfo info, int ano,int mes, int dia) throws Exception {
        System.out.println("ITENS:" + items.toString());
		try {
            String soapMessage = montarMensagemSOAP(codigoCliente, codigoPedido, items, info, ano,mes,dia);
            //System.out.println("info.getNomeVendedor(): " + info.getDtEntrega());
            enviarMensagemSOAP(soapMessage);
        } catch (IOException erro) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            erro.printStackTrace(pw);
            String mensagem = "Erro Exceção: " + erro.getMessage() + sw.toString();
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
        
        System.out.println("bodyResponse_LOG:" + bodyResponse.toString());

        return bodyResponse.toString();
    }

    private static void enviarMensagemSOAP(String soapMessage) throws IOException {
        URL url = new URL("https://pathfindsistema.com.br/pathfind_centro_do_aluminio/PedidoService?wsdl");
        //URL url = new URL("https://pathfindsistema.com.br/pathfind_centro_do_aluminio_sup/PedidoService?WSDL");
        
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        // Configurações da conexão
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
        //connection.setRequestProperty("usuario", "integracao_centro");
        //connection.setRequestProperty("senha", "Q1w2e3r4$");
        connection.setRequestProperty("usuario", "luana");
        connection.setRequestProperty("senha", "Q1w2e3r4@");
        connection.setDoOutput(true);
        connection.setReadTimeout(30000);
        connection.setConnectTimeout(30000);

        // Envia a requisição
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
            throw new IOException("Retorno durante a conexão: Code Response: " + connection.getResponseCode());
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
        
        String sql = "SELECT CAB.CODPARC AS CODPARC, P.NOMEPARC AS NOMEPARC, CAB.AD_TELEFONE_PAR AS FONE, CAB.DTPREVENT AS DTENTREGA, CI.NOMECID as municipio, B.NOMEBAI as bairro, UU.UF as estado, CAB.AD_CEP as cep, " +
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
        //String jdbcUrl = "jdbc:sqlserver://10.1.100.15:1433;databaseName=sankhya_prod;user=sankhya;password=tecsis;";
        String jdbcUrl = "jdbc:sqlserver://10.1.100.9:1433;databaseName=sankhya_prod;user=sankhya;password=tecsis;";
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
                    
                    String codparc = rs.getString("CODPARC");
                    String nomeParc = rs.getString("NOMEPARC");
                    String fone = rs.getString("FONE");
                    Timestamp dtEntrega = rs.getTimestamp("DTENTREGA");
                    
                    System.out.println("getPedidoInfoReprogramacaoPathfindDTENTREGA:" + dtEntrega);
 
                    return new PedidoInfo(municipio, bairro, estado, cep, valor, endereco, codVendedor.toString(), apelido, filial.toString(), obs, latitude, longitude, codparc, nomeParc, fone, dtEntrega );
                } else {
                	
                    throw new SQLException("Nenhum pedido encontrado com NUNOTA: " + NUNOTA);
                }
            } catch (Exception e) {
            	System.out.println("ReprogramarEntrega :" +e.fillInStackTrace());
    			e.printStackTrace();
			}
        }
		return null;
    }
    
    public static List<PedidoItem> getItensPedido(BigDecimal nUNOTA) throws Exception {
        List<PedidoItem> itens = new ArrayList<>();
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            String jdbcUrl = "jdbc:sqlserver://10.1.100.9:1433;databaseName=sankhya_prod;user=sankhya;password=tecsis;";
            //String jdbcUrl = "jdbc:sqlserver://10.1.100.15:1433;databaseName=sankhya_prod;user=sankhya;password=tecsis;";
        	//String jdbcUrl = "jdbc:sqlserver://10.1.100.5:8280;databaseName=sankhya_prod;user=sankhya;password=tecsis;";
            
            connection = DriverManager.getConnection(jdbcUrl);
            connection.setAutoCommit(false); // Start transaction

            String sql = "SELECT CODPROD, QTDNEG, C.PESO, C.VOLUME FROM TGFCAB C INNER JOIN TGFITE I ON C.NUNOTA = I.NUNOTA WHERE C.NUNOTA = ?";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setBigDecimal(1, nUNOTA);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
            	System.out.println("getPedidoInfoReprogramacaoPathfind: "+resultSet.getString("CODPROD"));
                String codigoProdutoStr = resultSet.getString("CODPROD");
                String quantidadeProdutoStr = resultSet.getString("QTDNEG");
                String pesoStr = resultSet.getString("PESO");
                String volumeStr = resultSet.getString("VOLUME");

                
                // Default values if null
                codigoProdutoStr = (codigoProdutoStr != null) ? codigoProdutoStr.trim() : "0";
                quantidadeProdutoStr = (quantidadeProdutoStr != null) ? quantidadeProdutoStr.trim() : "0";
                pesoStr = (pesoStr != null) ? pesoStr.trim() : "0";
                volumeStr = (volumeStr != null) ? volumeStr.trim() : "0";
                
                //System.out.println("getPedidoInfoReprogramacaoPathfind_codigoProdutoStr:"+codigoProdutoStr);

                BigDecimal codigoProduto;
                BigDecimal quantidadeProduto;
                BigDecimal peso;
                BigDecimal volume;
                
                
                try {
                	//System.out.println(codigoProdutoStr);
                    codigoProduto = new BigDecimal(codigoProdutoStr);
                    quantidadeProduto = new BigDecimal(quantidadeProdutoStr);
                    peso = new BigDecimal(pesoStr);
                    volume = new BigDecimal(volumeStr);
                    
                    //System.out.println("getPedidoInfoReprogramacaoPathfind_codigoProduto:" + codigoProduto);
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Erro ao converter valores para BigDecimal.", e);
                }

                if (codigoProduto.compareTo(BigDecimal.ZERO) <= 0) {
                    throw new IllegalArgumentException("Código do produto deve ser maior que zero.");
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
    
    private boolean validaNunota(BigDecimal nunota, ContextoAcao ctx) {
        JapeWrapper cabDAO = JapeFactory.dao(DynamicEntityNames.CABECALHO_NOTA);

        try {
            DynamicVO cabVO = cabDAO.findOne("NUNOTA = ?", nunota);
            System.out.println("validaNunota: " + cabVO);

            if (cabVO == null) {
                ctx.setMensagemRetorno("Nota não encontrada no sistema.");
                return false;
            }

            // Lista de valores permitidos para AD_ENTREGA
            List<String> valoresPermitidosAD_ENTREGA = Arrays.asList("D", "F", "R", "S", "A", "P");

            // Lista de valores não permitidos para CODTIPOPER
            List<BigDecimal> valoresNaoPermitidosCODTIPOPER = Arrays.asList(BigDecimal.valueOf(27), BigDecimal.valueOf(199));

            // Verificar se AD_ENTREGA está na lista de valores permitidos
            if (!valoresPermitidosAD_ENTREGA.contains(cabVO.asString("AD_ENTREGA"))) {
                ctx.setMensagemRetorno("Valor de AD_ENTREGA não permitido: " + cabVO.asString("AD_ENTREGA"));
                return false;
            }

            // Verificar se CODTIPOPER está na lista de valores não permitidos
            if (valoresNaoPermitidosCODTIPOPER.contains(cabVO.asBigDecimal("CODTIPOPER"))) {
                ctx.setMensagemRetorno("Operação não permitida: CODTIPOPER " + cabVO.asBigDecimal("CODTIPOPER"));
                return false;
            }

            // Verificar STATUSNOTA, STATUSNFE, TIPMOV e DTPREVENT
            if (!"L".equals(cabVO.asString("STATUSNOTA"))) {
                ctx.setMensagemRetorno("Nota fiscal não liberada (STATUSNOTA: " + cabVO.asString("STATUSNOTA") + ")");
                return false;
            }

            if (!"V".equals(cabVO.asString("TIPMOV"))) {
                ctx.setMensagemRetorno("Movimento inválido (TIPMOV: " + cabVO.asString("TIPMOV") + ")");
                return false;
            }

            if (cabVO.asTimestamp("DTPREVENT") == null) {
                ctx.setMensagemRetorno("Data de prevenção (DTPREVENT) não encontrada.");
                return false;
            }

        } catch (Exception e) {
            e.printStackTrace();
            ctx.setMensagemRetorno("Erro ao validar nota: " + e.getMessage());
            return false;
        }

        return true;
    }   

    private void atualizarReprogramacaoNotaVenda(BigDecimal nunota, Timestamp dtEntrega, String motivo) {
    	JapeWrapper cabDAO = JapeFactory.dao(DynamicEntityNames.CABECALHO_NOTA);
    	//JapeWrapper CA_SEND_PF_DAO = JapeFactory.dao("CA_SEND_PF");
    	
		
		try {
			DynamicVO cabVO = cabDAO.findOne("NUNOTA = ?", nunota);
			System.out.println("LOGcabVO : " + cabVO );
			
			cabDAO.prepareToUpdate(cabVO)
				.set("DTPREVENT", dtEntrega)
				.set("AD_REPRO", "S")
				.set("AD_MOTRETREGA", motivo)
				.update();
	
			
		} catch (Exception e) {
			System.out.println("LOGcabVOErro:" +e.fillInStackTrace());
			e.printStackTrace();
		}
		
    }
    
    private void atualizarReprogramacaoNotaPedido(BigDecimal nunota, Timestamp dtEntrega, String motivo) {
    	JapeWrapper cabDAO = JapeFactory.dao(DynamicEntityNames.CABECALHO_NOTA);
		try {
			DynamicVO cabVO = cabDAO.findOne("NUNOTA = ?", nunota);
			System.out.println("LOGcabVO : " + cabVO );
			
			cabDAO.prepareToUpdate(cabVO)
				.set("DTPREVENT", dtEntrega)
				.set("AD_REPRO", "S")
				.set("AD_MOTRETREGA", motivo)
				.update();
	
			
		} catch (Exception e) {
			System.out.println("LOGcabVOErro:" +e.fillInStackTrace());
			e.printStackTrace();
		}
		
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
		private String codparc;
		private String nomeParc;
		private String fone;
		private Timestamp dtEntrega;
    

   
    public PedidoInfo(String municipio, String bairro, String estado, String cep, BigDecimal valor, String endereco, String string, String apelido, String filial, String obs, String latitude, String longitude, String codparc, String nomeParc, String fone, Timestamp dtEntrega) {
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
        this.codparc = codparc;
        this.nomeParc = nomeParc;
        this.fone = fone;
        this.dtEntrega = dtEntrega;
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
    
    public String getCodParc() {
        return codparc;
    }

    public void setCodParc(String codparc) {
        this.codparc = codparc;
    }
    
    public String getnomeParc() {
        return nomeParc;
    }
    
    public String getFone() {
        return fone;
    }
    
    public Timestamp getDtEntrega() {
        return dtEntrega;
    }
  
}
