package br.com.valida.exclusao.nota;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.ResultSet;

import com.sankhya.util.BigDecimalUtil;
import com.sankhya.util.StringUtils;
import com.sankhya.util.TimeUtils;

import br.com.sankhya.extensions.eventoprogramavel.EventoProgramavelJava;
import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.event.PersistenceEvent;
import br.com.sankhya.jape.event.TransactionContext;
import br.com.sankhya.jape.sql.NativeSql;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.modelcore.auth.AuthenticationInfo;
import br.com.sankhya.modelcore.comercial.LiberacaoAlcadaHelper;
import br.com.sankhya.modelcore.comercial.LiberacaoSolicitada;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;
import br.com.sankhya.ws.ServiceContext;

public class eventoValidaExcluir implements EventoProgramavelJava {

	public void beforeUpdate(PersistenceEvent arg0) throws Exception {
		// TODO Auto-generated method stub		
		//proibirAlteracao(arg0);		
	}

	public void afterDelete(PersistenceEvent arg0) throws Exception {
		// TODO Auto-generated method stub  
	}

	public void afterInsert(PersistenceEvent arg0) throws Exception {
		DynamicVO iteNota = (DynamicVO)arg0.getVo();
		EntityFacade dwfEntityFacade = EntityFacadeFactory.getDWFFacade();
		   JdbcWrapper jdbc = dwfEntityFacade.getJdbcWrapper();
		   BigDecimal nunota = BigDecimalUtil.getValueOrZero((BigDecimal)iteNota.getProperty("NUNOTA"));
		   BigDecimal codtipoper = BigDecimalUtil.getValueOrZero((BigDecimal)iteNota.getProperty("CODTIPOPER"));
		   BigDecimal numerodaNota = BigDecimalUtil.getValueOrZero((BigDecimal)iteNota.getProperty("AD_NRCANCEL"));
			BigDecimal usuarioLogado = ((AuthenticationInfo)ServiceContext.getCurrent().getAutentication()).getUserID(); 
		   String statusnota = (String) iteNota.getProperty("STATUSNOTA");
		   NativeSql nativeSql = new NativeSql(jdbc);
		   String Nota = "";
		   String NunotaOrig = "";
		   int Evento ;
		   

		 if ( codtipoper.compareTo(BigDecimal.valueOf(9018)) == 0 ) {
			StringBuilder sqlConf = new StringBuilder();
			sqlConf.append(" SELECT AD_NRCANCEL AS NOTA FROM TGFCAB WHERE NUNOTA ="+nunota);
			System.out.println("A query 1 é:"+sqlConf.toString());
			ResultSet Conf = nativeSql.executeQuery(sqlConf.toString());
			if (Conf.next())
			{
			Nota = Conf.getString("NOTA");
			System.out.println("o Status da Nota é:"+Nota);
			}
			
			StringBuilder sqlConf2 = new StringBuilder();
			sqlConf2.append("SELECT NUNOTAORIG " );
			sqlConf2.append(" FROM TGFVAR ");
			sqlConf2.append(" WHERE NUNOTA = "+numerodaNota);
			System.out.println("A query 2 é:"+sqlConf2.toString());
			ResultSet Conf2 = nativeSql.executeQuery(sqlConf2.toString());
			if(Conf2.next())
			{
				NunotaOrig = Conf2.getString("NUNOTAORIG");
			}
			
			StringBuilder LibAreas = new StringBuilder();
			LibAreas.append(" SELECT \r\n"
					+ "    CASE \r\n"
					+ "        WHEN ASEPAR IN (1,9,12,13,18,23) THEN 1055\r\n"
					+ "        WHEN ASEPAR IN (6,19,24) THEN 1056\r\n"
					+ "        WHEN ASEPAR IN (7,10,14,15,20,25) THEN 1057\r\n"
					+ "        WHEN ASEPAR IN (8,11,16,17,21,26) THEN 1058\r\n"
					+ "    END AS EVENTO,\r\n"
					+ "    ROW_NUMBER() OVER (ORDER BY \r\n"
					+ "        CASE \r\n"
					+ "            WHEN ASEPAR IN (1,9,12,13,18,23) THEN 1055\r\n"
					+ "            WHEN ASEPAR IN (6,19,24) THEN 1056\r\n"
					+ "            WHEN ASEPAR IN (7,10,14,15,20,25) THEN 1057\r\n"
					+ "            WHEN ASEPAR IN (8,11,16,17,21,26) THEN 1058\r\n"
					+ "        END\r\n"
					+ "    ) AS SEQUENCIA\r\n"
					+ "FROM AD_CONFITE\r\n"
					+ "WHERE NUNOTA ="+ NunotaOrig +" and ASEPAR IN (1,9,12,13,18,23,6,19,24,7,10,14,15,20,25,8,11,16,17,21,26)\r\n"
					+ "GROUP BY\r\n"
					+ "    CASE \r\n"
					+ "        WHEN ASEPAR IN (1,9,12,13,18,23) THEN 1055\r\n"
					+ "        WHEN ASEPAR IN (6,19,24) THEN 1056\r\n"
					+ "        WHEN ASEPAR IN (7,10,14,15,20,25) THEN 1057\r\n"
					+ "        WHEN ASEPAR IN (8,11,16,17,21,26) THEN 1058\r\n"
					+ "    END");
			System.out.println("A QUERY 3 é"+LibAreas.toString());
			ResultSet ArrayAreas = nativeSql.executeQuery(LibAreas.toString());
		
			while (ArrayAreas.next())
			{
				Evento = ArrayAreas.getInt("EVENTO");
				System.out.println(" A query entrou em loop"+Evento);
				LiberacaoSolicitada ls = new LiberacaoSolicitada(nunota,"TGFCAB",Evento,BigDecimalUtil.ZERO_VALUE);
				ls.setSequenciaCascata(BigDecimal.ZERO);
				ls.setDhSolicitacao(TimeUtils.getNow());
				ls.setLiberador(BigDecimal.ZERO);
				ls.setSolicitante(usuarioLogado);
				ls.setCodTipOper(new BigDecimal(9018));
				ls.setVlrLiberado(BigDecimal.ZERO);
				ls.setVlrLimite(BigDecimal.ZERO);
				ls.setVlrAtual(new BigDecimal(1));
				ls.setVlrTotal(BigDecimal.ZERO);
				LiberacaoAlcadaHelper.inserirSolicitacao(ls);
				LiberacaoAlcadaHelper.processarLiberacao(ls);
				System.out.println(" A query Saiu do loop");
			}
		 }
		   
	}

	public void afterUpdate(PersistenceEvent arg0) throws Exception {
		
		
	}

	public void beforeCommit(TransactionContext arg0) throws Exception {
		// TODO Auto-generated method stub
	}

	public void beforeDelete(PersistenceEvent arg0) throws Exception {
		// TODO Auto-generated method stub
		proibirExclusao(arg0);
	}

	public void beforeInsert(PersistenceEvent arg0) throws Exception {
		// TODO Auto-generated method stub

	}

	private void proibirExclusao(PersistenceEvent arg0) throws Exception {

		DynamicVO cabNota = (DynamicVO) arg0.getVo();
		EntityFacade dwfEntityFacade = EntityFacadeFactory.getDWFFacade();
		JdbcWrapper jdbc = dwfEntityFacade.getJdbcWrapper();
		NativeSql nativeSql = new NativeSql(jdbc);

		BigDecimal nunota = BigDecimalUtil.getValueOrZero((BigDecimal) cabNota.getProperty("NUNOTA"));
		String statusnota = StringUtils.getEmptyAsNull(cabNota.getProperty("STATUSNOTA"));
		String TipMov = StringUtils.getEmptyAsNull(cabNota.getProperty("TIPMOV"));
		String Confirmada = "A";


		StringBuilder sqlConf = new StringBuilder();
		sqlConf.append(" SELECT ISNULL(STATUSNOTA,'F') AS NOTA ");
		sqlConf.append(" FROM TGFCAB ");
		sqlConf.append(" WHERE AD_NRCANCEL = "+nunota );
		ResultSet Conf = nativeSql.executeQuery(sqlConf.toString());
		if (Conf.next())
		{
		Confirmada = Conf.getString("NOTA");
		System.out.println("o Status da Nota é:"+Confirmada);
		}


		if (Confirmada.equals("A") && statusnota.equals("L") && TipMov.equals("V") ) {
			exibirMensagem2(nunota);
		}

	}
	
	public static void exibirMensagem(BigDecimal codprod) throws IOException {
		throw new IOException("O produto " + codprod + " não possui fórmula na tela de composição do produto. ");
	}

	public static void exibirMensagem2(BigDecimal existeOp) throws IOException {
		throw new IOException(
				"<br><b>Exclusão não permitida, pois a Solicitação de Cancelamento não foi confirmada.</b><br>");
	}

	public static void exibirMensagem3() throws IOException {
		throw new IOException(
				"<br><b>Alteração/Confirmação não permitida, as ordens de produção dessa nota foram canceladas ou estão em andamento, favor verificar.</b><br>");
	}




	
}
