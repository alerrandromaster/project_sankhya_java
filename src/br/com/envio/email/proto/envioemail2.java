package br.com.envio.email.proto;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.HeaderFooter;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfImportedPage;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfWriter;
import com.sankhya.util.TimeUtils;

import br.com.sankhya.extensions.actionbutton.AcaoRotinaJava;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.extensions.actionbutton.Registro;
import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.bmp.PersistentLocalEntity;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.vo.EntityVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.modelcore.MGEModelException;
import br.com.sankhya.modelcore.util.DynamicEntityNames;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;

public class envioemail2 implements AcaoRotinaJava {
	
	byte[] pdfUnico;
	@Override
	public void doAction(ContextoAcao arg0) throws Exception {
		// TODO Auto-generated method stub
		Registro registros = arg0.getLinhas()[0];
		System.out.println("BOTAO ENVIAR EMAIL");
		 String seuEmail = "alerrandro.barreto@centrodoaluminio.com.br,felipe.souza@centrodoaluminio.com.br,fiscal4@centrodoaluminio.com.br,\r\n"
		 		+ "financeiro1@centrodoaluminio.com.br,\r\n"
		 		+ "margreis.pinheiro@centrodoaluminio.com.br,\r\n"
		 		+ "thayla.souza@centrodoaluminio.com.br,\r\n"
		 		+ "fiscal1@centrodoaluminio.com.br,\r\n"
		 		+ "bruno.vasconcelos@centrodoaluminio.com.br,\r\n"
		 		+ "ronan.morais@centrodoaluminio.com.br,\r\n"
		 		+ "juliana.cabral@centrodoaluminio.com.br,\r\n"
		 		+ "thalia.lima@centrodoaluminio.com.br,\r\n"
		 		+ "rayssa.sena@centrodoaluminio.com.br";
	        
	        Timestamp hoje = TimeUtils.clearTime(TimeUtils.getNow());
	        String NumeroUnicoad = "";
	        Object param = arg0.getParam("NUMEROUNICO");
	        if (param != null) {
	            NumeroUnicoad = param.toString(); // garante que não vem só espaço
	        }
	      
	        String CONTA = arg0.getParam("CONTA").toString();
	        String extraInfo = (NumeroUnicoad != null && !NumeroUnicoad.trim().isEmpty()) 
	                ? ", " + NumeroUnicoad 
	                : "";
	        System.out.println("Numeros unicos adicionais são"+NumeroUnicoad);
	        System.out.println("a Conta é:"+CONTA);
	        String Obs = (String) registros.getCampo("OBSERVACAO");
	        BigDecimal nunota = (BigDecimal) registros.getCampo("NUNOTA");
	        String subjetct = "Número único: " + nunota + extraInfo + "\n\n" +
	        		"Prezados,<br><br>" +
	                  "Solicitamos o lançamento referente " + Obs + "<br><br>" +
	                  "Att,<br>" +
	                  "Felipe Jhone";


	        
	        
	        try {
				BigDecimal pk = (BigDecimal) registros.getCampo("NUNOTA");
				System.out.println("A pk usada é:"+pk);
				Collection<DynamicVO> anexoSistema = JapeFactory.dao(DynamicEntityNames.ANEXO)
						.find("CODATA = ?", pk);
				for (DynamicVO anexos : anexoSistema) {
					String descricao = anexos.asString("DESCRICAO");
					System.out.println("A descrição do Anexo:"+descricao);
					//String chaveanexo = anexos.asString("CONTEUDO");
					//String pathOrig = "/home/mgeweb/repositorio/Sistema/Anexos/AD_IMPFAT/" + chaveanexo;
					//Path path = Paths.get(chaveanexo);
					//System.out.println("O conteudo do PDF x é :"+path);
					
					byte[] fileContent = anexos.asBlob("CONTEUDO");
					addPdfFile(fileContent);
					ByteArrayOutputStream merged = run();
					pdfUnico = merged.toByteArray();
				}		
			} catch (Exception ex) {
				arg0.mostraErro(ex.getMessage());
				System.out.println("O erro é :"+ex);}
	        enviarEmailComRelatorio(pdfUnico, subjetct.toCharArray(), "Fatura Mensal:"+CONTA, seuEmail,CONTA);
	        arg0.setMensagemRetorno("Email Enviado");
	        System.out.println("FIM");
	}
	
	
	
	
	
	
	
	
	 private Collection<byte[]> pdfFiles = new ArrayList<>();
	    private boolean numeration = false;


	    public Collection<byte[]> getPdfFiles() {
	        return pdfFiles;
	    }

	    public boolean isNumeration() {
	        return numeration;
	    }

	    public void setNumeration(boolean numeration) {
	        this.numeration = numeration;
	    }

	    public void addPdfFile(byte[] pdf) {
	        Objects.requireNonNull(pdf, "Não é possível adicionar um PDF nulo.");
	        this.pdfFiles.add(pdf);
	    }

	    public int getSize() {
	        return this.pdfFiles.size();
	    }

	    public void ConcatenatePDF(Collection<byte[]> pdfFiles) {
	        Objects.requireNonNull(pdfFiles, "A coleção de PDFs não pode ser nula.");
	        this.pdfFiles = pdfFiles;
	    }

	    public void setPdfFiles(Collection<byte[]> pdfFiles) {
	        Objects.requireNonNull(pdfFiles, "A coleção de PDFs não pode ser nula.");
	        this.pdfFiles = pdfFiles;
	    }

	    public ByteArrayOutputStream run() throws DocumentException, IOException {
	        ByteArrayOutputStream pdfConcatenated = new ByteArrayOutputStream();
	        int f = 0;
	        Document document = null;
	        PdfContentByte pdfContentByte = null;
	        PdfWriter pdfWriter = null;
	        for (byte[] bytes : this.pdfFiles) {
	            PdfReader reader = new PdfReader(bytes);
	            reader.consolidateNamedDestinations();
	            if (f == 0) {
	                document = new Document(reader.getPageSizeWithRotation(1));
	                pdfWriter = PdfWriter.getInstance(document, pdfConcatenated);
	                document.open();
	                if (isNumeration()) {
	                    HeaderFooter header = new HeaderFooter(new Phrase("Fls.: "), true);
	                    header.setAlignment(HeaderFooter.ALIGN_RIGHT);
	                    header.setBorder(HeaderFooter.NO_BORDER);
	                    document.resetHeader();
	                    document.setHeader(header);
	                }
	                pdfContentByte = pdfWriter.getDirectContent();
	            }
	            int numPages = reader.getNumberOfPages();
	            for (int i = 1; i <= numPages; i++) {
	                document.setPageSize(reader.getPageSizeWithRotation(i));
	                document.newPage();
	                PdfImportedPage page = pdfWriter.getImportedPage(reader, i);
	                int rotation = reader.getPageRotation(i);
	                if (rotation == 90 || rotation == 270) {
	                    pdfContentByte.addTemplate(page, 0, -1f, 1f, 0, 0, reader.getPageSizeWithRotation(i).getHeight());
	                } else {
	                    pdfContentByte.addTemplate(page, 1f, 0, 0, 1f, 0, 0);
	                }
	            }
	            f++;
	        }
	        if (document != null)
	            document.close();
	        return pdfConcatenated;
	    }
	
	
	
	
	 public void enviarEmailComRelatorio(byte[] relatorio, char[] mensagem, String assunto, String email,String nomearquivo) throws Exception {
	        BigDecimal codigoFila = BigDecimal.ZERO;
	        BigDecimal nuAnexoRelatorio = BigDecimal.ZERO;
	        JapeSession.SessionHandle hnd = null;
	        try {
	            hnd = JapeSession.open();
	            EntityFacade dwfFacade = EntityFacadeFactory.getDWFFacade();

	            // Email
	            EntityVO entityVO = dwfFacade.getDefaultValueObjectInstance("MSDFilaMensagem");
	            DynamicVO dynamicVO = (DynamicVO) entityVO;
	            dynamicVO.setProperty("ASSUNTO", assunto);
	            dynamicVO.setProperty("DTENTRADA", TimeUtils.getNow());
	            dynamicVO.setProperty("STATUS", "Pendente");
	            dynamicVO.setProperty("EMAIL", email);
	            dynamicVO.setProperty("TENTENVIO", new BigDecimal(1));
	            dynamicVO.setProperty("MENSAGEM", mensagem);
	            dynamicVO.setProperty("TIPOENVIO", "E");
	            dynamicVO.setProperty("MAXTENTENVIO", new BigDecimal(3));
	            dynamicVO.setProperty("CODSMTP", new BigDecimal(3));
	            dynamicVO.setProperty("CODCON", new BigDecimal(0));
	            PersistentLocalEntity createEntity = dwfFacade.createEntity("MSDFilaMensagem", entityVO);
	            DynamicVO save = (DynamicVO) createEntity.getValueObject();
	            codigoFila = save.asBigDecimal("CODFILA");


	            // Cria anexo do relatorio
	            entityVO = dwfFacade.getDefaultValueObjectInstance("AnexoMensagem");
	            dynamicVO = (DynamicVO) entityVO;
	            dynamicVO.setProperty("NOMEARQUIVO", nomearquivo+".pdf");
	            dynamicVO.setProperty("TIPO", "application/pdf");
	            dynamicVO.setProperty("ANEXO", relatorio);
	            createEntity = dwfFacade.createEntity("AnexoMensagem", entityVO);
	            save = (DynamicVO) createEntity.getValueObject();
	            nuAnexoRelatorio = save.asBigDecimal("NUANEXO");

	            // Fila de mensagem
	            entityVO = dwfFacade.getDefaultValueObjectInstance("AnexoPorMensagem");
	            dynamicVO = (DynamicVO) entityVO;
	            dynamicVO.setProperty("CODFILA", codigoFila);
	            dynamicVO.setProperty("NUANEXO", nuAnexoRelatorio);
	            createEntity = dwfFacade.createEntity("AnexoPorMensagem", entityVO);
	            save = (DynamicVO) createEntity.getValueObject();
	        } catch (Exception e) {
	            throw new MGEModelException("Erro ao tentar incluir os dados dentro do e-mail!" + e.getMessage());
	        } finally {
	            JapeSession.close(hnd);
	        }
	    }
	}
	
	
	
	


