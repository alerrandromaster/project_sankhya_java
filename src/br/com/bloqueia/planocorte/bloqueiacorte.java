package br.com.bloqueia.planocorte;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.ResultSet;

import com.sankhya.util.BigDecimalUtil;
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

public class bloqueiacorte implements EventoProgramavelJava {

	@Override
	public void afterDelete(PersistenceEvent arg0) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void afterInsert(PersistenceEvent arg0) throws Exception {
		// TODO Auto-generated method stub
		DynamicVO cabNota = (DynamicVO)arg0.getVo();
		BigDecimal nunota = BigDecimalUtil.getValueOrZero((BigDecimal)cabNota.getProperty("NUNOTA"));
		BigDecimal usuarioLogado = ((AuthenticationInfo)ServiceContext.getCurrent().getAutentication()).getUserID(); 
		EntityFacade dwfEntityFacade = EntityFacadeFactory.getDWFFacade();
		JdbcWrapper jdbc = dwfEntityFacade.getJdbcWrapper();
		NativeSql nativeSql = new NativeSql(jdbc);
		BigDecimal vlrlib = new BigDecimal(0);
		BigDecimal vlratu = new BigDecimal(0);
		
		StringBuilder sqlConf = new StringBuilder();
		sqlConf.append(" SELECT VLRATUAL,VLRLIBERADO  FROM TSILIB WHERE NUCHAVE ="+nunota);
		System.out.println("A query 1 é:"+sqlConf.toString());
		ResultSet Conf = nativeSql.executeQuery(sqlConf.toString());
		if (Conf.next())
		{
			vlrlib = Conf.getBigDecimal("VLRLIBERADO");
			vlratu = Conf.getBigDecimal("VLRATUAL");
		}
		
		if (vlratu.compareTo(BigDecimal.ZERO) == 0){
		int Evento = 0;
		LiberacaoSolicitada ls = new LiberacaoSolicitada(nunota,"AD_OPERPROD",Evento,BigDecimalUtil.ZERO_VALUE);
		ls.setSequenciaCascata(BigDecimal.ZERO);
		ls.setDhSolicitacao(TimeUtils.getNow());
		ls.setLiberador(BigDecimal.ZERO);
		ls.setSolicitante(usuarioLogado);
		ls.setCodTipOper(new BigDecimal(0));
		ls.setVlrLiberado(BigDecimal.ZERO);
		ls.setVlrLimite(BigDecimal.ZERO);
		ls.setVlrAtual(new BigDecimal(1));
		ls.setVlrTotal(BigDecimal.ZERO);
		LiberacaoAlcadaHelper.inserirSolicitacao(ls);
		LiberacaoAlcadaHelper.processarLiberacao(ls);}
	}

	@Override
	public void afterUpdate(PersistenceEvent arg0) throws Exception {
		// TODO Auto-generated method stub
		
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
		DynamicVO cabNota = (DynamicVO)arg0;
		BigDecimal nunota = BigDecimalUtil.getValueOrZero((BigDecimal)cabNota.getProperty("NUNOTA"));
		EntityFacade dwfEntityFacade = EntityFacadeFactory.getDWFFacade();
		JdbcWrapper jdbc = dwfEntityFacade.getJdbcWrapper();
		NativeSql nativeSql = new NativeSql(jdbc);
		BigDecimal vlrlib = new BigDecimal(0);
		BigDecimal vlratu = new BigDecimal(0);
		
		boolean QTDFURO = false;
		boolean QTDRASGO = false;
		boolean QTDFITA = false;
		boolean CODMAQRASGO = false;
		boolean CODMAQFITA = false;
		boolean CODMAQ = false;
		boolean CODMAQFURO = false;
		boolean QTDCORTE = false;
				
		
		QTDCORTE =arg0.getModifingFields().isModifing("QTDCORTE");
		CODMAQFURO =arg0.getModifingFields().isModifing("CODMAQFURO");
		CODMAQ =arg0.getModifingFields().isModifing("CODMAQ");
		CODMAQFITA =arg0.getModifingFields().isModifing("CODMAQFITA");
		CODMAQRASGO =arg0.getModifingFields().isModifing("CODMAQRASGO");
		QTDFITA =arg0.getModifingFields().isModifing("QTDFITA");
		QTDRASGO =arg0.getModifingFields().isModifing("QTDRASGO");
		QTDFURO =arg0.getModifingFields().isModifing("QTDFURO");
		
		StringBuilder sqlConf = new StringBuilder();
		sqlConf.append(" SELECT VLRATUAL,VLRLIBERADO  FROM TSILIB WHERE NUCHAVE ="+nunota);
		System.out.println("A query 1 é:"+sqlConf.toString());
		ResultSet Conf = nativeSql.executeQuery(sqlConf.toString());
		if (Conf.next())
		{
			vlrlib = Conf.getBigDecimal("VLRLIBERADO");
			vlratu = Conf.getBigDecimal("VLRATUAL");
		}
		
		
		if ((vlrlib.compareTo(vlratu)==0) && (QTDCORTE || CODMAQFURO || CODMAQ  || CODMAQFITA || CODMAQRASGO  || QTDFITA || QTDRASGO || QTDFURO) ) {
			exibirMensagem();
		}
	}
	
	public static void exibirMensagem()
			throws IOException {
		throw new IOException("<b>Plano de Corte não liberado!!!</b>");
	}

}
