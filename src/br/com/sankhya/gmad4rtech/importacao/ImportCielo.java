package br.com.sankhya.gmad4rtech.importacao;

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

public class ImportCielo implements AcaoRotinaJava {
    // Autor: Raul 4rTech
    // Data : 06/09/2024
    // Objt : Importar .csv Cielo para tabela 

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

        final JapeWrapper impDAO = JapeFactory.dao("AD_IMPCIELO");
        final DynamicVO impVo = impDAO.findOne("NUIMP = ?", new Object[]{codImp});
        byte[] blobData = impVo.asBlob("ARQUIVO");

        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(blobData);
             Scanner scanner = new Scanner(inputStream)) {

        	int nlinha = 0;

            // Desconsidera as primeiras 7 linhas (cabeçalho e metadados)
            for (int i = 0; i < 22; i++) {
            	nlinha++;
                if (scanner.hasNextLine()) {
                    scanner.nextLine();
                } else {
                    throw new IllegalArgumentException("O arquivo contém menos de 7 linhas, impossível processar.");
                }
            }

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                System.out.println("Linha inicial"+line);
                String[] tokens = line.split(";");

                if (tokens.length >= 48 && tokens.length <= 57) {
	                    nlinha++;
	                    String dtpag = tokens[0];
	                    String codestab = tokens[2];
	                    String bandeira = tokens[5];
	                    /*switch (bandeira) {
	                    case "001":
	                        bandeira = "VISA";
	                        break;
	                    case "002":
	                        bandeira = "MASTERCARD";
	                        break;
	                    case "003":
	                        bandeira = "AMERICAN EXPRESS";
	                        break;
	                    case "007":
	                        bandeira = "ELO";
	                        break;
	                    default:
	                        // Mantém o valor original se não for nenhum dos códigos
	                        break;
	                }*/
	                    String nrocart = tokens[21];
	                    String codaut = tokens[15];
	                    String nsu = tokens[16];
	                    System.out.println("vlrbruto_cielo"+tokens[7].toString());
	                    String vlrbruto = tokens[6]
	                    	    .replace("R$", "")                    // Remove "R$"
	                    	    .replace("\u00A0", "")                // Remove espaço especial (0xA0)
	                    	    .replace("-", "")                     // Remove sinal negativo
	                    	    .replace(".", "")                     // Remove ponto de milhar
	                    	    .replace(",", ".")                    // Substitui vírgula por ponto
	                    	    .replace("\"", "")                    // Remove aspas duplas
	                    	    .trim();    // Substitui vírgula por ponto
	                    String vlrtaxa = tokens[7]
	                    	    .replace("R$", "")                    // Remove "R$"
	                    	    .replace("\u00A0", "")                // Remove espaço especial (0xA0)
	                    	    .replace("-", "")                     // Remove sinal negativo temporariamente
	                    	    .replace(".", "")                     // Remove ponto de milhar
	                    	    .replace(",", ".")                    // Substitui vírgula por ponto
	                    	    .replace("\"", "")                    // Remove aspas duplas
	                    	    .trim();           
	                    if (tokens[7].startsWith("-")) {
	                        vlrtaxa = "-" + vlrtaxa;
	                    }
	                    System.out.println("vlrtaxa"+vlrtaxa);
	                    String dtvenda = tokens[11];
	                    String codvenda = tokens[17];
	                    String numparcela = tokens[26];
	                    String numtotparcela = tokens[27];
	                    String numequip = tokens[31];
	                    String perctaxa = tokens[35].replace(",", ".").replace("\"", "").replace("%", "");
	                    String codbco = tokens[48];
	                    String codage = tokens[49];
	                    String codctabcoo = tokens[50];
						String formpag = tokens[4];
						
						System.out.println("dtpag cielo"+dtpag);
	
	
	                    Registro proc = contextoAcao.novaLinha("AD_IMPLINCIELO");
	                    proc.setCampo("NUIMP", codImp);
	                    proc.setCampo("NUSEQ", nlinha);
	                    proc.setCampo("DTPAG", new SimpleDateFormat("dd/MM/yyyy").format(new SimpleDateFormat("dd/MM/yyyy").parse(dtpag)));
	                    proc.setCampo("CODESTAB", codestab);
	                    proc.setCampo("BANDEIRA", bandeira);
	                    proc.setCampo("NROCART", nrocart);
	                    proc.setCampo("CODAUT", codaut);
	                    proc.setCampo("NSU", nsu);
	                    proc.setCampo("VLRBRUTO", vlrbruto);
	                    System.out.println("VlR BRUTO"+vlrbruto);
	                    System.out.println("VlR TAxa"+vlrtaxa);
	                    BigDecimal taxa = new BigDecimal(vlrtaxa);
	                    proc.setCampo("VLRTAXA", taxa.multiply(new BigDecimal(-1)));
	                    proc.setCampo("DTVENDA", new SimpleDateFormat("dd/MM/yyyy").parse(dtvenda));
	                    proc.setCampo("CODVENDA", codvenda);
	                    proc.setCampo("NUMPARCELA", numparcela);
	                    proc.setCampo("NUMTOTPARCELA", numtotparcela);
	                    proc.setCampo("NUMEQUIP", numequip);
	                    proc.setCampo("PERCTAXA", perctaxa);
	                    proc.setCampo("CODBCO", codbco);
	                    proc.setCampo("CODAGE", codage);
	                    proc.setCampo("CODCTABCO", codctabcoo);
						proc.setCampo("FORMPAG",formpag);
	
	                    proc.save();
	                } else {
	                    throw new IllegalArgumentException("<br><b>O número de campos na linha é inválido</b><br> Linha: " + nlinha);
	                }
            	}
            
            //Atualiza data e hora do procesamento
            FluidUpdateVO impUpdVO = impDAO.prepareToUpdate(impVo);
            impUpdVO.set("DHPROC", new Timestamp(System.currentTimeMillis()));
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
