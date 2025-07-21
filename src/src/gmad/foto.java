package src.gmad;


	
	import java.math.BigDecimal;

import br.com.sankhya.extensions.actionbutton.AcaoRotinaJava;
	import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.extensions.actionbutton.Registro;
import br.com.sankhya.jape.EntityFacade;
	import br.com.sankhya.jape.PersistenceException;
	import br.com.sankhya.jape.vo.DynamicVO;
	import br.com.sankhya.jape.vo.EntityVO;
	import br.com.sankhya.modelcore.util.EntityFacadeFactory;
	



	public class foto implements AcaoRotinaJava {

		public BigDecimal codparc = new BigDecimal(0);
		public String Codparc_s = "";
		public BigDecimal Codparc = new BigDecimal(0);
	    @Override
	    public void doAction(ContextoAcao contextoAcao) throws Exception {
	    	
	    	System.out.println("Codigo do parametro: " + Codparc_s);
	    	System.out.println("Codigo na variavel : " + Codparc);
	    	
	    	Registro line = contextoAcao.getLinhas()[0];

	    	getcodparc(line, contextoAcao);
			

	    	
	    	contextoAcao.setMensagemRetorno("<p align=\"center\"><img src=\"/mge/Parceiro@AD_FOTO@CODPARC="+Codparc+" .dbimage\" height=\"200\" width=\"300\"></img></p><br/><br/><br/><br/><br/><br/>\n\n\n\n<font size=\"15\" color=\"#BF2C2C\"><b> "+"</b></font>\n\n\n");
	    	
	    }

	    
	    private void getcodparc (Registro line, ContextoAcao contextoAcao) {
	    	Codparc = (BigDecimal)line.getCampo("CODPARC");
	    }
	    
	
}



