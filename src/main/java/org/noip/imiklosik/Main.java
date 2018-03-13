package org.noip.imiklosik;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/***
 * Created by ioan.miklosik (streppuiu@yahoo.com) on 3/13/2018.
 */

public class Main {

    private static final Path credentialsFilePath = Paths.get(System.getProperty("user.home"), ".aws", "credentials");
    private static final String commandLineTemplate = "aws sts assume-role " +
            "--role-arn %s "+
            "--role-session-name temp " +
            "--serial-number %s " +
            "--profile %s " +
            "--token-code %s";

    public static void main(String[] args) throws IOException, InterruptedException {
        // check arguments
        if(args.length < 4){
            System.out.println("Usage: java -jar <path-to-this-jar> <role-arn> <your-arn> <profile> <auth-app-code>");
            return;
        }

        // run aws command
        String commandLine = String.format(commandLineTemplate, (Object[]) args);
        System.out.println("== Running Command:\n" + commandLine);
        Process p = Runtime.getRuntime().exec(commandLine);
        String jsonString = new BufferedReader(new InputStreamReader(p.getInputStream())).lines().collect(Collectors.joining());
        p.waitFor();
        if(jsonString == null || jsonString.trim().isEmpty()){
            System.out.println("== That didn't work. Empty string returned. Exiting.");
            return;
        }

        // parse json and get new values
        System.out.println("== Parsing Json: " + jsonString);
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonObj = mapper.readTree(jsonString);
        JsonNode credentials = jsonObj.get("Credentials");
        Map<String, String> newVals = Collections.unmodifiableMap(new HashMap<String, String>(3){{
            put("aws_access_key_id", credentials.get("AccessKeyId").textValue());
            put("aws_secret_access_key", credentials.get("SecretAccessKey").textValue());
            put("aws_session_token", credentials.get("SessionToken").textValue());
        }});

        // read original file, replace tokens and write the file back with the new values
        boolean replace[] = new boolean[]{ false };
        Files.write(credentialsFilePath, Files.lines(credentialsFilePath)
                .peek(line -> {
                    line = line.trim();
                    replace[0] = line.equals("[default]") || (!(replace[0] && line.matches("^\\[[^\\]]*\\]$")) && replace[0]);
                })
                .map(line -> {
                    if(!replace[0]){ return line; }
                    String key = line.split("=")[0];
                    String newVal = newVals.get(key);
                    if(newVal == null){ return line; }
                    String newLine = key + "=" + newVal;
                    System.out.println("== Replacing\n" + line + "\nwith\n" + newLine + "\n");
                    return newLine;
                })
                .collect(Collectors.toList())
        );
    }
}
