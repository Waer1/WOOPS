package com.springboot.app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class Robot {
    //public static HashMap<String, ArrayList<String>> RobotedLinks = new HashMap<String, ArrayList<String>>();

    public  static  boolean  RobotAllowed(URL url) throws IOException {
        String host = url.getHost(); //get the host name
        String RobotString = url.getProtocol()+"://"+host+(url.getPort()>-1?":"+url.getPort():"")+"/robots.txt";
        URL RobotUrl;
        try{
            RobotUrl= new URL(RobotString);
        }
        catch (MalformedURLException e)
        {
            return  false;
        }
        String path= url.getPath();
        System.out.println("Robot.txt : " + RobotString+"  is to exploring now");
        BufferedReader robotfile;
        try{
            robotfile = new BufferedReader(new InputStreamReader(RobotUrl.openStream()));
        }
        catch (IOException e)
        {
            return  false;
        }
        String content;
        boolean start_checking= false; //false till reach user-agent:*
        while ((content = robotfile.readLine()) != null)
        {
            // removes whitespace from both ends of a string.
            content = content.trim();
            if((!start_checking)&&content.toLowerCase().startsWith("user-agent"))
            {
                int start = content.indexOf(":") + 1;
                int end   = content.length();
                String agent= content.substring(start, end).trim();
                if(agent.equals("*"))
                    start_checking = true;
            }
            else if(start_checking && content.toLowerCase().startsWith("user-agent"))
            {
                robotfile.close();
                return  true;
            }
            else if(start_checking && content.toLowerCase().startsWith("disallow")) //if i reached Disallow:
            {
                int start = content.indexOf(":") + 1;
                int end   = content.length();
                String disallowedPath= content.substring(start, end).trim();
                if(disallowedPath.equals("/"))
                {
                    robotfile.close();
                    // all website not allowed to be crawled
                    return false;
                }
                if(disallowedPath.length()==0)  //Disallow:
                {
                    robotfile.close();
                    return true;
                }
                if(disallowedPath.length()<=path.length())
                {
                    String subPath= path.substring(0, disallowedPath.length());
                    if(subPath.equals(disallowedPath))
                    {
                        robotfile.close();
                        return  false;
                    }
                }
            }
            else if(start_checking && content.toLowerCase().startsWith("allow")) //if i reached allow:
            {
                int start = content.indexOf(":") + 1;
                int end   = content.length();
                String allowedPath= content.substring(start, end).trim();
                if(allowedPath.equals("/"))
                {
                    robotfile.close();
                    return true;
                }
                if(allowedPath.length()==0)
                {
                    robotfile.close();
                    return false;
                }

            }
        }
        robotfile.close();
        return  true;
    }
}
