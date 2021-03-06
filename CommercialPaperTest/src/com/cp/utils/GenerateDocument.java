package com.cp.utils;

import com.newgen.iforms.custom.IFormReference;
import org.apache.log4j.Logger;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class GenerateDocument implements Constants{
    private static final Logger logger = LogGenerator.getLoggerInstance(GenerateDocument.class);

    public static String generateDoc (IFormReference ifr, String sessionId) {
            String templateName = empty;
            String responseXML;
            if (Shared.getProcess(ifr).equalsIgnoreCase(treasuryProcess)) {
                templateName = tbTemplateName;
            } else if (Shared.getProcess(ifr).equalsIgnoreCase(commercialProcess)) {
                templateName = cpTemplateName;
            }
            logger.info("templateName-- "+templateName);
            String sTemplateName = templateName;
            logger.info("sTemplateName-- "+sTemplateName);
            String sProcessName = ifr.getProcessName();
            logger.info("processName : "+sProcessName);
            String sActivityName = ifr.getActivityName();
            logger.info("sActivityName : "+sActivityName);
            String cabinetName = ifr.getCabinetName();
             logger.info("cabinetName : "+cabinetName);
            int portNo = Integer.parseInt(LoadProp.templatePort);
            logger.info("portNo : "+portNo);
            String serverIp = LoadProp.serverIp;
            logger.info("serverIp : "+serverIp);
            String serverPort = LoadProp.serverPort;
            logger.info("serverPort : "+serverPort);

          String requestXml = "WI_NAME=" + Shared.getWorkItemNumber(ifr) + "~~JTS_IP=127.0.0.1~~JTS_PORT=3333~~SESSION_ID=" + sessionId + "~~SERVER_IP=" + serverIp + "~~SERVER_PORT=" + serverPort + "~~SERVER_NAME=WebSphere~~CABINET_NAME=" + cabinetName + "~~PROCESS_NAME=" + sProcessName + "~~TEMPLATE_NAME=" + sTemplateName + "~~ACTIVITY_NAME=" + sActivityName;
          logger.info("requestXml-- " + requestXml);

            try {
                responseXML = callSocketServer(portNo, requestXml);
                logger.info("SocketCall : sResponseXML : " + responseXML);
            } catch (Exception e) {
                responseXML = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n"
                        + "<message>\n"
                        + "<ErrorCode>1</ErrorCode>\n"
                        + "<ErrorDesc>Error while generating template.</ErrorDesc>\n"
                        + "</message>";
            }
            return responseXML;
    }

    private static String callSocketServer(int iPortNo, String requestXml) {
        final String SS_EXEC_ERROR_MSG="Error from Call Client Socket Server while Web-Service execution";
        final String SS_CONN_ERROR_MSG="Could not connect to Call Client Socket Server";
        String responseXml;
        String sTemp = "";
        try
        {
            logger.info("from call SocketServer try block");
            String serverIp = LoadProp.serverIp;
            String tempResponseXml = "";
            Socket client = new Socket(serverIp, iPortNo);
            client.setSoTimeout(300000);

            try
            {
                DataOutputStream outData = new DataOutputStream(client.getOutputStream());
                byte[] dataByteArr = requestXml.getBytes(StandardCharsets.UTF_8);
                outData.writeInt(dataByteArr.length);
                outData.write(dataByteArr);
                DataInputStream in = new DataInputStream(client.getInputStream());
                int dataLength = in.readInt();
                byte[] data = new byte[dataLength];
                in.readFully(data);
                tempResponseXml = new String(data, StandardCharsets.UTF_8);
                logger.info("CommonMethods : tempResponseXml : "+tempResponseXml);
                in.close();
            } catch (IOException e)
            {
                tempResponseXml = SS_CONN_ERROR_MSG;
            } catch (Exception e)
            {
                tempResponseXml = SS_EXEC_ERROR_MSG;
            }

            if(tempResponseXml.length() == 0)
            {
                responseXml= "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                        "<message>\n" +
                        "<MainCode>-1</MainCode>\n" +
                        "<EDESC>No Response Received from Call Client Socket Server.</EDESC>\n" +
                        "</message>";

            }
            else
            {
                if (tempResponseXml.equals(SS_EXEC_ERROR_MSG)|| tempResponseXml.equals(SS_CONN_ERROR_MSG))
                {
                    responseXml="<?xml version=\"1.0\" encoding=\"utf-8\"?>\n"
                            +"<message>\n"
                            + "<MainCode>-1</MainCode>\n"
                            + "<EDESC>"+tempResponseXml+"</EDESC>\n"
                            + "</message>";
                }
                else
                {
                    responseXml=tempResponseXml;
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            responseXml="<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                    "<message>\n" +
                    "<MainCode>-1</MainCode>\n" +
                    "<EDESC>Not able to Connect with Socket Server.</EDESC>\n" +
                    "</message>";
        }

        return sTemp+responseXml;
    }
}
