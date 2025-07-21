package src.gmad;

import java.math.BigDecimal;

import org.joda.time.LocalDateTime;

import com.google.gson.JsonObject;

import br.com.sankhya.extensions.actionbutton.AcaoRotinaJava;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.extensions.actionbutton.Registro;
import br.com.sankhya.graphql.types.extensive.Query;
import br.com.sankhya.jape.ejbcontainer.EntityManager;
import br.com.sankhya.nfe.io.File;
import br.com.sankhya.nfe.io.FileOutputStream;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.extensions.actionbutton.QueryExecutor;
import br.com.sankhya.extensions.actionbutton.Registro;
import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.sql.NativeSql;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.vo.EntityVO;
import br.com.sankhya.modelcore.auth.AuthenticationInfo;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;
import br.com.sankhya.extensions.actionbutton.AcaoRotinaJava;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.extensions.actionbutton.QueryExecutor;
import br.com.sankhya.extensions.actionbutton.Registro;
import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.core.JapeSession.SessionHandle;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.sql.NativeSql;
import br.com.sankhya.jape.util.JapeSessionContext;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.vo.EntityVO;
import br.com.sankhya.jape.vo.PrePersistEntityState;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;
import br.com.sankhya.jape.wrapper.fluid.FluidCreateVO;
import br.com.sankhya.modelcore.MGEModelException;
import br.com.sankhya.modelcore.auth.AuthenticationInfo;
import br.com.sankhya.modelcore.comercial.BarramentoRegra;
import br.com.sankhya.modelcore.comercial.CentralFaturamento;
import br.com.sankhya.modelcore.comercial.ConfirmacaoNotaHelper;
import br.com.sankhya.modelcore.comercial.centrais.CACHelper;
import br.com.sankhya.modelcore.facades.avisossistema.AvisoSistemaHelper.AvisoSistema;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;
import com.sankhya.util.BigDecimalUtil;
import com.sankhya.util.StringUtils;
import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import br.com.sankhya.ws.ServiceContext;
import java.net.URLEncoder;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.codec.binary.Base64;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.Collection;

public class imprimir implements AcaoRotinaJava {
	
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
	    
	    public class DanfeDAO {
	        SankhyaService sankhyaService = new SankhyaService();
	        public String gerarDanfePDF(EntityManager entityManager, int nuNota) {
	            String dirReports = "c:/tmp";
	            byte[] pdf = getPdfNotaFiscal(entityManager, nuNota);
	            if (null == pdf) {
	                this.gerarPdfDanfeSankhya(nuNota);
	                pdf = getPdfNotaFiscal(entityManager, nuNota);
	                if (null == pdf) {
	                    System.out.println("O Sankhya não gerou o DANFE na tabela TGFPDF");
	                    return null;
	                }
	            }

	            String sequencia = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_hhmmssSSS"));
	            String fileName = dirReports + File.separator + "DanfeNf" + nuNota + "_" + sequencia + ".pdf";

	            File file = new File(fileName);
	            try ( OutputStream fileOut = new FileOutputStream(file)) {
	                fileOut.write(pdf);
	            } catch (FileNotFoundException ex) {
	                System.out.println("Arquivo não encontrado " + file.getAbsolutePath());
	                System.out.println(ex);
	            } catch (IOException ex) {
	                System.out.println("Erro de escrita do arquivo " + file.getAbsolutePath());
	                System.out.println(ex);
	            }

	            System.out.println("Danfe Salvo " + file.getAbsolutePath());
	            return file.getAbsolutePath();
	        }

	        private byte[] getPdfNotaFiscal(EntityManager em, int nuNota) {

	            StringBuilder query = new StringBuilder();
	            query.append("SELECT PDFDANFESIMPLIF FROM TGFPDF");
	            query.append(" WHERE NUNOTA=?nuNota");

	            Query q = em.createNativeQuery(query.toString());
	            q.setParameter("nuNota", nuNota);

	            byte[] pdf = null;
	            try {
	                pdf = (byte[]) q.getSingleResult();
	            } catch (Exception e) {
	                System.out.println("Erro ao buscar o PDF no banco da Nota Fiscal " + nuNota);
	                System.out.println(e);
	            }

	            return pdf;
	        }

	        private void gerarPdfDanfeSankhya(int nuNota) {

	            String tmp = "\"notas\": { \"pedidoWeb\": \"false\", \"gerarpdf\": \"true\", \"nota\": { \"nuNota\": \"@@nunota@@\", \"tipoImp\": \"13\" } }";
	            String jsonStr = tmp.replace("@@nunota@@", "" + nuNota);

	            try {
	                JsonObject json = sankhyaService.sankhyaInstanceJson("ImpressaoNotasSP.imprimeDocumentos", "mge", jsonStr);
	                System.out.println("Retorno Sankhya: " + json.toString());
	            } catch (Exception e) {
	                System.out.println("Erro no Sankhya ao gerar o PDF da Nota Fiscal.");
	                System.out.println(e);
	            }
	        }
	    }
	   
	    public class SWServiceInvoker {
	    	private String	domain;
	    	private String	user;
	    	private String	pass;
	    	private boolean	debug;
	    	private boolean	silentMode;
	    	private boolean criptedPass;
	    	private boolean useJson;

	    	public SWServiceInvoker(String domain, String user, String pass) {
	    		this.domain = domain;
	    		this.user = user;
	    		this.pass = pass;
	    	}

	    	public void setSilentMode(boolean silentMode) {
	    		this.silentMode = silentMode;
	    	}

	    	public void setDebugMode() {
	    		this.debug = true;
	    	}

	    	public Document call(String serviceName, String module, String body, String jsessionid) throws Exception {
	    		useJson = false;
	    		URLConnection conn = openConn(serviceName, module, jsessionid);
	    		
	    		Document docResp = (Document) callService(conn, body, serviceName);

	    		return docResp;
	    	}

	    	public Document call(String serviceName, String module, String body) throws Exception {
	    		useJson = false;
	    		String jsessionid = doLogin();

	    		URLConnection conn = openConn(serviceName, module, jsessionid);

	    		Document docResp = (Document) callService(conn, body, serviceName);

	    		doLogout(jsessionid);

	    		return docResp;
	    	}
	    	
	    	public JsonObject callAsJson(String serviceName, String module, String body) throws Exception {
	    		useJson = true;
	    		String jsessionid = doLogin();
	    		
	    		URLConnection conn = openConn(serviceName, module, jsessionid);
	    		
	    		JsonObject docResp = (JsonObject) callService(conn, body, serviceName);
	    		
	    		doLogout(jsessionid);
	    		
	    		return docResp;
	    	}

	    	public String doLogin() throws Exception {
	    		URLConnection conn = openConn("MobileLoginSP.login", "mge", null);
	    		
	    		String session = null;
	    		if (useJson) {
	    			StringBuffer bodyBuf = new StringBuffer();
	    			if (getEmptyAsNull(user) != null) {
	    				bodyBuf.append(" NOMUSU: { $: ").append(user).append(" }, ");
	    			} else {
	    				bodyBuf.append(" NOMUSU: {}, ");
	    			}
	    			
	    			String interno = criptedPass ? "INTERNO2" : "INTERNO";
	    			
	    			if (getEmptyAsNull(pass) != null) {
	    				bodyBuf.append(interno).append(": { $: ").append(pass).append(" }");
	    			} else {
	    				bodyBuf.append(interno).append(": {}");
	    			}
	    			
	    			JsonObject docResp = (JsonObject) callService(conn, bodyBuf.toString(), "MobileLoginSP.login");
	    			if (docResp.has("responseBody") && docResp.getAsJsonObject("responseBody").has("jsessionid")) {
	    				session = docResp.getAsJsonObject("responseBody").getAsJsonObject("jsessionid").get("$").getAsString().trim();
	    			}
	    		} else {
	    			StringBuffer bodyBuf = new StringBuffer();
	    			
	    			bodyBuf.append("<NOMUSU>").append(user).append("</NOMUSU>");
	    			
	    			if(criptedPass) {
	    				bodyBuf.append("<INTERNO2>").append(pass).append("</INTERNO2>");
	    			}else {
	    				bodyBuf.append("<INTERNO>").append(pass).append("</INTERNO>");
	    			}
	    			
	    			Document docResp = (Document) callService(conn, bodyBuf.toString(), "MobileLoginSP.login");
	    			
	    			Node jsessionNode = (Node) xpath(docResp, "//jsessionid", XPathConstants.NODE);
	    			
	    			session = jsessionNode.getTextContent().trim();
	    		}

	    		return session;
	    	}

	    	public void doLogout(String jsessionid) throws Exception {
	    		try {
	    			URLConnection conn = openConn("MobileLoginSP.logout", "mge", jsessionid);

	    			callService(conn, null, "MobileLoginSP.logout");
	    		} catch (Exception e) {
	    			e.printStackTrace(); // pode ser ignorado
	    		}
	    	}

	    	private void checkResultStatus(Node sr) throws Exception {
	    		Node statusNode = sr.getAttributes().getNamedItem("status");

	    		String status = statusNode.getTextContent().trim();

	    		if (!"1".equals(status) && !silentMode) {
	    			String msg = getChildNode("statusMessage", sr).getTextContent();
	    			throw new Exception(decodeB64(msg.trim()));
	    		}
	    	}

	    	private Node getChildNode(String name, Node parent) {
	    		NodeList l = parent.getChildNodes();

	    		for (int i = 0; i < l.getLength(); i++) {
	    			Node n = l.item(i);

	    			if (n.getNodeName().equalsIgnoreCase(name)) {
	    				return n;
	    			}
	    		}

	    		return null;
	    	}

	    	private String decodeB64(String s) {
	    		return new String(Base64.decodeBase64(s.getBytes()));
	    	}

	    	private Object xpath(Document d, String query, QName type) throws Exception {
	    		XPath xp = XPathFactory.newInstance().newXPath();

	    		XPathExpression xpe = xp.compile(query);
	    		return xpe.evaluate(d, type);
	    	}

	    	private void printNode(Node n) {
	    		System.out.println(n.toString());

	    		NodeList l = n.getChildNodes();

	    		if (l != null && l.getLength() > 0) {
	    			for (int i = 0; i < l.getLength(); i++) {
	    				printNode(l.item(i));
	    			}
	    		}
	    	}

	    	private Object callService(URLConnection conn, String body, String serviceName) throws Exception {
	    		OutputStream out = null;
	    		InputStream inp = null;

	    		try {
	    			out = conn.getOutputStream();
	    			OutputStreamWriter wout = new OutputStreamWriter(out, "ISO-8859-1");

	    			String requestBody = null;
	    			if (useJson) {
	    				requestBody = buildRequestBodyAsJson(body, serviceName);
	    			} else {
	    				requestBody = buildRequestBody(body, serviceName);
	    			}

	    			if (debug) {
	    				System.out.println(requestBody);
	    			}
	    			wout.write(requestBody);

	    			wout.flush();

	    			inp = conn.getInputStream();

	    			if (useJson) {
	    				JsonObject doc = readInputStreamAsJsonObject(inp);
	    				
	    				if (!doc.has("responseBody")) {
	    					throw new Exception("Json de resposta não possui dados de resposta.");
	    				}
	    				
	    				return doc;
	    				
	    			} else {
	    				DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	    				DocumentBuilder db = dbf.newDocumentBuilder();
	    				
	    				Document doc = null;
	    				NodeList nodes = null;
	    				
	    				try {
	    					doc = db.parse(inp);
	    					
	    					if(debug) {
	    						printDocument(doc);
	    					}
	    					
	    					nodes = doc.getElementsByTagName("serviceResponse");
	    					
	    					if(debug) {
	    						printNode(nodes.item(0));
	    					}
	    					
	    					if (nodes == null || nodes.getLength() == 0) {
	    						throw new Exception("XML de resposta não possui um elemento de resposta");
	    					}
	    				} catch (Exception e) {
	    					Exception error = new Exception("Erro ao interpretar resposta do servidor");
	    					error.initCause(e);
	    					throw error;
	    				}
	    				
	    				checkResultStatus(nodes.item(0));
	    				
	    				return doc;
	    			}
	    		} finally {
	    			if (out != null) {
	    				try {
	    					out.close();
	    				} catch (Exception e) {
	    				}
	    			}

	    			if (inp != null) {
	    				try {
	    					inp.close();
	    				} catch (Exception e) {
	    				}
	    			}
	    		}
	    	}

	    	private String buildRequestBody(String body, String serviceName) {
	    		StringBuffer buf = new StringBuffer();

	    		buf.append("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n");
	    		buf.append("<serviceRequest serviceName=\"").append(serviceName).append("\">\n");
	    		buf.append("<requestBody>\n");
	    		buf.append(body == null ? "" : body);
	    		buf.append("</requestBody>\n");
	    		buf.append("</serviceRequest>");

	    		return buf.toString();
	    	}
	    	
	    	private String buildRequestBodyAsJson(String body, String serviceName) {
	    		StringBuffer buf = new StringBuffer();
	    		
	    		buf.append(" {");
	    		buf.append("    serviceName: ").append(serviceName).append(", ");
	    		buf.append("    requestBody: {");
	    		buf.append(body);
	    		buf.append("    }");
	    		buf.append(" }");
	    		
	    		return buf.toString();
	    	}

	    	private URLConnection openConn(String serviceName, String module, String sessionID) throws Exception {
	    		StringBuffer buf = new StringBuffer();

	    		buf.append(domain).append(domain.endsWith("/") ? "" : "/").append(module == null ? "mge" : module).append("/service.sbr");

	    		/*if (sessionID != null) {
	    			buf.append(";JSESSIONID=").append(sessionID);
	    		}*/

	    		buf.append("?serviceName=").append(serviceName);

	    		if (sessionID != null) {
	    			buf.append("&mgeSession=").append(sessionID);
	    		}
	    		
	    		if (useJson) {
	    			buf.append("&outputType=json");
	    		}

	    		URL u = new URL(buf.toString());

	    		URLConnection uc = u.openConnection();
	    		HttpURLConnection connection = (HttpURLConnection) uc;

	    		connection.setDoOutput(true);
	    		connection.setDoInput(true);
	    		connection.setRequestMethod("POST");
	    		
	    		if (useJson) {
	    			connection.setRequestProperty("content-type", "application/json");
	    		} else {
	    			connection.setRequestProperty("content-type", "text/xml");
	    		}
	    		
	    		if(sessionID != null) {
	    			connection.setRequestProperty("Cookie", "JSESSIONID=" + sessionID);
	    		}
	    		
	    		connection.setRequestProperty("User-Agent", "SWServiceInvoker");

	    		return connection;
	    	}

	    	public String getDomain() {
	    		return domain;
	    	}
	    	
	    	private void printDocument(Document doc) throws Exception {
	    		Transformer transformer = TransformerFactory.newInstance().newTransformer();
	    		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
	    		StreamResult result = new StreamResult(new StringWriter());
	    		DOMSource source = new DOMSource(doc);
	    		transformer.transform(source, result);
	    		String xmlString = result.getWriter().toString();
	    		System.out.println("----inicio---");
	    		System.out.println(xmlString);
	    		System.out.println("----fim-----");
	    	}

	    	public void setCriptedPass(boolean criptedPass) {
	    		this.criptedPass = criptedPass;
	    	}
	    	
	    	private JsonObject readInputStreamAsJsonObject(InputStream is) throws IOException {
	    		JsonParser parser = new JsonParser();
	    		JsonObject bundle = null;
	    		FileInputStream fileStream = null;
	    		Reader reader = null;
	    		
	    		try {
	    			reader = new InputStreamReader(is, Charset.forName("UTF-8"));
	    			bundle = (JsonObject) parser.parse(reader);
	    		} catch (Exception e) {
	    			if(!(e instanceof FileNotFoundException)) {
	    				e.printStackTrace();
	    			}
	    		} finally {
	    			if (fileStream != null) {
	    				fileStream.close();	
	    			}
	    			
	    			if (reader != null) {
	    				reader.close();
	    			}
	    		}
	    		
	    		return bundle;
	    	}
	    	
	    	public String getEmptyAsNull(String s) {
	    		if(s == null || s.length() == 0){
	    			return null;
	    		}
	    		
	    		String trimed = s.trim();
	    		
	    		return trimed.length() == 0 ? null : trimed;
	    	}
	    }
	    

}
