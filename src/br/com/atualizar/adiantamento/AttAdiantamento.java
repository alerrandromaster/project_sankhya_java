package br.com.atualizar.adiantamento;


import br.com.sankhya.extensions.eventoprogramavel.EventoProgramavelJava;
import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.event.PersistenceEvent;
import br.com.sankhya.jape.event.TransactionContext;
import br.com.sankhya.jape.sql.NativeSql;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;
import br.com.sankhya.jape.wrapper.fluid.FluidUpdateVO;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;

import java.math.BigDecimal;
import java.sql.SQLException;

import com.sankhya.util.BigDecimalUtil;

import java.sql.ResultSet;




public class AttAdiantamento implements EventoProgramavelJava{

	public BigDecimal Comprador = new BigDecimal(0);
	@Override
	public void afterDelete(PersistenceEvent arg0) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void afterInsert(PersistenceEvent ctx) throws Exception {
	}

	
	 private BigDecimal Procurarcomprador(BigDecimal NUFIN) throws Exception {
			EntityFacade dwfEntityFacade = EntityFacadeFactory.getDWFFacade();
			JdbcWrapper jdbc = dwfEntityFacade.getJdbcWrapper();
			StringBuilder sqlite1 = new StringBuilder();
		    NativeSql nativeSql = new NativeSql(jdbc);
		    String Comp = "";
		    BigDecimal Buy = new BigDecimal(0);
			sqlite1.append(" SELECT FIN.NUFIN, CAB.CODVEND AS COMPRADOR");
			sqlite1.append(" FROM TGFCAB CAB");
			sqlite1.append (" INNER JOIN TGFFIN FIN ON FIN.CODEMP = CAB.CODEMP AND FIN.NUMNOTA = CAB.NUMNOTA and FIN.CODPARC = CAB.CODPARC ");
			sqlite1.append("where CAB.tipmov = 'C' and fin.CODTIPOPER IN (72,75) and fin.NUCOMPENS IS NOT NULL and fin.NUFIN = " + NUFIN);
		    ResultSet query = nativeSql.executeQuery(sqlite1.toString());
		    if (query.next()) {
		    //System.out.println("A query do Adiantamento é:"+sqlite1.toString());		  
		    	Comp = query.getString("COMPRADOR");
		    	Buy = new BigDecimal(Comp);
		    	//System.out.println("O Comprador a ser incluido é esse "+Buy);
		    }
		    query.close();
		    
		    //System.out.println("O comprador de Saida é:"+Buy);
		    
		    return Buy; 

	 }
	
	
	@Override
	public void afterUpdate(PersistenceEvent arg0) throws Exception {
		// TODO Auto-generated method stub
		 DynamicVO registro = (DynamicVO)arg0.getVo();
		    BigDecimal NUFIN = registro.asBigDecimal("NUFIN");
		    BigDecimal CODTIPOPER = registro.asBigDecimal("CODTIPOPER");
		    BigDecimal Buyer = new BigDecimal(0);
		    //BigDecimal BAIXA = registro.asBigDecimal("VLRBAIXA");
		    BigDecimal CODVEND = registro.asBigDecimal("CODVEND");
		    JapeWrapper financeiroDAO = JapeFactory.dao("Financeiro");
		    
		    if ((CODTIPOPER.compareTo(new BigDecimal(72)) == 0 || CODTIPOPER.compareTo(new BigDecimal(75)) == 0) && CODVEND.compareTo(BigDecimal.ZERO) == 0) {
		    	Buyer = Procurarcomprador(NUFIN);
		    	//System.out.println("Atualiza o Comprador no Adiantamento");
		      DynamicVO fin = financeiroDAO.findByPK(NUFIN);
		      financeiroDAO.findOne("NUFIN = ?", new Object[] { NUFIN });
		      //System.out.println("O comprador é "+Buyer+ "Numero do fin a ser att é:"+fin);
		      //System.out.println("after insert >> financeiroVO:" + financeiroDAO);
		      ((FluidUpdateVO)financeiroDAO.prepareToUpdate(fin)
		        .set("CODVEND", Buyer))
		        .update();
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
		// TODO Auto-generated method stub
		
	}

	@Override
	public void beforeUpdate(PersistenceEvent arg0) throws Exception {
		// TODO Auto-generated method stub
		
	}

	
	
}
