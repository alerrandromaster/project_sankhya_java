package br.consulta.cor;

import java.math.BigDecimal;

import br.com.sankhya.extensions.actionbutton.AcaoRotinaJava;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.extensions.actionbutton.QueryExecutor;
import br.com.sankhya.extensions.actionbutton.Registro;


public class consultar_cor implements AcaoRotinaJava {


		public BigDecimal codparc = new BigDecimal(0);
		public String Codparc_s = "";
		public BigDecimal Codparc = new BigDecimal(0);
		public BigDecimal Saldo = new BigDecimal(0);
		public String Sald = "";
		public String ID_COR = "";
		public String Emb = "";
    	//StringBuilder Resultado = new StringBuilder();
    	String Resultado = "";
	    @Override
	    public void doAction(ContextoAcao contextoAcao) throws Exception {
	    	
	    	

	    	Registro line = contextoAcao.getLinhas()[0];

	    	getTinta(line, contextoAcao);
			

	    	System.out.println("RESULTADO RESULTOU");
	    	contextoAcao.setMensagemRetorno(Resultado.toString());
	    	
	    }

	    
	    private void getTinta (Registro line, ContextoAcao contextoAcao) {
	    
	    	ID_COR = contextoAcao.getParam("ID_COR").toString();
	    	Emb = contextoAcao.getParam("EMB").toString();
	    	System.out.println("o ID_COR é"+ID_COR);
	    	System.out.println("O emb é"+Emb);
	    	
	    	
	    	QueryExecutor rset5 = contextoAcao.getQuery();
	    	
	    	try {
	    	StringBuffer sqlIt5 = new StringBuffer();

	    	sqlIt5.append("  select distinct  COR.COR2_ID AS COR2_ID,\r\n"
	    			+ " COR.NOME_COR AS NOME_COR,b.codigo AS BASE,pro.codprod AS CODPROD, PRO.DESCRPROD AS DESCRPROD,l.descr AS LINHA,round(SANKHYA.CA_PRECOTINTA(PRO.CODPROD,FM.FORMULA,em.nominal/800),2) AS PRECO,EM.descr AS LITRO ");
	    	sqlIt5.append(" FROM  CA_CORAL_FRMPADRAO  fm ");
	    	sqlIt5.append(" INNER JOIN  AD_CORESPADRAO COR \r\n"
	    			+ "							ON fm.cor_id=COR.COR_ID COLLATE Latin1_General_CI_AI					\r\n"
	    			+ "					INNER JOIN CA_CORAL_BASES B\r\n"
	    			+ "							ON fm.id_base=B.id_base									\r\n"
	    			+ "					INNER JOIN  CA_CORAL_LINHA L\r\n"
	    			+ "							ON L.id_linha=b.id_linha						\r\n"
	    			+ "					INNER JOIN CA_CORAL_PRODUTOBASE PB\r\n"
	    			+ "							on PB.id_base=B.id_base and L.id_linha=PB.id_linha									\r\n"
	    			+ "					INNER JOIN CA_CORAL_EMBALAGENS EM\r\n"
	    			+ "							ON EM.id_emb=PB.id_emb							\r\n"
	    			+ "					INNER  JOIN TGFPRO PRO \r\n"
	    			+ "							ON PRO.AD_IDCORAL=PB.ID_PRODBASE ");
	    	sqlIt5.append(" WHERE ");
	    	sqlIt5.append(" COR.IDCOR="+ID_COR+ "  AND round(SANKHYA.CA_PRECOTINTA(PRO.CODPROD,FM.FORMULA,em.nominal/800),2) IS NOT NULL " + "and EM.id_emb=" +Emb);
		 	
			rset5.nativeSelect(sqlIt5.toString());
				
			boolean isNomeCorDisplayed = false;
			
			while (rset5.next())
			 	{
					String corId = rset5.getString("COR2_ID");
                    String nomeCor = rset5.getString("NOME_COR");
                    String BASE = rset5.getString("BASE");
                    int codProd = rset5.getInt("CODPROD");
                    String descrProd = rset5.getString("DESCRPROD").trim();
                    String linhaDescr = rset5.getString("LINHA");
                    double preco = rset5.getDouble("PRECO");
                    String embDescr = rset5.getString("LITRO");
                    
                    if (!isNomeCorDisplayed) {
                        Resultado += String.format("<b>Cor:</b> %s\n", nomeCor);
                        Resultado += "======================\n"; // Linha separadora para destacar o nome da cor
                        isNomeCorDisplayed = true; // Marca como exibido
                    }

                
                    Resultado+= String.format(
                            "<b>Prod:</b> %d - %s \n<b>Base:</b> %s <b>Emb.:</b> %s  <b>Valor:</b> %.2f\n",
                            codProd,descrProd, BASE, embDescr,preco);
                    
                   Resultado += "----------------------\n";
	
			 	}
				
				System.out.println(Resultado.toString());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally {
				rset5.close();
			}
	    	
		 	
		 	
	    }
	    
	
}





