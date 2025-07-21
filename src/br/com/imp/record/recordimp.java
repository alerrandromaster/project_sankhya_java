package br.com.imp.record;

import java.math.BigDecimal;

import com.sankhya.util.BigDecimalUtil;
import com.sankhya.util.TimeUtils;

import br.com.sankhya.extensions.eventoprogramavel.EventoProgramavelJava;
import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.PersistenceException;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.event.PersistenceEvent;
import br.com.sankhya.jape.event.TransactionContext;
import br.com.sankhya.jape.sql.NativeSql;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.vo.EntityVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;
import br.com.sankhya.jape.wrapper.fluid.FluidCreateVO;
import br.com.sankhya.jape.wrapper.fluid.FluidUpdateVO;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;

public class recordimp implements EventoProgramavelJava {
	EntityFacade dwfEntityFacade = EntityFacadeFactory.getDWFFacade();
	JdbcWrapper jdbc = this.dwfEntityFacade.getJdbcWrapper();
	NativeSql nativeSql = new NativeSql(jdbc);
 
	
	@Override
	public void afterDelete(PersistenceEvent arg0) throws Exception {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void afterInsert(PersistenceEvent arg0) throws Exception {
		JapeSession	hnd = JapeSession.getCurrentSession();
		DynamicVO jprnota = (DynamicVO)arg0.getVo();
	    BigDecimal usuario = BigDecimalUtil.getValueOrZero((BigDecimal)jprnota.getProperty("CODUSU"));
	    String IDDOC = jprnota.asString("IDDOC");
	    String TIPDOC = jprnota.asString("TIPODOC");
	    String MsgErr = jprnota.asString("MSGERRO");
	    String StatsJob = jprnota.asString("JOBSTATUS");
	    
	    System.out.println("o Insert do Log caiu aqui ");	    
	    
	    if ("NOTA".equals(TIPDOC))
	    {
	    	
	    
	      try {
	  		JapeWrapper printV = JapeFactory.dao("AD_PRINTHISTORY");
	  		DynamicVO printVo = ((FluidCreateVO)((FluidCreateVO)((FluidCreateVO)((FluidCreateVO)((FluidCreateVO)
	  				printV.create().set("CODUSU", usuario)).set("IDDOC",IDDOC)).set("TIPODOC", TIPDOC)).set("MSGERRO", MsgErr))
	  				.set("JOBSTATUS", StatsJob)).save();

	      
	    		}catch (Exception e) {
	    			  e.printStackTrace();
	    			  System.out.println("Deu erro no insert");
				}finally
	      {
	      }
	      }
	      
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

	}
	@Override
	public void beforeUpdate(PersistenceEvent arg0) throws Exception {
		// TODO Auto-generated method stub
		
	}
	
	
	private void exibirErro(String mensagem) throws Exception  {
		throw new PersistenceException("--"+mensagem+"----");
	}
	


	
}