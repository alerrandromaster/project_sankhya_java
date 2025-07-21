package br.com.sankhya.importacaosystock;

import br.com.sankhya.extensions.actionbutton.AcaoRotinaJava;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.extensions.actionbutton.QueryExecutor;
import br.com.sankhya.extensions.actionbutton.Registro;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;
import br.com.sankhya.jape.wrapper.fluid.FluidUpdateVO;
import br.com.sankhya.modelcore.MGEModelException;

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Scanner;

public class ImportSystock implements AcaoRotinaJava {

    @Override
    public void doAction(final ContextoAcao contextoAcao) throws Exception {
        final Registro[] registros = contextoAcao.getLinhas();

        if (registros.length == 0) {
            throw new MGEModelException("Linha não selecionada!");
        }

        BigDecimal codImp = (BigDecimal) registros[0].getCampo("NUSYTOCK");

        //Valida se arquivo já foi processado
       if (getValArq(contextoAcao, codImp) >= 1) {
           throw new IllegalArgumentException("<b>Arquivo já processado!</b><br>Exclua o lançamento se deseja processar novamente.");
       }

        final JapeWrapper impDAO = JapeFactory.dao("AD_IMPSYSTOCK");
        final DynamicVO impVo = impDAO.findOne("NUSYTOCK = ?", new Object[]{codImp});
        byte[] blobData = impVo.asBlob("ARQUIVO");

        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(blobData);
             Scanner scanner = new Scanner(inputStream)) {

        	int nlinha = 0;
        	
            
        	
        
        	
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                System.out.println("Linha inicial"+line);
                String[] tokens = line.split(";");

                if (tokens.length >= 4 /*&& tokens.length <= 3*/) {
                		nlinha++;
                		
                		  String token0 = tokens[0];
                	        String marker = "__end_fileinformation__";
                	        String origem = "";
                	        if (token0.contains(marker)) {
                	            int index = token0.indexOf(marker) + marker.length();
                	            origem = token0.substring(index).trim();
                	        } else {
                	            origem = token0; // Caso não contenha o marcador, usa o token inteiro
                	        }
                		
	                    //String origem = tokens[0];
	                    String destino = tokens[1];
	                    String produto = tokens[2];
	                    String qtd = tokens[3];
	                    
	                    System.out.println("origem é "+tokens[0]);
	                   
						System.out.println("origem systock "+origem);
						System.out.println("destino systock "+destino);
						System.out.println("produto systock "+produto);
						System.out.println("Qtd systock "+qtd);
						
	                    Registro proc = contextoAcao.novaLinha("AD_ITESYSTOCK");
	                    proc.setCampo("NUSYTOCK", codImp);
	                    proc.setCampo("NUSEQ", nlinha);
	                   /* BigDecimal Origem = new BigDecimal(origem);
	                    BigDecimal Destino = new BigDecimal(destino);
	                    BigDecimal Codprod = new BigDecimal(produto);
	                    BigDecimal Quantd = new BigDecimal(qtd);*/
	                  //  System.out.println("TODAS AS VAR"+Origem+","+Destino+","+Codprod+","+Quantd);
	                    
	                    proc.setCampo("ORIGEM", origem);
	                    proc.setCampo("DESTINO", destino);
	                    proc.setCampo("CODPROD", produto);
	                    proc.setCampo("QTD", qtd);
	                   
	                    if ("10".equals(origem) && "5".equals(destino)) {
	                        proc.setCampo("CODTIPOPER", 7999);
	                    } else if ("10".equals(origem) && "11".equals(destino)) {
	                        proc.setCampo("CODTIPOPER", 7800);
	                    } else if ("9".equals(origem) && "5".equals(destino)) {
	                        proc.setCampo("CODTIPOPER", 9401);
	                    }
	                    else if ("11".equals(origem) && "5".equals(destino)) {
	                        proc.setCampo("CODTIPOPER", 7800);

	                    } else
	                    {
	                    	proc.setCampo("CODTIPOPER", 0);
	                    }
	                    proc.save();
	                } else {
	                    throw new IllegalArgumentException("<br><b>O número de campos na linha é inválido</b><br> Linha: " + nlinha);
	                }
            	}
            
            //Atualiza data e hora do procesamento
           // FluidUpdateVO impUpdVO = impDAO.prepareToUpdate(impVo);
          //  impUpdVO.set("DHPROC", new Timestamp(System.currentTimeMillis()));
           // impUpdVO.update();

        } catch (Exception e) {
            throw new MGEModelException("Problemas ao importar na tabela detalhe,  Erro: " + e.getMessage(), e);
        }

        contextoAcao.setMensagemRetorno("Arquivo processado com sucesso!");
    }


  //Valida se ja tem registros importado desse aquivo
  	public int getValArq(ContextoAcao contexto, BigDecimal codImp) throws MGEModelException {
  	    QueryExecutor query = contexto.getQuery();
  	    String sql = null;
  	    int valarq = 0;
  	    
  	    sql = "SELECT COUNT(*) COUNT FROM AD_ITESYSTOCK WHERE NUSYTOCK ='" + codImp +"'";

  	    try {
  	      query.nativeSelect(sql);
  	      
  	      if (query.next()) {
  	    	  valarq = query.getInt("COUNT");
  	    	  
  	      }
  	      
  	      query.close();
  	    }
  	    catch (Exception e) {
  	    	throw new MGEModelException("Problemas buscar linha importada, Erro: " + e.getMessage(), e);
  	    }
  	    return valarq;
  	  }
}
