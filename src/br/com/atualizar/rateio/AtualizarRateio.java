package br.com.atualizar.rateio;

import br.com.sankhya.extensions.eventoprogramavel.EventoProgramavelJava;
import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.PersistenceException;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.event.PersistenceEvent;
import br.com.sankhya.jape.event.TransactionContext;
import br.com.sankhya.jape.sql.NativeSql;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;
import br.com.sankhya.modelcore.MGEModelException;
import br.com.sankhya.modelcore.auth.AuthenticationInfo;
import br.com.sankhya.modelcore.util.DynamicEntityNames;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;
import br.com.sankhya.ws.ServiceContext;

import com.sankhya.util.BigDecimalUtil;
import com.sankhya.util.TimeUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.Arrays;




public class AtualizarRateio implements EventoProgramavelJava {
	
	  public void afterInsert(PersistenceEvent arg0) throws Exception {

		    
		  }

	private void VerificaRateio(PersistenceEvent arg0) throws Exception {
		DynamicVO RatNota = (DynamicVO)arg0.getVo();
		BigDecimal nufin = BigDecimalUtil.getValueOrZero((BigDecimal)RatNota.getProperty("NUFIN"));


	    EntityFacade dwfEntityFacade = EntityFacadeFactory.getDWFFacade();
	    JdbcWrapper jdbc = dwfEntityFacade.getJdbcWrapper();
	    System.out.println("EVENTO ON");
	    NativeSql sqlverifica = new NativeSql(jdbc);
	    if (nufin.compareTo(BigDecimal.valueOf(3)) == 0) {
	    	 sqlverifica.executeUpdate("UPDATE TGFRAT SET PERCRATEIO = ROUND((AD_PEOPLE * 100.0 / (SELECT SUM(AD_PEOPLE) FROM TGFRAT WHERE NUFIN = 3)), 8) WHERE NUFIN = 3 ");
	    	  System.out.println("O CALCULO DO RATEIO");
	    }
	   
	  
	    
	    
	}
	
	public void exibirErro() throws Exception  {
		throw new PersistenceException("A confirmação da Nota para seu usuario nao está permitida");
	}
	

	@Override
	public void afterDelete(PersistenceEvent arg0) throws Exception {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void afterUpdate(PersistenceEvent arg0) throws Exception {
		// TODO Auto-generated method stub
		VerificaRateio(arg0);
	    System.out.println("TA FUNCIONANDO O UPDATE");
	}

	@Override
	public void beforeCommit(TransactionContext arg0) throws Exception {
		
	}

	@Override
	public void beforeDelete(PersistenceEvent arg0) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void beforeUpdate(PersistenceEvent arg0) throws Exception {

		
	}

	@Override
	public void beforeInsert(PersistenceEvent arg0) throws Exception {

		
	}
}
