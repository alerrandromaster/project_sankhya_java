package br.com.gmad.listeners;

import java.math.BigDecimal;

class PedidoInfo {
  private String municipio;
  
  private String bairro;
  
  private String estado;
  
  private String cep;
  
  private BigDecimal valor;
  
  private String endereco;
  
  private String codVendedor;
  
  private String apelido;
  
  private String filial;
  
  private String obs;
  
  private String latitude;
  
  private String longitude;
  
  public PedidoInfo(String municipio, String bairro, String estado, String cep, BigDecimal valor, String endereco, String string, String apelido, String filial, String obs, String latitude, String longitude) {
    this.municipio = municipio;
    this.bairro = bairro;
    this.estado = estado;
    this.cep = cep;
    this.valor = valor;
    this.endereco = endereco;
    this.codVendedor = string;
    this.apelido = apelido;
    this.filial = filial;
    this.obs = obs;
    this.latitude = latitude;
    this.longitude = longitude;
  }
  
  public String getObservacao() {
    return this.obs;
  }
  
  public Object getlongitude() {
    return null;
  }
  
  public String getNomeVendedor() {
    return this.apelido;
  }
  
  public String getCodVendedor() {
    return this.codVendedor;
  }
  
  public String getFilial() {
    return this.filial;
  }
  
  public String getLatitude() {
    return this.latitude;
  }
  
  public String getLongitude() {
    return this.longitude;
  }
  
  public String getMunicipio() {
    return this.municipio;
  }
  
  public void setMunicipio(String municipio) {
    this.municipio = municipio;
  }
  
  public String getBairro() {
    return this.bairro;
  }
  
  public void setBairro(String bairro) {
    this.bairro = bairro;
  }
  
  public String getEstado() {
    return this.estado;
  }
  
  public void setEstado(String estado) {
    this.estado = estado;
  }
  
  public String getCep() {
    return this.cep;
  }
  
  public void setCep(String cep) {
    this.cep = cep;
  }
  
  public BigDecimal getValor() {
    return this.valor;
  }
  
  public void setValor(BigDecimal valor) {
    this.valor = valor;
  }
  
  public String getEndereco() {
    return this.endereco;
  }
  
  public void setEndereco(String endereco) {
    this.endereco = endereco;
  }
}
