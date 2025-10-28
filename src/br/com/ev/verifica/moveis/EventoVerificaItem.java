package br.com.ev.verifica.moveis;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.sql.ResultSet;

import com.sankhya.util.BigDecimalUtil;
import com.sankhya.util.StringUtils;

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

public class EventoVerificaItem implements EventoProgramavelJava {
	public BigDecimal reserva = new BigDecimal(0);
	public BigDecimal qtdReserva = new BigDecimal(0);
	public BigDecimal qtdPedido = new BigDecimal(0);
	public boolean acusa ;
	public void beforeUpdate(PersistenceEvent arg0) throws Exception {
		// TODO Auto-generated method stub
		 marcarItemProduzir(arg0);
		 boolean modificandoMeuCampo = false;
		 
		 if (reserva.compareTo(BigDecimal.ZERO) != 0)
		 {
		 modificandoMeuCampo = arg0.getModifingFields().isModifing("QTDNEG");
		 boolean modificandoMeuCampo2 = false;
		 modificandoMeuCampo2 =arg0.getModifingFields().isModifing("CODPROD");
		 boolean modificandoMeuCampo3 = false;
		 modificandoMeuCampo3 = arg0.getModifingFields().isModifing("CODLOCALORIG");
		 if(modificandoMeuCampo && reserva.compareTo(BigDecimal.ZERO) != 0) 
		 {
			 exibirMensagem();
	            		
	        }
		 
		 if(modificandoMeuCampo2 && reserva.compareTo(BigDecimal.ZERO) != 0) 
		 {
			 exibirMensagem();
	            		
	        }
		 
		 if(modificandoMeuCampo3 && reserva.compareTo(BigDecimal.ZERO) != 0) 
		 {
			 exibirMensagem();
	            		
	        }
		 
		 }
		


	}

	public void afterDelete(PersistenceEvent arg0) throws Exception {
		// TODO Auto-generated method stub
	}

	public void afterInsert(PersistenceEvent arg0) throws Exception {
		// TODO Auto-generated method stub
		//contaqtdreserva(arg0,reserva);	
	//	System.out.println("O valor de Acusa é:"+acusa);	
		//if ( acusa) {
		//		exibirMensagem();
	//	}
		//DynamicVO iteNota = (DynamicVO)arg0.getVo();
		//EntityFacade dwfEntityFacade = EntityFacadeFactory.getDWFFacade();
		//BigDecimal nunota = BigDecimalUtil.getValueOrZero((BigDecimal)iteNota.getProperty("NUNOTA"));
		//BigDecimal codtipoper = BigDecimalUtil.getValueOrZero((BigDecimal)iteNota.getProperty("CODTIPOPER"));
		
		// (codtipoper.compareTo(BigDecimal.valueOf(46)) == 0)
		//{
			//atualizarStatus(nunota);
		//}

	}

	public void afterUpdate(PersistenceEvent arg0) throws Exception {
		
		//marcarItemProduzir(arg0);
		//System.out.println("O valor de Acusa é:"+acusa);	
		//if ( acusa) {
		//exibirMensagem();
			//}
	}

	public void beforeCommit(TransactionContext arg0) throws Exception {
		// TODO Auto-generated method stub
	}

	public void beforeDelete(PersistenceEvent arg0) throws Exception {
		// TODO Auto-generated method stub
	}

	public void beforeInsert(PersistenceEvent arg0) throws Exception {
		//marcarItemProduzir(arg0);
		//if ( acusa) {
		//	exibirMensagem();
		//}

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
	
	
	
	private void contaqtdreserva(PersistenceEvent arg0,BigDecimal nunota) throws Exception {
		EntityFacade dwfEntityFacade = EntityFacadeFactory.getDWFFacade();
		JdbcWrapper jdbc = dwfEntityFacade.getJdbcWrapper();
		NativeSql nativeSql = new NativeSql(jdbc);

		
		StringBuilder sqlReserva = new StringBuilder();
		sqlReserva.append(" SELECT SUM(QTDNEG) as QTD");
		sqlReserva.append(" FROM TGFITE ");
		sqlReserva.append(" WHERE NUNOTA = " + reserva);
		ResultSet queryRe = nativeSql.executeQuery(sqlReserva.toString());
		if(queryRe.next()) {
		qtdReserva = queryRe.getBigDecimal("QTD") != null
		? queryRe.getBigDecimal("QTD"): BigDecimal.ZERO;}
		
		System.out.println("O QTD da Reserva"+qtdReserva);
		
		StringBuilder sqlPedido = new StringBuilder();
		sqlPedido.append(" SELECT SUM(QTDNEG) as QTD");
		sqlPedido.append(" FROM TGFITE ");
		sqlPedido.append(" WHERE NUNOTA = " + nunota);
		ResultSet queryPedi = nativeSql.executeQuery(sqlPedido.toString());
		queryPedi.next();
		qtdPedido = queryPedi.getBigDecimal("QTD") != null
		? queryRe.getBigDecimal("QTD"): BigDecimal.ZERO;
		
		System.out.println("O QTD do Pedido "+qtdPedido);
		
		if (qtdReserva.compareTo(BigDecimal.ZERO) == 0){
			acusa = false;
		}
		if (qtdReserva.compareTo(BigDecimal.ZERO) != 0 && qtdReserva.compareTo(qtdPedido) != 0 ) {
			acusa = true;
		}
		
		}
		
	
	

	private void marcarItemProduzir(PersistenceEvent arg0) throws Exception {

		DynamicVO iteNota = (DynamicVO) arg0.getVo();
		BigDecimal nunota = BigDecimalUtil.getValueOrZero((BigDecimal) iteNota.getProperty("NUNOTA"));
		EntityFacade dwfEntityFacade = EntityFacadeFactory.getDWFFacade();
		JdbcWrapper jdbc = dwfEntityFacade.getJdbcWrapper();
		NativeSql nativeSql = new NativeSql(jdbc);
		StringBuilder sqlTop = new StringBuilder();
		sqlTop.append(" SELECT MAX(AD_ORC_VEND_MDF) AS RESERVA ");
		sqlTop.append(" FROM TGFCAB ");
		sqlTop.append(" WHERE NUNOTA = " + nunota);
		sqlTop.append(" AND CODTIPOPER = 9042 ");//9044
		ResultSet queryTop = nativeSql.executeQuery(sqlTop.toString());
		System.out.println("sqlTop: " + sqlTop.toString());
		queryTop.next();
		reserva = queryTop.getBigDecimal("RESERVA") != null 
			    ? queryTop.getBigDecimal("RESERVA") 
			    : BigDecimal.ZERO;
		System.out.println("O numero da Reserva"+reserva);

		
		}

	public static void exibirMensagem()
			throws IOException {
		throw new IOException("<b>Ação não permitida, já foi gerada reserva para o pedido de venda. Para realizar a alteração é necessário clicar no botão \"Desfazer a ordem de produção\"</b>");
	}

}
