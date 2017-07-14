package webServices;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;

import dbConnect.FeedDAO;


@Path("/webApi")
public class WebApi {

	@POST
	@Path("/postData")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public String postData(String s)
	{
		JsonParser parser=new JsonParser();
		JsonObject obj=(JsonObject) parser.parse(s);
		Gson gson=new Gson();
		FeedDAO feed=gson.fromJson(obj, FeedDAO.class);
		try{
			Connection con=dbConnect.MySqlConnection.getCon();
			String partner_id=feed.getPartner_id();
			int duration=feed.getDuration();
			String ad_content=feed.getAd_content();
			String insertQuery="insert into adcampaign (partner_id,duration,ad_content) values("+partner_id+",'"+duration+"','"+ad_content+"')";
			PreparedStatement pst=con.prepareStatement(insertQuery);
			pst.executeUpdate();
			pst.close();
			con.close();
			
		}
		catch(MySQLIntegrityConstraintViolationException e)
		{
			
			return "A campaign already exist for a given partner_id.";
		}
		
		catch(Exception e)
		{
			
			return "Sorry we are facing some errors.";
		}

	return "New record inserted.";
		
}
	
	@GET
    @Path("/getData")
	@Consumes(MediaType.APPLICATION_JSON)
    public String selectAll(){
    	JSONObject objFinal=new JSONObject();
    	JSONArray arr=new JSONArray();
    	try 
    	{
        	Connection con = dbConnect.MySqlConnection.getCon();
			PreparedStatement pst=con.prepareStatement("select * from adcampaign");
			ResultSet rs=pst.executeQuery();
			
				while(rs.next())
				{
				JSONObject obj= new JSONObject();
				obj.put("partner_id", rs.getString(1));
				obj.put("duration", rs.getString(2));
				obj.put("ad_content", rs.getString(3));
				obj.put("date", rs.getString(4));
				arr.put(obj);
				}
				objFinal.put("JSONDATA", arr);
		   }
    	catch (SQLException e) 
    	{
		e.printStackTrace();	
		}
    	catch (JSONException e)
    	{
    	 e.printStackTrace();	
    	}
    	return objFinal+"" ;
    }
	

	@GET
    @Path("/getData/{partner_id}")
	@Consumes(MediaType.APPLICATION_JSON)
    public String selectId(@PathParam(value ="partner_id")String partner_id){
    	JSONObject objFinal=new JSONObject();
    	JSONArray arr=new JSONArray();
    	try 
    	{
        	Connection con = dbConnect.MySqlConnection.getCon();
        	PreparedStatement pst=con.prepareStatement("select * from adcampaign where partner_id='"+partner_id+"'");
			ResultSet rs=pst.executeQuery();
			
				while(rs.next())
				{
				JSONObject obj= new JSONObject();
				obj.put("partner_id", rs.getString(1));
				obj.put("duration", rs.getString(2));
				obj.put("ad_content", rs.getString(3));
				obj.put("date", rs.getString(4));
				arr.put(obj);
				}
				objFinal.put("JSONDATA", arr);
		   }
    	catch (SQLException e) 
    	{
		e.printStackTrace();	
		}
    	catch (JSONException e)
    	{
    	 e.printStackTrace();	
    	}
    	return objFinal+"" ;
    }
	
	@GET
    @Path("/getData/getActive/{partner_id}")
    public String checkActive(@PathParam(value ="partner_id")String partner_id){

		String adcontent=null;
		int seconds = 0;
		Timestamp t1=null;
		
    	try 
    	{
        	Connection con = dbConnect.MySqlConnection.getCon();
        	PreparedStatement pst=con.prepareStatement("select * from adcampaign where partner_id='"+partner_id+"'");
			ResultSet rs=pst.executeQuery();
			
				while(rs.next())
				{
					
					seconds=rs.getInt("duration");
					t1=rs.getTimestamp("date");
				}
				
				
		   }
    	catch (SQLException e) 
    	{
		e.printStackTrace();	
		}
    	catch (JSONException e)
    	{
    	 e.printStackTrace();	
    	}

    		//adding duration to timestamp
    		Calendar cal = Calendar.getInstance();
    	    cal.setTimeInMillis(t1.getTime());
    	    cal.add(Calendar.SECOND, seconds);
    	    t1 = new Timestamp(cal.getTime().getTime());
    	    
        	//Getting current timestamp
        	Calendar calendar = Calendar.getInstance();
        	java.util.Date now = calendar.getTime();
        	java.sql.Timestamp currentTimestamp = new java.sql.Timestamp(now.getTime());
        	
        	//if currenttime comes after sum of date+duration
        	if(currentTimestamp.compareTo(t1)>0)  	
        	{
        		return "ERROR: no active ad campaigns exist for the specified partner.";
        	}
        	else
        	{
        		return "SUCCESS: Camapiagn is still active";
        	}
    	
    	
    }
	
}