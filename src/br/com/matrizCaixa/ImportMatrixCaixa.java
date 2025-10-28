package br.com.matrizCaixa;

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;


import br.com.sankhya.extensions.actionbutton.AcaoRotinaJava;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.extensions.actionbutton.QueryExecutor;
import br.com.sankhya.extensions.actionbutton.Registro;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;
import br.com.sankhya.jape.wrapper.fluid.FluidUpdateVO;
import br.com.sankhya.modelcore.MGEModelException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ImportMatrixCaixa implements AcaoRotinaJava {


    @Override
    public void doAction(final ContextoAcao contextoAcao) throws Exception {
        final Registro[] registros = contextoAcao.getLinhas();

        if (registros.length == 0) {
            throw new MGEModelException("Linha não selecionada!");
        }

        BigDecimal codImp = (BigDecimal) registros[0].getCampo("NUSYTOCK");

        final JapeWrapper impDAO = JapeFactory.dao("AD_IMPCAIXAMAT");
        final DynamicVO impVo = impDAO.findOne("NUSYTOCK = ?", new Object[]{codImp});
        byte[] blobData = impVo.asBlob("ARQUIVO");

        String lastNat1 = "";
        
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(blobData)) {
            Workbook workbook = Workbook.getWorkbook(inputStream);
            Sheet sheet = workbook.getSheet(0); // primeira aba
            
            int rows = sheet.getRows();
            int cols = sheet.getColumns();
            

            int nlinha = 0;
            String Dt ;
            String Natureza1;
            String Natureza2;
            String Valor ;
            

            for (int r = 3; r < rows; r++) { // começa na linha 1 (pula cabeçalho)
                nlinha++;
                  Dt = sheet.getCell(0, r).getContents();
                  Natureza1 = sheet.getCell(1, r).getContents();
                  Natureza2 = sheet.getCell(2, r).getContents();
                  Valor = sheet.getCell(3, r).getContents();
                  
                  
                  if (!Natureza1.isEmpty()) {
                      lastNat1 = Natureza1.trim();
                  } else if (!lastNat1.isEmpty()) {
                      Natureza1 = lastNat1;
                  }
                  
	                    Registro proc = contextoAcao.novaLinha("AD_ITECAXMAT");
	                    proc.setCampo("NUIMP", codImp);
	                    proc.setCampo("NUSEQ", nlinha);
	                    proc.setCampo("NAT1",Natureza1 );
	                    proc.setCampo("NAT2",Natureza2);
	                    proc.setCampo("VALOR", Valor);
	                    
	                    proc.save();
            }
	

            
            //Atualiza data e hora do procesamento
            FluidUpdateVO impUpdVO = impDAO.prepareToUpdate(impVo);
            impUpdVO.update();

        } catch (Exception e) {
            throw new MGEModelException("Problemas ao importar na tabela detalhe,  Erro: " + e.getMessage(), e);
        }

        contextoAcao.setMensagemRetorno("Arquivo processado com sucesso!");
    }

    
    private static boolean isXLSX(byte[] data) {
        return data != null && data.length >= 2 && data[0] == 'P' && data[1] == 'K'; // "PK" -> ZIP
    }
    private static String getCell(List<String> row, int idx) {
        if (row == null || idx < 0 || idx >= row.size()) return null;
        String v = row.get(idx);
        return v == null ? null : v.trim();
    }
    private static boolean isEmpty(String s) {
        return s == null || s.trim().isEmpty();
    }

    
   
   }
