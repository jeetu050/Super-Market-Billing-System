/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package emart.dao;

import emart.dbutil.DBConnection;
import emart.pojo.ProductsPojo;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ProductsDAO {
    public static String getNextProductId() throws SQLException
     {
         Connection conn=DBConnection.getConnection();
         Statement st=conn.createStatement();
         ResultSet rs=st.executeQuery("select max(p_id) from products");
         rs.next();
         String productId=rs.getString(1);
         if(productId==null)
             return "P101";
         String productid=rs.getString(1);
         int productno=Integer.parseInt(productid.substring(1));
         productno=productno+1;
         return "P"+productno;
     }
    
    public static boolean addProduct(ProductsPojo p) throws SQLException
    {
        Connection conn=DBConnection.getConnection();
        PreparedStatement ps=conn.prepareStatement("insert into products values(?,?,?,?,?,?,?,'Y')");
        ps.setString(1,p.getProductId());
        ps.setString(2,p.getProductName());
        ps.setString(3,p.getProductCompany());
        ps.setDouble(4,p.getProductPrice());
        ps.setDouble(5,p.getOurPrice());
        ps.setInt(6,p.getTax());
        ps.setInt(7,p.getQuantity());
        return ps.executeUpdate()==1;
    }
    
    public static List<ProductsPojo> getProductDetails() throws SQLException
    {
        Connection conn=DBConnection.getConnection();
        Statement st=conn.createStatement();
        ResultSet rs=st.executeQuery("select * from products where status='Y' order by p_id");
        ArrayList <ProductsPojo> productsList=new ArrayList<>();
        while(rs.next())
        {
            ProductsPojo p=new ProductsPojo();
            p.setProductId(rs.getString(1));
            p.setProductName(rs.getString(2));
            p.setProductCompany(rs.getString(3));
            p.setProductPrice(Double.parseDouble(rs.getString(4)));
            p.setOurPrice(Double.parseDouble(rs.getString(5)));
            p.setTax(Integer.parseInt(rs.getString(6)));
            p.setQuantity(Integer.parseInt(rs.getString(7)));
            productsList.add(p);
        }
        return productsList;
    }
    
    public static boolean deleteProduct(String productId) throws SQLException
    {
        Connection conn=DBConnection.getConnection();
        PreparedStatement ps=conn.prepareStatement("update products set status='N' where p_id=?");
        ps.setString(1,productId);
        return ps.executeUpdate()==1;
    }
    
    public static boolean updateProduct(ProductsPojo p) throws SQLException
    {
        Connection conn=DBConnection.getConnection();
        PreparedStatement ps=conn.prepareStatement("update products set p_name=?,p_companyname=?,p_price=?,our_price=?,p_tax=?,quantity=? where p_id=?");
        ps.setString(1,p.getProductName());
        ps.setString(2,p.getProductCompany());
        ps.setDouble(3,p.getProductPrice());
        ps.setDouble(4,p.getOurPrice());
        ps.setInt(5,p.getTax());
        ps.setInt(6,p.getQuantity());
        ps.setString(7,p.getProductId());
        return ps.executeUpdate()==1;
    }
    
    public static ProductsPojo getProductDetails(String id) throws SQLException
    {
        Connection conn=DBConnection.getConnection();
        PreparedStatement ps=conn.prepareStatement("select * from products where p_id=? and status='Y'");
        ps.setString(1,id);
        ResultSet rs=ps.executeQuery();
        ProductsPojo p=null;
        if(rs.next())
        {
            p=new ProductsPojo();
            p.setProductId(rs.getString(1));
            p.setProductName(rs.getString(2));
            p.setProductCompany(rs.getString(3));
            p.setProductPrice(Double.parseDouble(rs.getString(4)));
            p.setOurPrice(Double.parseDouble(rs.getString(5)));
            p.setTax(Integer.parseInt(rs.getString(6)));
            p.setQuantity(Integer.parseInt(rs.getString(7)));        //Here we have to set Total also but we will set it from frondend
        }
        return p;
    }
    
    public static boolean updateStocks(List<ProductsPojo> productsList) throws SQLException
    {
        Connection conn=DBConnection.getConnection();
        PreparedStatement ps=conn.prepareStatement("update products set quantity=quantity-? where p_id=?");
        for(ProductsPojo p: productsList)
        {
            //ps.setString(1,String.valueOf(p.getQuantity()));
            ps.setInt(1,p.getQuantity());
            ps.setString(2,p.getProductId());
            ps.addBatch();
        }
        conn.setAutoCommit(false);
        int []arr=ps.executeBatch();
        boolean result=false;
        for(int x: arr)
        {
            switch(x)
            {
                case Statement.SUCCESS_NO_INFO:
                    result=true;
                    conn.commit();
                    break;
                case Statement.EXECUTE_FAILED:
                    conn.rollback();
                    result=false;
                    break; 
                default:
                    result=true;
                    conn.commit();
            }
        }
        return result;
    }
}
