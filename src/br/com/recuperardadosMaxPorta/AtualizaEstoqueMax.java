package br.com.recuperardadosMaxPorta;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.ResultSet;

import com.sankhya.util.BigDecimalUtil;

import br.com.sankhya.extensions.eventoprogramavel.EventoProgramavelJava;
import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.PersistenceException;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.event.PersistenceEvent;
import br.com.sankhya.jape.event.TransactionContext;
import br.com.sankhya.jape.sql.NativeSql;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;

public class AtualizaEstoqueMax implements EventoProgramavelJava {
	EntityFacade dwfEntityFacade = EntityFacadeFactory.getDWFFacade();
	JdbcWrapper jdbc = this.dwfEntityFacade.getJdbcWrapper();
	NativeSql nativeSql = new NativeSql(jdbc);
	StringBuilder sqlite1 = new StringBuilder();
	StringBuilder sqlite2 = new StringBuilder();
	public BigDecimal nunota = new BigDecimal(0);
	public BigDecimal id = new BigDecimal(0);
	public BigDecimal orc = new BigDecimal(0);
	public BigDecimal notaantiga;

	@Override
	public void afterDelete(PersistenceEvent arg0) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void afterInsert(PersistenceEvent arg0) throws Exception {

	}

	@Override
	public void afterUpdate(PersistenceEvent arg0) throws Exception {
		// TODO Auto-generated method stub
		JapeSession hnd = JapeSession.getCurrentSession();
		DynamicVO cabNota = (DynamicVO) arg0.getVo();
		BigDecimal Nunota = BigDecimalUtil.getValueOrZero((BigDecimal) cabNota.getProperty("NUNOTA"));
		String StatusNfe = (String) cabNota.getProperty("STATUSNFE");
		BigDecimal Top = BigDecimalUtil.getValueOrZero((BigDecimal) cabNota.getProperty("CODTIPOPER"));
		//BigDecimal Prod = BigDecimalUtil.getValueOrZero((BigDecimal) cabNota.getProperty("CODPROD"));
		//BigDecimal est = BigDecimalUtil.getValueOrZero((BigDecimal) cabNota.getProperty("ESTOQUE"));
		System.out.println("O EVENTO de Integração Max Portas INICIOU");
		//System.out.println("NUNOTA"+Nunota+"STATUSNFE : "+StatusNfe);
		//enviarProdutoMax(Prod,est);
		//BigDecimal Estoque = AtualizarProdMaxPortas(Codprod);
		
		EntityFacade dwfEntityFacade = EntityFacadeFactory.getDWFFacade();
		JdbcWrapper jdbc = dwfEntityFacade.getJdbcWrapper();
		StringBuilder sqlite1 = new StringBuilder();
		NativeSql nativeSql = new NativeSql(jdbc);
		
		if ("A".equals(StatusNfe) && Top.compareTo(new BigDecimal(5)) == 0)
		{
		try {

			sqlite1.append(" SELECT MP.CODPRODMP AS CODPROD,MP.QTD,MP.CODVOL,MP.SEQMP FROM TPRIPROC POC \r\n"
					+ "INNER JOIN  TPRIATV ATV ON ATV.IDIPROC = POC.IDIPROC\r\n"
					+ "INNER JOIN TPRAPO APO ON APO.IDIATV = ATV.IDIATV\r\n"
					+ "INNER JOIN TPRAMP MP ON MP.NUAPO = APO.NUAPO\r\n");
			sqlite1.append(" WHERE POC.NROLOTE = " + Nunota);
			ResultSet Query = nativeSql.executeQuery(sqlite1.toString());

			if (Query.next()) {
				String Codprod = Query.getString("CODPROD");
				BigDecimal CODPROD = new BigDecimal(Codprod);
				BigDecimal Estoque = AtualizarProdMaxPortas(CODPROD);
				enviarProdutoMax(CODPROD, Estoque);
			}
			Query.close();

		} catch (Exception e) {
			System.out.println("Erro na Query:" + e.getMessage());
			e.toString();
			e.printStackTrace();
		}
		}

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

	}

	@Override
	public void beforeUpdate(PersistenceEvent arg0) throws Exception {
		// TODO Auto-generated method stub

	}

	
	
	private BigDecimal AtualizarProdMaxPortas(BigDecimal CODPROD) throws Exception {
		EntityFacade dwfEntityFacade = EntityFacadeFactory.getDWFFacade();
		JdbcWrapper jdbc = dwfEntityFacade.getJdbcWrapper();
		StringBuilder sqlite1 = new StringBuilder();
		NativeSql nativeSql = new NativeSql(jdbc);
		String Estoque = "";
		BigDecimal EST = new BigDecimal(0);
		try {

			sqlite1.append(" SELECT SUM(EST.ESTOQUE-EST.RESERVADO) AS ESTOQUE");
			sqlite1.append(" FROM TGFEST EST" + " INNER JOIN TGFLOC LOC ON LOC.CODLOCAL = EST.CODLOCAL");
			sqlite1.append(" WHERE LOC.AD_SYSTOCK = 'S' AND LOC.ATIVO = 'S' AND EST.CODPROD = " + CODPROD);
			ResultSet Query = nativeSql.executeQuery(sqlite1.toString());

			if (Query.next()) {
				Estoque = Query.getString("ESTOQUE");
				EST = new BigDecimal(Estoque);
				System.out.println("O Estoque Atual de" + CODPROD + "Estoque" + Estoque);
			}
			Query.close();

		} catch (Exception e) {
			System.out.println("Erro na Query:" + e.getMessage());
			e.toString();
			e.printStackTrace();
		}
		return EST;

	}

	public void enviarProdutoMax(BigDecimal Codprod, BigDecimal Estoque) throws IOException {
		URL url = new URL("https://www.maxiportas.com.br/ap_produtos/index.php?Produtos_Estoque=null");
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("POST");
		connection.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
		connection.setRequestProperty("Token", "3838dac474636d8d14c177effc9cbc22");
		connection.setRequestProperty("Empresa","139");
		connection.setDoOutput(true);
		connection.setReadTimeout(30000);
		connection.setConnectTimeout(30000);

		String jsonInputString = String.format(
				"[{\"PRODUTO_CODIGO_FABRICA\":\"%s\", \"ESTOQUE_OPERACAO\":\"A\", \"ESTOQUE_MOVIMENTACAO\":\"%s\"}]",
				Codprod, Estoque);
		try (OutputStream os = connection.getOutputStream()) {
			byte[] input = jsonInputString.getBytes("utf-8");
			os.write(input, 0, input.length);
		}

		StringBuilder responseBuilder = new StringBuilder();

		try {
			int responseCode = connection.getResponseCode();
			if (responseCode == HttpURLConnection.HTTP_OK) {
				BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));
				String line;
				while ((line = reader.readLine()) != null) {
					responseBuilder.append(line);
				}
				reader.close();
			} else {
				responseBuilder.append("Request failed with status: ").append(responseCode);
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Error: " + e.getMessage());
		} finally {
			connection.disconnect();
		}

	}

}
