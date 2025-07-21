package src.gmad;

import java.math.BigDecimal;

import br.com.sankhya.extensions.actionbutton.AcaoRotinaJava;
	import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.extensions.actionbutton.QueryExecutor;
import br.com.sankhya.extensions.actionbutton.Registro;
import br.com.sankhya.jape.EntityFacade;
	import br.com.sankhya.jape.PersistenceException;
	import br.com.sankhya.jape.vo.DynamicVO;
	import br.com.sankhya.jape.vo.EntityVO;
	import br.com.sankhya.modelcore.util.EntityFacadeFactory;


public class mostrar_saldo implements AcaoRotinaJava {


		public BigDecimal codparc = new BigDecimal(0);
		public String Codparc_s = "";
		public BigDecimal Codparc = new BigDecimal(0);
		public BigDecimal Saldo = new BigDecimal(0);
		public String Sald = "";
	    @Override
	    public void doAction(ContextoAcao contextoAcao) throws Exception {
	    	
	    	System.out.println("Codigo do parametro: " + Codparc_s);
	    	System.out.println("Codigo na variavel : " + Codparc);
	    	
	    	Registro line = contextoAcao.getLinhas()[0];

	    	getcodparc(line, contextoAcao);
			

	    	
	    	contextoAcao.setMensagemRetorno("O credito sendo usado em outras nota é :"+Saldo);
	    	
	    }

	    
	    private void getcodparc (Registro line, ContextoAcao contextoAcao) {
	    
	    	
	    	
	    	QueryExecutor rset5 = contextoAcao.getQuery();
	    	
	    	try {
	    	Codparc = (BigDecimal)line.getCampo("CODPARC");
	    	StringBuffer sqlIt5 = new StringBuffer();
	    	sqlIt5.append(" SELECT SUM(VLRNOTA) AS VLR");
	    	sqlIt5.append(" FROM ");
	    	sqlIt5.append(" TGFCAB ");
	    	sqlIt5.append(" WHERE ");
	    	sqlIt5.append(" CODTIPVENDA = 132 AND TIPMOV = 'V' AND STATUSNOTA <> 'L' AND CODPARC =  " + Codparc);
		 	
				rset5.nativeSelect(sqlIt5.toString());
				
				if (rset5.next())
			 	{
			 		Sald = rset5.getString("VLR");
			 		System.out.println("NUNOTA QUERY1: " + Sald);	
			 		Saldo = new BigDecimal(Sald);
			 				System.out.println("NUNOTA QUERY1: " + Saldo);	
			 	}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally {
				rset5.close();
			}
	    	
		 	
		 	
	    }
	    
	
}





