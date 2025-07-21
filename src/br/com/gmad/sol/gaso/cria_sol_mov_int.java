package br.com.gmad.sol.gaso;
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
import br.com.sankhya.modelcore.comercial.LiberacaoAlcadaHelper;
import br.com.sankhya.modelcore.comercial.LiberacaoSolicitada;
import br.com.sankhya.modelcore.comercial.centrais.CACHelper;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;

import com.hazelcast.org.apache.calcite.linq4j.tree.SwitchCase;
import com.sankhya.util.BigDecimalUtil;
import com.sankhya.util.StringUtils;
import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;

import br.com.sankhya.ws.ServiceContext;
import java.net.URLEncoder;


public class cria_sol_mov_int  implements AcaoRotinaJava {
	
	private String msgErro = "";
	  
	  private BigDecimal nuNota = new BigDecimal(0);
	  
	  private BigDecimal nuNotaPedido = new BigDecimal(0);
	  
	  private BigDecimal newNota = new BigDecimal(0);
	  
	  private BigDecimal nunota = new BigDecimal(0);
	  
	  private BigDecimal top = new BigDecimal(0);
	  
	  public BigDecimal VLRVENDA = new BigDecimal(0);
	  
	  public BigDecimal nunotaProd = BigDecimal.ZERO;
	  
	  AuthenticationInfo auth = AuthenticationInfo.getCurrent();
	  
	  EntityFacade dwfEntityFacade = EntityFacadeFactory.getDWFFacade();
	  
	  JdbcWrapper jdbc = this.dwfEntityFacade.getJdbcWrapper();
	  
	  NativeSql nativeSql = new NativeSql(this.jdbc);
	  
	  StringBuilder stringBuild = new StringBuilder();
	  
	  private BigDecimal Empresa = new BigDecimal(0);
	  
	  public String control = "";
	  
	  public String ven = "";
	  
	  public String volume = "";
	  
	  private BigDecimal P_produto = new BigDecimal(0);
	  
	  private String P_tipo = "";
	  
	  public String P_Observacao = "";
	  
	  public  String P_Quantidade = "";
	  
	  public String P_ValorUnit = "";
	  public String P_tipo_Uber = "";
	  public String P_Parc = "";
	  
	  public String Descrproduto = "";
	  StringBuffer mensagem = new StringBuffer();
	  
	  public String mensagefinal = "";
	  
	  public BigDecimal PA_Produto = new BigDecimal(0);
	  public BigDecimal codvend = new BigDecimal(0);
	  
	  public BigDecimal Sol_Uber = new BigDecimal(0);
	  public BigDecimal Parc = new BigDecimal(0);
	  public BigDecimal codusu = new BigDecimal(0);
	  public BigDecimal usuPolichat = new BigDecimal(0);
	  public String P_codusu = "";
	  public Integer Msg = 0;
	  public BigDecimal Parc_gas = new BigDecimal(0);
	  public BigDecimal Valor_Unitario = new BigDecimal(0);
	  public Integer evento;
	  public BigDecimal ValrEvento18 = new BigDecimal(0);
	  
	  
	  
	  
	  public void doAction(ContextoAcao ctx) throws Exception {

			System.out.println("OBS :" + ctx.getParam("OBS"));
			System.out.println("LOG VALOR DA SOL:" + ctx.getParam("VLRUNIT"));
			System.out.println("LOG TIPO:" + ctx.getParam("TTP"));
			System.out.println("LOG TIPO UBER :" + ctx.getParam("AP"));
			System.out.println("INICIO");

			P_Observacao = ctx.getParam("OBS").toString();
			P_ValorUnit = ctx.getParam("VLRUNIT").toString();
			P_tipo = ctx.getParam("TTP").toString();
			
			if (ctx.getParam("AP") != null) {
			P_tipo_Uber = ctx.getParam("AP").toString();
			}
			
			
			
			
			if (ctx.getParam("PARC") != null) {
				P_Parc = ctx.getParam("PARC").toString();
			}
			
			
			System.out.println("A desgrçaç é "+P_Parc);

		

			Registro line = ctx.getLinhas()[0];

			geraNota(line, ctx);

			if (this.msgErro.equals("")) {
				ctx.setMensagemRetorno("A solicitação concluida! "+nunotaProd + "/////" + mensagem);
			    
				mensagefinal =  mensagem.toString();
				System.out.println("A MSG ENCODADA"+mensagefinal);
				
				
				enviar_msg_polichat();
			} else {
				ctx.setMensagemRetorno(this.msgErro);
			}

		}

		private void geraNota(Registro line, ContextoAcao ctx) throws Exception {
			
	
			codusu = ctx.getUsuarioLogado(); 
			
			
			if (ctx.getParam("PARC") != null) {
			Parc_gas = new BigDecimal(P_Parc);}
		    
			switch (P_tipo_Uber) {
			case "1":
				Parc = new BigDecimal(46319);
				break;
			case "2":
				Parc = new BigDecimal(62141);
				break;
			default:
				break;
			
			}
		    
			
			P_codusu = codusu.toString();
			
			
			switch (P_codusu) {
			case "514":
				Msg = new Integer(39712562);
				break;
			case "494":
				Msg = new Integer(45776859);
			case "484":
				Msg = new Integer(45776939);
			case "557":
				Msg = new Integer(45777023);
			default:
				break;
			}
			
			
			Valor_Unitario = new BigDecimal(P_ValorUnit);

			switch (P_tipo) {
			case "1":
				this.newNota = criaCabecalho(new BigDecimal(5), new BigDecimal(231), Parc, new BigDecimal(800000000), new BigDecimal(130), new BigDecimal(10716), BigDecimal.ZERO, codusu, P_Observacao, "A", "F", new BigDecimal(800000000),new BigDecimal(10300000),"Q",Valor_Unitario);
		        
				insertnaite(this.newNota, new BigDecimal(1),new BigDecimal(5), new BigDecimal(18633), new BigDecimal(0),"S", codusu,
		        		new BigDecimal(1),Valor_Unitario ,Valor_Unitario,"UN", new BigDecimal(0),"N","N","A","S");
				criarSolicitacaoDeLiberacao2(new Integer(44));	
				

		        System.out.println("ANTES DO ITE");
		        System.out.println("Valor nunotaprod" + this.nunotaProd);
		        
		        mensagem.append(" *Numero da Solicitacao do App Transporte é :* ");
				mensagem.append(" :: "+newNota);
				System.out.println("Msg para polichat" + mensagem);
				break;
			
			case "2":
				this.newNota = criaCabecalho(new BigDecimal(5), new BigDecimal(226), Parc_gas, new BigDecimal(800000000), new BigDecimal(130), new BigDecimal(10716), BigDecimal.ZERO, codusu, P_Observacao, "A", "F", new BigDecimal(800000000),new BigDecimal(10320000),"J",Valor_Unitario);
		        
				insertnaite(this.newNota, new BigDecimal(1),new BigDecimal(5), new BigDecimal(19184), new BigDecimal(95000000),"C", codusu,
		        		new BigDecimal(1),Valor_Unitario ,Valor_Unitario,"LI", new BigDecimal(0),"N","S","A","S");
				criarSolicitacaoDeLiberacao2(new Integer(18));	
	

		        System.out.println("ANTES DO ITE");
		        System.out.println("Valor nunotaprod" + this.nunotaProd);
		        
		        mensagem.append(" *Numero da Solicitacao da Gasolina é :* ");
				mensagem.append(" :: "+newNota);
				System.out.println("Msg para polichat" + mensagem);

			default:
				break;
			}
					

			
			System.out.println("O valor do evento é antes de passar o parametro criar sol "+ evento);
			
			
			
			

		}

		public void insertnaite(BigDecimal nuNota, BigDecimal sequencia,BigDecimal Codemp ,BigDecimal codprod, 
				BigDecimal codlocalorig,  String uso, BigDecimal codusu,
				BigDecimal qtd, BigDecimal unitario,BigDecimal vlrvenda,String codvol, 
				BigDecimal AtualEstoque,String Reserva,String Pendente,String Statusnota, String faturar) throws Exception {
			



			CACHelper cacHelper = new CACHelper();
			JapeSessionContext.putProperty("br.com.sankhya.com.CentralCompraVenda", Boolean.TRUE);
			Collection<PrePersistEntityState> itensNota = new ArrayList<>();
			DynamicVO itemVO = (DynamicVO) this.dwfEntityFacade.getDefaultValueObjectInstance("ItemNota");
			 	itemVO.setProperty("NUNOTA", nuNota);
			    itemVO.setProperty("CODEMP", Codemp);
			    itemVO.setProperty("CODPROD", codprod);
			    itemVO.setProperty("SEQUENCIA", "1");
			    itemVO.setProperty("CODLOCALORIG", codlocalorig);
			    itemVO.setProperty("SEQUENCIA", sequencia);
			    itemVO.setProperty("USOPROD", uso);
			    itemVO.setProperty("CODUSU", codusu);
			    itemVO.setProperty("QTDNEG", qtd);
			    itemVO.setProperty("VLRUNIT", vlrvenda);
			    itemVO.setProperty("VLRTOT", vlrvenda);
			    itemVO.setProperty("ATUALESTOQUE", AtualEstoque);
			    itemVO.setProperty("RESERVA", Reserva);
			    itemVO.setProperty("FATURAR", faturar);
			    itemVO.setProperty("PENDENTE", Pendente);
			    itemVO.setProperty("STATUSNOTA", Statusnota);
			    
			    
			    


			PrePersistEntityState itePreState = PrePersistEntityState.build(this.dwfEntityFacade, "ItemNota", itemVO);
			itensNota.add(itePreState);
			cacHelper.incluirAlterarItem(nunotaProd, auth, itensNota, true);
		}

		@SuppressWarnings("null")
		public BigDecimal criaCabecalho(BigDecimal empresa, BigDecimal tipoOperacao, BigDecimal parceiro, BigDecimal natureza, BigDecimal codTipVenda, BigDecimal codCenCus, BigDecimal modeloNota, BigDecimal codUsu, String Obs, String Statusnota, String Fob, BigDecimal codproj, BigDecimal Codnat,String TipMov,BigDecimal Vlrnota) throws Exception {

			EntityFacade dwfFacade = EntityFacadeFactory.getDWFFacade();
			EntityVO padraoNPVO = null;
			try {
				padraoNPVO = dwfFacade.getDefaultValueObjectInstance("CabecalhoNota");
			} catch (Exception e) {
				throw new Exception("Nfoi possencontrar um nvalido.\nVerifique se o modelo cadastrado no parametro NOTAMODMED, estcom um numero de nota v");
			}
			DynamicVO cabecalhoVO = (DynamicVO) padraoNPVO;
			System.out.println("modeloNota" + modeloNota);
			System.out.println("cabecalhoVO" + cabecalhoVO);
			System.out.println("log_parceiro" + parceiro);
			cabecalhoVO.setProperty("CODTIPOPER", tipoOperacao);
			cabecalhoVO.setProperty("TIPMOV", TipMov);
			cabecalhoVO.setProperty("CODPARC", parceiro);
			cabecalhoVO.setProperty("CODCENCUS", codCenCus);
			cabecalhoVO.setProperty("CODTIPVENDA", codTipVenda);
			cabecalhoVO.setProperty("CODEMP", empresa);
			cabecalhoVO.setProperty("CODNAT", natureza);
			cabecalhoVO.setProperty("NUMNOTA", new BigDecimal(0));
			cabecalhoVO.setProperty("APROVADO", "N");
			cabecalhoVO.setProperty("CODUSU", codUsu);
			cabecalhoVO.setProperty("OBSERVACAO", Obs);
			cabecalhoVO.setProperty("STATUSNOTA", Statusnota);
			cabecalhoVO.setProperty("CIF_FOB", Fob);
			cabecalhoVO.setProperty("CODPROJ", codproj);
			cabecalhoVO.setProperty("CODNAT", Codnat);
			cabecalhoVO.setProperty("CODVEND", new BigDecimal(0));
			cabecalhoVO.setProperty("VLRNOTA", Vlrnota);
			dwfFacade.createEntity("CabecalhoNota", (EntityVO) cabecalhoVO);
			nunotaProd = cabecalhoVO.asBigDecimal("NUNOTA");

			System.out.println("Cabe" + cabecalhoVO);
			return nunotaProd;
		}
		
		 private void enviar_msg_polichat() {
				SessionHandle hnd = null;
				JdbcWrapper jdbc = null;

				try {
					hnd = JapeSession.open();
					EntityFacade dwfFacade = EntityFacadeFactory.getDWFFacade();

					jdbc = dwfFacade.getJdbcWrapper();
					jdbc.openSession();


					CallableStatement cstmt = jdbc.getConnection().prepareCall("{CALL SANKHYA.CA_PC_SEND_MSG_ID (?,?)}");
					cstmt.setQueryTimeout(30);
					cstmt.setInt(1, Msg);
					cstmt.setString(2, mensagefinal);
			    

					cstmt.execute();


				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					JdbcWrapper.closeSession(jdbc);
					JapeSession.close(hnd);
				}
		

	}
		 
		 
	
		 
		  public void criarSolicitacaoDeLiberacao2(Integer evento) throws Exception {
			  JapeWrapper liberacaoLimiteDAO = JapeFactory.dao("LiberacaoLimite");
			    DynamicVO liberacaoLimiteVO;
			    LiberacaoSolicitada liberacaoSolicitada;
			    
			    if (evento == 18)
			    {
			    	ValrEvento18 = new BigDecimal(1);
			    }
			    else if (evento == 44){
			    	ValrEvento18 = Valor_Unitario;
			    }
			    
			    System.out.print("O evento é   "+evento);
			    System.out.print("O valor do unitario do evento   "+ValrEvento18);
			    System.out.print("O valor do unitario da var global é   :"+Valor_Unitario);
			    
			  liberacaoSolicitada = new LiberacaoSolicitada(
					  	nunotaProd,
		                "TGFCAB",
		                evento,
		                BigDecimal.ZERO,
		                ("Nota: " + nunotaProd),
		                BigDecimal.ZERO,
		                ValrEvento18,
		                ValrEvento18,
		                new BigDecimal(482),
		                AuthenticationInfo.getCurrent().getUserID()
		        );
		        LiberacaoAlcadaHelper.inserirSolicitacao(liberacaoSolicitada);
		        LiberacaoAlcadaHelper.processarLiberacao(liberacaoSolicitada);
		    } 
		 
		 
		 
		 
		
		 
		 
		 
		 
		 
		 
		 
		 
		 
		 
	}


