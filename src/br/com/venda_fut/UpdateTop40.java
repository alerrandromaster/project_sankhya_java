package br.com.venda_fut;

import java.math.BigDecimal;

import com.sankhya.util.BigDecimalUtil;

import br.com.sankhya.extensions.eventoprogramavel.EventoProgramavelJava;
import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.event.PersistenceEvent;
import br.com.sankhya.jape.event.TransactionContext;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;
import br.com.sankhya.jape.wrapper.fluid.FluidUpdateVO;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;

public class UpdateTop40 implements EventoProgramavelJava {

	@Override
	public void afterDelete(PersistenceEvent arg0) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void afterInsert(PersistenceEvent arg0) throws Exception {
		// TODO Auto-generated method stub

		DynamicVO iteNota = (DynamicVO)arg0.getVo();
				EntityFacade dwfEntityFacade = EntityFacadeFactory.getDWFFacade();
				BigDecimal nunota = BigDecimalUtil.getValueOrZero((BigDecimal)iteNota.getProperty("NUNOTA"));
				BigDecimal codtipoper = BigDecimalUtil.getValueOrZero((BigDecimal)iteNota.getProperty("CODTIPOPER"));
				
				 if (codtipoper.compareTo(BigDecimal.valueOf(46)) == 0)
				{
					atualizarStatus(nunota);
				}
	}
	
	
	 private void atualizarStatus(BigDecimal NUNOTA) {
		    JapeWrapper separacaoDAO = JapeFactory.dao("TGFCAB");
		    try {
		      DynamicVO separacaoVO = separacaoDAO.findOne("NUNOTA = ?", new Object[] { NUNOTA });
		      if (NUNOTA != null) {
		        ((FluidUpdateVO)separacaoDAO.prepareToUpdate(separacaoVO)
		          .set("AD_ENTREGA", "N"))
		          .update();}
		    } catch (Exception e) {
		      System.out.println("erro:" + e.toString());
		      e.printStackTrace();
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
		// TODO Auto-generated method stub
		
	}

	@Override
	public void beforeUpdate(PersistenceEvent arg0) throws Exception {
		// TODO Auto-generated method stub
		
	}

}
