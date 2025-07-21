package br.importacaocvs;

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

public class ImportPanilha implements AcaoRotinaJava {


    @Override
    public void doAction(final ContextoAcao contextoAcao) throws Exception {
        final Registro[] registros = contextoAcao.getLinhas();

        if (registros.length == 0) {
            throw new MGEModelException("Linha não selecionada!");
        }

        BigDecimal codImp = (BigDecimal) registros[0].getCampo("NUIMP");

        // Valida se arquivo já foi processado
        if (getValArq(contextoAcao, codImp) >= 1) {
            throw new IllegalArgumentException("<b>Arquivo já processado!</b><br>Exclua o lançamento se deseja processar novamente.");
        }

        final JapeWrapper impDAO = JapeFactory.dao("AD_IMPORTWMS");
        final DynamicVO impVo = impDAO.findOne("NUIMP = ?", new Object[]{codImp});
        byte[] blobData = impVo.asBlob("ARQ");

        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(blobData);
             Scanner scanner = new Scanner(inputStream)) {

        	int nlinha = 0;

            // Desconsidera as primeiras 7 linhas (cabeçalho e metadados)
            for (int i = 0; i < 3; i++) {
            	nlinha++;
                if (scanner.hasNextLine()) {
                    System.out.println("A LINHA ATUAL é "+scanner.nextLine().toString());
                	scanner.nextLine();
                    
                } else {
                    throw new IllegalArgumentException("O arquivo contém menos de 7 linhas, impossível processar.");
                }
            }

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] tokens = line.split(";");

                if (tokens.length >= 0 && tokens.length <= 10) {
	                    nlinha++;
	                    String descrend = tokens[0];
	                    String codprod = tokens[1];
	                    String ativo = tokens[2];
	                   // String dtinicio = tokens[20];
	                   // String dtfim = tokens[15];
	                   // String estmin = tokens[16];
	                   // String estmax = tokens[6];
	                    String codvol = tokens[7];
	                    String ESTMINVOLPAD = tokens[8];
	                    String ESTMAXVOLPAD = tokens[9];
	                    System.out.println("as linhas"+"--"+descrend+"--"+codprod+"--"+ativo+"--"+codvol+"--"+ESTMINVOLPAD+"--"+"--"+ESTMAXVOLPAD);
	                    Registro proc = contextoAcao.novaLinha("AD_IMPDETAIL");
	                    proc.setCampo("DESCREND", descrend);
	                    proc.setCampo("CODPROD", codprod);
	                    proc.setCampo("ATIVO", ativo);
	                    proc.setCampo("ESTMINVOLPAD", ESTMINVOLPAD);
	                    proc.setCampo("ESTMAXVOLPAD", ESTMAXVOLPAD);
	                    proc.setCampo("CODVOL", codvol);
	
	                    proc.save();
	                } else {
	                    throw new IllegalArgumentException("<br><b>O número de campos na linha é inválido</b><br> Linha: " + nlinha);
	                }
            	}
            
            //Atualiza data e hora do procesamento
            FluidUpdateVO impUpdVO = impDAO.prepareToUpdate(impVo);
            impUpdVO.update();

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
  	    
  	    sql = "SELECT COUNT(*) COUNT FROM AD_IMPLINCIELO WHERE NUIMP ='" + codImp +"'";

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
