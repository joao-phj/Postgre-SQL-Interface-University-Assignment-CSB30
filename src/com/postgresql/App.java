/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.postgresql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

/**
 *
 * @author joaop
 */
public class App {
    String url = "jdbc:postgresql://200.134.10.32:5432/1802Wildcats";
    String user = "1802Wildcats";
    String password = "349878";
    Connection conn = this.connect();
    
    public Connection connect() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url, user, password);
            System.out.println("Connected to the PostgreSQL server successfully.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
 
        return conn;
    }
    
    public void create() throws SQLException {
        Scanner in = new Scanner(System.in);
        String nome, homeT, birthD, uri, uriC="";
        String conhecido="";
        String SQL="";
        
        System.out.println("********************************************Menu Cadastro***********************************************\n"
                            + "Insira seu nome completo (sem acentos): \n");
        nome = in.nextLine();
        while(nome.length() == 0) {
            System.out.println("O nome não pode ser deixado em branco\nInsira seu nome completo (sem acentos):\n");
            nome = in.nextLine();
        }
        
        String array[] = nome.split(" ");
        
        uri = "http://utfpr.edu.br/CSB30/2018/2/DI1802" + array[0].toLowerCase() + array[array.length-1].toLowerCase();
        
        /********************************************************Fazer Relacionamentos************************************************************/
        
        System.out.println("Insira sua cidade natal:\n");
        homeT = in.nextLine();
        
        System.out.println("Insira sua data de nascmeto (aaaa-mm-dd): \n");
        birthD = in.nextLine();
        
        PreparedStatement ps = conn.prepareStatement("insert into pessoas (uri, nome, cnatal, nascimento) values (?, ?, ?, ?)");
        
        ps.setString(1, uri);
        ps.setString(2, nome);
        ps.setString(3, homeT);
        ps.setString(4, birthD);
        
        long id = 0;
        int affectedRows = ps.executeUpdate();
            
        if (affectedRows > 0) {
            // get the ID back
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    id = rs.getLong(1);
                    System.out.println("\nEntrada criada com sucesso com ID: " + id);
                }
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }
        
        this.list();
        
        while(!conhecido.equals("exit")) {
            System.out.println("Insira o nome Completo de um conhecido(Digite 'exit' para sair): \n");
            conhecido = in.nextLine();
            
            if(conhecido.equals("exit")) {
                break;
            }
            
            SQL = "select uri from pessoas where nome = '" + conhecido + "'";
                        
            PreparedStatement ps2 = conn.prepareStatement(SQL);
            ResultSet rs2 = ps2.executeQuery();
            while (rs2.next()) {
                uriC = rs2.getString(1);
            }
            ps2.close();
            
            System.out.println(SQL);
            
            SQL = "insert into conhece (pessoa, colega) values ('" + uri + "', '" + uriC + "')";
            ps2 = conn.prepareStatement(SQL);
            
            affectedRows = ps2.executeUpdate();
            
            if (affectedRows > 0) {
                // get the ID back
                try (ResultSet rs3 = ps.getGeneratedKeys()) {
                    if (rs3.next()) {
                        id = rs3.getLong(1);
                        }
                } catch (SQLException ex) {
                    System.out.println(ex.getMessage());
                }
            }
        }
    }
    
    public void list() throws SQLException {
        Scanner in = new Scanner(System.in);
        String returnedFields;
        boolean ver[] = new boolean[4];
        boolean error = true;
        String array[]=null;
        String uriQuery, nomeQuery, homeTQuery, birthDQuery;
        
        
        String SQL="select nome from pessoas";
        
        System.out.println(SQL);
        
        PreparedStatement ps = conn.prepareStatement(SQL);
        ResultSet rs = ps.executeQuery();
        
        
        System.out.println("********************************************Nomes***********************************************\n");
        while (rs.next()) {
            System.out.println(rs.getString(1) + "\t");
        }
        
        System.out.println("\nAperte enter para continuar\n");
        returnedFields = in.nextLine();
        
        ps.close();
    }
    
    public void update() throws SQLException {
        Scanner in = new Scanner(System.in);
        String uriQuery, nomeQuery, homeTQuery, birthDQuery;
        String uriNew,nomeNew, homeTNew, birthDNew; 
        
        System.out.println("********************************************Atualização**********************************************\n"
                    + "Digite o nome da Pessoa a ser atualizada:\n");
        nomeQuery = in.nextLine();
        
        System.out.println("Digite o novo nome(Digite NA caso não aplicável):\n");
        nomeNew = in.nextLine();
        
        System.out.println("Digite a nova cidade natal(Digite NA caso não aplicável):\n");
        homeTNew = in.nextLine();
        
        System.out.println("Digite a nova data de nascimento(ex: aaaa-mm-dd)(Digite NA caso não aplicável):\n");
        birthDNew = in.nextLine();
        
        String SQL = "Update pessoas set ";
        
        if(!nomeNew.equals("NA")) {
            SQL += "nome = '" + nomeNew + "', ";
        }
        if(!homeTNew.equals("NA")) {
            SQL += "cnatal = '" + homeTNew + "', ";
        }
        if(!birthDNew.equals("NA")) {
            SQL += "nascimento = '" + birthDNew + "', ";
        }
        
        SQL = SQL.substring(0, SQL.length()-2) + " where nome = '" + nomeQuery + "';";
        
        System.out.println(SQL);
        
        PreparedStatement ps = conn.prepareStatement(SQL);
        
        long id = 0;
        int affectedRows = ps.executeUpdate();
            
        if (affectedRows > 0) {
            // get the ID back
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    id = rs.getLong(1);
                }
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }
        System.out.println("\nEntrada atualizada com sucesso com ID: " + id);
        ps.close();
    }
    
    public void delete() throws SQLException {
        Scanner in = new Scanner(System.in);
        String nomeQuery;
        
        System.out.println("********************************************Deletar**********************************************\n"
                    + "Digite o nome da Pessoa a ser deletada:\n");
        nomeQuery = in.nextLine();
        
        String SQL = "Delete from pessoas where nome = '" + nomeQuery + "';";
        
        System.out.println(SQL);
        
        PreparedStatement ps = conn.prepareStatement(SQL);
        
        long id = 0;
        int affectedRows = ps.executeUpdate();
            
        if (affectedRows > 0) {
            // get the ID back
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    id = rs.getLong(1);
                }
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }
        System.out.println("\nEntrada Deletada com sucesso com ID: " + id);
        ps.close();
    }
    
    public static void main(String[] args) throws SQLException {
        int escolha=0, escolha2=0;
        Scanner in = new Scanner(System.in);
        
        while(escolha != 3) {
            System.out.println("********************************************Menu Inicial**********************************************\n"
                                + "Escolha o numero da sua opção:\n1 - Cadastro\n2 - Listar\n3 - Sair\n");
            escolha = in.nextInt();
            App app = new App();
            
            while(escolha < 1 || escolha > 3) {
                System.out.println("\nOpção Invalida\nEscolha o numero da sua opção:\n1 - Cadastro\n2 - Listar\n3 - Sair\n");
                escolha = in.nextInt();
            }
            
            if(escolha == 1) {
                app.create();
            }
            else if(escolha == 2) {
                app.list();
                
                System.out.println("\nEscolha o numero da sua opção:\n1 - Atualizar\n2 - Deletar\n3 - Sair\n");
                escolha2 = in.nextInt();
                
                while(escolha2 < 1 || escolha2 > 3) {
                    System.out.println("\nOpção Invalida\nEscolha o numero da sua opção:\n1 - Atualizar\n2 - Deletar\n3 - Sair\n");
                    escolha2 = in.nextInt();
                }
                
                if(escolha2 == 1){
                    app.update();
                }
                
                else if(escolha2 == 2){
                    app.delete();
                }
                
            }
            
        }
    }
    
}
