package br.com.confirma;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.ResultSet;
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
import com.sankhya.util.BigDecimalUtil;
import com.sankhya.util.StringUtils;
import com.sankhya.util.TimeUtils;

import br.com.sankhya.extensions.eventoprogramavel.EventoProgramavelJava;
import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.bmp.PersistentLocalEntity;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.event.PersistenceEvent;
import br.com.sankhya.jape.event.TransactionContext;
import br.com.sankhya.jape.sql.NativeSql;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.vo.EntityVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.modelcore.MGEModelException;
import br.com.sankhya.modelcore.auth.AuthenticationInfo;
import br.com.sankhya.modelcore.comercial.BarramentoRegra;
import br.com.sankhya.modelcore.comercial.CentralFaturamento;
import br.com.sankhya.modelcore.comercial.ConfirmacaoNotaHelper;
import br.com.sankhya.modelcore.comercial.LiberacaoAlcadaHelper;
import br.com.sankhya.modelcore.comercial.LiberacaoSolicitada;
import br.com.sankhya.modelcore.util.DynamicEntityNames;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;
import br.com.sankhya.ws.ServiceContext;

public class eventoConfrm implements EventoProgramavelJava {

	public void beforeUpdate(PersistenceEvent arg0) throws Exception {
		// TODO Auto-generated method stub		
		//proibirAlteracao(arg0);		
	}

	public void afterDelete(PersistenceEvent arg0) throws Exception {
		// TODO Auto-generated method stub  
	}

	public void afterInsert(PersistenceEvent arg0) throws Exception {
		
		   
	}

	public void afterUpdate(PersistenceEvent arg0) throws Exception {
		DynamicVO iteNota = (DynamicVO)arg0.getVo();
		EntityFacade dwfEntityFacade = EntityFacadeFactory.getDWFFacade();
		   JdbcWrapper jdbc = dwfEntityFacade.getJdbcWrapper();
		   BigDecimal nuchave = BigDecimalUtil.getValueOrZero((BigDecimal)iteNota.getProperty("NUCHAVE"));
		   BigDecimal codtipoper = BigDecimalUtil.getValueOrZero((BigDecimal)iteNota.getProperty("CODTIPOPER"));
		   
		   NativeSql nativeSql = new NativeSql(jdbc);
		   BigDecimal vlrlib = new BigDecimal(0);
		   BigDecimal vlratual = new BigDecimal(0);
		   BigDecimal usuLib = new BigDecimal(0);
		   
			StringBuilder sqlConf = new StringBuilder();
			sqlConf.append(" SELECT VLRLIBERADO,VLRATUAL,CODUSULIB ");
			sqlConf.append	( " FROM TSILIB WHERE NUCHAVE = "+nuchave);
			System.out.println("A query 1 é:"+sqlConf.toString());
			ResultSet Conf = nativeSql.executeQuery(sqlConf.toString());
			if (Conf.next())
			{
			vlrlib = Conf.getBigDecimal("VLRLIBERADO");
			vlratual = Conf.getBigDecimal("VLRATUAL");
			usuLib = Conf.getBigDecimal("CODUSULIB");
			System.out.println("VLRLEBERADO"+vlrlib+"VLRATUAL"+vlratual);
			}
			
			/*BigDecimal Nunota = new BigDecimal(0);
			String OBS = "";
			String CodP = "";
			StringBuilder sqlNota = new StringBuilder();
			sqlNota.append(" SELECT NUNOTA,OBSERVACAO,CASE WHEN CODPARC IN (158355) THEN 'ALURA'\r\n"
					+ "WHEN CODPARC IN (130308) THEN 'MSTI TECNOLOGIA'\r\n"
					+ "WHEN CODPARC IN (168996,249) THEN 'CLARO'\r\n"
					+ "WHEN CODPARC IN (130309) THEN 'HELP TECH' END AS PARC ");
			sqlNota.append	( " FROM TGFCAB WHERE NUNOTA = "+nuchave);
			System.out.println("A query 1 é:"+sqlNota.toString());
			ResultSet Nota = nativeSql.executeQuery(sqlNota.toString());
			if (Nota.next())
			{
				
				OBS = Nota.getString("OBSERVACAO");
				CodP = Nota.getString("PARC");
			System.out.println("VLRLEBERADO"+vlrlib+"VLRATUAL"+vlratual);
			}*/
			
			
			
			if ((vlrlib.compareTo(vlratual)==0) && usuLib.compareTo(new BigDecimal(482)) == 0)
			{
				confirmarNota(nuchave);
				//enviar(nuchave,CodP,OBS);
			}
		
	}

	
	private static void confirmarNota(BigDecimal nroUnico) throws Exception {
	    try {
	      BarramentoRegra barramentoConfirmacao = BarramentoRegra.build(CentralFaturamento.class, 
	          "regrasConfirmacaoSilenciosa.xml", AuthenticationInfo.getCurrent());
	      barramentoConfirmacao.setValidarSilencioso(true);
	      ConfirmacaoNotaHelper.confirmarNota(nroUnico, barramentoConfirmacao);
	    } catch (Exception e) {
	      e.printStackTrace();
	      throw new Exception(e);
	    } 
	  }
	
	
	
	public void beforeCommit(TransactionContext arg0) throws Exception {
		// TODO Auto-generated method stub
	}

	public void beforeDelete(PersistenceEvent arg0) throws Exception {
		// TODO Auto-generated method stub
	}

	public void beforeInsert(PersistenceEvent arg0) throws Exception {
		// TODO Auto-generated method stub

	}

	
	public static void exibirMensagem(BigDecimal codprod) throws IOException {
		throw new IOException("O produto " + codprod + " não possui fórmula na tela de composição do produto. ");
	}

	public static void exibirMensagem2(BigDecimal existeOp) throws IOException {
		throw new IOException(
				"<br><b>Exclusão não permitida, pois a Solicitação de Cancelamento não foi confirmada.</b><br>");
	}

	public static void exibirMensagem3() throws IOException {
		throw new IOException(
				"<br><b>Alteração/Confirmação não permitida, as ordens de produção dessa nota foram canceladas ou estão em andamento, favor verificar.</b><br>");
	}
	
	
	byte[] pdfUnico;
	public void enviar (BigDecimal Nunota,String cONTA,String OBS) throws Exception {

		String seuEmail = "alerrandro.barreto@centrodoaluminio.com.br,felipe.souza@centrodoaluminio.com.br";
		        
		        Timestamp hoje = TimeUtils.clearTime(TimeUtils.getNow());
		        String NumeroUnicoad = "";
		       // Object param = arg0.getParam("NUMEROUNICO");
		      //  if (param != null) {
		      //      NumeroUnicoad = param.toString(); // garante que não vem só espaço
		    //    }
		      
		        String CONTA = cONTA;
		        String extraInfo = (NumeroUnicoad != null && !NumeroUnicoad.trim().isEmpty()) 
		                ? ", " + NumeroUnicoad 
		                : "";
		        System.out.println("Numeros unicos adicionais são"+NumeroUnicoad);
		        System.out.println("a Conta é:"+CONTA);
		        String Obs = OBS;
		        BigDecimal nunota = Nunota;
		        String subjetct = "Número único: " + nunota + extraInfo + "\n\n" +
		        		"Prezados,<br><br>" +
		                  "Solicitamos o lançamento referente " + Obs + "<br><br>" +
		                  "Att,<br>" +
		                  "Felipe Jhone";
		        
		        try {
					BigDecimal pk = nunota;
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
					
					System.out.println("O erro é :"+ex);}
		        enviarEmailComRelatorio(pdfUnico, subjetct.toCharArray(), "Fatura Mensal:"+CONTA, seuEmail,CONTA);
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
