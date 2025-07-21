package br.com.evento.fin;


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






public class atualizatipotitulo implements EventoProgramavelJava{

	public BigDecimal NUFin;
	EntityFacade dwfEntityFacade = EntityFacadeFactory.getDWFFacade();
	JdbcWrapper jdbc = this.dwfEntityFacade.getJdbcWrapper();
	@Override
	public void afterDelete(PersistenceEvent arg0) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void afterInsert(PersistenceEvent ctx) throws Exception {
		 DynamicVO registro = (DynamicVO)ctx.getVo();
		    BigDecimal CODTIPVENDA = registro.asBigDecimal("CODTIPVENDA");
		    BigDecimal NUNOTA = registro.asBigDecimal("NUNOTA");
		    JapeWrapper financeiroDAO = JapeFactory.dao("Financeiro");
		    atualizarStatusEntregue(NUNOTA);
		    if (CODTIPVENDA.compareTo(new BigDecimal(54)) == 0) {
		      System.out.println("Atualiza o tip titulo da venda Saldo Credito");
		      DynamicVO fin = financeiroDAO.findByPK(NUFin);
		      financeiroDAO.findOne("NUFIN = ?", new Object[] { NUFin });
		      System.out.println("after insert >> financeiroVO:" + financeiroDAO);
		      ((FluidUpdateVO)financeiroDAO.prepareToUpdate(fin)
		        .set("CODTIPTIT", new BigDecimal(49)))
		        .update();
		    } 
		
	}

	
	 private void atualizarStatusEntregue(BigDecimal NUNOTA) throws Exception {
		    System.out.println("atualizarStatusEntregue >> NUNOTALOG:" + NUNOTA);
		    StringBuilder sqlite1 = new StringBuilder();
		    NativeSql nativeSql = new NativeSql(jdbc);
			sqlite1.append(" SELECT NUFIN");
			sqlite1.append(" FROM TGFFIN ");
			sqlite1.append(" WHERE NUNOTA = " + NUNOTA);
		    ResultSet query = nativeSql.executeQuery(sqlite1.toString());
		  
		
		    if(query.next())
				    {
		    	NUFin = query.getBigDecimal("NUFIN");
		    	System.out.println("O Numero do Financeiro a ser atualizado é"+NUFin);
				    }
		    query.close();

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
		
	}

	
	
}
