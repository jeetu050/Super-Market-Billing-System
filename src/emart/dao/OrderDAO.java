/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package emart.dao;

import emart.dbutil.DBConnection;
import emart.pojo.ProductsPojo;
import emart.pojo.UserProfile;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Omen
 */
public class OrderDAO {
    public static String getNextOrderId() throws SQLException
     {
         Connection conn=DBConnection.getConnection();
         Statement st=conn.createStatement();
         ResultSet rs=st.executeQuery("select max(order_id) from orders");
         rs.next();
         String ordId=rs.getString(1);
         if(ordId==null)
             return "O-101";
         String ordid=rs.getString(1);
         int ordno=Integer.parseInt(ordid.substring(2));
         ordno=ordno+1;
         return "O-"+ordno;
     }
    
    public static boolean addOrder(ArrayList<ProductsPojo> al,String ordId)throws SQLException
    {
        Connection conn=DBConnection.getConnection();
        PreparedStatement ps=conn.prepareStatement("insert into orders values(?,?,?,?)");
        int count=0;
        for(ProductsPojo prd: al)
        {
            ps.setString(1,ordId);                                              
            ps.setString(2,prd.getProductId());                                 
            ps.setString(3,String.valueOf(prd.getQuantity()));                  
            ps.setString(4,UserProfile.getUserid());           
            count=count+ps.executeUpdate();
        }
        return count==al.size();
    }
    
    public static Set<String> getAllOrderId() throws SQLException
     {
         Connection conn=DBConnection.getConnection();
         Statement st=conn.createStatement();
         ResultSet rs=st.executeQuery("select order_id from orders order by order_id");
         HashSet<String> allId=new HashSet<>();
         while(rs.next())
         {
             allId.add(rs.getString(1));
         }
         return allId;
     }
    
    public static List<ProductsPojo> getOrderDetails(String ordid) throws SQLException
    {
        Connection conn=DBConnection.getConnection();
        PreparedStatement ps=conn.prepareStatement("select p_id,quantity from orders where order_id=?");
        ps.setString(1,ordid);
        ResultSet rs=ps.executeQuery();
        
        ArrayList<ProductsPojo> arr=new ArrayList<>();
        while(rs.next())
        {
            ProductsPojo p=new ProductsPojo();
            p.setProductId(rs.getString(1));
            p.setQuantity(Integer.parseInt(rs.getString(2)));
            arr.add(p);
        }
        return arr;
    }
}
