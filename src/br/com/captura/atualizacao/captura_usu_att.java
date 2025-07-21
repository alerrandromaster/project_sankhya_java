package br.com.captura.atualizacao;
import java.math.BigDecimal;

import com.sankhya.util.BigDecimalUtil;
import com.sankhya.util.TimeUtils;

import br.com.sankhya.extensions.eventoprogramavel.EventoProgramavelJava;
import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.event.PersistenceEvent;
import br.com.sankhya.jape.event.TransactionContext;
import br.com.sankhya.jape.sql.NativeSql;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;
import br.com.sankhya.jape.wrapper.fluid.FluidUpdateVO;
import br.com.sankhya.modelcore.auth.AuthenticationInfo;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;
import br.com.sankhya.ws.ServiceContext;


public class captura_usu_att implements EventoProgramavelJava {

	EntityFacade dwfEntityFacade = EntityFacadeFactory.getDWFFacade();
	JdbcWrapper jdbc = this.dwfEntityFacade.getJdbcWrapper();
	NativeSql nativeSql = new NativeSql(jdbc);
	@Override
	public void afterDelete(PersistenceEvent arg0) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void afterInsert(PersistenceEvent arg0) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void afterUpdate(PersistenceEvent arg0) throws Exception {

		JapeSession	hnd = JapeSession.getCurrentSession();
		DynamicVO cabNota = (DynamicVO)arg0.getVo();
		BigDecimal usuarioLogado = ((AuthenticationInfo)ServiceContext.getCurrent().getAutentication()).getUserID();
		BigDecimal ID = BigDecimalUtil.getValueOrZero((BigDecimal)cabNota.getProperty("ID"));
		
		System.out.println("Depois da Atualização!!!"+"Usuario é: "+usuarioLogado+" o Id é "+ID);
		
		if (hnd != null){
			JapeWrapper attDAO = JapeFactory.dao("AD_PROGFORD");
			DynamicVO attVO = attDAO.findOne("ID = ?", new Object[] { ID });
		      ((FluidUpdateVO)attDAO.prepareToUpdate(attVO)
		        .set("CODUSUALTER",usuarioLogado ))
		      	.set("DTALTHER", TimeUtils.getNow())
		        .update();
		      System.out.println("Atulizou!!!");

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
