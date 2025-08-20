package br.com.matrizCaixa;

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.util.Scanner;

import br.com.sankhya.extensions.actionbutton.AcaoRotinaJava;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.extensions.actionbutton.QueryExecutor;
import br.com.sankhya.extensions.actionbutton.Registro;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;
import br.com.sankhya.jape.wrapper.fluid.FluidUpdateVO;
import br.com.sankhya.modelcore.MGEModelException;

public class ImportPanilha implements AcaoRotinaJava {


    @Override
    public void doAction(final ContextoAcao contextoAcao) throws Exception {
        final Registro[] registros = contextoAcao.getLinhas();

        if (registros.length == 0) {
            throw new MGEModelException("Linha não selecionada!");
        }

        BigDecimal codImp = (BigDecimal) registros[0].getCampo("NUIMP");

        // Valida se arquivo já foi processado
       

        final JapeWrapper impDAO = JapeFactory.dao("AD_ACOMPPUXADA");
        final DynamicVO impVo = impDAO.findOne("NUIMP = ?", new Object[]{codImp});
        byte[] blobData = impVo.asBlob("ARQUIVO");

        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(blobData);
             Scanner scanner = new Scanner(inputStream)) {

        	int nlinha = 0;

            // Desconsidera as primeiras 7 linhas (cabeçalho e metadados)
            for (int i = 0; i < 1; i++) {
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

                for (int i = 0; i < tokens.length; i++) {
                    System.out.println("Token para o log é " + i + ": " + tokens[i]);
                }
                
                if (tokens.length >= 0 && tokens.length <= 19) {
	                    nlinha++;
	                   
	                    System.out.println("Número de tokens: " + tokens.length);
	                    String Dt = tokens[0];
	                    String Seq = tokens[1];
	                    String Placa = tokens[2];
	                    String Motorista = tokens[3];
	                    String Origem = tokens[4];
	                    String Destino = tokens[5];
	                    String Almox_origem = tokens[6];
	                    String Almox_destino = tokens[7];
	                    String Hora_saida = tokens[8];
	                    String Hora_Chegada = tokens[9];
	                    String Tempo_Chegada = tokens[10];
	                    String Inicio_Desova = tokens[11];
	                    String Fim_Desova = tokens[12];
	                    String Tempo_Desova = tokens[13];
	                    String Qtd_Nf = tokens[14];
	                    String Volumes = tokens[15];
	                    String TempoporVolume = tokens[16];
	                    String ValorNf = tokens[17];
	                    String Km = tokens[18];
	                    String obs = (tokens.length > 19) ? tokens[19] : null;
	                    
	                    System.out.println("as linhas"+"--"+Dt+"--"+Seq+"--"+Placa+"--"+Motorista+"--"+Origem+"--"+"--"+Destino
	                    		+Almox_origem+"--"+Almox_destino+"--"+"--"+Hora_saida+"-"+Hora_Chegada+"--"+Tempo_Chegada+"--"+"--"+Inicio_Desova);
	                    Registro proc = contextoAcao.novaLinha("AD_ACOMPDETAIL");
	                    proc.setCampo("NUIMP", codImp);
	                    proc.setCampo("NUSEQ", nlinha);
	                    proc.setCampo("PLACA", Placa);
	                    proc.setCampo("MOTORISTA", Motorista);
	                    proc.setCampo("ORIGEM", Origem);
	                    proc.setCampo("DESTINO", Destino);
	                    proc.setCampo("ALMOXORIGEM", Almox_origem);
	                    proc.setCampo("ALMOXDESTINO",Almox_destino );
	                    proc.setCampo("HORASAIDA",Hora_saida );
	                    proc.setCampo("HORACHEGADA",Hora_Chegada );
	                    proc.setCampo("TEMPOCHEGADA",Tempo_Chegada );
	                    proc.setCampo("INICIODESOVA",Inicio_Desova );
	                    proc.setCampo("FIMDESOVA",Fim_Desova );
	                    proc.setCampo("TEMPODESOVA",Tempo_Desova );
	                    proc.setCampo("QTDNF",Qtd_Nf );
	                    proc.setCampo("VOLUMES",Volumes );
	                    proc.setCampo("TEMPOPORVOLUME",TempoporVolume );
	                    proc.setCampo("VALORNF",ValorNf );
	                    proc.setCampo("KM",Km );
	                   proc.setCampo("OBS",obs );
	                    proc.setCampo("DT",Dt );
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
