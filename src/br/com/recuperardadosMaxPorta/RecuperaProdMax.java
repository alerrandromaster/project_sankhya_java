package br.com.recuperardadosMaxPorta;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;




import br.com.sankhya.extensions.actionbutton.AcaoRotinaJava;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.extensions.actionbutton.Registro;


import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;



public class RecuperaProdMax implements AcaoRotinaJava {

	public BigDecimal codparc = new BigDecimal(0);
	public String Codparc_s = "";
	public BigDecimal Codparc = new BigDecimal(0);
	public String response = "";
	public String codigo = "";
    public List<String> codigosFabrica = new ArrayList<>();

	@Override
	public void doAction(ContextoAcao contextoAcao) throws Exception {


		Registro line = contextoAcao.getLinhas()[0];
		 response = recebeDadosMax();

	        
		 contextoAcao.setMensagemRetorno("A resposta da API é: " + response);
	        enviarProdutoMax(new BigDecimal(22615),new BigDecimal(800));


	}



	
	
	public String recebeDadosMax() throws IOException {
        URL url = new URL("https://www.maxiportas.com.br/ap_produtos/index.php?Produtos_Lista=null");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
        connection.setRequestProperty("Token", "3838dac474636d8d14c177effc9cbc22");
        connection.setRequestProperty("Empresa", "139");
        connection.setRequestProperty("Ativo", "S");
        connection.setRequestProperty("Dlimite", "2025-02-20");
        connection.setDoOutput(true);
        connection.setReadTimeout(30000);
        connection.setConnectTimeout(30000);

        StringBuilder responseBuilder = new StringBuilder();
        
        try {
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    responseBuilder.append(line);
                }
                reader.close();
            } else {
                responseBuilder.append("Request failed with status: ").append(responseCode);
            }
            
            
            
            
        } catch (IOException e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        } finally {
            connection.disconnect();
        }
        
        

        JSONObject jsonObject = new JSONObject(responseBuilder.toString());
        System.out.println("JSON é"+jsonObject.toString()); 
        JSONArray produtos ;
        
        if (jsonObject.has("dados")) {
            JSONObject dados = jsonObject.getJSONObject("dados");

            // Verificando se a chave "LISTA" existe dentro de "dados"
            if (dados.has("LISTA")) {
                JSONObject lista = dados.getJSONObject("LISTA");

                // Verificando se a chave "ITEM" existe dentro de "LISTA"
                if (lista.has("ITEM")) {
                    produtos = lista.getJSONArray("ITEM");

                    // Verificando os produtos na lista
                    for (int i = 0; i < produtos.length(); i++) {
                        JSONObject produto = produtos.getJSONObject(i);

                        // Verifica se a chave "PRODUTO_CODIGO_FABRICA" existe antes de acessar
                        if (produto.has("PRODUTO_CODIGO_FABRICA")) {
                            String codigoFabrica = produto.optString("PRODUTO_CODIGO_FABRICA", "").trim();

                            // Se o código de fábrica não estiver vazio, adicione à lista
                            if (!codigoFabrica.isEmpty()) {
                                codigosFabrica.add(codigoFabrica);
                                System.out.println("Produto " + (i + 1) + " - Código de Fábrica: " + codigoFabrica);
                            }
                        }
                    }
                } else {
                    System.out.println("Chave 'ITEM' não encontrada.");
                }
            } else {
                System.out.println("Chave 'LISTA' não encontrada dentro de 'dados'.");
            }
        } else {
            System.out.println("Chave 'dados' não encontrada.");
        }
        
        return responseBuilder.toString();
    }
	
	

	
	
	
	public void enviarProdutoMax(BigDecimal Codprod, BigDecimal Estoque) throws IOException {
		
		 boolean existe = false;
	        for (String codigo : codigosFabrica) {
	            try {
	                // Convertendo o código de fábrica (String) para BigDecimal e comparando
	                BigDecimal codigoFabricaBD = new BigDecimal(codigo);
	                if (codigoFabricaBD.compareTo(Codprod) == 0) {
	                    existe = true;
	                    break;
	                }
	            } catch (NumberFormatException e) {
	                // Caso o valor não seja um número válido
	                System.out.println("Erro ao converter código de fábrica para BigDecimal.");
	            }
	        }

	        // Realize uma ação se o código de fábrica existir
	        if (existe) {
	            System.out.println("Código de fábrica encontrado. Realizando ação...");
	            URL url = new URL("https://www.maxiportas.com.br/ap_produtos/index.php?Produtos_Estoque=null");
	    		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
	    		connection.setRequestMethod("POST");
	    		connection.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
	    		connection.setRequestProperty("Token", "3838dac474636d8d14c177effc9cbc22");
	    		connection.setRequestProperty("Empresa","139");
	    		connection.setDoOutput(true);
	    		connection.setReadTimeout(30000);
	    		connection.setConnectTimeout(30000);

	    		String jsonInputString = String.format(
	    				"[{\"PRODUTO_CODIGO_FABRICA\":\"%s\", \"ESTOQUE_OPERACAO\":\"A\", \"ESTOQUE_MOVIMENTACAO\":\"%s\"}]",
	    				Codprod, Estoque);
	    		try (OutputStream os = connection.getOutputStream()) {
	    			byte[] input = jsonInputString.getBytes("utf-8");
	    			os.write(input, 0, input.length);
	    		}
	    		
	    		System.out.println("O JSON DA MAXPORTA :"+jsonInputString);
	    		StringBuilder responseBuilder = new StringBuilder();

	    		try {
	    			int responseCode = connection.getResponseCode();
	    			if (responseCode == HttpURLConnection.HTTP_OK) {
	    				BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));
	    				String line;
	    				while ((line = reader.readLine()) != null) {
	    					responseBuilder.append(line);
	    				}
	    				reader.close();
	    			} else {
	    				responseBuilder.append("Request failed with status: ").append(responseCode);
	    			}
	    		} catch (IOException e) {
	    			e.printStackTrace();
	    			System.out.println("Erro no envio max: " + e.getMessage());
	    		} finally {
	    			connection.disconnect();
	    		}

	    	
	        } else {
	            System.out.println("Código de fábrica não encontrado.");
	            // Lógica para quando o código não for encontrado
	        }
	    }
		

		
		

	}
	





